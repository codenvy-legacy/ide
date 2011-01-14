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
package org.exoplatform.ide.client.framework.vfs.event;

import org.exoplatform.ide.client.framework.vfs.Item;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class MoveCompleteEvent extends GwtEvent<MoveCompleteHandler>
{

   public static final GwtEvent.Type<MoveCompleteHandler> TYPE = new GwtEvent.Type<MoveCompleteHandler>();

   private Item item;

   private String sourceHref;

   public MoveCompleteEvent(Item item, String sourceHref)
   {
      this.item = item;
      this.sourceHref = sourceHref;
   }

   @Override
   protected void dispatch(MoveCompleteHandler handler)
   {
      handler.onMoveComplete(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<MoveCompleteHandler> getAssociatedType()
   {
      return TYPE;
   }

   public Item getItem()
   {
      return item;
   }

   public String getSourceHref()
   {
      return sourceHref;
   }
}
