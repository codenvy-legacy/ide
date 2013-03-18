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
package org.exoplatform.ide.client.framework.websocket.rest;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.ide.client.framework.websocket.Message;
import org.exoplatform.ide.client.framework.websocket.events.MessageHandler;
import org.exoplatform.ide.client.framework.websocket.rest.exceptions.ServerException;

/**
 * Handler to receive messages by subscription.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: SubscriptionHandler.java Jul 30, 2012 9:54:41 AM azatsarynnyy $
 */
public abstract class SubscriptionHandler<T> implements MessageHandler
{
   /**
    * Deserializer for the body of the {@link ResponseMessage}.
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
    * Perform actions when {@link ResponseMessage} was received.
    *
    * @param message received {@link ResponseMessage}
    */
   public void onMessage(Message message)
   {

      if (!(message instanceof ResponseMessage))
         throw new IllegalArgumentException("Invalid input message.");

      ResponseMessage response = (ResponseMessage)message;

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


   @Override
   public void onMessage(String message)
   {
   }

   /**
    * Is message successful?
    * 
    * @param message {@link ResponseMessage}
    * @return <code>true</code> if message is successful and <code>false</code> if not
    */
   protected final boolean isSuccessful(ResponseMessage message)
   {
      for (Pair header : message.getHeaders())
      {
         if ("x-everrest-websocket-message-type".equals(header.getName()) && "none".equals(header.getValue()))
         {
            return true;
         }
      }

      return false;
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
