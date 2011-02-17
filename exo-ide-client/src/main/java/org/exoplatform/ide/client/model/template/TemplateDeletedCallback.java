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
package org.exoplatform.ide.client.model.template;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.Request;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.ClientRequestCallback;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: TemplateListReceivedCallback.java Feb 7, 2011 12:56:15 PM vereshchaka $
 *
 */
public abstract class TemplateDeletedCallback extends ClientRequestCallback
{
   
   private HandlerManager eventBus;
   
   Template template;
   
   public TemplateDeletedCallback(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
   }
   
   public Template getTemplate()
   {
      return template;
   }
   
   public void setTemplate(Template template)
   {
      this.template = template;
   }
   
   /**
    * @see com.google.gwt.http.client.RequestCallback#onError(com.google.gwt.http.client.Request, java.lang.Throwable)
    */
   public void onError(Request request, Throwable exception)
   {
      if (exception != null)
      {
         eventBus.fireEvent(new ExceptionThrownEvent(exception));
      }
      else
      {
         fireException();
      }
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.ClientRequestCallback#onUnsuccess(com.google.gwt.http.client.Request)
    */
   @Override
   public void onUnsuccess(Throwable exception)
   {
      fireException();
   }
   
   private void fireException()
   {
      String errorMessage = "Registry service is not deployed.<br>Template not found.";
      ExceptionThrownEvent errorEvent = new ExceptionThrownEvent(errorMessage);
      eventBus.fireEvent(errorEvent);
   }

}
