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

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.ide.client.framework.ui.api.Panel;
import org.exoplatform.ide.client.ui.impl.Layer;
import org.exoplatform.ide.client.ui.impl.panel.HidePanelEvent;
import org.exoplatform.ide.client.ui.impl.panel.HidePanelHandler;
import org.exoplatform.ide.client.ui.impl.panel.MaximizePanelEvent;
import org.exoplatform.ide.client.ui.impl.panel.MaximizePanelHandler;
import org.exoplatform.ide.client.ui.impl.panel.PanelImpl;
import org.exoplatform.ide.client.ui.impl.panel.RestorePanelEvent;
import org.exoplatform.ide.client.ui.impl.panel.RestorePanelHandler;
import org.exoplatform.ide.client.ui.impl.panel.ShowPanelEvent;
import org.exoplatform.ide.client.ui.impl.panel.ShowPanelHandler;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DockLayoutPanel.Direction;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class LayoutLayer extends Layer implements ShowPanelHandler, HidePanelHandler, MaximizePanelHandler,
   RestorePanelHandler
{

   private static final int MARGIN = 3;

   private static final int MENU_HEIGHT = 20;

   private static final int STATUSBAR_HEIGHT = 30;

   private static final int TOOLBAR_HEIGHT = 32;

   private int left = MARGIN;

   private int top = MENU_HEIGHT + TOOLBAR_HEIGHT + MARGIN;

   private AbsolutePanel layoutWrapper;

   private SplitLayoutPanel layoutPanel;

   private Map<String, PanelController> panelControllers = new HashMap<String, PanelController>();

   private Map<String, PanelImpl> panels = new HashMap<String, PanelImpl>();

   //   private ViewsLayer viewsLayer;

   public LayoutLayer(ViewsLayer viewsLayer)
   {
      super("layout");
      //      this.viewsLayer = viewsLayer;
      layoutWrapper = new AbsolutePanel();
      add(layoutWrapper, left, top);

      layoutPanel = new SplitLayoutPanel();
      layoutWrapper.add(layoutPanel, 0, 0);
   }

   private void registerPanel(PanelImpl panel, PanelController panelController)
   {
      panelController.setLeftBorder(left);
      panelController.setTopBorder(top);

      panels.put(panel.getPanelId(), panel);
      panel.setShowPanelHandler(this);
      panel.setHidePanelHandler(this);

      panel.setMaximizePanelHandler(this);
      panel.setRestorePanelHandler(this);

      panelControllers.put(panel.getPanelId(), panelController);

      if (panel.getViewMap().size() == 0)
      {
         hidePanel(panel.getPanelId());
      }
      else
      {
         showPanel(panel.getPanelId());
      }
   }

   public void addCenter(PanelImpl panel)
   {
      PanelController controller = new PanelController(panel);
      layoutPanel.add(controller);

      registerPanel(panel, controller);
   }

   public void addEast(PanelImpl panel, int size)
   {
      PanelController controller = new PanelController(panel);
      controller.setWidth(size);
      layoutPanel.addEast(controller, size);

      registerPanel(panel, controller);
   }

   public void addNorth(PanelImpl panel, int size)
   {
      PanelController controller = new PanelController(panel);
      controller.setHeight(size);
      layoutPanel.addNorth(controller, size);

      registerPanel(panel, controller);
   }

   public void addSouth(PanelImpl panel, int size)
   {
      PanelController controller = new PanelController(panel);
      controller.setHeight(size);
      layoutPanel.addSouth(controller, size);

      registerPanel(panel, controller);
   }

   public void addWest(PanelImpl panel, int size)
   {
      PanelController controller = new PanelController(panel);
      controller.setWidth(size);
      layoutPanel.addWest(controller, size);

      registerPanel(panel, controller);
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
   public void onHidePanel(HidePanelEvent event)
   {
      hidePanel(event.getPanelId());
   }

   public void hidePanel(String panelId)
   {
      panels.get(panelId).setVisible(false);

      PanelController controller = panelControllers.get(panelId);
      if (layoutPanel.getWidgetDirection(controller) == Direction.CENTER)
      {
         return;
      }

      controller.setVisible(false);

      layoutPanel.setWidgetSize(controller, 0);
      layoutPanel.setWidgetMinSize(controller, 0);

      layoutPanel.forceLayout();
   }

   @Override
   public void onShowPanel(ShowPanelEvent event)
   {
      showPanel(event.getPanelId());
   }

   public void showPanel(String panelId)
   {
      panels.get(panelId).setVisible(true);

      PanelController controller = panelControllers.get(panelId);
      if (layoutPanel.getWidgetDirection(controller) == Direction.CENTER)
      {
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

   private int width;

   private int height;

   @Override
   public void onResize(int width, int height)
   {
      this.width = width - MARGIN - MARGIN;
      this.height = height - MENU_HEIGHT - TOOLBAR_HEIGHT - MARGIN - MARGIN - STATUSBAR_HEIGHT;

      updateSizes();
   }

   private void updateSizes()
   {
      if (maximizedPanel != null)
      {
         maximizedPanel.setPosition(left, top);
         maximizedPanel.resize(width, height);
         return;
      }

      layoutWrapper.setPixelSize(width, height);
      layoutPanel.setPixelSize(width, height);

      for (int i = 0; i < layoutPanel.getWidgetCount(); i++)
      {
         Widget pC = layoutPanel.getWidget(i);
         if (pC instanceof PanelController)
         {
            ((PanelController)pC).onResize();
         }
      }
   }

   private PanelImpl maximizedPanel;

   @Override
   public void onMaximizePanel(MaximizePanelEvent event)
   {
      if (maximizedPanel != null)
      {
         Window.alert("Panel [" + maximizedPanel.getPanelId() + "] already maximized!");
         return;
      }

      layoutWrapper.setVisible(false);
      for (Panel panel : panels.values())
      {
         if (panel instanceof PanelImpl)
         {
            PanelImpl pi = (PanelImpl)panel;
            if (!pi.getPanelId().equals(event.getPanel().getPanelId()))
            {
               pi.setPanelHidden(true);
            }
         }
      }

      maximizedPanel = panels.get(event.getPanel().getPanelId());

      //updateSizes();
      new MaximizeAnimation(maximizedPanel).run(200);
   }

   @Override
   public void onRestorePanel(RestorePanelEvent event)
   {
      if (maximizedPanel != null) {
         maximizedPanel.setPanelMaximized(false);
         maximizedPanel = null;
      }

      layoutWrapper.setVisible(true);

      for (Panel panel : panels.values())
      {
         if (panel instanceof PanelImpl)
         {
            PanelImpl pi = (PanelImpl)panel;
            if (!pi.getPanelId().equals(event.getPanel().getPanelId()))
            {
               if (pi.isPanelHidden())
               {
                  pi.setPanelHidden(false);
               }

            }
         }
      }

      updateSizes();
   }

   private class MaximizeAnimation extends Animation
   {

      private PanelImpl panel;

      private int startX;

      private int dx;

      private int startY;

      private int dy;

      private int startWidth;

      private int dWidth;

      private int startHeight;

      private int dHeight;

      public MaximizeAnimation(PanelImpl panel)
      {
         this.panel = panel;

         startX = panel.getAbsoluteLeft();
         dx = left - startX;

         startY = panel.getAbsoluteTop();
         dy = top - startY;

         startWidth = panel.getWidth();
         dWidth = width - startWidth;

         startHeight = panel.getHeight();
         dHeight = height - startHeight;
      }

      @Override
      protected void onUpdate(double progress)
      {
         int newLeft = left;
         int newTop = top;
         int newWidth = width;
         int newHeight = height;

         if (progress != 1)
         {
            newLeft = startX + (int)(dx * progress);
            newTop = startY + (int)(dy * progress);
            newWidth = startWidth + (int)(dWidth * progress);
            newHeight = startHeight + (int)(dHeight * progress);
            panel.setPanelMaximized(true);
         }

         panel.setPosition(newLeft, newTop);
         panel.resize(newWidth, newHeight);
      }

   }

}
