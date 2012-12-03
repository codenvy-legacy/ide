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
package com.google.collide.client.communication;

import com.google.collide.client.util.logging.Log;

import com.google.gwt.http.client.RequestBuilder.Method;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestLoader;
import org.exoplatform.gwtframework.commons.rest.RequestStatusHandler;
import org.exoplatform.ide.client.framework.websocket.MessageBus;
import org.exoplatform.ide.client.framework.websocket.WebSocket;
import org.exoplatform.ide.client.framework.websocket.exceptions.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.messages.Pair;
import org.exoplatform.ide.client.framework.websocket.messages.RESTfulRequestCallback;
import org.exoplatform.ide.client.framework.websocket.messages.RESTfulRequestMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Builder for constructing {@link RESTfulRequestMessage} objects.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: RESTfulRequestBuilder.java Nov 9, 2012 4:14:46 PM azatsarynnyy $
 *
 */
public class RESTfulRequestBuilder
{
   /**
    * Message which is constructing and may be send.
    */
   private final RESTfulRequestMessage requestMessage;

   /**
    * Handler to show an execution state of operation.
    */
   private RequestStatusHandler statusHandler;

   /**
    * Loader to show while request is calling.
    */
   protected AsyncRequestLoader loader;

   /**
    * Creates a {@link RESTfulRequestBuilder} using the parameters for configuration.
    * 
    * @param method HTTP method to use for the request
    * @param path URI
    */
   protected RESTfulRequestBuilder(Method method, String path)
   {
      requestMessage = WebSocket.AUTO_BEAN_FACTORY.restFulRequestMessage().as();
      requestMessage.setUuid(generateUuid());
      requestMessage.setMethod((method == null) ? null : method.toString());
      requestMessage.setPath(path);
      loader = new EmptyLoader();
   }

   /**
    * Creates a {@link RESTfulRequestBuilder} using the parameters for configuration.
    * 
    * @param method HTTP method to use for the request
    * @param path URI
    * @return a new {@link RESTfulRequestBuilder}
    */
   public static final RESTfulRequestBuilder build(Method method, String path)
   {
      return new RESTfulRequestBuilder(method, path);
   }

   /**
    * Sets a request header with the given name and value. If a header with the
    * specified name has already been set then the new value overwrites the
    * current value.
    * 
    * @param name the name of the header
    * @param value the value of the header
    * @return this {@link RESTfulRequestBuilder}
    */
   public final RESTfulRequestBuilder header(String name, String value)
   {
      List<Pair> headers = requestMessage.getHeaders();
      if (headers == null)
      {
         headers = new ArrayList<Pair>();
      }

      for (Pair header : headers)
      {
         if (name.equals(header.getName()))
         {
            header.setValue(value);
            return this;
         }
      }

      Pair header = WebSocket.AUTO_BEAN_FACTORY.pair().as();
      header.setName(name);
      header.setValue(value);
      headers.add(header);
      requestMessage.setHeaders(headers);
      return this;
   }

   /**
    * Sets the data to send as body of this request.
    * 
    * @param requestData the data to send as body of the request
    * @return this {@link RESTfulRequestBuilder}
    */
   public final RESTfulRequestBuilder data(String requestData)
   {
      requestMessage.setBody(requestData);
      return this;
   }

   /**
    * Set handler to show an execution state of operation.
    * 
    * @param handler status handler
    * @return this {@link RESTfulRequestBuilder}
    */
   public final RESTfulRequestBuilder requestStatusHandler(RequestStatusHandler handler)
   {
      this.statusHandler = handler;
      return this;
   }

   /**
    * Set the loader to show while request is calling.
    * 
    * @param loader loader to show
    * @return this {@link RESTfulRequestBuilder}
    */
   public final RESTfulRequestBuilder loader(AsyncRequestLoader loader)
   {
      this.loader = loader;
      return this;
   }

   /**
    * Sends a request.
    * 
    * @param callback the response handler to be notified when the request fails or completes
    */
   public void send(MessageBus messageBus, RESTfulRequestCallback<?> callback)
   {
      AutoBean<RESTfulRequestMessage> autoBean = AutoBeanUtils.getAutoBean(requestMessage);
      String message = AutoBeanCodex.encode(autoBean).getPayload();
      if (callback != null)
      {
         callback.setLoader(loader);
         callback.setStatusHandler(statusHandler);
      }

      try
      {
         messageBus.send(message, callback, requestMessage.getUuid());
         if (callback != null)
         {
            loader.show();
            if (statusHandler != null)
            {
               statusHandler.requestInProgress(requestMessage.getUuid());
            }
            
         }
      }
      catch (WebSocketException e)
      {
         Log.error(getClass(), e);
//         if (callback != null)
//         {
//            callback.onFailure(e);
//         }
      }
   }

   /**
    * Returns randomly generated identifier.
    * 
    * @return a randomly generated identifier
    */
   public static String generateUuid()
   {
      Random rand = new Random();
      String id = "";

      for (int i = 0; i < 12; i++)
      {
         int r = rand.nextInt(62);
         if (r > 35)
            id += (char)(r + 61);
         else if (r > 9)
            id += (char)(r + 55);
         else
            id += r;
      }
      return id;
   }

}
