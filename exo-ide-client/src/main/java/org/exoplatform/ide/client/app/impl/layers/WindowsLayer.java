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

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.ui.client.window.CloseClickHandler;
import org.exoplatform.gwtframework.ui.client.window.Window;
import org.exoplatform.ide.client.app.impl.Layer;
import org.exoplatform.ide.client.framework.ui.gwt.ViewEx;

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

   private List<WindowController> windowControllers = new ArrayList<WindowsLayer.WindowController>();

   private List<Widget> lockPanels = new ArrayList<Widget>();

   private int width;

   private int height;

   @Override
   public void resize(int width, int height)
   {
      this.width = width;
      this.height = height;
      super.resize(width, height);

      for (Widget p : lockPanels)
      {
         p.setWidth(0 + "px");
         p.setHeight(0 + "px");
      }
   }

   public void openWindow(ViewEx view)
   {
      AbsolutePanel lockPanel = new AbsolutePanel();
      lockPanels.add(lockPanel);
      lockPanel.setWidth(0 + "px");
      lockPanel.setHeight(0 + "px");
      add(lockPanel, 0, 0);

      Window window = new Window(view.getTitle());
      window.setWidth(view.getDefaultWidth());
      window.setHeight(view.getDefaultHeight());
      window.center();
      window.show();

      int left = window.getAbsoluteLeft();
      int top = window.getAbsoluteTop();

      add(window);
      DOM.setStyleAttribute(window.getElement(), "left", left + "px");
      DOM.setStyleAttribute(window.getElement(), "top", top + "px");

      if (view instanceof Widget) {
         window.add((Widget)view);
      }

      WindowController controller = new WindowController(window, lockPanel);
      windowControllers.add(controller);
   }

   private class WindowController implements CloseClickHandler
   {

      private Window window;

      private Widget lockPanel;

      public WindowController(Window window, Widget lockPanel)
      {
         this.window = window;
         this.lockPanel = lockPanel;
         window.addCloseClickHandler(this);
      }

      @Override
      public void onCloseClick()
      {
         window.hide();
         window.destroy();
         windowControllers.remove(this);
         lockPanels.remove(lockPanel);
         lockPanel.removeFromParent();
      }

   }

}
