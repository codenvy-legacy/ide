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
package org.exoplatform.ide.client.ui.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.ui.client.window.CloseClickHandler;
import org.exoplatform.gwtframework.ui.client.window.ResizeableWindow;
import org.exoplatform.gwtframework.ui.client.window.Window;
import org.exoplatform.ide.client.framework.ui.ListBasedHandlerRegistration;
import org.exoplatform.ide.client.framework.ui.api.HasViews;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewHandler;
import org.exoplatform.ide.client.framework.ui.api.event.HasClosingViewHandler;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class WindowsLayer extends Layer implements HasViews, HasClosingViewHandler
{

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
         ClosingViewEvent event = new ClosingViewEvent(view);
         for (ClosingViewHandler closingViewHandler : closingViewHandlers)
         {
            closingViewHandler.onClosingView(event);
         }
      }

   }

   private List<ClosingViewHandler> closingViewHandlers = new ArrayList<ClosingViewHandler>();

   protected Map<String, WindowController> windowControllers = new HashMap<String, WindowController>();

   protected Map<String, Window> windows = new HashMap<String, Window>();

   private boolean hasModalWindows = false;

   private int layerHeight;

   private int layerWidth;

   private Map<String, Widget> lockPanels = new HashMap<String, Widget>();

   public WindowsLayer(String layerId)
   {
      super(layerId);
   }

   public WindowsLayer(String layerId, boolean hasModalWindows)
   {
      super(layerId);
      this.hasModalWindows = hasModalWindows;
   }

   @Override
   public boolean removeView(View view)
   {
      if (hasModalWindows)
      {
         Widget lockPanel = lockPanels.get(view.getId());
         if (lockPanel == null)
         {
            return false;
         }
         lockPanel.removeFromParent();
         lockPanels.remove(view.getId());
      }

      Window window = windows.get(view.getId());
      if (window == null)
      {
         return false;
      }

      windows.remove(view.getId());
      window.destroy();
      windowControllers.remove(view.getId());
      
      return true;
   }

   @Override
   public void addView(View view)
   {
      Widget viewWidget = (Widget)view;
      
      if (hasModalWindows)
      {
         AbsolutePanel lockPanel = new AbsolutePanel();
         lockPanel.getElement().getStyle().setBackgroundColor("#9999FF");
         lockPanel.getElement().getStyle().setOpacity(0.1);
         add(lockPanel, 0, 0);
         lockPanels.put(view.getId(), lockPanel);
         resizeLockPanels();
      }

      Window window = view.canResize() ? new ResizeableWindow(view.getTitle()) : new Window(view.getTitle());
      window.getElement().setAttribute("id", view.getId() + "-window");
      //window.getElement().getStyle().setProperty("zIndex", "auto");

      window.setWidth(view.getDefaultWidth());
      window.setHeight(view.getDefaultHeight());
      window.setCanMaximize(view.canResize());
      window.showCentered(this);

      windows.put(view.getId(), window);
      window.add(viewWidget);

      WindowController controller = new WindowController(view, window);
      windowControllers.put(view.getId(), controller);
   }

   private void resizeLockPanels()
   {
      for (Widget lockPanel : lockPanels.values())
      {
         lockPanel.setPixelSize(layerWidth, layerHeight);
      }
   }

   @Override
   public void onResize(int width, int height)
   {
      layerWidth = width;
      layerHeight = height;
      resizeLockPanels();
   }

   @Override
   public HandlerRegistration addClosingViewHandler(ClosingViewHandler closingViewHandler)
   {
      closingViewHandlers.add(closingViewHandler);
      return new ListBasedHandlerRegistration(closingViewHandlers, closingViewHandler);
   }

}
