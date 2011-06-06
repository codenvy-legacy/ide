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

import org.exoplatform.ide.client.framework.ui.ListBasedHandlerRegistration;
import org.exoplatform.ide.client.framework.ui.api.Direction;
import org.exoplatform.ide.client.framework.ui.api.HasViews;
import org.exoplatform.ide.client.framework.ui.api.Panel;
import org.exoplatform.ide.client.framework.ui.api.Perspective;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler;
import org.exoplatform.ide.client.ui.impl.panel.HidePanelEvent;
import org.exoplatform.ide.client.ui.impl.panel.HidePanelHandler;
import org.exoplatform.ide.client.ui.impl.panel.MaximizePanelEvent;
import org.exoplatform.ide.client.ui.impl.panel.MaximizePanelHandler;
import org.exoplatform.ide.client.ui.impl.panel.PanelImpl;
import org.exoplatform.ide.client.ui.impl.panel.RestorePanelEvent;
import org.exoplatform.ide.client.ui.impl.panel.RestorePanelHandler;
import org.exoplatform.ide.client.ui.impl.panel.ShowPanelEvent;
import org.exoplatform.ide.client.ui.impl.panel.ShowPanelHandler;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class PerspectiveImpl extends Layer implements Perspective
{

   private WindowsLayer popupWindowsLayer;

   private WindowsLayer modalWindowsLayer;

   private List<ViewVisibilityChangedHandler> viewVisibilityChangedHandlers =
      new ArrayList<ViewVisibilityChangedHandler>();

   private List<ViewOpenedHandler> viewOpenedHandlers = new ArrayList<ViewOpenedHandler>();

   private List<ClosingViewHandler> closingViewHandlers = new ArrayList<ClosingViewHandler>();

   private List<ViewClosedHandler> viewClosedHandlers = new ArrayList<ViewClosedHandler>();

   private Layer viewsLayer;

   private Layer panelsLayer;

   private LayoutLayer layoutLayer;

   private Map<String, View> views = new HashMap<String, View>();

   private Map<String, Panel> panels = new HashMap<String, Panel>();

   private Map<String, HasViews> viewTargets = new HashMap<String, HasViews>();

   public PerspectiveImpl()
   {
      super("test-perspective");

      layoutLayer = new LayoutLayer();
      addLayer(layoutLayer);

      panelsLayer = new Layer("panels");
      addLayer(panelsLayer);

      viewsLayer = new Layer("views");
      addLayer(viewsLayer);

      popupWindowsLayer = new WindowsLayer("popup-windows");
      popupWindowsLayer.addClosingViewHandler(closingViewHandler);
      addLayer(popupWindowsLayer);

      modalWindowsLayer = new WindowsLayer("modal-windows", true);
      modalWindowsLayer.addClosingViewHandler(closingViewHandler);
      addLayer(modalWindowsLayer);
   }

   public Panel addPanel(String panelId, Direction direction, int initialSize)
   {
      PanelImpl panel = new PanelImpl(panelId);
      panelsLayer.add(panel);
      panels.put(panelId, panel);

      if (direction == Direction.WEST)
      {
         layoutLayer.addWest(panel, initialSize);
      }
      else if (direction == Direction.EAST)
      {
         layoutLayer.addEast(panel, initialSize);
      }
      else if (direction == Direction.NORTH)
      {
         layoutLayer.addNorth(panel, initialSize);
      }
      else if (direction == Direction.SOUTH)
      {
         layoutLayer.addSouth(panel, initialSize);
      }
      else
      {
         layoutLayer.addCenter(panel);
      }

      panel.addMaximizePanelHandler(maximizePanelHandler);
      panel.addRestorePanelHandler(restorePanelHandler);
      panel.addShowPanelHandler(showPanelHandler);
      panel.addHidePanelHandler(hidePanelHandler);
      panel.addViewVisibilityChangedHandler(viewVisibilityChangedHandler);
      panel.addClosingViewHandler(closingViewHandler);
      return panel;
   }

   @Override
   public HandlerRegistration addViewVisibilityChangedHandler(ViewVisibilityChangedHandler viewVisibilityChangedHandler)
   {
      viewVisibilityChangedHandlers.add(viewVisibilityChangedHandler);
      return new ListBasedHandlerRegistration(viewVisibilityChangedHandlers, viewVisibilityChangedHandler);
   }

   @Override
   public HandlerRegistration addViewOpenedHandler(ViewOpenedHandler viewOpenedHandler)
   {
      viewOpenedHandlers.add(viewOpenedHandler);
      return new ListBasedHandlerRegistration(viewOpenedHandlers, viewOpenedHandler);
   }

   @Override
   public HandlerRegistration addViewClosedHandler(ViewClosedHandler viewClosedHandler)
   {
      viewClosedHandlers.add(viewClosedHandler);
      return new ListBasedHandlerRegistration(viewClosedHandlers, viewClosedHandler);
   }

   @Override
   public HandlerRegistration addClosingViewHandler(ClosingViewHandler closingViewHandler)
   {
      closingViewHandlers.add(closingViewHandler);
      return new ListBasedHandlerRegistration(closingViewHandlers, closingViewHandler);
   }

   @Override
   public void openView(View view)
   {
      if (views.containsKey(view.getId()))
      {
         Window.alert("View [" + view.getId() + "] already opened!");
         return;
      }

      views.put(view.getId(), view);
      Widget viewWidget = (Widget)view;
      DOM.setStyleAttribute(viewWidget.getElement(), "zIndex", "0");

      for (Panel panel : panels.values())
      {
         if (panel.getAcceptedTypes().contains(view.getType()))
         {
            layoutLayer.restore();

            viewsLayer.add(viewWidget, 100, 100);

            panel.addView(view);

            viewTargets.put(view.getId(), panel);
            fireViewOpenedEvent(view);
            activateView(view);
            return;
         }
      }

      if ("popup".equals(view.getType()))
      {
         popupWindowsLayer.addView(view);
         viewTargets.put(view.getId(), popupWindowsLayer);
         fireViewOpenedEvent(view);
         activateView(view);
      }
      else if ("modal".equals(view.getType()))
      {
         modalWindowsLayer.addView(view);
         viewTargets.put(view.getId(), modalWindowsLayer);
         fireViewOpenedEvent(view);
         activateView(view);
      }
      else
      {
         Window.alert("Can't open view [" + view.getId() + "] of type [" + view.getType() + "]");
      }
   }

   public void activateView(View view)
   {
      view.activate();
   }

   private void fireViewOpenedEvent(View view)
   {
      ViewOpenedEvent viewOpenedEvent = new ViewOpenedEvent(view);
      for (ViewOpenedHandler viewOpenedHandler : viewOpenedHandlers)
      {
         viewOpenedHandler.onViewOpened(viewOpenedEvent);
      }
   }

   @Override
   public void closeView(String viewId)
   {
      View view = views.get(viewId);
      HasViews viewTarget = viewTargets.get(viewId);
      if (view == null)
      {
         return;
      }

      if (layoutLayer.isMaximized())
      {
         layoutLayer.restore();
      }

      Widget viewWidget = (Widget)view;
      viewWidget.removeFromParent();

      viewTarget.removeView(view);
      views.remove(view.getId());
      viewTargets.remove(view.getId());

      ViewClosedEvent viewClosedEvent = new ViewClosedEvent(view);
      for (ViewClosedHandler viewClosedHandler : viewClosedHandlers)
      {
         viewClosedHandler.onViewClosed(viewClosedEvent);
      }
   }

   private ViewVisibilityChangedHandler viewVisibilityChangedHandler = new ViewVisibilityChangedHandler()
   {
      @Override
      public void onViewVisibilityChanged(ViewVisibilityChangedEvent event)
      {
         for (ViewVisibilityChangedHandler handler : viewVisibilityChangedHandlers)
         {
            handler.onViewVisibilityChanged(event);
         }
      }
   };

   private ClosingViewHandler closingViewHandler = new ClosingViewHandler()
   {
      @Override
      public void onClosingView(ClosingViewEvent event)
      {
         for (ClosingViewHandler handler : closingViewHandlers)
         {
            handler.onClosingView(event);
         }

         if (!event.isClosingCanceled())
         {
            closeView(event.getView().getId());
         }
      }
   };

   private MaximizePanelHandler maximizePanelHandler = new MaximizePanelHandler()
   {
      @Override
      public void onMaximizePanel(MaximizePanelEvent event)
      {
         layoutLayer.maximizePanel(event.getPanel().getPanelId());
      }
   };

   private RestorePanelHandler restorePanelHandler = new RestorePanelHandler()
   {
      @Override
      public void onRestorePanel(RestorePanelEvent event)
      {
         layoutLayer.restore();
      }
   };

   private ShowPanelHandler showPanelHandler = new ShowPanelHandler()
   {
      @Override
      public void onShowPanel(ShowPanelEvent event)
      {
         layoutLayer.showPanel(event.getPanelId());
      }
   };

   private HidePanelHandler hidePanelHandler = new HidePanelHandler()
   {
      @Override
      public void onHidePanel(HidePanelEvent event)
      {
         layoutLayer.hidePanel(event.getPanelId());
      }
   };

   @Override
   public Map<String, View> getViews()
   {
      return views;
   }

   @Override
   public Map<String, Panel> getPanels()
   {
      return panels;
   };

}
