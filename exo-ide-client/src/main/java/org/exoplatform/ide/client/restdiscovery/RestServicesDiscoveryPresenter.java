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
package org.exoplatform.ide.client.restdiscovery;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.commons.wadl.IllegalWADLException;
import org.exoplatform.gwtframework.commons.wadl.WadlApplication;
import org.exoplatform.gwtframework.commons.wadl.WadlProcessor;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.model.discovery.DiscoveryService;
import org.exoplatform.ide.client.model.discovery.event.RestServicesReceivedEvent;
import org.exoplatform.ide.client.model.discovery.event.RestServicesReceivedHandler;
import org.exoplatform.ide.client.model.discovery.marshal.RestService;
import org.exoplatform.ide.client.module.groovy.service.wadl.WadlService;
import org.exoplatform.ide.client.module.groovy.service.wadl.event.WadlServiceOutputReceiveHandler;
import org.exoplatform.ide.client.module.groovy.service.wadl.event.WadlServiceOutputReceivedEvent;
import org.exoplatform.ide.client.restdiscovery.event.ShowRestServicesDiscoveryEvent;
import org.exoplatform.ide.client.restdiscovery.event.ShowRestServicesDiscoveryHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 22, 2010 9:39:28 AM evgen $
 *
 */
public class RestServicesDiscoveryPresenter implements ShowRestServicesDiscoveryHandler, RestServicesReceivedHandler,
   ExceptionThrownHandler, WadlServiceOutputReceiveHandler, InitializeServicesHandler
{

   public interface Display
   {
      HasClickHandlers getOkButton();

      //ListGridItem<RestService> getListGrid();

      UntypedTreeGrid getTreeGrid();

      void closeView();
   }

   private HandlerManager eventBus;

   private Handlers handlers;

   private Display dispaly;

   private String wadlXml =
      "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><application xmlns=\"http://research.sun.com/wadl/2006/10\"><resources base=\"http://127.0.0.1:8888/rest/private\"><resource path=\"/jcr\"><method name=\"OPTIONS\"><response><representation mediaType=\"application/vnd.sun.wadl+xml\"/></response></method><resource path=\"/{repoName}/{path:.*}/\"><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"template\" name=\"path\"/><method name=\"OPTIONS\" id=\"options\"><response><representation mediaType=\"*/*\"/></response></method></resource><resource path=\"/{repoName}/{repoPath:.*}/\"><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"template\" name=\"repoName\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"template\" name=\"repoPath\"/><method name=\"MKCOL\" id=\"mkcol\"><request><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"lock-token\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"If\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"Content-NodeType\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"Content-MixinTypes\"/></request><response><representation mediaType=\"*/*\"/></response></method><method name=\"REPORT\" id=\"report\"><request><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"depth\"/><representation mediaType=\"*/*\"/></request><response><representation mediaType=\"*/*\"/></response></method><method name=\"MOVE\" id=\"move\"><request><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"Destination\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"lock-token\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"If\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"depth\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"Overwrite\"/><representation mediaType=\"*/*\"/></request><response><representation mediaType=\"*/*\"/></response></method><method name=\"COPY\" id=\"copy\"><request><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"Destination\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"lock-token\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"If\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"depth\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"Overwrite\"/><representation mediaType=\"*/*\"/></request><response><representation mediaType=\"*/*\"/></response></method><method name=\"CHECKOUT\" id=\"checkout\"><request><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"lock-token\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"If\"/></request><response><representation mediaType=\"*/*\"/></response></method><method name=\"GET\" id=\"get\"><request><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"Range\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"If-Modified-Since\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"query\" name=\"version\"/></request><response><representation mediaType=\"*/*\"/></response></method><method name=\"SEARCH\" id=\"search\"><request><representation mediaType=\"*/*\"/></request><response><representation mediaType=\"*/*\"/></response></method><method name=\"ORDERPATCH\" id=\"order\"><request><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"lock-token\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"If\"/><representation mediaType=\"*/*\"/></request><response><representation mediaType=\"*/*\"/></response></method><method name=\"DELETE\" id=\"delete\"><request><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"lock-token\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"If\"/></request><response><representation mediaType=\"*/*\"/></response></method><method name=\"CHECKIN\" id=\"checkin\"><request><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"lock-token\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"If\"/></request><response><representation mediaType=\"*/*\"/></response></method><method name=\"VERSION-CONTROL\" id=\"versionControl\"><request><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"lock-token\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"If\"/></request><response><representation mediaType=\"*/*\"/></response></method><method name=\"UNCHECKOUT\" id=\"uncheckout\"><request><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"lock-token\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"If\"/></request><response><representation mediaType=\"*/*\"/></response></method><method name=\"PROPFIND\" id=\"propfind\"><request><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"depth\"/><representation mediaType=\"*/*\"/></request><response><representation mediaType=\"*/*\"/></response></method><method name=\"LOCK\" id=\"lock\"><request><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"lock-token\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"If\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"depth\"/><representation mediaType=\"*/*\"/></request><response><representation mediaType=\"*/*\"/></response></method><method name=\"PROPPATCH\" id=\"proppatch\"><request><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"lock-token\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"If\"/><representation mediaType=\"*/*\"/></request><response><representation mediaType=\"*/*\"/></response></method><method name=\"PUT\" id=\"put\"><request><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"lock-token\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"If\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"File-NodeType\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"Content-NodeType\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"Content-MixinTypes\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"Content-Type\"/><representation mediaType=\"*/*\"/></request><response><representation mediaType=\"*/*\"/></response></method><method name=\"UNLOCK\" id=\"unlock\"><request><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"lock-token\"/><param xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" type=\"xs:string\" style=\"header\" name=\"If\"/></request><response><representation mediaType=\"*/*\"/></response></method></resource></resource></resources></application>";

   private RestService currentRestService;

   private String restContext;

   public RestServicesDiscoveryPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);

      eventBus.addHandler(ShowRestServicesDiscoveryEvent.TYPE, this);
      eventBus.addHandler(InitializeServicesEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.restdiscovery.event.ShowRestServicesDiscoveryHandler#onShowRestServicesDiscovery(org.exoplatform.ide.client.restdiscovery.event.ShowRestServicesDiscoveryEvent)
    */
   public void onShowRestServicesDiscovery(ShowRestServicesDiscoveryEvent event)
   {
      Display d = new RestServicesDiscoveryForm(eventBus);

      bundDisplay(d);

      handlers.addHandler(ExceptionThrownEvent.TYPE, this);
      handlers.addHandler(RestServicesReceivedEvent.TYPE, this);

      DiscoveryService.getInstance().getRestServices();
   }

   /**
    * @param d
    */
   private void bundDisplay(Display d)
   {
      if (dispaly != null)
      {
         dispaly.closeView();
      }

      dispaly = d;

      dispaly.getOkButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            dispaly.closeView();
            dispaly = null;
         }
      });

      dispaly.getTreeGrid().addOpenHandler(new OpenHandler<Object>()
      {

         public void onOpen(OpenEvent<Object> event)
         {
            if (event.getTarget() instanceof RestService)
            {
               updateResourceWadl((RestService)event.getTarget());
            }
         }
      });
   }

   /**
    * @param target
    */
   private void updateResourceWadl(RestService target)
   {
      currentRestService = target;
      String url = restContext;

      if (target.getPath().startsWith("/"))
      {
         url += target.getPath();
      }
      else
      {
         url += "/" + target.getPath();
      }
      handlers.addHandler(WadlServiceOutputReceivedEvent.TYPE, this);

      WadlService.getInstance().getWadl(url);
   }

   /**
    * @see org.exoplatform.ide.client.model.discovery.event.RestServicesReceivedHandler#onRestServicesReceived(org.exoplatform.ide.client.model.discovery.event.RestServicesReceivedEvent)
    */
   public void onRestServicesReceived(RestServicesReceivedEvent event)
   {
      handlers.removeHandlers();

      dispaly.getTreeGrid().setRootValues(event.getRestServices());
   }

   /**
    * @see org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler#onError(org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent)
    */
   public void onError(ExceptionThrownEvent event)
   {
      handlers.removeHandlers();
   }

   /**
    * @see org.exoplatform.ide.client.module.groovy.service.wadl.event.WadlServiceOutputReceiveHandler#onWadlServiceOutputReceived(org.exoplatform.ide.client.module.groovy.service.wadl.event.WadlServiceOutputReceivedEvent)
    */
   public void onWadlServiceOutputReceived(WadlServiceOutputReceivedEvent event)
   {
      handlers.removeHandlers();
      if (event.getException() == null)
      {
         WadlApplication a = event.getApplication();
         dispaly.getTreeGrid()
            .setPaths(currentRestService, a.getResources().getResource().get(0).getMethodOrResource());
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent)
    */
   public void onInitializeServices(InitializeServicesEvent event)
   {
      restContext = event.getApplicationConfiguration().getContext();
      if (restContext.endsWith("/"))
      {
         restContext = restContext.substring(0, restContext.length());
      }
   }

}
