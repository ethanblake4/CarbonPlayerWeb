package carbonplayerweb.utils.general;

import carbonplayerweb.CarbonAppState;
import java.util.Random;

public class IdentityUtils
{
   public static String getLoggingID(){

      String loggingID = CarbonAppState.getInstance().prefs.getLoggingID();
      if(loggingID == null){
         loggingID = Long.toHexString(new Random().nextLong());
         CarbonAppState.getInstance().prefs.setLoggingID(loggingID);
      }
      return loggingID;
   }

   public static String getDeviceCountryCode() {
      return "us";
   }

   public static String getOperatorCountryCode() {
      return "us";
   }

   public static String getDeviceLanguage() {
      return "en";
   }

}
