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
package org.exoplatform.ide.client.framework.ui.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event is fired to select view in panel.
 * 
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class SelectViewEvent extends GwtEvent<SelectViewHandler>
{

   /**
    *  Type used to register this event.
    */
   public static final GwtEvent.Type<SelectViewHandler> TYPE = new GwtEvent.Type<SelectViewHandler>();

   /**
    * Id of the view to select.
    */
   private String viewId;

   /**
    * @param viewId id of the view to select
    */
   public SelectViewEvent(String viewId)
   {
      this.viewId = viewId;
   }

   /**
    * @return {@link String} id of view to select
    */
   public String getViewId()
   {
      return viewId;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(SelectViewHandler handler)
   {
      handler.onSelectView(this);
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<SelectViewHandler> getAssociatedType()
   {
      return TYPE;
   }

}
