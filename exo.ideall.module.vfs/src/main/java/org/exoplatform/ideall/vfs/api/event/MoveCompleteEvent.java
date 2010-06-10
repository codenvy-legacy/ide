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
package org.exoplatform.ideall.vfs.api.event;

import org.exoplatform.ideall.vfs.api.Item;

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
