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

package org.exoplatform.ide.client.app.impl.layers;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.ide.client.app.impl.Layer;
import org.exoplatform.ide.client.app.impl.panel.HidePanelHandler;
import org.exoplatform.ide.client.app.impl.panel.PanelImpl;
import org.exoplatform.ide.client.app.impl.panel.ShowPanelHandler;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DockLayoutPanel.Direction;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class LayoutLayer extends Layer implements ShowPanelHandler, HidePanelHandler
{

   private class PanelController extends AbsolutePanel implements RequiresResize
   {

      private int height;

      private PanelImpl panel;

      private int width;

      public PanelController(PanelImpl panel)
      {
         this.panel = panel;
      }

      public int getHeight()
      {
         return height;
      }

      public int getWidth()
      {
         return width;
      }

      @Override
      public void onResize()
      {
         int left = getAbsoluteLeft();
         int top = getAbsoluteTop();
         int width = getOffsetWidth();
         int height = getOffsetHeight();

         if (panel.isVisible())
         {
            this.width = width;
            this.height = height;

            panel.setPosition(left, top);
            panel.resize(width, height);
         }
      }

      public void setHeight(int height)
      {
         this.height = height;
      }

      public void setWidth(int width)
      {
         this.width = width;
      }

   }

   private static final int MARGIN = 5;

   private static final int MENU_HEIGHT = 20;

   private static final int STATUSBAR_HEIGHT = 30;

   private static final int TOOLBAR_HEIGHT = 32;

   private SplitLayoutPanel layoutPanel;

   private int left = MARGIN;

   private Map<String, PanelController> panelControllers = new HashMap<String, PanelController>();

   private Map<String, PanelImpl> panels = new HashMap<String, PanelImpl>();

   private int top = MENU_HEIGHT + TOOLBAR_HEIGHT + MARGIN;

   public LayoutLayer()
   {
      layoutPanel = new SplitLayoutPanel();
      add(layoutPanel, left, top);
   }

   public void addCenter(PanelImpl panel)
   {
      PanelController controller = new PanelController(panel);
      layoutPanel.add(controller);
      //layoutPanel.setWidgetMinSize(controller, 50);

      panels.put(panel.getPanelId(), panel);
      panel.setShowPanelHandler(this);
      panel.setHidePanelHandler(this);
      panelControllers.put(panel.getPanelId(), controller);
      
      if (panel.getViews().size() == 0)
      {
         onHidePanel(panel.getPanelId());
      } else {
         onShowPanel(panel.getPanelId());
      }
   }

   public void addEast(PanelImpl panel, int size)
   {
      PanelController controller = new PanelController(panel);
      controller.setWidth(size);
      layoutPanel.addEast(controller, size);
      //layoutPanel.setWidgetMinSize(controller, 50);

      panels.put(panel.getPanelId(), panel);
      panel.setShowPanelHandler(this);
      panel.setHidePanelHandler(this);
      panelControllers.put(panel.getPanelId(), controller);
      
      if (panel.getViews().size() == 0)
      {
         onHidePanel(panel.getPanelId());
      } else {
         onShowPanel(panel.getPanelId());
      }
   }

   public void addNorth(PanelImpl panel, int size)
   {
      PanelController controller = new PanelController(panel);
      controller.setHeight(size);
      layoutPanel.addNorth(controller, size);
      //layoutPanel.setWidgetMinSize(controller, 50);

      panels.put(panel.getPanelId(), panel);
      panel.setShowPanelHandler(this);
      panel.setHidePanelHandler(this);
      panelControllers.put(panel.getPanelId(), controller);
      
      if (panel.getViews().size() == 0)
      {
         onHidePanel(panel.getPanelId());
      } else {
         onShowPanel(panel.getPanelId());
      }      
   }

   public void addSouth(PanelImpl panel, int size)
   {
      PanelController controller = new PanelController(panel);
      controller.setHeight(size);
      layoutPanel.addSouth(controller, size);
      //layoutPanel.setWidgetMinSize(controller, 50);

      panels.put(panel.getPanelId(), panel);
      panel.setShowPanelHandler(this);
      panel.setHidePanelHandler(this);
      panelControllers.put(panel.getPanelId(), controller);
      
      if (panel.getViews().size() == 0)
      {
         onHidePanel(panel.getPanelId());
      } else {
         onShowPanel(panel.getPanelId());
      }      
   }

   public void addWest(PanelImpl panel, int size)
   {
      PanelController controller = new PanelController(panel);
      controller.setWidth(size);
      layoutPanel.addWest(controller, size);
      //layoutPanel.setWidgetMinSize(controller, 50);

      panels.put(panel.getPanelId(), panel);
      panel.setShowPanelHandler(this);
      panel.setHidePanelHandler(this);
      panelControllers.put(panel.getPanelId(), controller);

      if (panel.getViews().size() == 0)
      {
         onHidePanel(panel.getPanelId());
      } else {
         onShowPanel(panel.getPanelId());
      }
   }

   public void beginBuildLayot()
   {
   }

   public void finishBuildLayot()
   {
      layoutPanel.forceLayout();
      //layoutPanel.animate(1000);
   }

   @Override
   public void onHidePanel(String panelId)
   {
      PanelController controller = panelControllers.get(panelId);
      panels.get(panelId).setVisible(false);

      if (layoutPanel.getWidgetDirection(controller) == Direction.CENTER) {
         return;
      }
      
      controller.setVisible(false);

      layoutPanel.setWidgetSize(controller, 0);
      layoutPanel.setWidgetMinSize(controller, 0);

      layoutPanel.forceLayout();
   }

   @Override
   public void onShowPanel(String panelId)
   {
      PanelController controller = panelControllers.get(panelId);
      panels.get(panelId).setVisible(true);

      if (layoutPanel.getWidgetDirection(controller) == Direction.CENTER) {
         controller.onResize();
         return;
      }

      controller.setVisible(true);

      if (layoutPanel.getWidgetDirection(controller) == Direction.EAST
         || layoutPanel.getWidgetDirection(controller) == Direction.WEST)
      {
         layoutPanel.setWidgetSize(controller, controller.getWidth());
         layoutPanel.setWidgetMinSize(panelControllers.get(panelId), 50);
      }
      else
      {
         layoutPanel.setWidgetSize(controller, controller.getHeight());
         layoutPanel.setWidgetMinSize(panelControllers.get(panelId), 50);
      }

      layoutPanel.forceLayout();
   }

   @Override
   public void resize(int width, int height)
   {
      super.resize(width, height);

      int w = width - MARGIN - MARGIN;
      int h = height - MENU_HEIGHT - TOOLBAR_HEIGHT - MARGIN - MARGIN - STATUSBAR_HEIGHT;

      layoutPanel.setPixelSize(w, h);

      for (int i = 0; i < layoutPanel.getWidgetCount(); i++)
      {
         Widget ww = layoutPanel.getWidget(i);
         if (ww instanceof PanelController)
         {
            ((PanelController)ww).onResize();
         }
      }

   }

}
