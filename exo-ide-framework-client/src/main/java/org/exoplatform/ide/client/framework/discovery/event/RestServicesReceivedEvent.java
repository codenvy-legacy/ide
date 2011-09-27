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
package org.exoplatform.ide.client.framework.discovery.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.client.framework.discovery.RestService;

import java.util.List;

/**
 * Calls from {@link DiscoveryService} when list of REST Services received
 * <br>
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 21, 2010 4:54:55 PM evgen $
 *
 */
public class RestServicesReceivedEvent extends GwtEvent<RestServicesReceivedHandler>
{

   public static GwtEvent.Type<RestServicesReceivedHandler> TYPE = new Type<RestServicesReceivedHandler>();

   private List<RestService> restServices;

   /**
    * @param services
    */
   public RestServicesReceivedEvent(List<RestService> services)
   {
      this.restServices = services;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<RestServicesReceivedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(RestServicesReceivedHandler handler)
   {
      handler.onRestServicesReceived(this);
   }

   /**
    * Get REST Services
    * @return the {@link List} of {@link RestService} 
    */
   public List<RestService> getRestServices()
   {
      return restServices;
   }
}
