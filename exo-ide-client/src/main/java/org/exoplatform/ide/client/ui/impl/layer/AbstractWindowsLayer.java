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
package org.exoplatform.ide.client.ui.impl.layer;

import org.exoplatform.gwtframework.ui.client.window.CloseClickHandler;
import org.exoplatform.gwtframework.ui.client.window.Window;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
import org.exoplatform.ide.client.ui.impl.Layer;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public abstract class AbstractWindowsLayer extends Layer
{

   public AbstractWindowsLayer(String layerId)
   {
      super(layerId);
   }

   protected ClosingViewHandler closingViewHandler;

   protected ViewClosedHandler viewClosedHandler;

   protected ViewOpenedHandler viewOpenedHandler;

   public void setClosingViewHandler(ClosingViewHandler closingViewHandler)
   {
      this.closingViewHandler = closingViewHandler;
   }

   public void setViewClosedHandler(ViewClosedHandler viewClosedHandler)
   {
      this.viewClosedHandler = viewClosedHandler;
   }

   public void setViewOpenedHandler(ViewOpenedHandler viewOpenedHandler)
   {
      this.viewOpenedHandler = viewOpenedHandler;
   }

   protected class WindowController implements CloseClickHandler
   {

      private View view;

      public WindowController(View view, Window window)
      {
         this.view = view;
         window.addCloseClickHandler(this);
      }

      @Override
      public void onCloseClick()
      {
         if (closingViewHandler != null)
         {
            ClosingViewEvent event = new ClosingViewEvent(view);
            closingViewHandler.onClosingView(event);

            if (event.isClosingCanceled())
            {
               return;
            }
         }

         closeView(view.getId());
      }

   }

   public abstract boolean closeView(String viewId);

}
