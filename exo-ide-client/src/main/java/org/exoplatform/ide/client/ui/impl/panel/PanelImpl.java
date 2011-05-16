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
import org.exoplatform.ide.client.framework.ui.api.Panel;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
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
import org.exoplatform.ide.client.ui.impl.layout.ViewsLayer;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
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
public class PanelImpl extends AbsolutePanel implements Panel, Resizeable, RequiresResize, SetViewVisibleHandler
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
    * Each View wrapped in special wrapper
    */
   private LinkedHashMap<String, Widget> viewWrappers = new LinkedHashMap<String, Widget>();

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
    * 
    */
   private List<String> acceptableTypes = new ArrayList<String>();

   /**
    * 
    */
   private TabPanel tabPanel;

   /**
    * 
    */
   private ShowPanelHandler showPanelHandler;

   private HidePanelHandler hidePanelHandler;

   private MaximizePanelHandler maximizePanelHandler;

   private RestorePanelHandler restorePanelHandler;

   private ViewVisibilityChangedHandler viewVisibilityChangedHandler;

   private ViewOpenedHandler viewOpenedHandler;

   private ViewClosedHandler viewClosedHandler;

   private ClosingViewHandler closingViewHandler;

   /**
    * Layer for storing all opened views.
    */
   private ViewsLayer viewsLayer;

   /**
    * Maximize button
    */
   private TabButton maximizePanelControl;

   /**
    * Restore button
    */
   private TabButton restorePanelControl;

   /**
    * Image for Maximize button
    */
   private Image maximizeImage = new Image(IDEImageBundle.INSTANCE.maximize());

   /**
    * Image for Restore button
    */
   private Image restoreImage = new Image(IDEImageBundle.INSTANCE.restore());

   private boolean panelMaximized = false;

   public PanelImpl(String panelId, ViewsLayer viewsLayer)
   {
      this.panelId = panelId;
      this.viewsLayer = viewsLayer;

      /*
       * For selenium tests
       */
      getElement().setAttribute("panel-id", panelId);
      getElement().setAttribute("is-panel", "true");
      setPanelMaximized(false);

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

      maximizePanelControl = new TabButton(panelId + "-maximize", maximizeImage, maximizeImage);
      maximizePanelControl.addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            doMaximize();
         }
      });

      restorePanelControl = new TabButton(panelId + "-restore", restoreImage, restoreImage);
      restorePanelControl.setVisible(false);
      restorePanelControl.addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            doRestore();
         }
      });

      tabPanel.addTabButton(maximizePanelControl);
      tabPanel.addTabButton(restorePanelControl);
   }

   public String getSelectedViewId()
   {
      return selectedViewId;
   }

   public void setSelectedViewId(String selectedViewId)
   {
      this.selectedViewId = selectedViewId;
   }

   protected void doMaximize()
   {
      MaximizePanelEvent maximizePanelEvent = new MaximizePanelEvent(this);
      maximizePanelHandler.onMaximizePanel(maximizePanelEvent);

      maximizePanelControl.setVisible(false);
      restorePanelControl.setVisible(true);
   }

   protected void doRestore()
   {
      RestorePanelEvent restorePanelEvent = new RestorePanelEvent(this);
      restorePanelHandler.onRestorePanel(restorePanelEvent);

      restorePanelControl.setVisible(false);
      maximizePanelControl.setVisible(true);
   }

   private SelectionHandler<Integer> tabSelectionHandler = new SelectionHandler<Integer>()
   {
      @Override
      public void onSelection(SelectionEvent<Integer> event)
      {
         int selectedTabIndex = event.getSelectedItem();
         String viewId = tabPanel.getTabIdByIndex(selectedTabIndex);
         
         if (viewId.equals(selectedViewId)) {
            View view = views.get(selectedViewId);
            if (!view.isActive()) {
               view.activate();
            }
            
            return;
         }

         if (selectedViewId != null && !viewId.equals(selectedViewId)) {
            setViewVisible(selectedViewId, false);
            fireVisibilityChangedEvent(selectedViewId);            
         }
         
         selectedViewId = viewId;
         setViewVisible(selectedViewId, true);
         resizeView(selectedViewId);
         fireVisibilityChangedEvent(selectedViewId);
         
         View view = views.get(selectedViewId);
         if (!view.isActive()) {
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

   /**
    * @param viewId
    */
   private void fireVisibilityChangedEvent(String viewId)
   {
      View view = views.get(viewId);

      if (viewVisibilityChangedHandler != null)
      {
         ViewVisibilityChangedEvent event = new ViewVisibilityChangedEvent(view);
         viewVisibilityChangedHandler.onViewVisibilityChanged(event);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.Panel#closeView(java.lang.String)
    */
   public void closeView(String viewId)
   {
      tabPanel.removeTab(viewId);
      doCloseView(viewId);
   }

   /**
    * Closes view with specified id
    * 
    * @param viewId id of the view to be closed 
    */
   protected void doCloseView(String viewId)
   {
      if (viewId.equals(selectedViewId))
      {
         selectedViewId = null;
      }

      View view = views.get(viewId);
      if (view instanceof Widget)
      {
         ((Widget)view).removeFromParent();
      }

      views.remove(viewId);

      Widget viewWrapper = viewWrappers.get(viewId);
      viewWrapper.removeFromParent();
      viewWrappers.remove(viewId);

      viewControllers.remove(viewId);

      if (viewClosedHandler != null)
      {
         ViewClosedEvent viewClosedEvent = new ViewClosedEvent(view);
         viewClosedHandler.onViewClosed(viewClosedEvent);
      }

      if (views.size() == 0 && hidePanelHandler != null)
      {
         HidePanelEvent hidePanelEvent = new HidePanelEvent(panelId);
         hidePanelHandler.onHidePanel(hidePanelEvent);
      }

      updateViewTabIndex();
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
         String viewId = tabPanel.getTabIdByIndex(i);

         View view = views.get(viewId);
         if (view == null)
         {
            continue;
         }

         Widget viewWidget = (Widget)view;
         DOM.setElementAttribute(viewWidget.getElement(), "tab-index", "" + i);
      }
   }

   private CloseTabHandler closeTabHandler = new CloseTabHandler()
   {
      @Override
      public void onCloseTab(CloseTabEvent event)
      {
         String viewId = event.getTabId();
         View view = views.get(viewId);

         try
         {

            if (closingViewHandler != null)
            {
               ClosingViewEvent closingViewEvent = new ClosingViewEvent(view);
               closingViewHandler.onClosingView(closingViewEvent);
               if (closingViewEvent.isClosingCanceled())
               {
                  event.cancelClosing();
                  return;
               }
            }

         }
         catch (Exception e)
         {
            e.printStackTrace();
         }

         doCloseView(viewId);
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
         int left = getAbsoluteLeft();
         int top = getAbsoluteTop();
         int width = getOffsetWidth();
         int height = getOffsetHeight();

         DOM.setStyleAttribute(widget.getElement(), "left", "" + (left + 0) + "px");
         DOM.setStyleAttribute(widget.getElement(), "top", "" + (top + 0) + "px");

         if (widget instanceof Resizeable)
         {
            ((Resizeable)widget).resize(width, height);
         }
         else
         {
            DOM.setStyleAttribute(widget.getElement(), "width", "" + width + "px");
            DOM.setStyleAttribute(widget.getElement(), "height", "" + height + "px");
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

   public void setShowPanelHandler(ShowPanelHandler showPanelHandler)
   {
      this.showPanelHandler = showPanelHandler;
   }

   public void setHidePanelHandler(HidePanelHandler hidePanelHandler)
   {
      this.hidePanelHandler = hidePanelHandler;
   }

   public void setMaximizePanelHandler(MaximizePanelHandler maximizePanelHandler)
   {
      this.maximizePanelHandler = maximizePanelHandler;
   }

   public void setRestorePanelHandler(RestorePanelHandler restorePanelHandler)
   {
      this.restorePanelHandler = restorePanelHandler;
   }

   public void setViewOpenedHandler(ViewOpenedHandler viewOpenedHandler)
   {
      this.viewOpenedHandler = viewOpenedHandler;
   }

   public void setViewClosedHandler(ViewClosedHandler viewClosedHandler)
   {
      this.viewClosedHandler = viewClosedHandler;
   }

   public void setViewVisibilityChangedHandler(ViewVisibilityChangedHandler viewVisibilityChangedHandler)
   {
      this.viewVisibilityChangedHandler = viewVisibilityChangedHandler;
   }

   public void setClosingViewHandler(ClosingViewHandler closingViewHandler)
   {
      this.closingViewHandler = closingViewHandler;
   }

   public void onSetViewVisible(SetViewVisibleEvent event)
   {
      if (event.getViewId().equals(selectedViewId)) {
         return;
      }
      
      tabPanel.selectTab(event.getViewId());
   }

   @Override
   public void openView(View view)
   {
      /*
       * add view to ViewLayout
       */
      Widget viewWrapper = viewsLayer.addView(view);

      if (views.size() == 0 && showPanelHandler != null)
      {
         ShowPanelEvent showPanelEvent = new ShowPanelEvent(panelId);
         showPanelHandler.onShowPanel(showPanelEvent);
      }

      views.put(view.getId(), view);
      viewWrappers.put(view.getId(), viewWrapper);

      final ViewController controller = new ViewController(viewWrapper);
      viewControllers.put(view.getId(), controller);
      tabPanel.addTab(view.getId(), view.getIcon(), view.getTitle(), controller, view.hasCloseButton());

      /*
       * Set element attribute "panel-id" which is points to Panel with this ID.
       */
      if (view instanceof Widget)
      {
         Widget viewWidget = (Widget)view;
         DOM.setElementAttribute(viewWidget.getElement(), "panel-id", panelId);
      }

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

      if (viewOpenedHandler != null)
      {
         ViewOpenedEvent viewOpenedEvent = new ViewOpenedEvent(view);
         viewOpenedHandler.onViewOpened(viewOpenedEvent);
      }

      updateViewTabIndex();
      tabPanel.selectTab(view.getId());
   }

   @Override
   public Map<String, View> getViewMap()
   {
      return views;
   }

   @Override
   public boolean canOpenView(String viewType)
   {
      for (String t : acceptableTypes)
      {
         if (t.equals(viewType))
         {
            return true;
         }
      }

      return false;
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
         Widget viewWrapper = viewWrappers.get(selectedViewId);
         viewWrapper.setVisible(!panelHidden);
         if (viewVisibilityChangedHandler != null)
         {
            View selectedView = views.get(selectedViewId);
            ViewVisibilityChangedEvent viewVisibilityChangedEvent = new ViewVisibilityChangedEvent(selectedView);
            viewVisibilityChangedHandler.onViewVisibilityChanged(viewVisibilityChangedEvent);
         }

      }
   }

   @Override
   public boolean isPanelHidden()
   {
      return panelHidden;
   }

   public boolean isPanelMaximized()
   {
      return panelMaximized;
   }

   public void setPanelMaximized(boolean panelMaximized)
   {
      this.panelMaximized = panelMaximized;
      getElement().setAttribute("panel-maximized", "" + panelMaximized);
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

}
