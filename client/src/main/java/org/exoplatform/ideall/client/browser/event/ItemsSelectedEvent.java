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
package org.exoplatform.ideall.client.browser.event;

import java.util.List;

import org.exoplatform.ideall.client.model.vfs.api.Item;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class ItemsSelectedEvent extends GwtEvent<ItemsSelectedHandler>
{

   public static final GwtEvent.Type<ItemsSelectedHandler> TYPE = new GwtEvent.Type<ItemsSelectedHandler>();

   private List<Item> selectedItems;

   public ItemsSelectedEvent(List<Item> selectedItems)
   {
      this.selectedItems = selectedItems;
   }

   @Override
   protected void dispatch(ItemsSelectedHandler handler)
   {
      handler.onItemsSelected(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ItemsSelectedHandler> getAssociatedType()
   {
      return TYPE;
   }

   public List<Item> getSelectedItems()
   {
      return selectedItems;
   }

}
