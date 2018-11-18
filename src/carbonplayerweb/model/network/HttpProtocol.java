package carbonplayerweb.model.network;

import carbonplayerweb.CarbonAppState;
import carbonplayerweb.model.network.util.Uri;
import carbonplayerweb.utils.general.IdentityUtils;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import io.reactjava.client.gwt.providers.http.HttpClient;
import io.reactjava.client.gwt.providers.http.IHttpClient;
import io.reactjava.client.gwt.rxjs.observable.Observable;
import java.nio.charset.StandardCharsets;

public final class HttpProtocol {

   private static final AutoBeanFactory _jsonFactory =
      GWT.create(CarbonJsonFactory.class);

   public static final String SJ_URL = "https://mclients.googleapis.com/sj/v2.5/"; // JSON URL
   public static final String PA_URL = "https://music-pa.googleapis.com/v1/ij/"; // Protobuf URL
   public static final String STREAM_URL = "https://android.clients.google.com/music/mplay";

   /**
    * Makes a GET request to a skyjam endpoint
    *
    * @param rClass Reified R
    * @param request Request
    * @param capabilities capabilites
    * @param builder Uri to call
    * @return Response object
    */
   public static <R> Observable<R> get(Class<R> rClass, RequestCapabilities capabilities, Uri.Builder builder) {

      return _bestBuilder(capabilities)
         .setURL(appendDefaults(builder).build().toString())
         .setMethod(IHttpClient.kGET)
         .send()
         .map(response ->
         {
            AutoBean<R> bean = AutoBeanCodex.decode(
               _jsonFactory, rClass, response.getText());
            return bean.as();
         });
   }

   /**
    * Makes a POST request to a skyjam endpoint with a Request bean
    *
    * @param rClass Reified R
    * @param request Request
    * @param capabilities capabilites
    * @param builder Uri to call
    * @return Response object
    */
   public static <T, R> Observable<R> post(Class<R> rClass, RequestCapabilities capabilities, Uri.Builder builder, T request) {

      AutoBean<T> requestBean = AutoBeanUtils.getAutoBean(request);
      String requestJson = AutoBeanCodex.encode(requestBean).getPayload();

      return _bestBuilder(capabilities)
         .setURL(appendJsonDefaults(builder).build().toString())
         .setMethod(IHttpClient.kPOST)
         .send(requestJson.getBytes(StandardCharsets.UTF_8))
         .map(response ->
         {
            AutoBean<R> bean = AutoBeanCodex.decode(
               _jsonFactory, rClass, response.getText());
            return bean.as();
         });
   }

   private static IHttpClient _bestBuilder(RequestCapabilities capabilities) {
      if(capabilities.getAcceptCL()) return _defaultBuilder();
      return _playBuilder();
   }

   private static IHttpClient _defaultBuilder() {
      return new HttpClient()
         .setRequestHeader("User-Agent", CarbonAppState.googleUserAgent)
         .setRequestHeader("Authorization",
            "GoogleLogin auth="+ CarbonAppState.getInstance().prefs.getClientLoginToken())
         .setRequestHeader("X-Device-ID", CarbonAppState.deviceID)
         .setRequestHeader("X-Device-Logging-ID", IdentityUtils.getLoggingID());
   }


   private static IHttpClient _playBuilder() {
      return new HttpClient()
         .setRequestHeader("User-Agent", CarbonAppState.googleUserAgent)
         .setRequestHeader("Authorization",
            "GoogleLogin auth="+ CarbonAppState.getInstance().prefs.getPlayToken())
         .setRequestHeader("X-Device-ID", CarbonAppState.deviceID)
         .setRequestHeader("X-Device-Logging-ID", IdentityUtils.getLoggingID());
   }


   private static Uri.Builder appendDefaults(Uri.Builder builder) {
      return builder
         .appendQueryParameter("hl", "en")
         .appendQueryParameter("tier", "aa")
         .appendQueryParameter("dv", CarbonAppState.googleBuildNumber)
         .appendQueryParameter("client-build-type", "prod");
   }

   private static Uri.Builder appendJsonDefaults(Uri.Builder builder) {
      return appendDefaults(builder).appendQueryParameter("alt", "json");
   }
}
