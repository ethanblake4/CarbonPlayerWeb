package carbonplayerweb.model.network;

import carbonplayerweb.model.network.util.Uri;

public enum UrlType
{
   SKYJAM,
   INNERJAM,
   MPLAY;

   /**
    * @return The string representation of this URL
    */
   String toPath() {
      switch (this) {
         case SKYJAM: return HttpProtocol.SJ_URL;
         case INNERJAM: return HttpProtocol.PA_URL;
         case MPLAY: return HttpProtocol.STREAM_URL;
         default: return "";
      }
   }

   /**
    * Creates a URI with this URL and a path
    *
    * @param path The path to append
    * @return a Uri.Builder with this URL and the specified path
    */
   Uri.Builder with(String path) {
      return Uri.parse(toPath() + path).buildUpon();
   }
}
