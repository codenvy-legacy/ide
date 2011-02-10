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
package org.exoplatform.ide.extension.netvibes.client.service.deploy.event;

import org.exoplatform.gwtframework.commons.exception.ServerExceptionEvent;
import org.exoplatform.ide.extension.netvibes.client.model.Categories;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event is fired, when the UWA Widget's available categories response is received.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 30, 2010 $
 *
 */
public class WidgetCategoriesReceivedEvent extends ServerExceptionEvent<WidgetCategoriesReceivedHandler>
{
   /**
    * Type used to register this event.
    */
   public static final GwtEvent.Type<WidgetCategoriesReceivedHandler> TYPE =
      new GwtEvent.Type<WidgetCategoriesReceivedHandler>();

   /**
    * UWA Widget's categories.
    */
   private Categories categories;

   /**
    * Server exception.
    */
   private Throwable exception;

   /**
    * @param categories categories
    */
   public WidgetCategoriesReceivedEvent(Categories categories)
   {
      this.categories = categories;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<WidgetCategoriesReceivedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(WidgetCategoriesReceivedHandler handler)
   {
      handler.onWidgetCategoriesReceived(this);
   }

   /**
    * @return {@link Categories} widget's categories
    */
   public Categories getCategories()
   {
      return categories;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.exception.ServerExceptionEvent#setException(java.lang.Throwable)
    */
   @Override
   public void setException(Throwable exception)
   {
      this.exception = exception;
   }

   /**
    * @return {@link Throwable}
    */
   public Throwable getException()
   {
      return exception;
   }
}
