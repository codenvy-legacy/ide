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
package org.exoplatform.ideall.client.panel.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class PanelSelectedEvent extends GwtEvent<PanelSelectedHandler>
{

   public static final GwtEvent.Type<PanelSelectedHandler> TYPE = new GwtEvent.Type<PanelSelectedHandler>();

   private String panelId;

   public PanelSelectedEvent(String panelId)
   {
      this.panelId = panelId;
   }

   public String getPanelId()
   {
      return panelId;
   }

   @Override
   protected void dispatch(PanelSelectedHandler handler)
   {
      handler.onPanelSelected(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<PanelSelectedHandler> getAssociatedType()
   {
      return TYPE;
   }

}
