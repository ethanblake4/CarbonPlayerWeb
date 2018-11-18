package carbonplayerweb.model.network;

import carbonplayerweb.model.entity.response.ConfigResponse;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

interface CarbonJsonFactory extends AutoBeanFactory
{
   AutoBean<ConfigResponse> configResponse();

}
