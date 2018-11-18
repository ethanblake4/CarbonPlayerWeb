package carbonplayerweb.utils.js;

import jsinterop.annotations.JsType;

@JsType(namespace = "KJUR.crypto", isNative = true)
public class MessageDigest {

   public MessageDigest(MessageDigestOptions options) {}

   public native void updateString(String str);

   public native String digest();

}