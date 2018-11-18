package carbonplayerweb.utils;

import carbonplayerweb.model.entity.enums.StreamQuality;
import com.google.gwt.user.client.Cookies;

public class Preferences {

   public String getMasterToken() {
      return Cookies.getCookie("CarbonAuth_Master");
   }

   public String getClientLoginToken() {
      return Cookies.getCookie("CarbonAuth_CL");
   }

   public String getPlayToken() {
      return Cookies.getCookie("CarbonAuth_Play");
   }

   public StreamQuality getStreamQuality() {
      return StreamQuality.deserialize(
         provided(Cookies.getCookie("CarbonPlayback_Quality"), "0"));
   }

   public String getLoggingID() {
      return Cookies.getCookie("CarbonGSF_LoggingID");
   }

   public void setLoggingID(String loggingID) {
      Cookies.setCookie("CarbonGSF_LoggingID", loggingID);
   }

   public void setMasterToken(String token) {
      Cookies.setCookie("CarbonAuth_Master", token);
   }

   public void setClientLoginToken(String token) {
      Cookies.setCookie("CarbonAuth_CL", token);
   }

   public void setPlayToken(String token) {
      Cookies.setCookie("CarbonAuth_Play", token);
   }

   public void setUserEmail(String email) {
      Cookies.setCookie("CarbonUser_Email", email);
   }

   private String provided(String in, String ifNull) {
      if(in != null) return in;
      return ifNull;
   }




}
