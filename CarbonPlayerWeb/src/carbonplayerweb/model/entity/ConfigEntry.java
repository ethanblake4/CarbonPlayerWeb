package carbonplayerweb.model.entity;


public class ConfigEntry {
   private String name;
   private String value;

   @SuppressWarnings("unused")
   public ConfigEntry(String name, String value) {
      this.name = name;
      this.value = value;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }
}

