package carbonplayerweb;

import carbonplayerweb.utils.Preferences;

public class CarbonAppState {

   public static Long googleBuildNumberLong = 68381L;
   public static String googleBuildNumber = "68381";
   public static String googleUserAgent = "Android-Music/" + googleBuildNumber + " (" + "taimen" + " " + "PPR1.180610.00489" + "); gzip";
   public static boolean useWebAuthDialog = false;
   public static boolean useOkHttpForLogin = true;
   public static boolean useSearchClustering = true;
   public static boolean useSampleData = false;
   public static String deviceID = "7a4151b857faf24a";

   public Preferences prefs = new Preferences();

   static private CarbonAppState instance;

   public static CarbonAppState getInstance() {
      if(instance == null) instance = new CarbonAppState();
      return instance;
   }

}
