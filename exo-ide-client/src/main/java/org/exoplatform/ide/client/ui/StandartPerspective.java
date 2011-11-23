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

import org.exoplatform.ide.client.framework.ui.ListBasedHandlerRegistration;
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
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.client.ui.panel.PanelImpl;
import org.exoplatform.ide.client.ui.panel.event.HidePanelEvent;
import org.exoplatform.ide.client.ui.panel.event.HidePanelHandler;
import org.exoplatform.ide.client.ui.panel.event.MaximizePanelEvent;
import org.exoplatform.ide.client.ui.panel.event.MaximizePanelHandler;
import org.exoplatform.ide.client.ui.panel.event.RestorePanelEvent;
import org.exoplatform.ide.client.ui.panel.event.RestorePanelHandler;
import org.exoplatform.ide.client.ui.panel.event.ShowPanelEvent;
import org.exoplatform.ide.client.ui.panel.event.ShowPanelHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DockLayoutPanel.Direction;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Oct 4, 2011 evgen $
 *
 */
public class StandartPerspective extends FlowPanel implements Perspective
{

   /**
    * 
    */
   private static final int ANIMATION_PERIOD = 400;

   private SplitLayoutPanel layoutPanel;

   /**
    * Map of the opened panels.
    */
   private Map<String, Panel> panels = new HashMap<String, Panel>();

   /**
    * List of ViewVisibilityChanged handlers.
    */
   private List<ViewVisibilityChangedHandler> viewVisibilityChangedHandlers =
      new ArrayList<ViewVisibilityChangedHandler>();

   /**
    * List of ViewOpened handlers.
    */
   private List<ViewOpenedHandler> viewOpenedHandlers = new ArrayList<ViewOpenedHandler>();

   /**
    * List of ClosingView handlers.
    */
   private List<ClosingViewHandler> closingViewHandlers = new ArrayList<ClosingViewHandler>();

   /**
    * List of ViewClosed handlers.
    */
   private List<ViewClosedHandler> viewClosedHandlers = new ArrayList<ViewClosedHandler>();

   /**
    * Map of opened views.
    */
   private Map<String, View> views = new HashMap<String, View>();

   /**
    * Map that specifies target panel or window where a view is opened.
    */
   private Map<String, HasViews> viewTargets = new HashMap<String, HasViews>();

   private Map<Panel, Integer> panelsSizes = new HashMap<Panel, Integer>();

   private WindowsPanel popupWindowsPanel;

   private WindowsPanel modalWindowsPanel;

   public StandartPerspective()
   {
      layoutPanel = new SplitLayoutPanel();
      add(layoutPanel);
      layoutPanel.setWidth("100%");
      layoutPanel.setHeight("100%");
      popupWindowsPanel = new WindowsPanel(true);
      popupWindowsPanel.addClosingViewHandler(closingViewHandler);
      modalWindowsPanel = new WindowsPanel(true);
      modalWindowsPanel.addClosingViewHandler(closingViewHandler);

//      new GWTDialogs(modalWindowsPanel);

   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.HasViewVisibilityChangedHandler#addViewVisibilityChangedHandler(org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler)
    */
   @Override
   public HandlerRegistration addViewVisibilityChangedHandler(ViewVisibilityChangedHandler viewVisibilityChangedHandler)
   {
      viewVisibilityChangedHandlers.add(viewVisibilityChangedHandler);
      return new ListBasedHandlerRegistration(viewVisibilityChangedHandlers, viewVisibilityChangedHandler);
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.HasViewOpenedHandler#addViewOpenedHandler(org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler)
    */
   @Override
   public HandlerRegistration addViewOpenedHandler(ViewOpenedHandler viewOpenedHandler)
   {
      viewOpenedHandlers.add(viewOpenedHandler);
      return new ListBasedHandlerRegistration(viewOpenedHandlers, viewOpenedHandler);
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.HasViewClosedHandler#addViewClosedHandler(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler)
    */
   @Override
   public HandlerRegistration addViewClosedHandler(ViewClosedHandler viewClosedHandler)
   {
      viewClosedHandlers.add(viewClosedHandler);
      return new ListBasedHandlerRegistration(viewClosedHandlers, viewClosedHandler);
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
    * @see org.exoplatform.ide.client.framework.ui.api.Perspective#openView(org.exoplatform.ide.client.framework.ui.api.View)
    */
   @Override
   public void openView(View view)
   {
      views.put(view.getId(), view);
      for (Panel panel : panels.values())
      {
         if (panel.getAcceptedTypes().contains(view.getType()))
         {
            restorePanelHandler.onRestorePanel(null);
            panel.addView(view);
            viewTargets.put(view.getId(), panel);
            fireViewOpenedEvent(view);
            activateView(view);
            return;
         }
      }
      if (view.getType().equals(ViewType.POPUP))
      {

         popupWindowsPanel.addView(view);
         viewTargets.put(view.getId(), popupWindowsPanel);
         fireViewOpenedEvent(view);
         activateView(view);
      }
      else if (view.getType().equals(ViewType.MODAL))
      {
         modalWindowsPanel.addView(view);
         viewTargets.put(view.getId(), modalWindowsPanel);
         fireViewOpenedEvent(view);
         activateView(view);
      }

   }

   /**
    * @param view
    */
   public void activateView(View view)
   {
      view.activate();
   }

   /**
    * Creates and adds a new panel to perspective's layout.<br>
    * <b>Note : add panel with {@link Direction#CENTER} LAST of the all panel!</b>
    * @param panelId
    * @param direction
    * @param initialSize
    * @return
    */
   public Panel addPanel(String panelId, Direction direction, int initialSize)
   {
      PanelImpl panel = new PanelImpl(panelId);
      panels.put(panelId, panel);

      if (direction == Direction.WEST)
      {
         layoutPanel.addWest(panel, initialSize);
      }
      else if (direction == Direction.EAST)
      {
         layoutPanel.addEast(panel, initialSize);
      }
      else if (direction == Direction.NORTH)
      {
         layoutPanel.addNorth(panel, initialSize);
      }
      else if (direction == Direction.SOUTH)
      {
         layoutPanel.addSouth(panel, initialSize);
      }
      else
      {
         //center 
         layoutPanel.add(panel);
      }
      panel.addMaximizePanelHandler(maximizePanelHandler);
      panel.addRestorePanelHandler(restorePanelHandler);
      panel.addShowPanelHandler(showPanelHandler);
      panel.addHidePanelHandler(hidePanelHandler);
      panel.addViewVisibilityChangedHandler(viewVisibilityChangedHandler);
      panel.addClosingViewHandler(closingViewHandler);
      panel.hide();
      return panel;
   }

   /**
    * 
    */
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

   /**
    * 
    */
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

   /**
    * 
    */
   private MaximizePanelHandler maximizePanelHandler = new MaximizePanelHandler()
   {
      @Override
      public void onMaximizePanel(MaximizePanelEvent event)
      {
         panelsSizes.clear();
         for (Panel p : panels.values())
         {
            if (layoutPanel.getWidgetDirection(p.asWidget()) != Direction.CENTER)
            {
               if (layoutPanel.getWidgetDirection(p.asWidget()) == Direction.SOUTH)
               {
                  panelsSizes.put(p, p.asWidget().getOffsetHeight());
               }
               else
                  panelsSizes.put(p, p.asWidget().getOffsetWidth());
               if (p != event.getPanel())
               {
                  layoutPanel.setWidgetSize(p.asWidget(), 0);
                  layoutPanel.animate(ANIMATION_PERIOD);
               }
            }
         }
         if (layoutPanel.getWidgetDirection(event.getPanel().asWidget()) != Direction.CENTER)
         {
            if (layoutPanel.getWidgetDirection(event.getPanel().asWidget()) == Direction.SOUTH)
            {
               layoutPanel.setWidgetSize(event.getPanel().asWidget(), layoutPanel.getElement().getClientHeight());
               layoutPanel.animate(ANIMATION_PERIOD);
            }
            else
            {
               layoutPanel.setWidgetSize(event.getPanel().asWidget(), layoutPanel.getElement().getClientWidth());
               layoutPanel.animate(ANIMATION_PERIOD);
            }
         }
      }
   };

   /**
    * 
    */
   private RestorePanelHandler restorePanelHandler = new RestorePanelHandler()
   {
      @Override
      public void onRestorePanel(RestorePanelEvent event)
      {
         for (Panel p : panelsSizes.keySet())
         {
            if (layoutPanel.getWidgetDirection(p.asWidget()) != Direction.CENTER)
            {
               layoutPanel.setWidgetSize(p.asWidget(), panelsSizes.get(p));
               layoutPanel.animate(ANIMATION_PERIOD);
            }
         }

      }
   };

   /**
    * 
    */
   private ShowPanelHandler showPanelHandler = new ShowPanelHandler()
   {
      @Override
      public void onShowPanel(ShowPanelEvent event)
      {
         Panel p = panels.get(event.getPanelId());
         if (layoutPanel.getWidgetDirection(p.asWidget()) != Direction.CENTER)
         {
            layoutPanel.setWidgetSize(p.asWidget(), 300);
            layoutPanel.animate(ANIMATION_PERIOD);
         }
      }
   };

   /**
    * 
    */
   private HidePanelHandler hidePanelHandler = new HidePanelHandler()
   {
      @Override
      public void onHidePanel(HidePanelEvent event)
      {
         Panel p = panels.get(event.getPanelId());
         if (layoutPanel.getWidgetDirection(p.asWidget()) != Direction.CENTER)
         {
            layoutPanel.setWidgetSize(p.asWidget(), 0);
            layoutPanel.animate(ANIMATION_PERIOD);
         }
      }
   };

   /**
    * @param view
    */
   private void fireViewOpenedEvent(View view)
   {
      ViewOpenedEvent viewOpenedEvent = new ViewOpenedEvent(view);
      for (ViewOpenedHandler viewOpenedHandler : viewOpenedHandlers)
      {
         viewOpenedHandler.onViewOpened(viewOpenedEvent);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.Perspective#closeView(java.lang.String)
    */
   @Override
   public void closeView(String viewId)
   {
      View view = views.get(viewId);
      HasViews viewTarget = viewTargets.get(viewId);
      if (view == null)
      {
         return;
      }

      //TODO
      //      if (layoutLayer.isMaximized())
      //      {
      //         layoutLayer.restore();
      //      }

//      Widget viewWidget = view.asWidget();
//      viewWidget.removeFromParent();

      viewTarget.removeView(view);
      views.remove(view.getId());
      viewTargets.remove(view.getId());

      ViewClosedEvent viewClosedEvent = new ViewClosedEvent(view);
      for (ViewClosedHandler viewClosedHandler : viewClosedHandlers)
      {
         viewClosedHandler.onViewClosed(viewClosedEvent);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.Perspective#getViews()
    */
   @Override
   public Map<String, View> getViews()
   {
      return views;
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.Perspective#getPanels()
    */
   @Override
   public Map<String, Panel> getPanels()
   {
      // TODO Auto-generated method stub
      return null;
   }
}
