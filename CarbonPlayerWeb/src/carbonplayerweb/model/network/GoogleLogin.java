package carbonplayerweb.model.network;

import carbonplayerweb.CarbonAppState;
import carbonplayerweb.model.entity.exception.NeedsBrowserException;
import carbonplayerweb.model.entity.exception.SjNotSupportedException;
import carbonplayerweb.model.network.util.Uri;
import carbonplayerweb.model.network.util.Uri.Builder;
import carbonplayerweb.utils.Preferences;
import carbonplayerweb.utils.general.IdentityUtils;
import com.google.gwt.core.client.GWT;
import io.reactjava.client.gwt.providers.http.HttpClient;
import io.reactjava.client.gwt.providers.http.IHttpClient;
import io.reactjava.client.gwt.rxjs.observable.Observable;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;


/**
 * Contains methods used to authenticate to Google services,
 * as well as to retrieve a Play Music OAuth token.
 * <p>
 * There are several steps used when authenticating:
 * 1) Obtain an Android master token. Normally, this is only called by Google Play services
 *    when setting up a device for the first time. It is needed for steps #2 and #4.
 * 2) Obtain a ClientLogin token using the master token. This token is used for most API calls,
 *    including retrieving a user's music library.
 * 3) Obtain a carbonplayer OAuth token. This is used for retrieving streaming URLs.
 *    This is not enabled in the web version yet.
 * 4) Obtain a Google Play Music oAuth token by simulating Google Play Services API calls.
 *    Most of the code from this step is taken from the microG project. This token is required
 *    for newer features such as the adaptive homepage.
 * </p>
 */
public final class GoogleLogin {

   // The Google public key
   private static final String googleDefaultPublicKey =
      "AAAAgMom/1a/v0lblO2Ubrt60J2gcuXSljGFQXgcyZWveWLEwo6prwgi3iJIZdodyhKZ" +
         "QrNWp5nKJ3srRXcUW+F1BD3baEVGcmEgqaLZUNBjm057pKRI16kB0YppeGx5qIQ5QjKz" +
         "sR8ETQbKLNWgRY0QRNVz34kMJR3P/LgHax/6rmf5AAAAAwEAAQ==";

   private static final String LOGIN_SDK_VERSION = "26";

   private static String browserRecoverUrl;
   private static boolean sjNotSupported = false;

   /**
    * Performs a HTTP request using {@link HttpsURLConnection}
    *
    * @param url     URL
    * @param builder URI builder containing login params
    * @return ArrayMap of response values
    */
   private static Observable<Map<String, String>> loginCall(String url, Uri.Builder builder) {


      IHttpClient client = new HttpClient().setURL(url);
      client.setMethod(IHttpClient.kPOST);

      String query = builder.build().getEncodedQuery();

      client.setRequestHeader("Content-Length", String.valueOf(query.length()));
      client.setRequestHeader("User-Agent", CarbonAppState.googleUserAgent);

      return client.send(query.getBytes(StandardCharsets.UTF_8)).map(r -> {

         HashMap<String, String> response = new HashMap<>();

         String[] lines = r.getText().split("\n");

         for (String line : lines)
         {
            String[] s = line.split("=");
            response.put(s[0], s[1]);
         }

         return response;

      });
   }

   /**
    * Step 1: retrieve an Android master token
    * This endpoint is usually called by Google Play services to register a device
    * on initial activation. The token it gets is used by steps 2 and 4.
    * @param email user's email
    * @param password user's password, will be encrypted
    * @param androidId the actual Android device ID (not GPS id)
    * @return the master token
    */
   private static Observable<String> performMasterLogin(String email, String password, String androidId) {

      Uri.Builder builder = new Uri.Builder();

      try {

         builder.appendQueryParameter("accountType", "HOSTED_OR_GOOGLE")
            .appendQueryParameter("Email", email)
            .appendQueryParameter("has_permission", "1")
            .appendQueryParameter("Passwd", password)
            .appendQueryParameter("service", "ac2dm")
            .appendQueryParameter("source", "android")
            .appendQueryParameter("androidId", androidId)
            .appendQueryParameter("device_country", IdentityUtils.getDeviceCountryCode())
            .appendQueryParameter("operatorCountry", IdentityUtils.getOperatorCountryCode())
            .appendQueryParameter("lang", IdentityUtils.getDeviceLanguage())
            .appendQueryParameter("sdk_version", LOGIN_SDK_VERSION);

         return maybeExtractToken("https://android.clients.google.com/auth", builder);

      } catch (Exception e) {
         e.printStackTrace();
      }

      return null;

   }



   /**
    * Step 1a: retrieve an Android master token after 2FA
    * This endpoint is usually called by Google Play services to register a device
    * on initial activation. The token it gets is used by steps 2 and 4.
    * @param email user's email
    * @param token oauth token from web auth
    * @param androidId the actual Android device ID (not GPS id)
    * @return the master token
    */
   private static Observable<String> okReperformMasterLogin(String email, String token, String androidId) {


      Uri.Builder builder = new Uri.Builder()
         .appendQueryParameter("accountType", "HOSTED_OR_GOOGLE")
         .appendQueryParameter("Email", email)
         .appendQueryParameter("has_permission", "1")
         .appendQueryParameter("ACCESS_TOKEN", "1")
         .appendQueryParameter("Token", token)
         .appendQueryParameter("service", "ac2dm")
         .appendQueryParameter("source", "android")
         .appendQueryParameter("androidId", androidId)
         .appendQueryParameter("device_country", IdentityUtils.getDeviceCountryCode())
         .appendQueryParameter("operatorCountry", IdentityUtils.getOperatorCountryCode())
         .appendQueryParameter("lang", IdentityUtils.getDeviceLanguage())
         .appendQueryParameter("sdk_version", LOGIN_SDK_VERSION);

      return maybeExtractToken("https://android.clients.google.com/auth", builder);

   }

   private static Observable<String> maybeExtractToken(String url, Builder builder) {
      return loginCall(url, builder).map(response -> {
         if (response == null)
            return null;

         if (!response.containsKey("Token"))
         {
            GWT.log("GPS no Token found in Response");

            if (response.containsKey("Error"))
            {
               GWT.log("GoogleLogin -" + response.get("Error") + "-");
               if (response.get("Error").trim().equals("NeedsBrowser"))
               {
                  browserRecoverUrl = response.get("Url");
               }
            }
            return null;
         }

         if (response.containsKey("services"))
         {
            if (!response.get("services").contains("sj"))
            {
               sjNotSupported = true;
               return null;
            }
         }

         return response.get("Token");
      });
   }

   /**
    * Step 2: Get a ClientLogin token for the "sj" (skyjam) scope
    * IMPORTANT: This is *NOT* actually an OAuth token, that was a mistake
    * This token can be used on most Play Music endpoints but not all
    * @param email The user's email
    * @param masterToken The master token retrieved in step 1
    * @param androidId the actual device android ID, same as step 1
    * @return an OAuth master token
    */
   private static Observable<String> performOAuth(String email, String masterToken, String androidId) {

      Uri.Builder builder = new Uri.Builder();

      builder.appendQueryParameter("accountType", "HOSTED_OR_GOOGLE")
         .appendQueryParameter("Email", email)
         .appendQueryParameter("has_permission", "1")
         .appendQueryParameter("add_account", "1")
         .appendQueryParameter("EncryptedPasswd", masterToken)
         .appendQueryParameter("service", "sj")
         .appendQueryParameter("source", "android")
         .appendQueryParameter("androidId", androidId)
         .appendQueryParameter("app", "com.google.android.music")
         .appendQueryParameter("client_sig", "38918a453d07199354f8b19af05ec6562ced5788")
         .appendQueryParameter("device_country", IdentityUtils.getDeviceCountryCode())
         .appendQueryParameter("operatorCountry", IdentityUtils.getOperatorCountryCode())
         .appendQueryParameter("lang", IdentityUtils.getDeviceLanguage())
         .appendQueryParameter("sdk_version", LOGIN_SDK_VERSION);

      return authCall(builder, "https://android.clients.google.com/auth");


   }

   /**
    * Simple Observable wrapper of login code
    *
    * @param context  activity context
    * @param email    user email
    * @param password user password
    * @return Observable which will produce err
    */
   public static Observable<Object> login(String email, String password,
                                   String token) {

         String androidId = CarbonAppState.deviceID;


         // Step 1: Get a master token
         Observable<String> masterToken = token == null ?
            performMasterLogin(email, password, androidId) :
            okReperformMasterLogin(email, token, androidId);

         if(masterToken == null) return null;

         return masterToken.flatMap((tk, i) -> {
            if (tk == null) {
               if(browserRecoverUrl != null) {
                  return Observable._throw(new NeedsBrowserException(browserRecoverUrl));
               } else if (sjNotSupported) {
                  return Observable._throw(new SjNotSupportedException());
               } else return Observable._throw(new Exception());

            }
            prefs().setMasterToken(tk);
            return performOAuth(email, tk, androidId);
         }).flatMap((oauth, i) -> {
            if (oauth == null) {
               return Observable._throw(new Exception());
            }
            prefs().setClientLoginToken(oauth);
            return getMusicOAuth(email, prefs().getMasterToken());
         }).map(playToken -> {
            prefs().setPlayToken(playToken);
            prefs().setUserEmail(email);
            return new Object();
         });
   }

   /**
    * Simple Observable wrapper of login code
    *
    * @param context  activity context
    * @return Observable which will produce err
    */
   /*public static Completable testLogin(@NonNull Activity context) {
      //TODO Rx-ify
      return Completable.create(subscriber -> {
         @SuppressLint("HardwareIds")
         String androidId = Settings.Secure.getString(context.getContentResolver(),
            Settings.Secure.ANDROID_ID);

         // Step 4: Get a Google Play Music oAuth master token
         String playOAuth = getMusicOAuth(context,
            CarbonPlayerApplication.Companion.getDefaultMtoken());

         Timber.d("testPlayOAuth: %s", playOAuth == null ? "null" : playOAuth);

         if (playOAuth != null) CarbonPlayerApplication.Companion.getInstance()
            .preferences.testPlayOAuth = playOAuth;

         subscriber.onComplete();
      });
   }*/

   /**
    * Step 4: Get a Google Play Music oAuth token for the "skyjam" scope (not "sj")
    * This is not a master token, it can be directly used and expires (every hour?)
    * It is required for newer Play Music endpoints like AdaptiveHome
    * Its use should be avoided whenever possible because this function directly
    * impersonates Google Play Services in order to retrieve the token
    *
    * @param context a Context instance
    * @param authToken The master token retrieved in Step 1
    * @return a Play Music oAuth token
    */
   public static Observable<String> getMusicOAuth(String email, String authToken) {

      Uri.Builder builder = new Uri.Builder();


      builder.appendQueryParameter("accountType", "HOSTED_OR_GOOGLE")
      .appendQueryParameter("Email", email)
      .appendQueryParameter("service", "oauth2:https://www.googleapis.com/auth/skyjam")
      .appendQueryParameter("source", "android")
      .appendQueryParameter("androidId", CarbonAppState.deviceID)
      .appendQueryParameter("app", "com.google.android.music")
      .appendQueryParameter("callerPkg", "com.google.android.music")
      .appendQueryParameter("callerSig", "38918a453d07199354f8b19af05ec6562ced5788")
      .appendQueryParameter("client_sig", "38918a453d07199354f8b19af05ec6562ced5788")
      .appendQueryParameter("ACCESS_TOKEN", "1")
      .appendQueryParameter("system_partition", "1")
      .appendQueryParameter("Token", authToken)
      .appendQueryParameter("device_country", IdentityUtils.getDeviceCountryCode().toLowerCase())
      .appendQueryParameter("operatorCountry", IdentityUtils.getOperatorCountryCode())
      .appendQueryParameter("lang", IdentityUtils.getDeviceLanguage().toLowerCase())
      .appendQueryParameter("sdk_version", LOGIN_SDK_VERSION);

      return authCall(builder, "https://android.clients.google.com/auth");


   }

   private static Observable<String> authCall(Builder builder, String url) {
      return loginCall(url, builder).map(response -> {
         if (response == null) return null;

         if (!response.containsKey("Auth")) return null;
         return response.get("Auth");
      });


   }

   /*public static void retryPlayOAuthSync() {
      if(prefs().masterToken == null) return;
      prefs().PlayMusicOAuth = getMusicOAuth(context, prefs().masterToken);
      prefs().save();
   }

   public static void retryTestOAuthSync() {
      prefs().testPlayOAuth = getMusicOAuth(context,
         CarbonPlayerApplication.Companion.getDefaultMtoken());
      prefs().save();
   }

   public static Completable retryGoogleAuth(@NonNull Context context, @NonNull String email) {
      return Completable.create(subscriber -> {

         String mAuthToken = null;
         try {
            Account[] accounts = AccountManager.get(context).getAccounts();
            for (Account a : accounts) {
               Timber.d("|%s|", a.name);
               Timber.d(a.type);
               if (a.type.equals("com.google") && a.name.equals(email)) {
                  mAuthToken = GoogleAuthUtil.getToken(context, a, "oauth2:https://www.googleapis.com/auth/skyjam");
               }
            }
            if (mAuthToken == null) {
               Account a = new Account(email, "com.google");
               mAuthToken = GoogleAuthUtil.getToken(context, a, "oauth2:https://www.googleapis.com/auth/skyjam");
            }

         } catch (IOException | GoogleAuthException ex) {
            subscriber.onError(ex);
         }

         prefs().BearerAuth = mAuthToken;
         prefs().save();

         subscriber.onComplete();
      });
   }

   public static void retryGoogleAuthSync(@NonNull Context context) throws IOException, GoogleAuthException {
      String mAuthToken = null;
      String email = prefs().userEmail;
      Account[] accounts = AccountManager.get(context).getAccounts();
      for (Account a : accounts) {
         Timber.d("|%s|", a.name);
         Timber.d(a.type);
         if (a.type.equals("com.google") && a.name.equals(email)) {
            mAuthToken = GoogleAuthUtil.getToken(context, a, "oauth2:https://www.googleapis.com/auth/skyjam");
         }
      }
      if (mAuthToken == null) {
         Account a = new Account(email, "com.google");
         mAuthToken = GoogleAuthUtil.getToken(context, a, "oauth2:https://www.googleapis.com/auth/skyjam");
      }

      prefs().BearerAuth = mAuthToken;
      prefs().save();
   }

   public static Completable retryGoogleAuth(@NonNull Context context) {
      return retryGoogleAuth(context, prefs().userEmail);
   }*/

   private static Preferences prefs() {
      return CarbonAppState.getInstance().prefs;
   }

}