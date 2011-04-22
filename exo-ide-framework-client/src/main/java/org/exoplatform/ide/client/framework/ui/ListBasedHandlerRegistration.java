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
package org.exoplatform.ide.client.framework.ui;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * This HandlerRegistration points on event which stores in the List of handlers.
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ListBasedHandlerRegistration implements HandlerRegistration
{

   /**
    * List where handler stores.
    */
   private List<?> handlerList;

   /**
    * Event Handler instance.
    */
   private EventHandler handler;

   /**
    * Creates new instance of this HandlerRegistration.
    * 
    * @param handlerList list where handlers stores
    * @param handler event handler
    */
   public ListBasedHandlerRegistration(List<?> handlerList, EventHandler handler)
   {
      this.handlerList = handlerList;
      this.handler = handler;
   }

   /**
    * @see com.google.gwt.event.shared.HandlerRegistration#removeHandler()
    */
   @Override
   public void removeHandler()
   {
      handlerList.remove(handler);
   }

}
