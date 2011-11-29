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
package org.exoplatform.ide.client.ui;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.window.CloseClickHandler;
import org.exoplatform.gwtframework.ui.client.window.ResizeableWindow;
import org.exoplatform.gwtframework.ui.client.window.Window;
import org.exoplatform.ide.client.framework.ui.ListBasedHandlerRegistration;
import org.exoplatform.ide.client.framework.ui.api.HasViews;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewHandler;
import org.exoplatform.ide.client.framework.ui.api.event.HasClosingViewHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Oct 11, 2011 evgen $
 *
 */
public class WindowsPanel extends LayoutPanel implements HasViews, HasClosingViewHandler
{

   private List<ClosingViewHandler> closingViewHandlers = new ArrayList<ClosingViewHandler>();

   private boolean hasModalWindows = true;

   private Map<String, Widget> lockPanels = new HashMap<String, Widget>();

   protected Map<String, Window> windows = new HashMap<String, Window>();
   
   protected Map<String, WindowController> windowControllers = new HashMap<String, WindowController>();
   
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

   /**
    * 
    */
   public WindowsPanel()
   {
   }

   /**
    * @param hasModalWindows
    */
   public WindowsPanel(boolean hasModalWindows)
   {
      super();
      this.hasModalWindows = hasModalWindows;
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.HasClosingViewHandler#addClosingViewHandler(org.exoplatform.ide.client.framework.ui.api.event.ClosingViewHandler)
    */
   @Override
   public HandlerRegistration addClosingViewHandler(ClosingViewHandler closingViewHandler)
   {
      closingViewHandlers.add(closingViewHandler);
      return new ListBasedHandlerRegistration(closingViewHandlers, closingViewHandler);
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.HasViews#addView(org.exoplatform.ide.client.framework.ui.api.View)
    */
   @Override
   public void addView(View view)
   {
      if (hasModalWindows)
      {
         AbsolutePanel lockPanel = new AbsolutePanel();
         lockPanel.getElement().getStyle().setBackgroundColor("#FFFFFF");
         lockPanel.getElement().getStyle().setOpacity(0.5);
         add(lockPanel);
         lockPanels.put(view.getId(), lockPanel);
      }
      
      if (windows.size() == 0)
      {
         RootLayoutPanel.get().add(this);
         RootLayoutPanel.get().setWidgetLeftWidth(this, 0, Unit.PX, 100, Unit.PCT);
      }

      Window window = view.canResize()
         ? new ResizeableWindow(view.getTitle()) 
         : new Window(view.getTitle());

      window.getElement().setAttribute("id", view.getId() + "-window");
      //window.getElement().getStyle().setProperty("zIndex", "auto");
      window.setIcon(view.getIcon());

      window.setWidth(view.getDefaultWidth());
      window.setHeight(view.getDefaultHeight());
      window.setCanMaximize(view.canResize());
      window.showCentered();

      windows.put(view.getId(), window);
      window.add(view.asWidget());
      
      WindowController controller = new WindowController(view, window);
      windowControllers.put(view.getId(), controller);

   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.HasViews#removeView(org.exoplatform.ide.client.framework.ui.api.View)
    */
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
      if (windows.size() == 0)
      {
         RootLayoutPanel.get().remove(this);
      }
      return true;
   }

}
