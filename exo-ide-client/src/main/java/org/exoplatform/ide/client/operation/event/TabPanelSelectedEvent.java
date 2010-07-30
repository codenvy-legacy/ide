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

package org.exoplatform.ideall.client.operation.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS        .
 * @version $Id: $
 */

public class TabPanelSelectedEvent extends GwtEvent<TabPanelSelectedHandler>
{

   public static GwtEvent.Type<TabPanelSelectedHandler> TYPE = new GwtEvent.Type<TabPanelSelectedHandler>();

   @Override
   protected void dispatch(TabPanelSelectedHandler handler)
   {
      handler.onTabPanelSelected(this);
   }

   @Override
   public GwtEvent.Type<TabPanelSelectedHandler> getAssociatedType()
   {
      return TYPE;
   }

}
