package carbonplayerweb.model.network.api;

/*==============================================================================

name:       HttpResponse - gwt compatible HttpResponse

purpose:    GWT compatible HttpResponse

history:    Fri Nov 09, 2018 10:30:00 (Giavaneers - LBM) created

notes:
                  This program was created by Giavaneers
        and is the confidential and proprietary product of Giavaneers Inc.
      Any unauthorized use, reproduction or transfer is strictly prohibited.

                     COPYRIGHT 2018 BY GIAVANEERS, INC.
      (Subject to limited distribution and restricted disclosure only).
                           All rights reserved.

==============================================================================*/
// package --------------------------- //

import carbonplayerweb.model.network.api.CarbonHttpClientBase.JsXMLHttpRequest;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.gwt.typedarrays.shared.TypedArrays;
import java.util.HashMap;
import java.util.Map;
// imports --------------------------- //
// (none)                              //
// HttpResponse =======================//
public class HttpResponse implements IHttpResponse
{
   // constants ------------------------- //
   // (none)                              //
   // class variables ------------------- //
   // (none)                              //
   // public instance variables --------- //
   // (none)                              //
   // protected instance variables ------ //
   protected ICarbonHttpClientBase client;      // associated httpClient               //
   // private instance variables -------- //
   // (none)                              //

/*------------------------------------------------------------------------------

@name       HttpResponse - constructor for specified HttpClient
                                                                              */
   /**
    Constructor for specified HttpClient

    @return     An instance of HttpResponse iff successful.

    @param      httpClient     http client

    @history    Fri Nov 09, 2018 10:30:00 (Giavaneers - LBM) created

    @notes
    */
   //------------------------------------------------------------------------------
   public HttpResponse(
      ICarbonHttpClientBase client)
   {
      setClient(client);
   }
/*------------------------------------------------------------------------------

@name       getClient - get http client
                                                                              */
   /**
    Get http client.

    @return     http client

    @history    Fri Nov 09, 2018 10:30:00 (Giavaneers - LBM) created

    */
   //------------------------------------------------------------------------------
   public ICarbonHttpClientBase getClient()
   {
      return(client);
   }
/*------------------------------------------------------------------------------

@name       getBytes - get response data
                                                                              */
   /**
    Get response data.

    @return     response data

    @history    Fri Nov 09, 2018 10:30:00 (Giavaneers - LBM) created

    */
   //------------------------------------------------------------------------------
   public byte[] getBytes()
   {
      byte[]           bytes = null;
      JsXMLHttpRequest xhr   = getClient().getXHR();
      ResponseType     type  = ResponseType.fromString(xhr.getResponseType());

      if (type == ResponseType.kARRAYBUFFER)
      {
         ArrayBuffer buffer = xhr.getResponse();
         int         length = buffer != null ? buffer.byteLength() : 0;
         if (length > 0)
         {
            bytes =
               CarbonHttpClientBase.uint8ArrayToBytes(
                  TypedArrays.createUint8Array(buffer));
         }
      }
      return(bytes);
   }
/*------------------------------------------------------------------------------

@name       getError - get response error
                                                                              */
   /**
    Get response error.

    @return     response error

    @history    Fri Nov 09, 2018 10:30:00 (Giavaneers - LBM) created

    */
   //------------------------------------------------------------------------------
   public Throwable getError()
   {
      Throwable error  = null;
      String    reason = getClient().getErrorReason();
      if (reason != null && reason.length() > 0)
      {
         error = new Exception(reason);
      }
      return(error);
   }
/*------------------------------------------------------------------------------

@name       getHeaders - get all response headers
                                                                              */
   /**
    Get all response headers.

    @return     all response headers

    @history    Fri Nov 09, 2018 10:30:00 (Giavaneers - LBM) created

    @notes
    */
   //------------------------------------------------------------------------------
   public Map<String,String> getHeaders()
   {
      Map<String,String> allHeaders = new HashMap<String,String>();
      for (String header : getClient().getAllResponseHeaders().split("\n"))
      {
         String[] splits = header.split(":");
         allHeaders.put(splits[0].trim().toLowerCase(), splits[1].trim());
      }
      return(allHeaders);
   }
/*------------------------------------------------------------------------------

@name       getStatus - get response status code
                                                                              */
   /**
    Get response status code

    @return     response status code

    @history    Fri Nov 09, 2018 10:30:00 (Giavaneers - LBM) created

    */
   //------------------------------------------------------------------------------
   public int getStatus()
   {
      return(getClient().getStatus());
   }
/*------------------------------------------------------------------------------

@name       getStatusText - get response status text
                                                                              */
   /**
    Get response status text

    @return     response status text

    @history    Fri Nov 09, 2018 10:30:00 (Giavaneers - LBM) created

    */
   //------------------------------------------------------------------------------
   public String getStatusText()
   {
      return(getClient().getStatusText());
   }
/*------------------------------------------------------------------------------

@name       getText - get response text
                                                                              */
   /**
    Get response text.

    @return     response text

    @history    Fri Nov 09, 2018 10:30:00 (Giavaneers - LBM) created

    */
   //------------------------------------------------------------------------------
   public String getText()
   {
      String           text = null;
      JsXMLHttpRequest xhr   = getClient().getXHR();
      ResponseType     type  = ResponseType.fromString(xhr.getResponseType());

      switch(ResponseType.fromString(xhr.getResponseType()))
      {
         case kDEFAULT:
         case kJSON:
         case kTEXT:
         {
            text = xhr.getResponseText();
         }
      }

      return(text);
   }
/*------------------------------------------------------------------------------

@name       getType - get response type
                                                                              */
   /**
    Get response type.

    @return     response type

    @history    Fri Nov 09, 2018 10:30:00 (Giavaneers - LBM) created

    */
   //------------------------------------------------------------------------------
   public ResponseType getType()
   {
      return(getClient().getResponseType());
   }
/*------------------------------------------------------------------------------

@name       setClient - set http client
                                                                              */
   /**
    Set http client.

    @return     this

    @param      httpClient     http client

    @history    Fri Nov 09, 2018 10:30:00 (Giavaneers - LBM) created

    */
   //------------------------------------------------------------------------------
   protected IHttpResponse setClient(
      ICarbonHttpClientBase client)
   {
      this.client = client;
      return(this);
   }
}//====================================// HttpResponse =======================//
