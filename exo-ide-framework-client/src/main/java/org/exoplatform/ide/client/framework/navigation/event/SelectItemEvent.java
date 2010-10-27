/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ide.client.framework.navigation.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SelectItemEvent extends GwtEvent<SelectItemHandler>
{

   public static final GwtEvent.Type<SelectItemHandler> TYPE = new GwtEvent.Type<SelectItemHandler>();

   private String itemHref;

   public SelectItemEvent(String itemHref)
   {
      this.itemHref = itemHref;
   }

   public String getItemHref()
   {
      return itemHref;
   }

   @Override
   protected void dispatch(SelectItemHandler handler)
   {
      handler.onSelectItem(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<SelectItemHandler> getAssociatedType()
   {
      return TYPE;
   }

}
