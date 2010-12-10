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
 * Event is fired, when view is opened and contains it's id.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 8, 2010 $
 *
 */
public class ViewOpenedEvent extends GwtEvent<ViewOpenedHandler>
{

   /**
    * Type used to register this event.
    */
   public static final GwtEvent.Type<ViewOpenedHandler> TYPE = new GwtEvent.Type<ViewOpenedHandler>();

   /**
    * View's id.
    */
   private String viewId;

   /**
    * @param viewId
    */
   public ViewOpenedEvent(String viewId)
   {
      this.viewId = viewId;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ViewOpenedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(ViewOpenedHandler handler)
   {
      handler.onViewOpened(this);
   }

   /**
    * @return the viewId
    */
   public String getViewId()
   {
      return viewId;
   }
}
