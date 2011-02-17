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
package org.exoplatform.ide.extension.groovy.client.service.wadl;

import com.google.gwt.event.shared.HandlerManager;

import com.google.gwt.http.client.Request;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.ClientRequestCallback;
import org.exoplatform.gwtframework.commons.wadl.WadlApplication;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: WadlCallback.java Feb 11, 2011 5:40:38 PM vereshchaka $
 *
 */
public abstract class WadlCallback extends ClientRequestCallback
{
   private HandlerManager eventBus;
   
   private WadlApplication application;
   
   public WadlCallback(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
   }
   
   /**
    * @return the application
    */
   public WadlApplication getApplication()
   {
      return application;
   }
   
   /**
    * @param application the application to set
    */
   public void setApplication(WadlApplication application)
   {
      this.application = application;
   }
   
   /**
    * @see com.google.gwt.http.client.RequestCallback#onError(com.google.gwt.http.client.Request, java.lang.Throwable)
    */
   @Override
   public void onError(Request request, Throwable exception)
   {
      fireErrorEvent(exception);
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.ClientRequestCallback#onUnsuccess(java.lang.Throwable)
    */
   @Override
   public void onUnsuccess(Throwable exception)
   {
      fireErrorEvent(null);
   }
   
   public void fireErrorEvent(Throwable exception)
   {
      eventBus.fireEvent(new ExceptionThrownEvent("Service is not deployed."));
   }

}
