package carbonplayerweb.utils.js;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(namespace = JsPackage.GLOBAL, isNative = true)
public class JSEncrypt {
   public native void setPublicKey(String key);
   public native String encrypt(String data);
}