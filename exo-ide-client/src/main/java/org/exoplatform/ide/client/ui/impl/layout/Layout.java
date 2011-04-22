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
package org.exoplatform.ide.client.ui.impl.layout;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ide.client.framework.ui.ListBasedHandlerRegistration;
import org.exoplatform.ide.client.framework.ui.api.Panel;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewHandler;
import org.exoplatform.ide.client.framework.ui.api.event.HasClosingViewHandler;
import org.exoplatform.ide.client.framework.ui.api.event.HasViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.HasViewOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.HasViewVisibilityChangedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler;
import org.exoplatform.ide.client.ui.impl.Layer;
import org.exoplatform.ide.client.ui.impl.panel.PanelDirection;
import org.exoplatform.ide.client.ui.impl.panel.PanelImpl;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Layout extends Layer implements ViewVisibilityChangedHandler, ViewOpenedHandler,
   ClosingViewHandler, ViewClosedHandler, HasViewOpenedHandler,
   HasViewClosedHandler, HasClosingViewHandler, HasViewVisibilityChangedHandler
{

   private LayoutLayer layoutLayer;

   private PanelsLayer panelsLayer;

   private ViewsLayer viewsLayer;

   private List<Panel> panels = new ArrayList<Panel>();

   public Layout()
   {
      super("layout");

      viewsLayer = new ViewsLayer();
      panelsLayer = new PanelsLayer(viewsLayer);
      layoutLayer = new LayoutLayer(viewsLayer);

      addLayer(layoutLayer);
      addLayer(panelsLayer);
      addLayer(viewsLayer);
   }

//   public void beginBuildLayout()
//   {
//      layoutLayer.beginBuildLayot();
//   }
//
//   public void finishBuildLayout()
//   {
//      layoutLayer.finishBuildLayot();
//   }

   public Panel addPanel(String panelId, PanelDirection direction, int size)
   {
      PanelImpl panel = panelsLayer.addPanel(panelId);
      panels.add(panel);

      if (direction == PanelDirection.WEST)
      {
         layoutLayer.addWest(panel, size);
      }
      else if (direction == PanelDirection.EAST)
      {
         layoutLayer.addEast(panel, size);
      }
      else if (direction == PanelDirection.NORTH)
      {
         layoutLayer.addNorth(panel, size);
      }
      else if (direction == PanelDirection.SOUTH)
      {
         layoutLayer.addSouth(panel, size);
      }
      else
      {
         layoutLayer.addCenter(panel);
      }

      panel.setViewVisibilityChangedHandler(this);
      panel.setViewOpenedHandler(this);
      panel.setClosingViewHandler(this);
      panel.setViewClosedHandler(this);

      return panel;
   }

   public boolean isViewOpened(View view)
   {
      for (PanelImpl panel : panelsLayer.getPanels().values())
      {
         if (panel.getViewMap().get(view.getId()) != null)
         {
            return true;
         }
      }

      return false;
   }

   public boolean openView(View view)
   {
      for (PanelImpl panel : panelsLayer.getPanels().values())
      {
         if (panel.canOpenView(view.getType()))
         {
            panel.openView(view);
            return true;
         }
      }

      return false;
   }

   public boolean closeView(String viewId)
   {
      for (PanelImpl panel : panelsLayer.getPanels().values())
      {
         if (panel.getViewMap().get(viewId) != null)
         {
            panel.closeView(viewId);
            return true;
         }
      }

      return false;
   }

   @Override
   public void onViewVisibilityChanged(ViewVisibilityChangedEvent event)
   {
      for (ViewVisibilityChangedHandler handler : viewVisibilityChangedHandlers)
      {
         handler.onViewVisibilityChanged(event);
      }
   }

   @Override
   public void onViewOpened(ViewOpenedEvent event)
   {
      for (ViewOpenedHandler handler : viewOpenedHandlers)
      {
         handler.onViewOpened(event);
      }
   }

   @Override
   public void onClosingView(ClosingViewEvent event)
   {
      for (ClosingViewHandler handler : closingViewHandlers)
      {
         handler.onClosingView(event);
      }
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      for (ViewClosedHandler handler : viewClosedHandlers)
      {
         handler.onViewClosed(event);
      }
   }

   private List<ViewOpenedHandler> viewOpenedHandlers = new ArrayList<ViewOpenedHandler>();

   private List<ViewClosedHandler> viewClosedHandlers = new ArrayList<ViewClosedHandler>();

   private List<ClosingViewHandler> closingViewHandlers = new ArrayList<ClosingViewHandler>();

   private List<ViewVisibilityChangedHandler> viewVisibilityChangedHandlers =
      new ArrayList<ViewVisibilityChangedHandler>();

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
   public HandlerRegistration addViewVisibilityChangedHandler(ViewVisibilityChangedHandler viewVisibilityChangedHandler)
   {
      viewVisibilityChangedHandlers.add(viewVisibilityChangedHandler);
      return new ListBasedHandlerRegistration(viewVisibilityChangedHandlers, viewVisibilityChangedHandler);
   }

}
