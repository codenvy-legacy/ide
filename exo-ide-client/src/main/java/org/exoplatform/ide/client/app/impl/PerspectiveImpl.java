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
package org.exoplatform.ide.client.app.impl;

import org.exoplatform.ide.client.app.api.Perspective;
import org.exoplatform.ide.client.app.impl.layers.LayoutLayer;
import org.exoplatform.ide.client.app.impl.layers.ModalWindowsLayer;
import org.exoplatform.ide.client.app.impl.layers.PanelsLayer;
import org.exoplatform.ide.client.app.impl.layers.PopupWindowsLayer;
import org.exoplatform.ide.client.app.impl.layers.ViewsLayer;
import org.exoplatform.ide.client.framework.ui.gwt.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.gwt.ViewEx;
import org.exoplatform.ide.client.framework.ui.gwt.ViewOpenedHandler;
import org.exoplatform.ide.client.framework.ui.gwt.ViewVisibilityChangedHandler;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class PerspectiveImpl implements Perspective
{

   public LayoutLayer layoutLayer;

   public PanelsLayer panelsLayer;

   public ViewsLayer viewsLayer;

   public PopupWindowsLayer popupWindowsLayer;

   public ModalWindowsLayer modalWindowsLayer;

   public PerspectiveImpl(LayoutLayer layoutLayer, PanelsLayer panelsLayer, ViewsLayer viewsLayer,
      PopupWindowsLayer popupWindowsLayer, ModalWindowsLayer modalWindowsLayer)
   {
      this.layoutLayer = layoutLayer;
      this.panelsLayer = panelsLayer;
      this.viewsLayer = viewsLayer;
      this.popupWindowsLayer = popupWindowsLayer;
      this.modalWindowsLayer = modalWindowsLayer;

      build();
   }

   @Override
   public void build()
   {
      layoutLayer.beginBuildLayot();

      Panel p1 = panelsLayer.addPanel("navigation", new String[]{"navigation"});
      layoutLayer.addWest(p1, 300);

      Panel p2 = panelsLayer.addPanel("information", new String[]{"information"});
      layoutLayer.addEast(p2, 200);

      Panel p3 = panelsLayer.addPanel("operation", new String[]{"operation"});
      layoutLayer.addSouth(p3, 150);

      Panel p4 = panelsLayer.addPanel("editor", new String[]{"editor"});
      layoutLayer.addCenter(p4);

      layoutLayer.finishBuildLayot();
   }

   @Override
   public void openView(ViewEx view)
   {
      /*
       * search for opened view
       */
      boolean viewAlreadyOpened = false;
      for (Panel panel : panelsLayer.getPanelsAsList())
      {
         if (panel.getViews().get(view.getId()) != null)
         {
            viewAlreadyOpened = true;
            break;
         }
      }

      /*
       * return if view already opened
       */
      if (viewAlreadyOpened)
      {
         Window.alert("View [" + view.getId() + "] already opened!");
         return;
      }

      /*
       * search target panel
       */
      Panel targetPanel = null;
      for (Panel panel : panelsLayer.getPanelsAsList())
      {
         if (panel.isTypeAccepted(view.getType()))
         {
            targetPanel = panel;
            break;
         }
      }

      /*
       * return if target panel not found
       */

      if (targetPanel == null)
      {
         if ("popup".equals(view.getType()))
         {
            popupWindowsLayer.openView(view);
         }
         else if ("modal".equals(view.getType()))
         {
            modalWindowsLayer.openWindow(view);
         }
         else
         {
            Window.alert("Can't open view [" + view.getId() + "] of type [" + view.getType() + "]");
         }

         return;
      }

      /*
       * add view to ViewLayout
       */
      Widget viewWrapper = viewsLayer.openView(view);

      /*
       * add view to Panel
       */
      targetPanel.addView(view, viewWrapper);
   }

   @Override
   public void closeView(String viewId)
   {
      // if view is popup
      if (popupWindowsLayer.getViews().get(viewId) != null)
      {
         popupWindowsLayer.closeView(viewId);
      }

      // if view is modal

      // if view attached to panel
      for (Panel panel : panelsLayer.getPanelsAsList())
      {
         if (panel.getViews().get(viewId) != null)
         {
            panel.closeView(viewId);
            break;
         }
      }

   }

   public void addViewOpenedHandler(ViewOpenedHandler viewOpenedHandler)
   {
      for (Panel panel : panelsLayer.getPanelsAsList())
      {
         panel.addViewOpenedHandler(viewOpenedHandler);
      }
   }

   public void removeViewOpenedHandler(ViewOpenedHandler viewOpenedHandler)
   {
      for (Panel panel : panelsLayer.getPanelsAsList())
      {
         panel.removeViewOpenedHandler(viewOpenedHandler);
      }
   }

   @Override
   public void addViewClosedHandler(ViewClosedHandler viewClosedHandler)
   {
      for (Panel panel : panelsLayer.getPanelsAsList())
      {
         panel.addViewClosedHandler(viewClosedHandler);
      }
   }

   @Override
   public void removeViewClosedHandler(ViewClosedHandler viewClosedHandler)
   {
      for (Panel panel : panelsLayer.getPanelsAsList())
      {
         panel.removeViewClosedHandler(viewClosedHandler);
      }
   }

   @Override
   public HandlerRegistration addViewVisibilityChangedHandler(ViewVisibilityChangedHandler viewVisibilityChangedHandler)
   {
      for (Panel panel : panelsLayer.getPanelsAsList())
      {
         panel.addViewVisibilityChangedHandler(viewVisibilityChangedHandler);
      }

      return null;
   }

}
