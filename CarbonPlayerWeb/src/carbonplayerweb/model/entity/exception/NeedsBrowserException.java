package carbonplayerweb.model.entity.exception;

public class NeedsBrowserException {

   public NeedsBrowserException(String url) {
      this.url = url;
   }

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   String url;
}
