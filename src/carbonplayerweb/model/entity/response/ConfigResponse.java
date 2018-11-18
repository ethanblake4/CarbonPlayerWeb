package carbonplayerweb.model.entity.response;

import carbonplayerweb.model.entity.ConfigEntry;
import java.util.List;

@SuppressWarnings("unused")
/**
 * A response with configuration data for Play Music.
 */
public class ConfigResponse {
   public ConfigResponse(ConfigData data)
   {
      this.data = data;
   }

   public ConfigData getData()
   {
      return data;
   }

   public void setData(ConfigData data)
   {
      this.data = data;
   }

   private ConfigData data;

   /**
    * Configuration data containing a list of ConfigEntry.
    */
   public class ConfigData {

      public ConfigData(List<ConfigEntry> entries)
      {
         this.entries = entries;
      }

      public List<ConfigEntry> getEntries()
      {
         return entries;
      }

      public void setEntries(List<ConfigEntry> entries)
      {
         this.entries = entries;
      }

      private List<ConfigEntry> entries;
   }
}
