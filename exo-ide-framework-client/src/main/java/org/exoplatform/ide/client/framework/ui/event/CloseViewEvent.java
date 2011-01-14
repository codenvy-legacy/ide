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
package org.exoplatform.ide.client.framework.ui.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event is fired when to close view on the panel.
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CloseViewEvent extends GwtEvent<CloseViewHandler>
{

   /**
    * Type used to register this event.
    */
   public static final GwtEvent.Type<CloseViewHandler> TYPE = new GwtEvent.Type<CloseViewHandler>();

   /**
    * Id of the view to close.
    */
   private String viewId;

   public CloseViewEvent(String viewId)
   {
      this.viewId = viewId;
   }

   /**
    * @return the viewId
    */
   public String getViewId()
   {
      return viewId;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<CloseViewHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(CloseViewHandler handler)
   {
      handler.onCloseView(this);
   }

}
