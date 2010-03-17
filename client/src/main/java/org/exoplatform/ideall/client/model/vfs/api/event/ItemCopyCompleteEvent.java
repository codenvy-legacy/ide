/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.model.vfs.api.event;

import org.exoplatform.ideall.client.model.vfs.api.Item;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class ItemCopyCompleteEvent extends GwtEvent<ItemCopyCompleteHandler>
{
   public static GwtEvent.Type<ItemCopyCompleteHandler> TYPE = new GwtEvent.Type<ItemCopyCompleteHandler>();

   private Item copiedItem;

   private String destination;

   public ItemCopyCompleteEvent(Item item, String destination)
   {
      this.copiedItem = item;
      this.destination = destination;
   }

   public Item getCopiedItem()
   {
      return copiedItem;
   }

   public String getDestination()
   {
      return destination;
   }

   @Override
   protected void dispatch(ItemCopyCompleteHandler handler)
   {
      handler.onItemCopyComplete(this);
   }

   @Override
   public GwtEvent.Type<ItemCopyCompleteHandler> getAssociatedType()
   {
      return TYPE;
   }
}
