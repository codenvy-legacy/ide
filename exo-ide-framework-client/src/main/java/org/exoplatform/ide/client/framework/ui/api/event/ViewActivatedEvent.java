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

import org.exoplatform.ide.client.framework.ui.api.ViewEx;

import com.google.gwt.event.shared.GwtEvent;

/**
 * This event generates after activating of any View.
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ViewActivatedEvent extends GwtEvent<ViewActivatedHandler>
{

   /**
    * Type of this Event.
    */
   public static final GwtEvent.Type<ViewActivatedHandler> TYPE = new GwtEvent.Type<ViewActivatedHandler>();

   /**
    * View which was activated.
    */
   private ViewEx view;

   /**
    * Creates a new instance of this Event.
    * 
    * @param view View which was activated
    */
   public ViewActivatedEvent(ViewEx view)
   {
      this.view = view;
   }

   /**
    * Gets a view which was activated.
    * 
    * @return view which was activated
    */
   public ViewEx getView()
   {
      return view;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ViewActivatedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(ViewActivatedHandler handler)
   {
      handler.onViewActivated(this);
   }

}
