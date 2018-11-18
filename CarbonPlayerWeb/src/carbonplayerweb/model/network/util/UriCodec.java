package carbonplayerweb.model.network.util;

import carbonplayerweb.model.entity.exception.URISyntaxException;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class UriCodec {

   private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

   /**
    * Returns true if {@code c} does not need to be escaped.
    */
   protected abstract boolean isRetained(char c);
   /**
    * Throws if {@code s} is invalid according to this encoder.
    */
   public final String validate(String uri, int start, int end, String name)
      throws URISyntaxException
   {
      for (int i = start; i < end; ) {
         char ch = uri.charAt(i);
         if ((ch >= 'a' && ch <= 'z')
            || (ch >= 'A' && ch <= 'Z')
            || (ch >= '0' && ch <= '9')
            || isRetained(ch)) {
            i++;
         } else if (ch == '%') {
            if (i + 2 >= end) {
               throw new URISyntaxException(uri, "Incomplete % sequence in " + name, i);
            }
            int d1 = hexToInt(uri.charAt(i + 1));
            int d2 = hexToInt(uri.charAt(i + 2));
            if (d1 == -1 || d2 == -1) {
               throw new URISyntaxException(uri, "Invalid % sequence: "
                  + uri.substring(i, i + 3) + " in " + name, i);
            }
            i += 3;
         } else {
            throw new URISyntaxException(uri, "Illegal character in " + name, i);
         }
      }
      return uri.substring(start, end);
   }
   /**
    * Throws if {@code s} contains characters that are not letters, digits or
    * in {@code legal}.
    */
   public static void validateSimple(String s, String legal)
      throws URISyntaxException {
      for (int i = 0; i < s.length(); i++) {
         char ch = s.charAt(i);
         if (!((ch >= 'a' && ch <= 'z')
            || (ch >= 'A' && ch <= 'Z')
            || (ch >= '0' && ch <= '9')
            || legal.indexOf(ch) > -1)) {
            throw new URISyntaxException(s, "Illegal character", i);
         }
      }
   }
   /**
    * Encodes {@code s} and appends the result to {@code builder}.
    *
    * @param isPartiallyEncoded true to fix input that has already been
    *     partially or fully encoded. For example, input of "hello%20world" is
    *     unchanged with isPartiallyEncoded=true but would be double-escaped to
    *     "hello%2520world" otherwise.
    */
   private void appendEncoded(StringBuilder builder, String s, Charset charset,
                              boolean isPartiallyEncoded) {
      if (s == null) {
         throw new NullPointerException("s == null");
      }
      int escapeStart = -1;
      for (int i = 0; i < s.length(); i++) {
         char c = s.charAt(i);
         if ((c >= 'a' && c <= 'z')
            || (c >= 'A' && c <= 'Z')
            || (c >= '0' && c <= '9')
            || isRetained(c)
            || (c == '%' && isPartiallyEncoded)) {
            if (escapeStart != -1) {
               appendHex(builder, s.substring(escapeStart, i), charset);
               escapeStart = -1;
            }
            if (c == '%' && isPartiallyEncoded) {
               // this is an encoded 3-character sequence like "%20"
               builder.append(s, i, Math.min(i + 3, s.length()));
               i += 2;
            } else if (c == ' ') {
               builder.append('+');
            } else {
               builder.append(c);
            }
         } else if (escapeStart == -1) {
            escapeStart = i;
         }
      }
      if (escapeStart != -1) {
         appendHex(builder, s.substring(escapeStart, s.length()), charset);
      }
   }
   public final String encode(String s, Charset charset) {
      // Guess a bit larger for encoded form
      StringBuilder builder = new StringBuilder(s.length() + 16);
      appendEncoded(builder, s, charset, false);
      return builder.toString();
   }
   public final void appendEncoded(StringBuilder builder, String s) {
      appendEncoded(builder, s, StandardCharsets.UTF_8, false);
   }
   public final void appendPartiallyEncoded(StringBuilder builder, String s) {
      appendEncoded(builder, s, StandardCharsets.UTF_8, true);
   }
   /**
    * @param convertPlus true to convert '+' to ' '.
    * @param throwOnFailure true to throw an IllegalArgumentException on
    *     invalid escape sequences; false to replace them with the replacement
    *     character (U+fffd).
    */
   public static String decode(String s, boolean convertPlus, Charset charset,
                               boolean throwOnFailure) {
      if (s.indexOf('%') == -1 && (!convertPlus || s.indexOf('+') == -1)) {
         return s;
      }
      StringBuilder result = new StringBuilder(s.length());
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      for (int i = 0; i < s.length();) {
         char c = s.charAt(i);
         if (c == '%') {
            do {
               int d1, d2;
               if (i + 2 < s.length()
                  && (d1 = hexToInt(s.charAt(i + 1))) != -1
                  && (d2 = hexToInt(s.charAt(i + 2))) != -1) {
                  out.write((byte) ((d1 << 4) + d2));
               } else if (throwOnFailure) {
                  throw new IllegalArgumentException("Invalid % sequence at " + i + ": " + s);
               } else {
                  byte[] replacement = "\ufffd".getBytes(charset);
                  out.write(replacement, 0, replacement.length);
               }
               i += 3;
            } while (i < s.length() && s.charAt(i) == '%');
            result.append(new String(out.toByteArray(), charset));
            out.reset();
         } else {
            if (convertPlus && c == '+') {
               c = ' ';
            }
            result.append(c);
            i++;
         }
      }
      return result.toString();
   }
   /**
    * Like {@link Character#digit}, but without support for non-ASCII
    * characters.
    */
   private static int hexToInt(char c) {
      if ('0' <= c && c <= '9') {
         return c - '0';
      } else if ('a' <= c && c <= 'f') {
         return 10 + (c - 'a');
      } else if ('A' <= c && c <= 'F') {
         return 10 + (c - 'A');
      } else {
         return -1;
      }
   }
   public static String decode(String s) {
      return decode(s, false, StandardCharsets.UTF_8, true);
   }

   private static void appendHex(StringBuilder sb, String s, Charset charset) {
      byte[] bytes = s.getBytes(charset);
      char[] hexChars = new char[bytes.length * 2];
      for ( int j = 0; j < bytes.length; j++ ) {
         int v = bytes[j] & 0xFF;
         hexChars[j * 2] = hexArray[v >>> 4];
         hexChars[j * 2 + 1] = hexArray[v & 0x0F];
      }
      sb.append(hexChars);
   }
}