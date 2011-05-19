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
package org.exoplatform.ide.client.debug;

import org.exoplatform.gwtframework.ui.client.button.ext.IconButton;
import org.exoplatform.ide.client.Log;
import org.exoplatform.ide.client.ui.impl.Layer;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class DebugLayer extends Layer
{

   private static final String BACKGROUNG_COLOR = "#000000";

   private static final String FONT_COLOR = "#88FF88";

   private static final int CONTROL_WIDTH = 24;

   private static final int CONTROL_HEIGHT = 24;

   private static final int CONTROL_MARGIN = 3;

   private AbsolutePanel backgroundPanel;

   private FlowPanel textPanel;

   private IconButton control;

   boolean active = false;

   public DebugLayer()
   {
      super("debug");

      backgroundPanel = new AbsolutePanel();
      backgroundPanel.setVisible(false);
      DOM.setStyleAttribute(backgroundPanel.getElement(), "background", BACKGROUNG_COLOR);
      DOM.setStyleAttribute(backgroundPanel.getElement(), "border", "#eeeeee 1px solid");
      DOM.setStyleAttribute(backgroundPanel.getElement(), "opacity", "0.7");

      add(backgroundPanel, 0, 0);

      textPanel = new FlowPanel();
      textPanel.setVisible(false);
      DOM.setStyleAttribute(textPanel.getElement(), "overflow", "auto");
      add(textPanel, 15, 15);

      control = new IconButton(new Image("debug-icon.png"));
      DOM.setStyleAttribute(control.getElement(), "cursor", "pointer");
      add(control);
      control.addClickHandler(controlButtonClickHandler);

      new InBrowserLogger();
   }

   @Override
   public void onResize(int width, int height)
   {
      backgroundPanel.setWidth("" + (width) + "px");
      backgroundPanel.setHeight("" + (height - 30) + "px");

      textPanel.setWidth("" + (width - 20 - 10) + "px");
      textPanel.setHeight("" + (height - 10 - 35 - 10) + "px");

      int left = (width - CONTROL_WIDTH) / 2;
      int top = height - CONTROL_MARGIN - CONTROL_HEIGHT;
      DOM.setStyleAttribute(control.getElement(), "left", "" + left + "px");
      DOM.setStyleAttribute(control.getElement(), "top", "" + top + "px");
   }

   private ClickHandler controlButtonClickHandler = new ClickHandler()
   {
      @Override
      public void onClick(ClickEvent event)
      {
         if (active)
         {
            hideDebug();
         }
         else
         {
            showDebug();
         }
      }
   };

   public void showDebug()
   {
      if (active)
      {
         return;
      }

      backgroundPanel.setVisible(true);
      textPanel.setVisible(true);
      active = true;
   }

   public void hideDebug()
   {
      if (!active)
      {
         return;
      }

      backgroundPanel.setVisible(false);
      textPanel.setVisible(false);
      active = false;
   }

   public boolean isDebugActive()
   {
      return active;
   }

   private class InBrowserLogger extends Log
   {
      @Override
      public void _info(String message)
      {
         HTML html = new HTML(message);
         DOM.setStyleAttribute(html.getElement(), "color", FONT_COLOR);
         DOM.setStyleAttribute(html.getElement(), "fontSize", "14px");
         textPanel.add(html);
      }
   }

}
