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

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.gwtframework.ui.client.window.CloseClickHandler;
import org.exoplatform.gwtframework.ui.client.window.ResizeableWindow;
import org.exoplatform.gwtframework.ui.client.window.Window;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class WindowsLayer extends Layer
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

   protected ClosingViewHandler closingViewHandler;

   protected ViewClosedHandler viewClosedHandler;

   protected ViewOpenedHandler viewOpenedHandler;

   protected Map<String, View> views = new HashMap<String, View>();

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

   public boolean closeView(String viewId)
   {
      if (hasModalWindows)
      {
         if (lockPanels.get(viewId) == null)
         {
            return false;
         }

         Widget lockPanel = lockPanels.get(viewId);
         lockPanel.removeFromParent();
         lockPanels.remove(viewId);
      }

      if (windows.get(viewId) == null)
      {
         return false;
      }

      Window window = windows.get(viewId);
      windows.remove(viewId);

      window.hide();
      window.destroy();

      windowControllers.remove(viewId);

      View closedView = views.get(viewId);
      views.remove(viewId);

      if (viewClosedHandler != null)
      {
         ViewClosedEvent viewClosedEvent = new ViewClosedEvent(closedView);
         viewClosedHandler.onViewClosed(viewClosedEvent);
      }

      return true;
   }

   public Map<String, View> getViews()
   {
      return views;
   }

   public void openView(View view)
   {
      if (hasModalWindows)
      {
         AbsolutePanel lockPanel = new AbsolutePanel();
         DOM.setStyleAttribute(lockPanel.getElement(), "background", "#9999FF");
         DOM.setStyleAttribute(lockPanel.getElement(), "opacity", "0.1");
         add(lockPanel, 0, 0);
         lockPanels.put(view.getId(), lockPanel);
         resizeLockPanels();
      }

      views.put(view.getId(), view);

      Window window = view.canResize() ? new ResizeableWindow(view.getTitle()) : new Window(view.getTitle());
      
      window.getElement().setAttribute("id", view.getId() + "-window");
      window.getElement().getStyle().setProperty("zIndex", "auto");

      window.setWidth(view.getDefaultWidth());
      window.setHeight(view.getDefaultHeight());
      window.center();
      window.setCanMaximize(view.canResize());
      window.show();

      windows.put(view.getId(), window);

      int left = window.getAbsoluteLeft();
      int top = window.getAbsoluteTop();

      add(window);
      DOM.setStyleAttribute(window.getElement(), "left", left + "px");
      DOM.setStyleAttribute(window.getElement(), "top", top + "px");

      if (view instanceof Widget)
      {
         Widget viewWidget = (Widget)view;
         window.add(viewWidget);
      }
      
      WindowController controller = new WindowController(view, window);
      windowControllers.put(view.getId(), controller);

      if (viewOpenedHandler != null)
      {
         ViewOpenedEvent viewOpenedEvent = new ViewOpenedEvent(view);
         viewOpenedHandler.onViewOpened(viewOpenedEvent);
      }
   }

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

}
