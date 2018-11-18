package carbonplayerweb.model.network;

import carbonplayerweb.model.entity.ConfigEntry;
import carbonplayerweb.model.entity.response.ConfigResponse;
import io.reactjava.client.gwt.rxjs.observable.Observable;
import java.util.List;

public class Protocol {

   /**
    * Gets configuration data
    * @return A list of configuration entries
    */
   static Observable<List<ConfigEntry>> getConfig() {
      return HttpProtocol.get(
         ConfigResponse.class,
         RequestCapabilities.create(true),
         UrlType.SKYJAM.with("config"))
         .map(configResponse -> configResponse.getData().getEntries());
   }
}
