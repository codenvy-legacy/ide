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
package org.exoplatform.ide.client.module.vfs.api.event;

import org.exoplatform.ide.client.module.vfs.api.Item;
import org.exoplatform.ide.client.module.vfs.api.LockToken;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Sep 13, 2010 $
 *
 */
public class ItemLockedEvent extends GwtEvent<ItemLockedHandler>
{

   public static GwtEvent.Type<ItemLockedHandler> TYPE = new Type<ItemLockedHandler>();

   private LockToken lockToken;

   private Item item;

   /**
    * @param lockToken
    */
   public ItemLockedEvent(Item item, LockToken lockToken)
   {
      this.item = item;
      this.lockToken = lockToken;
   }

   @Override
   protected void dispatch(ItemLockedHandler handler)
   {
      handler.onItemLocked(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ItemLockedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @return the lockToken
    */
   public LockToken getLockToken()
   {
      return lockToken;
   }

   /**
    * @return the item
    */
   public Item getItem()
   {
      return item;
   }

}
