package carbonplayerweb.model.entity.enums;

public enum StreamQuality {
   UNDEFINED,
   LOW,
   MEDIUM,
   HIGH;

   public String serialize() {
      return String.valueOf(this.ordinal());
   }

   public static StreamQuality deserialize(String in) {
      return StreamQuality.values()[Integer.parseInt(in)];
   }
}