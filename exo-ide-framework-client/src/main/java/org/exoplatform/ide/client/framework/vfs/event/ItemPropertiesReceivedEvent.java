/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.framework.vfs.event;

import org.exoplatform.ide.client.framework.vfs.Item;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class ItemPropertiesReceivedEvent extends GwtEvent<ItemPropertiesReceivedHandler>
{

   public static final GwtEvent.Type<ItemPropertiesReceivedHandler> TYPE =
      new GwtEvent.Type<ItemPropertiesReceivedHandler>();

   private Item item;

   public ItemPropertiesReceivedEvent(Item item)
   {
      this.item = item;
   }

   /**
    * @return the item
    */
   public Item getItem()
   {
      return item;
   }

   @Override
   protected void dispatch(ItemPropertiesReceivedHandler handler)
   {
      handler.onItemPropertiesReceived(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ItemPropertiesReceivedHandler> getAssociatedType()
   {
      return TYPE;
   }

}
