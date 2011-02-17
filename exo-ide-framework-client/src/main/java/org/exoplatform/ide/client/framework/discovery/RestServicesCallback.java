/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.client.framework.discovery;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.Request;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.ClientRequestCallback;

import java.util.List;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DiscoveryCallback.java Feb 10, 2011 3:33:23 PM vereshchaka $
 *
 */
public abstract class RestServicesCallback extends ClientRequestCallback
{
   
   private HandlerManager eventBus;
   
   private List<RestService> services;
   
   public RestServicesCallback(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
   }
   
   /**
    * @return the services
    */
   public List<RestService> getServices()
   {
      return services;
   }
   
   /**
    * @param services the services to set
    */
   public void setServices(List<RestService> services)
   {
      this.services = services;
   }

   /**
    * @see com.google.gwt.http.client.RequestCallback#onError(com.google.gwt.http.client.Request, java.lang.Throwable)
    */
   @Override
   public void onError(Request request, Throwable exception)
   {
      fireErrorEvent();
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.ClientRequestCallback#onUnsuccess(java.lang.Throwable)
    */
   @Override
   public void onUnsuccess(Throwable exception)
   {
      fireErrorEvent();
   }
   
   private void fireErrorEvent()
   {
      final String errorMessage = "Service is not deployed.";
      ExceptionThrownEvent errorEvent = new ExceptionThrownEvent(errorMessage);
      eventBus.fireEvent(errorEvent);
   }

}