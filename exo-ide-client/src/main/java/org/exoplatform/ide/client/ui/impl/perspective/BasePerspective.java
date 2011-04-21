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
package org.exoplatform.ide.client.ui.impl.perspective;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ide.client.framework.ui.ListBasedHandlerRegistration;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler;
import org.exoplatform.ide.client.ui.api.Panel;
import org.exoplatform.ide.client.ui.api.Perspective;
import org.exoplatform.ide.client.ui.impl.LayerContainer;
import org.exoplatform.ide.client.ui.impl.layer.ModalWindowsLayer;
import org.exoplatform.ide.client.ui.impl.layer.PopupWindowsLayer;
import org.exoplatform.ide.client.ui.impl.layout.Layout;
import org.exoplatform.ide.client.ui.impl.panel.PanelDirection;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class BasePerspective extends LayerContainer implements Perspective, ViewVisibilityChangedHandler,
   ViewOpenedHandler, ViewClosedHandler, ClosingViewHandler
{

   private PopupWindowsLayer popupWindowsLayer;

   private ModalWindowsLayer modalWindowsLayer;

   private List<ViewVisibilityChangedHandler> viewVisibilityChangedHandlers =
      new ArrayList<ViewVisibilityChangedHandler>();

   private List<ViewOpenedHandler> viewOpenedHandlers = new ArrayList<ViewOpenedHandler>();

   private List<ClosingViewHandler> closingViewHandlers = new ArrayList<ClosingViewHandler>();

   private List<ViewClosedHandler> viewClosedHandlers = new ArrayList<ViewClosedHandler>();

   private Layout layout;

   public BasePerspective()
   {
      super("test-perspective");

      layout = new Layout();
      addLayer(layout);
      
      layout.addViewVisibilityChangedHandler(this);
      layout.addViewOpenedHandler(this);
      layout.addClosingViewHandler(this);
      layout.addViewClosedHandler(this);

      popupWindowsLayer = new PopupWindowsLayer();
      popupWindowsLayer.setViewOpenedHandler(this);
      popupWindowsLayer.setClosingViewHandler(this);
      popupWindowsLayer.setViewClosedHandler(this);
      addLayer(popupWindowsLayer);

      modalWindowsLayer = new ModalWindowsLayer();
      modalWindowsLayer.setViewOpenedHandler(this);
      modalWindowsLayer.setClosingViewHandler(this);
      modalWindowsLayer.setViewClosedHandler(this);
      addLayer(modalWindowsLayer);

      layout.beginBuildLayout();
      buildPerspective();
      layout.finishBuildLayout();
   }

   public void buildPerspective()
   {
   }

   public Panel addPanel(String panelId, PanelDirection direction, int size)
   {
      return layout.addPanel(panelId, direction, size);
   }

   @Override
   public HandlerRegistration addViewVisibilityChangedHandler(ViewVisibilityChangedHandler viewVisibilityChangedHandler)
   {
      viewVisibilityChangedHandlers.add(viewVisibilityChangedHandler);
      return new ListBasedHandlerRegistration(viewVisibilityChangedHandlers, viewVisibilityChangedHandler);
   }

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
   public void openView(View view)
   {
      if (view instanceof IsWidget)
      {
         Widget viewWidget = ((IsWidget)view).asWidget();
         DOM.setStyleAttribute(viewWidget.getElement(), "zIndex", "0");
      }

      /*
       * return if view already opened
       */
      if (layout.isViewOpened(view))
      {
         Window.alert("View [" + view.getId() + "] already opened!");
         return;
      }

      boolean viewOpened = layout.openView(view);

      if (!viewOpened)
      {
         if ("popup".equals(view.getType()))
         {
            popupWindowsLayer.openView(view);
         }
         else if ("modal".equals(view.getType()))
         {
            modalWindowsLayer.openView(view);
         }
         else
         {
            Window.alert("Can't open view [" + view.getId() + "] of type [" + view.getType() + "]");
         }
      }

      view.activate();
   }

   @Override
   public void closeView(String viewId)
   {
      if (modalWindowsLayer.closeView(viewId) || popupWindowsLayer.closeView(viewId) || layout.closeView(viewId))
      {
         return;
      }
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
   public void onViewClosed(ViewClosedEvent event)
   {
      for (ViewClosedHandler handler : viewClosedHandlers)
      {
         handler.onViewClosed(event);
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

}
