/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.model.discovery;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.discovery.DefaultEntryPointCallback;
import org.exoplatform.ide.client.framework.discovery.DiscoveryCallback;
import org.exoplatform.ide.client.framework.discovery.DiscoveryService;
import org.exoplatform.ide.client.framework.discovery.EntryPoint;
import org.exoplatform.ide.client.framework.discovery.RestService;
import org.exoplatform.ide.client.model.discovery.marshal.DefaultEntryPointUnmarshaller;
import org.exoplatform.ide.client.model.discovery.marshal.DiscoveryServiceDiscoverableUnmarshaller;
import org.exoplatform.ide.client.model.discovery.marshal.EntryPointListUnmarshaller;
import org.exoplatform.ide.client.model.discovery.marshal.RestServicesUnmarshaller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class DiscoveryServiceImpl extends DiscoveryService
{

   private static final String DISCOVERY_SERVICE_CONTEXT = "/ide/discovery/entrypoints";

   private HandlerManager eventBus;

   private Loader loader;

   private String restServiceContext;

   public DiscoveryServiceImpl(HandlerManager eventBus, Loader loader, String restServiceContext)
   {
      this.eventBus = eventBus;
      this.loader = loader;
      this.restServiceContext = restServiceContext;
   }

   @Override
   public void getEntryPoints(DiscoveryCallback discoveryCallback)
   {
      String url = restServiceContext + DISCOVERY_SERVICE_CONTEXT;
      getEntryPoints(url, discoveryCallback);
   }

   @Override
   public void getEntryPoints(String url, DiscoveryCallback callback)
   {
      List<EntryPoint> entryPointList = new ArrayList<EntryPoint>();
      callback.setResult(entryPointList);
      EntryPointListUnmarshaller unmarshaller = new EntryPointListUnmarshaller(entryPointList);

      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }

   /**
    * @see org.exoplatform.ide.client.framework.discovery.DiscoveryService#getDefaultEntryPoint()
    */
   @Override
   public void getDefaultEntryPoint(DefaultEntryPointCallback callback)
   {
      String url = restServiceContext + "/ide/discovery/defaultEntrypoint";

      DefaultEntryPointUnmarshaller unmarshaller = new DefaultEntryPointUnmarshaller(callback);

      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }

   /**
    * @see org.exoplatform.ide.client.framework.discovery.DiscoveryService#getRestServices()
    */
   @Override
   public void getRestServices(AsyncRequestCallback<List<RestService>> callback)
   {
      String url = restServiceContext;
      if (!url.endsWith("/"))
      {
         url += "/";
      }
      List<RestService> services = new ArrayList<RestService>();
      callback.setResult(services);
      RestServicesUnmarshaller unmarshaller = new RestServicesUnmarshaller(services);

      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);
      AsyncRequest.build(RequestBuilder.GET, url, loader).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
         .send(callback);
   }

   @Override
   public void getIsDiscoverable(AsyncRequestCallback<Boolean> callback)
   {
      String url = restServiceContext + "/ide/discovery/isdiscoverable";

      DiscoveryServiceDiscoverableUnmarshaller unmarshaller = new DiscoveryServiceDiscoverableUnmarshaller(callback);

      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }

}
