/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.client.framework.websocket.messages;

import com.google.gwt.http.client.Response;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.ide.client.framework.websocket.exceptions.ServerException;

/**
 * Class that implements this interface will receive all
 * messages published to the channels to which class subscribed,
 * and all subscribers to a channel will receive the same messages.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: WSEventHandler.java Jul 30, 2012 9:54:41 AM azatsarynnyy $
 */
public abstract class SubscriptionHandler<T>
{
// http code 207 is "Multi-Status"
   // IE misinterpreting HTTP status code 204 as 1223 (http://www.mail-archive.com/jquery-en@googlegroups.com/msg13093.html)
   private static final int[] DEFAULT_SUCCESS_CODES = {Response.SC_OK, Response.SC_CREATED, Response.SC_NO_CONTENT,
      207, 1223};

   /**
    * Status codes of the successful responses.
    */
   private int[] successCodes;

   /**
    * Deserializer for the body of the {@link RESTfulResponseMessage}.
    */
   private final Unmarshallable<T> unmarshaller;

   /**
    * An object deserialized from the response.
    */
   private final T payload;

   public SubscriptionHandler()
   {
      this(null);
   }

   /**
    * Constructor retrieves unmarshaller with initialized (this is important!) object.
    * When response comes callback calls <code>Unmarshallable.unmarshal()</code>
    * which populates the object.
    * 
    * @param unmarshaller {@link Unmarshallable}
    */
   public SubscriptionHandler(Unmarshallable<T> unmarshaller)
   {
      this.successCodes = DEFAULT_SUCCESS_CODES;

      if (unmarshaller == null)
      {
         this.payload = null;
      }
      else
      {
         this.payload = unmarshaller.getPayload();
      }
      this.unmarshaller = unmarshaller;
   }

   /**
    * Perform actions when {@link RESTfulResponseMessage} was received.
    * 
    * @param response received {@link RESTfulResponseMessage}
    */
   public void onResponseReceived(RESTfulResponseMessage response)
   {
      if (isSuccessful(response))
      {
         try
         {
            if (unmarshaller != null)
            {
               unmarshaller.unmarshal(response);
            }
            onSuccess(payload);
         }
         catch (UnmarshallerException e)
         {
            onFailure(e);
         }
      }
      else
      {
         onFailure(new ServerException(response));
      }
   }

   /**
    * Is response successful?
    * 
    * @param response {@link RESTfulResponseMessage}
    * @return <code>true</code> if response is successful and <code>false</code> if response is not successful
    */
   protected final boolean isSuccessful(RESTfulResponseMessage response)
   {
      if (successCodes == null)
      {
         successCodes = DEFAULT_SUCCESS_CODES;
      }

      for (int code : successCodes)
         if (response.getResponseCode() == code)
            return true;

      return false;
   }

   /**
    * Set the array of successful HTTP status codes.
    * 
    * @param successCodes the successCodes to set
    */
   public void setSuccessCodes(int[] successCodes)
   {
      this.successCodes = successCodes;
   }

   /**
    * Invokes if response is successfully received and
    * response status code is in set of success codes.
    * 
    * @param result
    */
   protected abstract void onSuccess(T result);

   /**
    * Invokes if an error received from the server.
    * 
    * @param exception caused failure
    */
   protected abstract void onFailure(Throwable exception);
}
