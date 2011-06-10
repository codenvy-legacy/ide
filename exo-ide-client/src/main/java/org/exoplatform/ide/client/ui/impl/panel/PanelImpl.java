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
package org.exoplatform.ide.client.ui.impl.panel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.ui.client.Resizeable;
import org.exoplatform.gwtframework.ui.client.tab.TabButton;
import org.exoplatform.gwtframework.ui.client.tab.TabPanel;
import org.exoplatform.gwtframework.ui.client.tab.event.CloseTabEvent;
import org.exoplatform.gwtframework.ui.client.tab.event.CloseTabHandler;
import org.exoplatform.gwtframework.ui.client.wrapper.Wrapper;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.ListBasedHandlerRegistration;
import org.exoplatform.ide.client.framework.ui.api.Panel;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewHandler;
import org.exoplatform.ide.client.framework.ui.api.event.HasClosingViewHandler;
import org.exoplatform.ide.client.framework.ui.api.event.HasViewVisibilityChangedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler;
import org.exoplatform.ide.client.framework.ui.impl.event.ChangeViewIconEvent;
import org.exoplatform.ide.client.framework.ui.impl.event.ChangeViewIconHandler;
import org.exoplatform.ide.client.framework.ui.impl.event.ChangeViewTitleEvent;
import org.exoplatform.ide.client.framework.ui.impl.event.ChangeViewTitleHandler;
import org.exoplatform.ide.client.framework.ui.impl.event.HasChangeViewIconHandler;
import org.exoplatform.ide.client.framework.ui.impl.event.HasChangeViewTitleHandler;
import org.exoplatform.ide.client.framework.ui.impl.event.HasSetViewVisibleHandler;
import org.exoplatform.ide.client.framework.ui.impl.event.SetViewVisibleEvent;
import org.exoplatform.ide.client.framework.ui.impl.event.SetViewVisibleHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
public class PanelImpl extends AbsolutePanel implements Panel, Resizeable, RequiresResize, SetViewVisibleHandler,
   HasClosingViewHandler, HasMaximizePanelHandler, HasRestorePanelHandler, HasShowPanelHandler, HasHidePanelHandler,
   HasViewVisibilityChangedHandler
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
   private LinkedHashMap<String, View> views = new LinkedHashMap<String, View>();

   /**
    * 
    */
   private LinkedHashMap<String, ViewController> viewControllers =
      new LinkedHashMap<String, PanelImpl.ViewController>();

   /**
    * Id of currently selected view.
    */
   private String selectedViewId;

   /**
    * List of types of views, which can be opened by this panel.
    */
   private List<String> acceptableTypes = new ArrayList<String>();

   /**
    * TabPanel for placing views in the tabs.
    */
   private TabPanel tabPanel;

   /**
    * List of handlers for notify that we have to show this panel.
    */
   private List<ShowPanelHandler> showPanelHandlers = new ArrayList<ShowPanelHandler>();

   /**
    * List of handlers for notify that we have to hide this panel.
    */
   private List<HidePanelHandler> hidePanelHandlers = new ArrayList<HidePanelHandler>();

   /**
    * List of handlers for notify that we have to maximize this panel.
    */
   private List<MaximizePanelHandler> maximizePanelHandlers = new ArrayList<MaximizePanelHandler>();

   /**
    * List of handlers for notify that we have to restore maxinized panel.
    */
   private List<RestorePanelHandler> restorePanelHandlers = new ArrayList<RestorePanelHandler>();

   private List<ViewVisibilityChangedHandler> viewVisibilityChangedHandlers =
      new ArrayList<ViewVisibilityChangedHandler>();

   private List<ClosingViewHandler> closingViewHandlers = new ArrayList<ClosingViewHandler>();

   /**
    * Maximize button
    */
   private TabButton maximizePanelButton;

   /**
    * Restore button
    */
   private TabButton restorePanelButton;

   /**
    * Image for Maximize button
    */
   private Image maximizeImage = new Image(IDEImageBundle.INSTANCE.maximize());

   /**
    * Image for Restore button
    */
   private Image restoreImage = new Image(IDEImageBundle.INSTANCE.restore());

   private boolean maximized = false;

   public PanelImpl(String panelId)
   {
      this.panelId = panelId;
      //      this.viewsLayer = viewsLayer;

      createMaxinizeRestorePanelButtons();
      
      /*
       * For selenium tests
       */
      getElement().setAttribute("panel-id", panelId);
      getElement().setAttribute("is-panel", "true");
      setMaximized(false);

      setWidth("100px");
      setHeight("100px");

      Wrapper wrapper = new Wrapper(5);
      add(wrapper);

      tabPanel = new TabPanel();
      tabPanel.getElement().setId(panelId + "-panel-switcher");
      tabPanel.setWidth("100%");
      tabPanel.setHeight("100%");
      tabPanel.setWrapperBorderSize(1);
      wrapper.add(tabPanel);

      tabPanel.addSelectionHandler(tabSelectionHandler);
      tabPanel.addCloseTabHandler(closeTabHandler);

      tabPanel.addTabButton(maximizePanelButton);
      tabPanel.addTabButton(restorePanelButton);
   }
   
   private void createMaxinizeRestorePanelButtons() {
      maximizePanelButton = new TabButton(panelId + "-maximize", maximizeImage, maximizeImage);
      maximizePanelButton.addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            maximize();
         }
      });

      restorePanelButton = new TabButton(panelId + "-restore", restoreImage, restoreImage);
      restorePanelButton.setVisible(false);
      restorePanelButton.addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            restore();
         }
      });      
   }

   public void maximize()
   {
      MaximizePanelEvent maximizePanelEvent = new MaximizePanelEvent(this);
      for (MaximizePanelHandler maximizePanelHandler : maximizePanelHandlers)
      {
         maximizePanelHandler.onMaximizePanel(maximizePanelEvent);
      }
   }

   public void restore()
   {
      RestorePanelEvent restorePanelEvent = new RestorePanelEvent(this);
      for (RestorePanelHandler restorePanelHandler : restorePanelHandlers)
      {
         restorePanelHandler.onRestorePanel(restorePanelEvent);
      }
   }

   private SelectionHandler<Integer> tabSelectionHandler = new SelectionHandler<Integer>()
   {
      @Override
      public void onSelection(SelectionEvent<Integer> event)
      {
         int selectedTabIndex = event.getSelectedItem();
         String viewId = tabPanel.getTabIdByIndex(selectedTabIndex);

         if (viewId.equals(selectedViewId))
         {
            View view = views.get(selectedViewId);
            if (!view.isActive())
            {
               view.activate();
            }

            return;
         }

         if (selectedViewId != null && !viewId.equals(selectedViewId))
         {
            setViewVisible(selectedViewId, false);
            fireVisibilityChangedEvent(selectedViewId);
         }

         selectedViewId = viewId;
         setViewVisible(selectedViewId, true);

         ViewController controller = viewControllers.get(viewId);
         controller.onResize();

         fireVisibilityChangedEvent(selectedViewId);

         View view = views.get(selectedViewId);
         if (!view.isActive())
         {
            view.activate();
         }
      }
   };

   /**
    * Set View and WiewWrapper are visible
    * 
    * @param viewId
    * @param isVisible
    */
   private void setViewVisible(String viewId, boolean isVisible)
   {
      View currentView = views.get(viewId);
      Widget viewWidget = (Widget)currentView;
      viewWidget.setVisible(isVisible);
   }

   /**
    * @param viewId
    */
   private void fireVisibilityChangedEvent(String viewId)
   {
      View view = views.get(viewId);

      ViewVisibilityChangedEvent event = new ViewVisibilityChangedEvent(view);
      for (ViewVisibilityChangedHandler viewVisibilityChangedHandler : viewVisibilityChangedHandlers)
      {
         viewVisibilityChangedHandler.onViewVisibilityChanged(event);
      }
   }

   /**
    * Updates value of a special attribute "tab-index" in each tab.
    * This attribute uses in Selenium tests.
    */
   private void updateViewTabIndex()
   {
      int tabs = tabPanel.getTabBar().getTabCount();
      for (int i = 0; i < tabs; i++)
      {
         View view = views.get(tabPanel.getTabIdByIndex(i));
         Widget viewWidget = (Widget)view;
         DOM.setElementAttribute(viewWidget.getElement(), "tab-index", "" + i);
      }
   }

   private CloseTabHandler closeTabHandler = new CloseTabHandler()
   {
      @Override
      public void onCloseTab(CloseTabEvent event)
      {
         event.cancelClosing();

         View view = views.get(event.getTabId());
         ClosingViewEvent closingViewEvent = new ClosingViewEvent(view);
         for (ClosingViewHandler closingViewHandler : closingViewHandlers)
         {
            closingViewHandler.onClosingView(closingViewEvent);
         }
      }
   };

   public void setPosition(int left, int top)
   {
      DOM.setStyleAttribute(getElement(), "left", "" + (left + 0) + "px");
      DOM.setStyleAttribute(getElement(), "top", "" + (top + 0) + "px");

      Iterator<ViewController> viewControllerIterator = viewControllers.values().iterator();
      while (viewControllerIterator.hasNext())
      {
         ViewController viewController = viewControllerIterator.next();
         viewController.repositionOnly();
      }
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

      private Widget widget;

      public ViewController(Widget widget)
      {
         this.widget = widget;
         setWidth("100%");
         setHeight("100%");
      }

      @Override
      public void onResize()
      {
         try {
            int left = getAbsoluteLeft();
            int top = getAbsoluteTop();
            int width = getOffsetWidth();
            int height = getOffsetHeight();

            DOM.setStyleAttribute(widget.getElement(), "left", "" + (left + 0) + "px");
            DOM.setStyleAttribute(widget.getElement(), "top", "" + (top + 0) + "px");
            DOM.setStyleAttribute(widget.getElement(), "width", "" + width + "px");
            DOM.setStyleAttribute(widget.getElement(), "height", "" + height + "px");
            
            if (width == 0 || height == 0) {
               return;
            }

            if (widget instanceof Resizeable)
            {
               ((Resizeable)widget).resize(width, height);
            }
            
         } catch (Exception e) {
            e.printStackTrace();
         }         
      }

      public void repositionOnly()
      {
         int left = getAbsoluteLeft();
         int top = getAbsoluteTop();
         DOM.setStyleAttribute(widget.getElement(), "left", "" + (left + 0) + "px");
         DOM.setStyleAttribute(widget.getElement(), "top", "" + (top + 0) + "px");
      }

   }

   public void onSetViewVisible(SetViewVisibleEvent event)
   {
      if (event.getViewId().equals(selectedViewId))
      {
         return;
      }

      tabPanel.selectTab(event.getViewId());
   }

   @Override
   public void addView(View view)
   {
      if (views.size() == 0 && showPanelHandlers.size() > 0)
      {
         ShowPanelEvent showPanelEvent = new ShowPanelEvent(panelId);
         for (ShowPanelHandler showPanelHandler : showPanelHandlers)
         {
            showPanelHandler.onShowPanel(showPanelEvent);
         }
      }

      views.put(view.getId(), view);

      Widget viewWidget = (Widget)view;
      /*
      * Set element attribute "panel-id" which is points to Panel with this ID.
      */
      DOM.setElementAttribute(viewWidget.getElement(), "panel-id", panelId);

      final ViewController controller = new ViewController(viewWidget);
      viewControllers.put(view.getId(), controller);
      tabPanel.addTab(view.getId(), view.getIcon(), view.getTitle(), controller, view.hasCloseButton());
      controller.onResize();

      // add handlers to view
      if (view instanceof HasChangeViewTitleHandler)
      {
         ((HasChangeViewTitleHandler)view).addChangeViewTitleHandler(changeViewTitleHandler);
      }

      if (view instanceof HasChangeViewIconHandler)
      {
         ((HasChangeViewIconHandler)view).addChangeViewIconHandler(changeViewIconHandler);
      }

      if (view instanceof HasSetViewVisibleHandler)
      {
         ((HasSetViewVisibleHandler)view).addSetViewVisibleHandler(this);
      }

      updateViewTabIndex();
      tabPanel.selectTab(view.getId());
   }

   @Override
   public Map<String, View> getViews()
   {
      return views;
   }

   @Override
   public List<String> getAcceptedTypes()
   {
      return acceptableTypes;
   }

   @Override
   public void acceptType(String viewType)
   {
      acceptableTypes.add(viewType);
   }

   private boolean panelHidden = false;

   @Override
   public void setPanelHidden(boolean panelHidden)
   {
      if (this.panelHidden == panelHidden)
      {
         return;
      }

      if (!isVisible() && !this.panelHidden)
      {
         return;
      }

      this.panelHidden = panelHidden;

      getElement().setAttribute("panel-hidden", "" + panelHidden);

      setVisible(!panelHidden);
      if (selectedViewId != null)
      {
         View selectedView = views.get(selectedViewId);
         Widget selectedViewWidget = (Widget)selectedView;
         selectedViewWidget.setVisible(!panelHidden);

         fireVisibilityChangedEvent(selectedViewId);
      }
   }

   @Override
   public boolean isPanelHidden()
   {
      return panelHidden;
   }

   public boolean isMaximized()
   {
      return maximized;
   }

   public void setMaximized(boolean maximized)
   {
      this.maximized = maximized;
      getElement().setAttribute("panel-maximized", "" + maximized);
      maximizePanelButton.setVisible(!maximized);
      restorePanelButton.setVisible(maximized);
   }

   @Override
   public void onResize()
   {
      if (selectedViewId != null)
      {
         ViewController controller = viewControllers.get(selectedViewId);
         controller.onResize();
      }
   }

   public void resize(int width, int height)
   {
      this.width = width;
      this.height = height;
      DOM.setStyleAttribute(getElement(), "width", "" + width + "px");
      DOM.setStyleAttribute(getElement(), "height", "" + height + "px");
      onResize();
   }

   @Override
   public HandlerRegistration addClosingViewHandler(ClosingViewHandler closingViewHandler)
   {
      closingViewHandlers.add(closingViewHandler);
      return new ListBasedHandlerRegistration(closingViewHandlers, closingViewHandler);
   }

   @Override
   public boolean removeView(View view)
   {
      tabPanel.removeTab(view.getId());

      if (view.getId().equals(selectedViewId))
      {
         selectedViewId = null;
      }

      views.remove(view.getId());
      viewControllers.remove(view.getId());

      if (views.size() == 0 && hidePanelHandlers.size() > 0)
      {
         HidePanelEvent hidePanelEvent = new HidePanelEvent(panelId);
         for (HidePanelHandler hidePanelHandler : hidePanelHandlers)
         {
            hidePanelHandler.onHidePanel(hidePanelEvent);
         }
      }

      updateViewTabIndex();

      return true;
   }

   @Override
   public HandlerRegistration addMaximizePanelHandler(MaximizePanelHandler maximizePanelHandler)
   {
      maximizePanelHandlers.add(maximizePanelHandler);
      return new ListBasedHandlerRegistration(maximizePanelHandlers, maximizePanelHandler);
   }

   @Override
   public HandlerRegistration addRestorePanelHandler(RestorePanelHandler restorePanelHandler)
   {
      restorePanelHandlers.add(restorePanelHandler);
      return new ListBasedHandlerRegistration(restorePanelHandlers, restorePanelHandler);
   }

   @Override
   public HandlerRegistration addShowPanelHandler(ShowPanelHandler showPanelHandler)
   {
      showPanelHandlers.add(showPanelHandler);
      return new ListBasedHandlerRegistration(showPanelHandlers, showPanelHandler);
   }

   @Override
   public HandlerRegistration addHidePanelHandler(HidePanelHandler hidePanelHandler)
   {
      hidePanelHandlers.add(hidePanelHandler);
      return new ListBasedHandlerRegistration(hidePanelHandlers, hidePanelHandler);
   }

   @Override
   public HandlerRegistration addViewVisibilityChangedHandler(ViewVisibilityChangedHandler viewVisibilityChangedHandler)
   {
      viewVisibilityChangedHandlers.add(viewVisibilityChangedHandler);
      return new ListBasedHandlerRegistration(viewVisibilityChangedHandlers, viewVisibilityChangedHandler);
   }

}
