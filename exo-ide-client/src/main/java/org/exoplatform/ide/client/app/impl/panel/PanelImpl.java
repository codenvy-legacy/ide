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
package org.exoplatform.ide.client.app.impl.panel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.exoplatform.gwtframework.ui.client.tab.CloseTabHandler;
import org.exoplatform.gwtframework.ui.client.tab.TabPanel;
import org.exoplatform.gwtframework.ui.client.wrapper.Wrapper;
import org.exoplatform.ide.client.framework.ui.gwt.ClosingViewEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ClosingViewHandler;
import org.exoplatform.ide.client.framework.ui.gwt.HasClosingViewHandler;
import org.exoplatform.ide.client.framework.ui.gwt.HasViewVisibilityChangedHandler;
import org.exoplatform.ide.client.framework.ui.gwt.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.gwt.ViewEx;
import org.exoplatform.ide.client.framework.ui.gwt.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ViewOpenedHandler;
import org.exoplatform.ide.client.framework.ui.gwt.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ViewVisibilityChangedHandler;
import org.exoplatform.ide.client.framework.ui.gwt.impl.ChangeViewIconEvent;
import org.exoplatform.ide.client.framework.ui.gwt.impl.ChangeViewIconHandler;
import org.exoplatform.ide.client.framework.ui.gwt.impl.ChangeViewTitleEvent;
import org.exoplatform.ide.client.framework.ui.gwt.impl.ChangeViewTitleHandler;
import org.exoplatform.ide.client.framework.ui.gwt.impl.HasChangeViewIconHandler;
import org.exoplatform.ide.client.framework.ui.gwt.impl.HasChangeViewTitleHandler;
import org.exoplatform.ide.client.framework.ui.gwt.impl.HasSetViewVisibleHandler;
import org.exoplatform.ide.client.framework.ui.gwt.impl.SetViewVisibleEvent;
import org.exoplatform.ide.client.framework.ui.gwt.impl.SetViewVisibleHandler;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
public class PanelImpl extends AbsolutePanel implements RequiresResize, HasClosingViewHandler,
   HasViewVisibilityChangedHandler, SetViewVisibleHandler
{

   /**
    * ID of this Panel
    */
   private String panelId;

   /**
    * Width of this Panel
    */
   private int width;

   /**
    * Height of this panel
    */
   private int height;

   /**
    * List of opened Views
    */
   private LinkedHashMap<String, ViewEx> views = new LinkedHashMap<String, ViewEx>();

   /**
    * Each View wrapped in special wrapper
    */
   private LinkedHashMap<String, Widget> viewWrappers = new LinkedHashMap<String, Widget>();

   private LinkedHashMap<String, ViewController> viewControllers =
      new LinkedHashMap<String, PanelImpl.ViewController>();

   private String currentViewId;

   private String[] acceptableTypes;

   private TabPanel tabPanel;

   private ShowPanelHandler showPanelHandler;

   private HidePanelHandler hidePanelHandler;

   private List<ViewOpenedHandler> viewOpenedHandlers = new ArrayList<ViewOpenedHandler>();

   private List<ViewClosedHandler> viewClosedHandlers = new ArrayList<ViewClosedHandler>();

   private List<ViewVisibilityChangedHandler> viewVisibilityChangedHandlers =
      new ArrayList<ViewVisibilityChangedHandler>();

   public PanelImpl(String panelId, String[] acceptableTypes)
   {
      this.panelId = panelId;
      this.acceptableTypes = acceptableTypes;
      setWidth("100px");
      setHeight("100px");

      Wrapper wrapper = new Wrapper(5);
      add(wrapper);

      tabPanel = new TabPanel();
      tabPanel.setWidth("100%");
      tabPanel.setHeight("100%");
      wrapper.add(tabPanel);

      //      tabPanel.addBeforeSelectionHandler(tabBeforeSelectionHandler);

      tabPanel.addSelectionHandler(tabSelectionHandler);
      tabPanel.addCloseTabHandler(closeTabHandler);
   }

   //   private BeforeSelectionHandler<Integer> tabBeforeSelectionHandler = new BeforeSelectionHandler<Integer>()
   //   {
   //      @Override
   //      public void onBeforeSelection(BeforeSelectionEvent<Integer> event)
   //      {
   //      }
   //   };

   private SelectionHandler<Integer> tabSelectionHandler = new SelectionHandler<Integer>()
   {
      @Override
      public void onSelection(SelectionEvent<Integer> event)
      {
         int selectedTabIndex = event.getSelectedItem();
         String viewId = tabPanel.getTabIdByIndex(selectedTabIndex);
         selectView(viewId);
      }
   };

   public void selectView(String viewId)
   {
      if (viewId == null)
      {
         Window.alert("View ID can not be NULL");
      }

      if (viewId == currentViewId)
      {
         resizeView(currentViewId);
         return;
      }

      if (currentViewId != null)
      {
         setViewVisible(currentViewId, false);
         fireVisibilityChangedEvent(currentViewId);         
      }

      currentViewId = viewId;
      setViewVisible(viewId, true);
      resizeView(viewId);
      fireVisibilityChangedEvent(viewId);
   }

   /**
    * Set View and WiewWrapper are visible
    * 
    * @param viewId
    * @param isVisible
    */
   private void setViewVisible(String viewId, boolean isVisible)
   {
      ViewEx currentView = views.get(viewId);
      if (currentView instanceof Widget)
      {
         ((Widget)currentView).setVisible(isVisible);
      }

      Widget currentViewWrapper = viewWrappers.get(viewId);
      currentViewWrapper.setVisible(isVisible);
   }

   /**
    * Resize View and ViewController
    * 
    * @param viewId
    */
   private void resizeView(String viewId)
   {
      ViewController selectedViewController = viewControllers.get(viewId);
      selectedViewController.onResize();
   }

   private void fireVisibilityChangedEvent(String viewId)
   {
      ViewEx view = views.get(viewId);

      for (ViewVisibilityChangedHandler handler : viewVisibilityChangedHandlers)
      {
         handler.onViewVisibilityChanged(new ViewVisibilityChangedEvent(view));
      }
   }

   public void closeView(String viewId)
   {
      tabPanel.removeTab(viewId);
      doCloseView(viewId);
   }

   protected void doCloseView(String viewId)
   {
      if (viewId.equals(currentViewId))
      {
         currentViewId = null;
      }

      ViewEx view = views.get(viewId);
      if (view instanceof Widget)
      {
         ((Widget)view).removeFromParent();
      }

      views.remove(viewId);

      Widget viewWrapper = viewWrappers.get(viewId);
      viewWrapper.removeFromParent();
      viewWrappers.remove(viewId);

      ViewController viewController = viewControllers.get(viewId);
      viewControllers.remove(viewId);

      for (ViewClosedHandler handler : viewClosedHandlers)
      {
         handler.onViewClosed(new ViewClosedEvent(view));
      }

      if (views.size() == 0 && hidePanelHandler != null)
      {
         hidePanelHandler.onHidePanel(panelId);
      }
   }

   private CloseTabHandler closeTabHandler = new CloseTabHandler()
   {
      @Override
      public boolean onCloseTab(String tabId)
      {
         String viewId = tabId;

         ViewEx view = views.get(viewId);
         ClosingViewEvent closingViewEvent = new ClosingViewEvent(view);
         for (ClosingViewHandler closingViewHandler : closingViewHandlers)
         {
            closingViewHandler.onClosingView(closingViewEvent);
         }

         if (closingViewEvent.isClosingCanceled())
         {
            return false;
         }

         doCloseView(viewId);
         return true;
      }
   };

   public void setPosition(int left, int top)
   {
      DOM.setStyleAttribute(getElement(), "left", "" + (left + 0) + "px");
      DOM.setStyleAttribute(getElement(), "top", "" + (top + 0) + "px");
   }

   public void resize(int width, int height)
   {
      DOM.setStyleAttribute(getElement(), "width", "" + width + "px");
      DOM.setStyleAttribute(getElement(), "height", "" + height + "px");
      onResize();
   }

   public String getPanelId()
   {
      return panelId;
   }

   public int getWidth()
   {
      return width;
   }

   public int getHeight()
   {
      return height;
   }

   public boolean isTypeAccepted(String type)
   {
      for (String t : acceptableTypes)
      {
         if (t.equals(type))
         {
            return true;
         }
      }

      return false;
   }

   public void addView(ViewEx view, Widget viewWrapper)
   {

      if (views.size() == 0 && showPanelHandler != null)
      {
         showPanelHandler.onShowPanel(panelId);
      }

      views.put(view.getId(), view);
      viewWrappers.put(view.getId(), viewWrapper);

      
      
      final ViewController controller = new ViewController(view, viewWrapper);
      viewControllers.put(view.getId(), controller);
      tabPanel.addTab(view.getId(), view.getIcon(), view.getTitle(), controller, true);

      
      // add handlers to view
      if (view instanceof HasChangeViewTitleHandler)
      {
         ((HasChangeViewTitleHandler)view).addChangeViewTitleHandler(changeViewTitleHandler);
      }

      
      if (view instanceof HasChangeViewIconHandler)
      {
         ((HasChangeViewIconHandler)view).addChangeViewIconHandler(changeViewIconHandler);
      }
      
      if (view instanceof HasSetViewVisibleHandler) {
         ((HasSetViewVisibleHandler)view).addSetViewVisibleHandler(this);
      }
      
      for (ViewOpenedHandler handler : viewOpenedHandlers)
      {
         handler.onViewOpened(new ViewOpenedEvent(view));
      }
      
      tabPanel.selectTab(view.getId());
   }

   private ChangeViewTitleHandler changeViewTitleHandler = new ChangeViewTitleHandler()
   {
      @Override
      public void onChangeViewTitle(ChangeViewTitleEvent event)
      {
         tabPanel.setTabTitle(event.getViewId(), event.getTitle());
      }
   };

   private ChangeViewIconHandler changeViewIconHandler = new ChangeViewIconHandler()
   {
      @Override
      public void onChangeViewIcon(ChangeViewIconEvent event)
      {
         tabPanel.setTabIcon(event.getViewId(), event.getIcon());
      }
   };

   private class ViewController extends FlowPanel implements RequiresResize
   {

      private ViewEx view;

      private Widget viewWrapper;

      public ViewController(ViewEx view, Widget viewWrapper)
      {
         this.view = view;
         this.viewWrapper = viewWrapper;
         setWidth("100%");
         setHeight("100%");
      }

      @Override
      public void onResize()
      {
         int left = getAbsoluteLeft();
         int top = getAbsoluteTop();
         int width = getOffsetWidth();
         int height = getOffsetHeight();

         System.out.println("resize:" + view.getId() + "  left:" + left + "  top:" + top + "  width:" + width + "  height:" + height);
         
         DOM.setStyleAttribute(viewWrapper.getElement(), "left", "" + (left + 0) + "px");
         DOM.setStyleAttribute(viewWrapper.getElement(), "top", "" + (top + 0) + "px");
         DOM.setStyleAttribute(viewWrapper.getElement(), "width", "" + width + "px");
         DOM.setStyleAttribute(viewWrapper.getElement(), "height", "" + height + "px");
      }

   }

   @Override
   public void onResize()
   {
      if (currentViewId != null)
      {
         ViewController controller = viewControllers.get(currentViewId);
         controller.onResize();
      }
   }

   public LinkedHashMap<String, ViewEx> getViews()
   {
      return views;
   }

   public void setShowPanelHandler(ShowPanelHandler showPanelHandler)
   {
      this.showPanelHandler = showPanelHandler;
   }

   public void setHidePanelHandler(HidePanelHandler hidePanelHandler)
   {
      this.hidePanelHandler = hidePanelHandler;
   }

   public void addViewOpenedHandler(ViewOpenedHandler viewOpenedHandler)
   {
      viewOpenedHandlers.add(viewOpenedHandler);
   }

   public void addViewClosedHandler(ViewClosedHandler viewClosedHandler)
   {
      viewClosedHandlers.add(viewClosedHandler);
   }

   @Override
   public HandlerRegistration addViewVisibilityChangedHandler(ViewVisibilityChangedHandler viewVisibilityChangedHandler)
   {
      viewVisibilityChangedHandlers.add(viewVisibilityChangedHandler);
      return null;
   }

   private List<ClosingViewHandler> closingViewHandlers = new ArrayList<ClosingViewHandler>();

   @Override
   public HandlerRegistration addClosingViewHandler(ClosingViewHandler closingViewHandler)
   {
      closingViewHandlers.add(closingViewHandler);
      return null;
   }

   @Override
   public void onSetViewVisible(SetViewVisibleEvent event)
   {
      tabPanel.selectTab(event.getViewId());
   }

}
