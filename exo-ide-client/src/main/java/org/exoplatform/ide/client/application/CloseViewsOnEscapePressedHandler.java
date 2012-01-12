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

package org.exoplatform.ide.client.application;

import com.google.gwt.event.dom.client.KeyCodes;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CloseViewsOnEscapePressedHandler implements ViewActivatedHandler
{

   private View activeView;

   public CloseViewsOnEscapePressedHandler()
   {
      Event.addNativePreviewHandler(nativePreviewHandler);

      IDE.addHandler(ViewActivatedEvent.TYPE, this);
   }

   private NativePreviewHandler nativePreviewHandler = new NativePreviewHandler()
   {
      @Override
      public void onPreviewNativeEvent(NativePreviewEvent event)
      {
         if (Event.ONKEYDOWN == event.getTypeInt() && event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE
            && activeView != null && activeView.closeOnEscape())
         {
            IDE.getInstance().closeView(activeView.getId());
            event.cancel();
            return;
         }

      }
   };

   @Override
   public void onViewActivated(ViewActivatedEvent event)
   {
      activeView = event.getView();
   }

}
