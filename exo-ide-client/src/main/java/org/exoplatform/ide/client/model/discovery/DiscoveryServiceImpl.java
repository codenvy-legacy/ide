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

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.discovery.DiscoveryService;
import org.exoplatform.ide.client.framework.discovery.RestService;
import org.exoplatform.ide.client.framework.discovery.event.DefaultEntryPointReceivedEvent;
import org.exoplatform.ide.client.framework.discovery.event.EntryPointsReceivedEvent;
import org.exoplatform.ide.client.framework.discovery.event.IsDiscoverableResultReceivedEvent;
import org.exoplatform.ide.client.framework.discovery.event.RestServicesReceivedEvent;
import org.exoplatform.ide.client.model.discovery.marshal.DefaultEntryPointUnmarshaller;
import org.exoplatform.ide.client.model.discovery.marshal.DiscoveryServiceDiscoverableUnmarshaller;
import org.exoplatform.ide.client.model.discovery.marshal.EntryPointListUnmarshaller;
import org.exoplatform.ide.client.model.discovery.marshal.RestServicesUnmarshaller;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

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
   public void getEntryPoints()
   {
      String url = restServiceContext + DISCOVERY_SERVICE_CONTEXT;
      getEntryPoints(url);
   }

   @Override
   public void getEntryPoints(String url)
   {
      EntryPointsReceivedEvent event = new EntryPointsReceivedEvent();
      EntryPointListUnmarshaller unmarshaller = new EntryPointListUnmarshaller(event);

      String errorMessage = "Service is not deployed.";
      ExceptionThrownEvent errorEvent = new ExceptionThrownEvent(errorMessage);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, errorEvent);
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }

   /**
    * @see org.exoplatform.ide.client.framework.discovery.DiscoveryService#getDefaultEntryPoint()
    */
   @Override
   public void getDefaultEntryPoint()
   {
      String url = restServiceContext + "/ide/discovery/defaultEntrypoint";

      DefaultEntryPointReceivedEvent event = new DefaultEntryPointReceivedEvent();
      DefaultEntryPointUnmarshaller unmarshaller = new DefaultEntryPointUnmarshaller(event);

      String errorMessage = "Service is not deployed.";
      ExceptionThrownEvent errorEvent = new ExceptionThrownEvent(errorMessage);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, errorEvent);
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }

   /**
    * @see org.exoplatform.ide.client.framework.discovery.DiscoveryService#getRestServices()
    */
   @Override
   public void getRestServices()
   {
      String url = restServiceContext;
      if (!url.endsWith("/"))
      {
         url += "/";
      }
      List<RestService> services = new ArrayList<RestService>();
      RestServicesReceivedEvent event = new RestServicesReceivedEvent(services);
      RestServicesUnmarshaller unmarshaller = new RestServicesUnmarshaller(services);
      String errorMessage = "Service is not deployed.";
      ExceptionThrownEvent errorEvent = new ExceptionThrownEvent(errorMessage);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, errorEvent);
      AsyncRequest.build(RequestBuilder.GET, url, loader).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
         .send(callback);
   }

   @Override
   public void getIsDiscoverable()
   {
      String url = restServiceContext + "/ide/discovery/isdiscoverable";

      IsDiscoverableResultReceivedEvent event = new IsDiscoverableResultReceivedEvent();
      DiscoveryServiceDiscoverableUnmarshaller unmarshaller = new DiscoveryServiceDiscoverableUnmarshaller(event);
      String errorMessage = "Service is not deployed.";
      ExceptionThrownEvent errorEvent = new ExceptionThrownEvent(errorMessage);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, errorEvent);
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }

}
