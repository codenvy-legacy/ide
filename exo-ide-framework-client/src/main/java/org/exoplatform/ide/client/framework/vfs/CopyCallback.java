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
package org.exoplatform.ide.client.framework.vfs;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.Request;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.ClientRequestCallback;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CopyCallback.java Feb 16, 2011 3:58:02 PM vereshchaka $
 *
 */
public abstract class CopyCallback extends ClientRequestCallback
{
   private Item item;
   
   private String destination;
   
   private HandlerManager eventuBus;
   
   public CopyCallback(HandlerManager eventBus)
   {
      this.eventuBus = eventBus;
   }
   
   /**
    * @return the item
    */
   public Item getItem()
   {
      return item;
   }
   
   /**
    * @param item the item to set
    */
   public void setItem(Item item)
   {
      this.item = item;
   }
   
   /**
    * @return the destination
    */
   public String getDestination()
   {
      return destination;
   }
   
   /**
    * @param destination the destination to set
    */
   public void setDestination(String destination)
   {
      this.destination = destination;
   }

   /**
    * @see com.google.gwt.http.client.RequestCallback#onError(com.google.gwt.http.client.Request, java.lang.Throwable)
    */
   @Override
   public void onError(Request request, Throwable exception)
   {
      fireError();
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.ClientRequestCallback#onUnsuccess(java.lang.Throwable)
    */
   @Override
   public void onUnsuccess(Throwable exception)
   {
      fireError();
   }
   
   private void fireError()
   {
      eventuBus.fireEvent(new ExceptionThrownEvent("Service is not deployed.<br>Destination path does not exist.<br>Folder already has item with same name."));
   }

}
