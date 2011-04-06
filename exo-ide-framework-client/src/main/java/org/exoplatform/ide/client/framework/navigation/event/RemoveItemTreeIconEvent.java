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
package org.exoplatform.ide.client.framework.navigation.event;

import org.exoplatform.gwtframework.ui.client.component.TreeIconPosition;
import org.exoplatform.ide.client.framework.vfs.Item;

import java.util.Map;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: RemoveItemTreeIconEvent Apr 6, 2011 9:50:25 AM evgen $
 *
 */
public class RemoveItemTreeIconEvent extends GwtEvent<RemoveItemTreeIconHandler>
{

   public static GwtEvent.Type<RemoveItemTreeIconHandler> TYPE = new Type<RemoveItemTreeIconHandler>();

   private Map<Item, TreeIconPosition> iconsToRemove;

   /**
    * @param iconsToRemove
    */
   public RemoveItemTreeIconEvent(Map<Item, TreeIconPosition> iconsToRemove)
   {
      super();
      this.iconsToRemove = iconsToRemove;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<RemoveItemTreeIconHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(RemoveItemTreeIconHandler handler)
   {
      handler.onRemoveItemTreeIcon(this);
   }

   /**
    * @return the iconsToRemove
    */
   public Map<Item, TreeIconPosition> getIconsToRemove()
   {
      return iconsToRemove;
   }

}
