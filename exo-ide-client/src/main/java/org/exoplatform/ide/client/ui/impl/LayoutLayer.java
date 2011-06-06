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

import org.exoplatform.ide.client.framework.ui.api.Panel;
import org.exoplatform.ide.client.ui.impl.panel.PanelImpl;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.Window;
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

public class LayoutLayer extends Layer
{

   private int marginLeft = 3;

   private int marginTop = 20 + 32 + 3;

   private int marginRight = 3;

   private int marginBottom = 30 + 3;

   private int left = marginLeft;

   private int top = marginTop;

   private int width;

   private int height;

   private SplitLayoutPanel layoutPanel;

   private Map<String, PanelController> panelControllers = new HashMap<String, PanelController>();

   private Map<String, PanelImpl> panels = new HashMap<String, PanelImpl>();

   private PanelImpl maximizedPanel;

   public LayoutLayer()
   {
      super("layout");

      layoutPanel = new SplitLayoutPanel();
      layoutPanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
      add(layoutPanel, left, top);
   }

   private void registerPanel(PanelImpl panel, PanelController panelController)
   {
      panelController.setLeftBorder(marginLeft);
      panelController.setTopBorder(marginTop);

      panels.put(panel.getPanelId(), panel);
      panelControllers.put(panel.getPanelId(), panelController);

      if (panel.getViews().size() == 0)
      {
         hidePanel(panel.getPanelId());
      }
      else
      {
         showPanel(panel.getPanelId());
      }
   }

   /**
    * Adds panel to the center of the layout.
    * 
    * @param panel
    */
   public void addCenter(PanelImpl panel)
   {
      PanelController controller = new PanelController(panel);
      layoutPanel.add(controller);

      registerPanel(panel, controller);
   }

   /**
    * Adds panel to the east of the layout.
    * 
    * @param panel
    * @param size
    */
   public void addEast(PanelImpl panel, int size)
   {
      PanelController controller = new PanelController(panel);
      controller.setWidth(size);
      layoutPanel.addEast(controller, size);

      registerPanel(panel, controller);
   }

   /**
    * Adds panel to the north of the layout.
    * 
    * @param panel
    * @param size
    */
   public void addNorth(PanelImpl panel, int size)
   {
      PanelController controller = new PanelController(panel);
      controller.setHeight(size);
      layoutPanel.addNorth(controller, size);

      registerPanel(panel, controller);
   }

   /**
    * Adds panel to the south of the layout.
    * 
    * @param panel
    * @param size
    */
   public void addSouth(PanelImpl panel, int size)
   {
      PanelController controller = new PanelController(panel);
      controller.setHeight(size);
      layoutPanel.addSouth(controller, size);

      registerPanel(panel, controller);
   }

   /**
    * Adds panel to the west of the layout.
    * 
    * @param panel
    * @param size
    */
   public void addWest(PanelImpl panel, int size)
   {
      PanelController controller = new PanelController(panel);
      controller.setWidth(size);
      layoutPanel.addWest(controller, size);

      registerPanel(panel, controller);
   }

   /**
    * Sets panel visible.
    * 
    * @param panelId
    */
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

   /**
    * Sets panel unvisible.
    * 
    * @param panelId
    */
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

   /**
    * @see org.exoplatform.ide.client.ui.impl.Layer#onResize(int, int)
    */
   @Override
   public void onResize(int width, int height)
   {
      this.width = width - marginLeft - marginRight;
      this.height = height - marginTop - marginBottom;
      updateSizes();
   }

   /**
    * 
    */
   private void updateSizes()
   {
      if (maximizedPanel != null)
      {
         maximizedPanel.setPosition(left, top);
         maximizedPanel.resize(width, height);
         return;
      }

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

   public void maximizePanel(String panelId)
   {
      if (maximizedPanel != null)
      {
         Window.alert("Panel [" + maximizedPanel.getPanelId() + "] already maximized!");
         return;
      }

      layoutPanel.setVisible(false);

      for (Panel panel : panels.values())
      {
         if (panel instanceof PanelImpl)
         {
            PanelImpl pi = (PanelImpl)panel;
            if (!pi.getPanelId().equals(panelId))
            {
               pi.setPanelHidden(true);
            }
         }
      }

      maximizedPanel = panels.get(panelId);

      new MaximizeAnimation(maximizedPanel).run(200);
   }

   public void restore()
   {
      if (maximizedPanel == null)
      {
         return;
      }

      String panelId = maximizedPanel.getPanelId();
      maximizedPanel.setMaximized(false);
      maximizedPanel = null;

      layoutPanel.setVisible(true);

      for (Panel panel : panels.values())
      {
         if (panel instanceof PanelImpl)
         {
            PanelImpl pi = (PanelImpl)panel;
            if (!pi.getPanelId().equals(panelId))
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

   public void setMargin(int marginLeft, int marginTop, int marginRight, int marginBottom)
   {
      this.marginLeft = marginLeft;
      this.marginTop = marginTop;
      this.marginRight = marginRight;
      this.marginBottom = marginBottom;
   }

   public boolean isMaximized()
   {
      return maximizedPanel != null;
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
            panel.setMaximized(true);
         }

         panel.setPosition(newLeft, newTop);
         panel.resize(newWidth, newHeight);
      }

   }

}
