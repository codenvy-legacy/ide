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

import org.exoplatform.gwtframework.ui.client.tab.TabControl;
import org.exoplatform.gwtframework.ui.client.tab.TabPanel;
import org.exoplatform.gwtframework.ui.client.tab.event.CloseTabEvent;
import org.exoplatform.gwtframework.ui.client.tab.event.CloseTabHandler;
import org.exoplatform.gwtframework.ui.client.wrapper.Wrapper;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.api.ViewEx;
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
import org.exoplatform.ide.client.ui.api.Panel;
import org.exoplatform.ide.client.ui.impl.layout.ViewsLayer;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
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
public class PanelImpl extends AbsolutePanel implements Panel, RequiresResize, SetViewVisibleHandler
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

   private String selectedViewId;

   private List<String> acceptableTypes = new ArrayList<String>();

   private TabPanel tabPanel;

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
   private TabControl maximizePanelControl;

   /**
    * Restore button
    */
   private TabControl restorePanelControl;

   /**
    * Image for Maximize button
    */
   private Image maximizeImage = new Image(IDEImageBundle.INSTANCE.maximize());

   /**
    * Image for Restore button
    */
   private Image restoreImage = new Image(IDEImageBundle.INSTANCE.restore());

   public PanelImpl(String panelId, ViewsLayer viewsLayer)
   {
      this.panelId = panelId;
      this.viewsLayer = viewsLayer;

      setWidth("100px");
      setHeight("100px");

      Wrapper wrapper = new Wrapper(5);
      add(wrapper);

      tabPanel = new TabPanel();
      tabPanel.setWidth("100%");
      tabPanel.setHeight("100%");
      wrapper.add(tabPanel);

      tabPanel.addSelectionHandler(tabSelectionHandler);
      tabPanel.addCloseTabHandler(closeTabHandler);

      maximizePanelControl = new TabControl(maximizeImage, maximizeImage);
      maximizePanelControl.addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            doMaximize();
         }
      });

      restorePanelControl = new TabControl(restoreImage, restoreImage);
      restorePanelControl.setVisible(false);
      restorePanelControl.addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            doRestore();
         }
      });

      tabPanel.addControl(maximizePanelControl);
      tabPanel.addControl(restorePanelControl);
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
         selectView(viewId);
      }
   };

   public void selectView(String viewId)
   {
      if (viewId == null)
      {
         Window.alert("View ID can not be NULL");
      }

      if (viewId == selectedViewId)
      {
         resizeView(selectedViewId);
         return;
      }

      if (selectedViewId != null)
      {
         setViewVisible(selectedViewId, false);
         fireVisibilityChangedEvent(selectedViewId);
      }

      selectedViewId = viewId;
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

      if (viewVisibilityChangedHandler != null)
      {
         ViewVisibilityChangedEvent event = new ViewVisibilityChangedEvent(view);
         viewVisibilityChangedHandler.onViewVisibilityChanged(event);
      }
   }

   public void closeView(String viewId)
   {
      tabPanel.removeTab(viewId);
      doCloseView(viewId);
   }

   protected void doCloseView(String viewId)
   {
      if (viewId.equals(selectedViewId))
      {
         selectedViewId = null;
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

      if (viewClosedHandler != null)
      {
         ViewClosedEvent viewClosedEvent = new ViewClosedEvent(view);
         viewClosedHandler.onViewClosed(new ViewClosedEvent(view));
      }

      if (views.size() == 0 && hidePanelHandler != null)
      {
         HidePanelEvent hidePanelEvent = new HidePanelEvent(panelId);
         hidePanelHandler.onHidePanel(hidePanelEvent);
      }
   }

   private CloseTabHandler closeTabHandler = new CloseTabHandler()
   {
      @Override
      public void onCloseTab(CloseTabEvent event)
      {
         String viewId = event.getTabId();
         ViewEx view = views.get(viewId);

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

   public void resize(int width, int height)
   {
      this.width = width;
      this.height = height;
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

         DOM.setStyleAttribute(viewWrapper.getElement(), "left", "" + (left + 0) + "px");
         DOM.setStyleAttribute(viewWrapper.getElement(), "top", "" + (top + 0) + "px");
         DOM.setStyleAttribute(viewWrapper.getElement(), "width", "" + width + "px");
         DOM.setStyleAttribute(viewWrapper.getElement(), "height", "" + height + "px");
      }

      public void repositionOnly()
      {
         int left = getAbsoluteLeft();
         int top = getAbsoluteTop();
         DOM.setStyleAttribute(viewWrapper.getElement(), "left", "" + (left + 0) + "px");
         DOM.setStyleAttribute(viewWrapper.getElement(), "top", "" + (top + 0) + "px");
      }

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
      tabPanel.selectTab(event.getViewId());
   }

   @Override
   public void openView(ViewEx view)
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

      final ViewController controller = new ViewController(view, viewWrapper);
      viewControllers.put(view.getId(), controller);
      tabPanel.addTab(view.getId(), view.getIcon(), view.getTitle(), controller, view.hasCloseButton());

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

      tabPanel.selectTab(view.getId());
   }

   @Override
   public Map<String, ViewEx> getViewMap()
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

      setVisible(!panelHidden);
      if (selectedViewId != null)
      {
         Widget viewWrapper = viewWrappers.get(selectedViewId);
         viewWrapper.setVisible(!panelHidden);
         if (viewVisibilityChangedHandler != null)
         {
            ViewEx selectedView = views.get(selectedViewId);
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

}
