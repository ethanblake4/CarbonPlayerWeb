/*==============================================================================

name:       App.java

purpose:    HelloWorld App.

history:    Sat May 13, 2018 10:30:00 (Giavaneers - LBM) created

notes:

                  This program was created by Giavaneers
        and is the confidential and proprietary product of Giavaneers Inc.
      Any unauthorized use, reproduction or transfer is strictly prohibited.

                     COPYRIGHT 2018 BY GIAVANEERS, INC.
      (Subject to limited distribution and restricted disclosure only).
                           All rights reserved.


==============================================================================*/
                                       // package --------------------------- //
package carbonplayerweb;

import carbonplayerweb.utils.js.MessageDigest;
import carbonplayerweb.utils.js.MessageDigestOptionsFactory;
import com.google.gwt.core.client.GWT;
import io.reactjava.client.gwt.react.AppComponentTemplate;
import io.reactjava.client.gwt.react.INativeEventHandler;
import io.reactjava.client.gwt.react.Properties;

public class App extends AppComponentTemplate<Properties> {

   /**
    * Required default constructor. This implementation is null, but it
    * is not required to be.
    *
    * @return     An instance of App iff successful.
    *
    * @history    Mon Aug 28, 2018 10:30:00 (Giavaneers - LBM) created
    *
    * @notes
    * */
   @SuppressWarnings("unused")
   public App() { }

   /**
    * Required constructor for specified properties. This implementation
    * is essentially null, but it often is not.
    *
    * @return     An instance of App iff successful.
    *
    * @history    Mon Aug 28, 2018 10:30:00 (Giavaneers - LBM) created
    *
    * @notes
    * */
   @SuppressWarnings("unused")
   public App(Properties props) {
      super(props);
   }

   public INativeEventHandler loginHandler = (elemental2.dom.Event e) -> {
      /*GoogleLogin.login("ethanblake@gmail.com", "gziptar2", null).subscribe(obj -> {
         GWT.log(obj.toString());
      });*/
      MessageDigest md = new MessageDigest(MessageDigestOptionsFactory.create("sha1", "cryptojs"));
      md.updateString("test");
      GWT.log(md.digest());
   };

   /**
    * Render component. This implementation is all markup, with no java
    * code included.
    *
    * @return     void
    *
    * @history    Mon May 21, 2018 10:30:00 (Giavaneers - LBM) created
    * Wed Oct 17, 2018 10:30:00 (Giavaneers - LBM) renamed per suggestion
    * by Ethan Elshyeb.
    *
    * @notes
    * */
   public void render() {
      /*--
      <h1 class='hello' style='color:blue;marginTop:30px;fontSize:20px' onClick={this.loginHandler}> Hello World! </h1>
      <div>Hello</div>
      --*/
   }

   /**
    *
    * Get component css. This implementation is elementary, but any css
    * can go here.
    *
    * @return     void
    * @history    Mon May 21, 2018 10:30:00 (Giavaneers - LBM) created
    * @notes
    * */
   public void renderCSS() {
   /*--
      .hello {
         color: green
      }
   --*/
   }
}