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
import com.google.gwt.http.client.Response;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.ClientRequestCallback;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: TemplateListReceivedCallback.java Feb 7, 2011 12:56:15 PM vereshchaka $
 *
 */
public abstract class TemplateListReceivedCallback extends ClientRequestCallback
{
   
   private HandlerManager eventBus;
   
   TemplateList templateList;
   
   public TemplateListReceivedCallback(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
   }
   
   public abstract void onTemplateListReceived();
   
   /**
    * @return the templateList
    */
   public TemplateList getTemplateList()
   {
      if (templateList == null)
         templateList = new TemplateList();
      return templateList;
   }
   
   /**
    * @param templateList the templateList to set
    */
   public void setTemplateList(TemplateList templateList)
   {
      this.templateList = templateList;
   }

   /**
    * @see com.google.gwt.http.client.RequestCallback#onResponseReceived(com.google.gwt.http.client.Request, com.google.gwt.http.client.Response)
    */
   public void onResponseReceived(Request request, Response response)
   {
      onTemplateListReceived();
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
         eventBus.fireEvent(new ExceptionThrownEvent());
      }
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.ClientRequestCallback#onUnsuccess(com.google.gwt.http.client.Request)
    */
   @Override
   public void onUnsuccess(Throwable exception)
   {
      onTemplateListReceived();
   }

}
