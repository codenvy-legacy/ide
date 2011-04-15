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

import org.exoplatform.gwtframework.ui.client.window.ResizeableWindow;
import org.exoplatform.gwtframework.ui.client.window.Window;
import org.exoplatform.ide.client.framework.ui.api.ViewEx;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class PopupWindowsLayer extends AbstractWindowsLayer
{

   private Map<String, WindowController> windowControllers = new HashMap<String, PopupWindowsLayer.WindowController>();

   private Map<String, ViewEx> views = new HashMap<String, ViewEx>();

   private Map<String, Window> windows = new HashMap<String, Window>();

   public PopupWindowsLayer()
   {
      super("popup-windows");
   }

   public void openView(ViewEx view)
   {
      views.put(view.getId(), view);

      Window window = view.canResize() ? new ResizeableWindow(view.getTitle()) : new Window(view.getTitle());

      DOM.setStyleAttribute(window.getElement(), "zIndex", "auto");
      window.setWidth(view.getDefaultWidth());
      window.setHeight(view.getDefaultHeight());
      window.center();
      window.setCanMaximize(true);
      window.show();

      windows.put(view.getId(), window);

      int left = window.getAbsoluteLeft();
      int top = window.getAbsoluteTop();

      add(window);
      DOM.setStyleAttribute(window.getElement(), "left", left + "px");
      DOM.setStyleAttribute(window.getElement(), "top", top + "px");

      if (view instanceof Widget)
      {
         Widget viewWidget = (Widget)view;
         window.add(viewWidget);
      }

      WindowController controller = new WindowController(view, window);
      windowControllers.put(view.getId(), controller);

      if (viewOpenedHandler != null)
      {
         ViewOpenedEvent viewOpenedEvent = new ViewOpenedEvent(view);
         viewOpenedHandler.onViewOpened(viewOpenedEvent);
      }
   }

   public Map<String, ViewEx> getViews()
   {
      return views;
   }

   public boolean closeView(String viewId)
   {
      if (windows.get(viewId) == null)
      {
         return false;
      }

      Window window = windows.get(viewId);
      windows.remove(viewId);

      window.hide();
      window.destroy();

      windowControllers.remove(viewId);

      ViewEx closedView = views.get(viewId);
      views.remove(viewId);

      if (viewClosedHandler != null)
      {
         ViewClosedEvent viewClosedEvent = new ViewClosedEvent(closedView);
         viewClosedHandler.onViewClosed(viewClosedEvent);
      }

      return true;
   }

}
