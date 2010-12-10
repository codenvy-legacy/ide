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

import org.exoplatform.ide.client.framework.ui.View;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event is fired to open view in panel.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 8, 2010 $
 *
 */
public class OpenViewEvent extends GwtEvent<OpenViewHandler>
{

   /**
    * Type used to register this event.
    */
   public static final GwtEvent.Type<OpenViewHandler> TYPE = new GwtEvent.Type<OpenViewHandler>();

   /**
    * Can close view or not.
    */
   private boolean canClose;

   /**
    * View.
    */
   private View view;

   /**
    * @param view view
    */
   public OpenViewEvent(View view)
   {
      this.view = view;
      this.canClose = true;
   }

   /**
    * @param view view
    * @param canClose can close view or not
    */
   public OpenViewEvent(View view, boolean canClose)
   {
      this.view = view;
      this.canClose = canClose;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<OpenViewHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(OpenViewHandler handler)
   {
      handler.onOpenView(this);
   }

   /**
    * @return the canClose
    */
   public boolean isCanClose()
   {
      return canClose;
   }

   /**
    * @return the view
    */
   public View getView()
   {
      return view;
   }
}
