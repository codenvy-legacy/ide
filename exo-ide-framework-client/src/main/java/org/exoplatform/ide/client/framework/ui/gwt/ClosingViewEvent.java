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
package org.exoplatform.ide.client.framework.ui.gwt;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ClosingViewEvent extends GwtEvent<ClosingViewHandler>
{

   public static final GwtEvent.Type<ClosingViewHandler> TYPE = new GwtEvent.Type<ClosingViewHandler>();

   private ViewEx view;

   private boolean closingCanceled = false;

   public ClosingViewEvent(ViewEx view)
   {
      this.view = view;
   }

   public ViewEx getView()
   {
      return view;
   }

   public void cancelClosing()
   {
      closingCanceled = true;
   }

   public boolean isClosingCanceled()
   {
      return closingCanceled;
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ClosingViewHandler> getAssociatedType()
   {
      return TYPE;
   }

   @Override
   protected void dispatch(ClosingViewHandler handler)
   {
      handler.onClosingView(this);
   }

}
