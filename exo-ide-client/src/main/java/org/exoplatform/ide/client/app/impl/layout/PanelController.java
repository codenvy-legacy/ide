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
package org.exoplatform.ide.client.app.impl.layout;

import org.exoplatform.ide.client.app.impl.panel.PanelImpl;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class PanelController extends AbsolutePanel implements RequiresResize
{
   
   public static final int PANEL_MINIMAL_WIDTH = 80;

   public static final int PANEL_MINIMAL_HEIGHT = 50;

   public static final String INVISIBLE_PANEL_BACKGROUND = "#F9F9F9";

   public static final String INVISIBLE_PANEL_MESSAGE = "Not enough space to display this panel.";
   
   
   private int height;

   private PanelImpl panel;

   private int width;

   private boolean cloaked = false;
   
   private boolean collapsed = false;

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
      
      System.out.println("resize controller for panel [" + panel.getPanelId() + "]");
      
      if (!panel.isVisible())
      {
         return;
      }

      int left = getAbsoluteLeft();
      int top = getAbsoluteTop();

//      int minimalTop = MENU_HEIGHT + TOOLBAR_HEIGHT + MARGIN;

      int heightDifference = 0;

      if (top < topBorder)
      {
         heightDifference = topBorder - top;
         top = topBorder;
      }

      int width = getOffsetWidth();

      int height = getOffsetHeight();
      height = height - heightDifference;

      if (width < PANEL_MINIMAL_WIDTH || height < PANEL_MINIMAL_HEIGHT)
      {
         if (!cloaked)
         {
            cloaked = true;
            panel.setPosition(-100000, -100000);

            DOM.setStyleAttribute(getElement(), "background", INVISIBLE_PANEL_BACKGROUND);
            getElement().setInnerHTML(INVISIBLE_PANEL_MESSAGE);

            return;
         }
         else
         {
            return;
         }
      }

      if (cloaked)
      {
         cloaked = false;
         DOM.setStyleAttribute(getElement(), "background", "transparent");
         getElement().setInnerHTML(null);
      }

      this.width = width;
      this.height = height;

      panel.setPosition(left, top);
      panel.resize(width, height);
   }

   public void setHeight(int height)
   {
      this.height = height;
   }

   public void setWidth(int width)
   {
      this.width = width;
   }
   
   private int leftBorder;
   
   public void setLeftBorder(int leftBorder) {
      this.leftBorder = leftBorder;
   }
   
   private int topBorder;
   
   public void setTopBorder(int topBorder) {
      this.topBorder = topBorder;
   }

}
