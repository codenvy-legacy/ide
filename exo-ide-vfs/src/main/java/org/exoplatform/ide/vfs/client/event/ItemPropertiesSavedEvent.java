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
package org.exoplatform.ide.vfs.client.event;

import org.exoplatform.ide.vfs.shared.Item;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Aug 31, 2011 evgen $
 *
 */
public class ItemPropertiesSavedEvent extends GwtEvent<ItemPropertiesSavedHandler>
{

   public static final GwtEvent.Type<ItemPropertiesSavedHandler> TYPE = new Type<ItemPropertiesSavedHandler>();

   private Item item;

   /**
    * @param item
    */
   public ItemPropertiesSavedEvent(Item item)
   {
      super();
      this.item = item;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ItemPropertiesSavedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(ItemPropertiesSavedHandler handler)
   {
      handler.onItemPropertiesSaved(this);
   }

   /**
    * @return the item
    */
   public Item getItem()
   {
      return item;
   }

}
