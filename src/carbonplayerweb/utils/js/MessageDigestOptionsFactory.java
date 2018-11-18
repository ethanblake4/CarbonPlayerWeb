package carbonplayerweb.utils.js;

public class MessageDigestOptionsFactory {
   public static MessageDigestOptions create(String alg, String prov) {
      MessageDigestOptions md = new MessageDigestOptions();
      md.alg = alg;
      md.prov = prov;
      return md;
   }
}