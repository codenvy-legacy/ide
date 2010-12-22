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
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.model.discovery.DiscoveryService;
import org.exoplatform.ide.client.model.discovery.event.RestServicesReceivedEvent;
import org.exoplatform.ide.client.model.discovery.event.RestServicesReceivedHandler;
import org.exoplatform.ide.client.model.discovery.marshal.RestService;
import org.exoplatform.ide.client.restdiscovery.event.ShowRestServicesDiscoveryEvent;
import org.exoplatform.ide.client.restdiscovery.event.ShowRestServicesDiscoveryHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 22, 2010 9:39:28 AM evgen $
 *
 */
public class RestServicesDiscoveryPresenter implements ShowRestServicesDiscoveryHandler, RestServicesReceivedHandler,
   ExceptionThrownHandler
{

   public interface Display
   {
      HasClickHandlers getOkButton();

      ListGridItem<RestService> getListGrid();

      void closeView();
   }

   private HandlerManager eventBus;

   private Handlers handlers;

   private Display dispaly;

   public RestServicesDiscoveryPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);

      eventBus.addHandler(ShowRestServicesDiscoveryEvent.TYPE, this);
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
   }

   /**
    * @see org.exoplatform.ide.client.model.discovery.event.RestServicesReceivedHandler#onRestServicesReceived(org.exoplatform.ide.client.model.discovery.event.RestServicesReceivedEvent)
    */
   public void onRestServicesReceived(RestServicesReceivedEvent event)
   {
      handlers.removeHandlers();
      dispaly.getListGrid().setValue(event.getRestServices());
   }

   /**
    * @see org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler#onError(org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent)
    */
   public void onError(ExceptionThrownEvent event)
   {
      handlers.removeHandlers();
   }

}
