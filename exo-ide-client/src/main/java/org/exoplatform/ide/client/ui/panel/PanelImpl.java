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
package org.exoplatform.ide.client.ui.panel;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.tab.TabButton;
import org.exoplatform.gwtframework.ui.client.tab.event.CloseTabEvent;
import org.exoplatform.gwtframework.ui.client.tab.event.CloseTabHandler;
import org.exoplatform.gwtframework.ui.client.tablayout.TabPanel;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Oct 7, 2011 evgen $
 * 
 */
public class PanelImpl implements Panel, HasViewVisibilityChangedHandler, SetViewVisibleHandler, HasClosingViewHandler,
   HasMaximizePanelHandler, HasRestorePanelHandler, HasShowPanelHandler, HasHidePanelHandler
{

   private String panelId;

   private TabPanel tab;

   /**
    * Id of currently selected view.
    */
   private String selectedViewId;

   /**
    * List of opened Views
    */
   private LinkedHashMap<String, View> views = new LinkedHashMap<String, View>();

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

   /**
    * List of types of views, which can be opened by this panel.
    */
   private List<String> acceptableTypes = new ArrayList<String>();

   /**
    * @param panelId
    */
   public PanelImpl(String panelId)
   {
      super();
      this.panelId = panelId;
      tab = new TabPanel();
      tab.getElement().setId(panelId);
      tab.getElement().getStyle().setMargin(2, Unit.PX);

      createMaxinizeRestorePanelButtons();

      tab.addSelectionHandler(tabSelectionHandler);
      tab.addCloseTabHandler(closeTabHandler);

      tab.addTabButton(maximizePanelButton);
      tab.addTabButton(restorePanelButton);
   }

   private void createMaxinizeRestorePanelButtons()
   {
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

   private SelectionHandler<Integer> tabSelectionHandler = new SelectionHandler<Integer>()
   {
      @Override
      public void onSelection(SelectionEvent<Integer> event)
      {
         int selectedTabIndex = event.getSelectedItem();
         String viewId = tab.getTabIdByIndex(selectedTabIndex);

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

         fireVisibilityChangedEvent(selectedViewId);

         View view = views.get(selectedViewId);
         if (!view.isActive())
         {
            view.activate();
         }
      }
   };

   public void maximize()
   {
      MaximizePanelEvent maximizePanelEvent = new MaximizePanelEvent(this);
      for (MaximizePanelHandler maximizePanelHandler : maximizePanelHandlers)
      {
         maximizePanelHandler.onMaximizePanel(maximizePanelEvent);
      }
      maximizePanelButton.setVisible(false);
      restorePanelButton.setVisible(true);
      asWidget().getElement().setAttribute("panel-maximized", String.valueOf(true));
   }

   public void restore()
   {
      RestorePanelEvent restorePanelEvent = new RestorePanelEvent(this);
      for (RestorePanelHandler restorePanelHandler : restorePanelHandlers)
      {
         restorePanelHandler.onRestorePanel(restorePanelEvent);
      }
      maximizePanelButton.setVisible(true);
      restorePanelButton.setVisible(false);
      asWidget().getElement().setAttribute("panel-maximized", String.valueOf(false));
   }

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
    * @see com.google.gwt.user.client.ui.IsWidget#asWidget()
    */
   @Override
   public Widget asWidget()
   {
      return tab;
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.Panel#getPanelId()
    */
   @Override
   public String getPanelId()
   {
      return panelId;
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.Panel#addView(org.exoplatform.ide.client.framework.ui.api.View)
    */
   @Override
   public void addView(View view)
   {
      if (views.size() == 0)
      {
         ShowPanelEvent showPanelEvent = new ShowPanelEvent(panelId);
         tab.fireEvent(showPanelEvent);
      }

      views.put(view.getId(), view);
      /*
       * Set element attribute "panel-id" which is points to Panel with this ID.
       */
      DOM.setElementAttribute(view.asWidget().getElement(), "panel-id", panelId);

      tab.addTab(view.getId(), view.getIcon(), view.getTitle(), view.asWidget(), view.canBeClosed());
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

      tab.selectTab(view.getId());
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.Panel#removeView(org.exoplatform.ide.client.framework.ui.api.View)
    */
   @Override
   public boolean removeView(View view)
   {
      tab.removeTab(view.getId());

      if (view.getId().equals(selectedViewId))
      {
         selectedViewId = null;
      }

      views.remove(view.getId());

      if (views.size() == 0)
      {
         HidePanelEvent hidePanelEvent = new HidePanelEvent(panelId);
         tab.fireEvent(hidePanelEvent);

      }

      return true;
   }

   private ChangeViewTitleHandler changeViewTitleHandler = new ChangeViewTitleHandler()
   {
      @Override
      public void onChangeViewTitle(ChangeViewTitleEvent event)
      {
         tab.setTabTitle(event.getViewId(), event.getTitle());
      }
   };

   private ChangeViewIconHandler changeViewIconHandler = new ChangeViewIconHandler()
   {
      @Override
      public void onChangeViewIcon(ChangeViewIconEvent event)
      {
         tab.setTabIcon(event.getViewId(), event.getIcon());
      }
   };

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.Panel#getViews()
    */
   @Override
   public Map<String, View> getViews()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.Panel#getAcceptedTypes()
    */
   @Override
   public List<String> getAcceptedTypes()
   {
      return acceptableTypes;
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.Panel#acceptType(java.lang.String)
    */
   @Override
   public void acceptType(String viewType)
   {
      acceptableTypes.add(viewType);
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.Panel#setPanelHidden(boolean)
    */
   @Override
   public void setPanelHidden(boolean panelHidden)
   {

      tab.getElement().setAttribute("panel-hidden", "" + panelHidden);

      tab.setVisible(!panelHidden);
      if (selectedViewId != null)
      {
         View selectedView = views.get(selectedViewId);
         Widget selectedViewWidget = (Widget)selectedView;
         selectedViewWidget.setVisible(!panelHidden);

         fireVisibilityChangedEvent(selectedViewId);
      }
      // TODO Auto-generated method stub
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.Panel#isPanelHidden()
    */
   @Override
   public boolean isPanelHidden()
   {
      return tab.isVisible();
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.impl.event.SetViewVisibleHandler#onSetViewVisible(org.exoplatform.ide.client.framework.ui.impl.event.SetViewVisibleEvent)
    */
   @Override
   public void onSetViewVisible(SetViewVisibleEvent event)
   {
      if (event.getViewId().equals(selectedViewId))
      {
         return;
      }
      tab.selectTab(event.getViewId());
   }

   @Override
   public HandlerRegistration addClosingViewHandler(ClosingViewHandler closingViewHandler)
   {
      closingViewHandlers.add(closingViewHandler);
      return new ListBasedHandlerRegistration(closingViewHandlers, closingViewHandler);
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
      return tab.addHandler(showPanelHandler, ShowPanelEvent.TYPE);

   }

   @Override
   public HandlerRegistration addHidePanelHandler(HidePanelHandler hidePanelHandler)
   {
      return tab.addHandler(hidePanelHandler, HidePanelEvent.TYPE);
   }

   @Override
   public HandlerRegistration addViewVisibilityChangedHandler(ViewVisibilityChangedHandler viewVisibilityChangedHandler)
   {
      viewVisibilityChangedHandlers.add(viewVisibilityChangedHandler);
      return new ListBasedHandlerRegistration(viewVisibilityChangedHandlers, viewVisibilityChangedHandler);
   }

   public void hide()
   {
      HidePanelEvent hidePanelEvent = new HidePanelEvent(panelId);
      tab.fireEvent(hidePanelEvent);
   }
}
