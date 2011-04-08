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
 * This event fired before closing View.
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ClosingViewEvent extends GwtEvent<ClosingViewHandler>
{

   /**
    * Type of this event.
    */
   public static final GwtEvent.Type<ClosingViewHandler> TYPE = new GwtEvent.Type<ClosingViewHandler>();

   /**
    * View to be closed.
    */
   private ViewEx view;

   /**
    * Revoked or not closing the View.
    */
   private boolean closingCanceled = false;

   /**
    * Creates a new instance of this Event.
    * 
    * @param view view to be closed
    */
   public ClosingViewEvent(ViewEx view)
   {
      this.view = view;
   }

   /**
    * Gets View to be closed.
    * 
    * @return View to be closed.
    */
   public ViewEx getView()
   {
      return view;
   }

   /**
    * Revoke closing View.
    */
   public void cancelClosing()
   {
      closingCanceled = true;
   }

   /**
    * Gets is closing was revoked.
    * 
    * @return
    */
   public boolean isClosingCanceled()
   {
      return closingCanceled;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ClosingViewHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(ClosingViewHandler handler)
   {
      handler.onClosingView(this);
   }

}
