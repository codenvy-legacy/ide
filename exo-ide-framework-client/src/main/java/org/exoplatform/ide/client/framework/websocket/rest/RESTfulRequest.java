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

import com.google.gwt.http.client.RequestBuilder.Method;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestLoader;
import org.exoplatform.gwtframework.commons.rest.RequestStatusHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for constructing and sending {@link RequestMessage}.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: RESTfulRequest.java Nov 9, 2012 4:14:46 PM azatsarynnyy $
 *
 */
public class RESTfulRequest
{
   /**
    * Message which is constructing and may be send.
    */
   private final RequestMessage requestMessage;

   /**
    * Handler to show an execution state of operation.
    */
   private RequestStatusHandler statusHandler;

   /**
    * Loader to show while request is calling.
    */
   protected AsyncRequestLoader loader;

   /**
    * Creates a {@link RESTfulRequest} using the parameters for configuration.
    * 
    * @param method HTTP method to use for the request
    * @param path URI
    */
   protected RESTfulRequest(Method method, String path)
   {
      requestMessage = RESTMessageBus.AUTO_BEAN_FACTORY.requestMessage().as();
      requestMessage.setUuid(UUID.uuid());
      requestMessage.setMethod((method == null) ? null : method.toString());
      requestMessage.setPath(path);
      loader = new EmptyLoader();
   }

   /**
    * Creates a {@link RESTfulRequest} using the parameters for configuration.
    * 
    * @param method HTTP method to use for the request
    * @param path URI
    * @return a new {@link RESTfulRequest}
    */
   public static final RESTfulRequest build(Method method, String path)
   {
      return new RESTfulRequest(method, path);
   }

   /**
    * Sets a request header with the given name and value. If a header with the
    * specified name has already been set then the new value overwrites the
    * current value.
    * 
    * @param name the name of the header
    * @param value the value of the header
    * @return this {@link RESTfulRequest}
    */
   public final RESTfulRequest header(String name, String value)
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

      Pair header = RESTMessageBus.AUTO_BEAN_FACTORY.pair().as();
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
    * @return this {@link RESTfulRequest}
    */
   public final RESTfulRequest data(String requestData)
   {
      requestMessage.setBody(requestData);
      return this;
   }

   /**
    * Set handler to show an execution state of operation.
    * 
    * @param handler status handler
    * @return this {@link RESTfulRequest}
    */
   public final RESTfulRequest requestStatusHandler(RequestStatusHandler handler)
   {
      this.statusHandler = handler;
      return this;
   }

   /**
    * Set the loader to show while request is calling.
    * 
    * @param loader loader to show
    * @return this {@link RESTfulRequest}
    */
   public final RESTfulRequest loader(AsyncRequestLoader loader)
   {
      this.loader = loader;
      return this;
   }

   /**
    * Sends a request.
    * 
    * @param callback the response handler to be notified when the request fails or completes
    */
   public void send(RequestCallback<?> callback)
   {
      AutoBean<RequestMessage> autoBean = AutoBeanUtils.getAutoBean(requestMessage);
      String message = AutoBeanCodex.encode(autoBean).getPayload();
      if (callback != null)
      {
         callback.setLoader(loader);
         callback.setStatusHandler(statusHandler);
      }

      try
      {
         IDE.messageBus().send(requestMessage.getUuid(), message, callback);
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
         if (callback != null)
         {
            callback.onFailure(e);
         }
      }
   }

}
