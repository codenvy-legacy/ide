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
package org.exoplatform.ide.client.framework.ui.api.event;

import org.exoplatform.ide.client.framework.ui.api.View;

import com.google.gwt.event.shared.GwtEvent;

/**
 * This event generates after changing of view visibility.
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ViewVisibilityChangedEvent extends GwtEvent<ViewVisibilityChangedHandler>
{

   /**
    * Type of this event.
    */
   public static final GwtEvent.Type<ViewVisibilityChangedHandler> TYPE =
      new GwtEvent.Type<ViewVisibilityChangedHandler>();

   /**
    * View which visibility was changed.
    */
   private View view;

   /**
    * Creates a new instance of this event.
    * 
    * @param view view which visibility was changed
    */
   public ViewVisibilityChangedEvent(View view)
   {
      this.view = view;
   }

   /**
    * Gets view which visibility was changed.
    * 
    * @return view instance of view which visibility was changed
    */
   public View getView()
   {
      return view;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ViewVisibilityChangedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(ViewVisibilityChangedHandler handler)
   {
      handler.onViewVisibilityChanged(this);
   }

}
