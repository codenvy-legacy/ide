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
package org.exoplatform.ide.client.framework.module.vfs.api.event;

import java.util.List;

import org.exoplatform.ide.client.framework.module.vfs.api.Item;
import org.exoplatform.ide.client.framework.module.vfs.api.Version;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 24, 2010 $
 *
 */
public class ItemVersionsReceivedEvent extends GwtEvent<ItemVersionsReceivedHandler>
{
   public static final GwtEvent.Type<ItemVersionsReceivedHandler> TYPE =
      new GwtEvent.Type<ItemVersionsReceivedHandler>();

   
   private Item item;
   
   private List<Version> versions;
   
   
   /**
    * @param item
    * @param versions
    */
   public ItemVersionsReceivedEvent(Item item, List<Version> versions)
   {
      this.item = item;
      this.versions = versions;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ItemVersionsReceivedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(ItemVersionsReceivedHandler handler)
   {
      handler.onItemVersionsReceived(this);
   }

   /**
    * @return the item
    */
   public Item getItem()
   {
      return item;
   }

   /**
    * @return the versions
    */
   public List<Version> getVersions()
   {
      return versions;
   }

   
}
