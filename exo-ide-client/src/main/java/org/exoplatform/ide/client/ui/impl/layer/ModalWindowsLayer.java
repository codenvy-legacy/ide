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
package org.exoplatform.ide.client.ui.impl.layer;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.gwtframework.ui.client.window.Window;
import org.exoplatform.ide.client.framework.ui.api.ViewEx;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ModalWindowsLayer extends AbstractWindowsLayer
{

   private int layerHeight;

   private int layerWidth;

   private Map<String, Widget> lockPanels = new HashMap<String, Widget>();

   private Map<String, ViewEx> views = new HashMap<String, ViewEx>();

   private Map<String, WindowController> windowControllers = new HashMap<String, WindowController>();

   private Map<String, Window> windows = new HashMap<String, Window>();
   
   public ModalWindowsLayer() {
      super("modal-windows");
   }

   public boolean closeView(String viewId)
   {
      if (lockPanels.get(viewId) == null)
      {
         return false;
      }

      Widget lockPanel = lockPanels.get(viewId);
      lockPanel.removeFromParent();
      lockPanels.remove(viewId);

      Window window = windows.get(viewId);
      windows.remove(viewId);

      window.hide();
      window.destroy();

      windowControllers.remove(viewId);

      ViewEx view = views.get(viewId);
      views.remove(viewId);

      if (viewClosedHandler != null) {
         ViewClosedEvent viewClosedEvent = new ViewClosedEvent(view);
         viewClosedHandler.onViewClosed(viewClosedEvent);
      }

      return true;
   }

   public Map<String, ViewEx> getViews()
   {
      return views;
   }

   public void openView(ViewEx view)
   {
      views.put(view.getId(), view);

      AbsolutePanel lockPanel = new AbsolutePanel();
      DOM.setStyleAttribute(lockPanel.getElement(), "background", "#9999FF");
      DOM.setStyleAttribute(lockPanel.getElement(), "opacity", "0.1");
      add(lockPanel, 0, 0);
      lockPanels.put(view.getId(), lockPanel);
      resizeLockPanels();

      Window window = new Window(view.getTitle());
      DOM.setStyleAttribute(window.getElement(), "zIndex", "auto");
      window.setWidth(view.getDefaultWidth());
      window.setHeight(view.getDefaultHeight());
      window.center();
      window.show();

      windows.put(view.getId(), window);

      int left = window.getAbsoluteLeft();
      int top = window.getAbsoluteTop();

      add(window);
      DOM.setStyleAttribute(window.getElement(), "left", left + "px");
      DOM.setStyleAttribute(window.getElement(), "top", top + "px");

      if (view instanceof Widget)
      {
         window.add((Widget)view);
      }

      WindowController controller = new WindowController(view, window);
      windowControllers.put(view.getId(), controller);

      if (viewOpenedHandler != null) {
         ViewOpenedEvent viewOpenedEvent = new ViewOpenedEvent(view);
         viewOpenedHandler.onViewOpened(viewOpenedEvent);
      }
   }

   @Override
   public void resize(int width, int height)
   {
      super.resize(width, height);

      layerWidth = width;
      layerHeight = height;
      resizeLockPanels();
   }

   private void resizeLockPanels()
   {
      for (Widget lockPanel : lockPanels.values())
      {
         lockPanel.setPixelSize(layerWidth, layerHeight);
      }
   }

}
