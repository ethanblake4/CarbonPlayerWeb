package carbonplayerweb.utils.js;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(namespace = JsPackage.GLOBAL, isNative = true, name = "Object")
public class MessageDigestOptions {
   public String alg;
   public String prov;


}