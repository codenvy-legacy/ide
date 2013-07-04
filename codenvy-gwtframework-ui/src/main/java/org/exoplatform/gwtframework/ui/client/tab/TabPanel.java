/**
 * Copyright (C) 2010 eXo Platform SAS.
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
 *
 */

package org.exoplatform.gwtframework.ui.client.tab;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.tab.event.*;
import org.exoplatform.gwtframework.ui.client.wrapper.Wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Decorated TabPanel with styled TabBar
 * <p/>
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TabPanel extends DecoratedTabPanel implements Scrollable, CloseTabHandler, HasCloseTabHandler,
                                                           HasTabClosedHandler, RequiresResize {

    /** Handler registration for removing registered CloseTabHandler */
    private class CloseTabHandlerRegistration implements HandlerRegistration {

        /** Handler */
        private CloseTabHandler handler;

        /**
         * Creates new instance of CloseTabHandlerRegistration
         *
         * @param handler
         *         handler
         */
        public CloseTabHandlerRegistration(CloseTabHandler handler) {
            this.handler = handler;
        }

        /** @see com.google.gwt.event.shared.HandlerRegistration#removeHandler() */
        @Override
        public void removeHandler() {
            closeTabHandlers.remove(handler);
        }
    }

    /** Handler registration for removing registered TabClosedHandler */
    private class TabClosedHandlerRegistration implements HandlerRegistration {

        /** Handler */
        private TabClosedHandler handler;

        /**
         * Creates new instance of TabClosedHandlerRegistration
         *
         * @param handler
         *         handler
         */
        public TabClosedHandlerRegistration(TabClosedHandler handler) {
            this.handler = handler;
        }

        /** @see com.google.gwt.event.shared.HandlerRegistration#removeHandler() */
        @Override
        public void removeHandler() {
            tabClosedHandlers.remove(handler);
        }

    }

    private class MoveAnimation extends Animation {

        private int dx;

        private int endX;

        private int startX;

        public MoveAnimation(int startX, int endX) {
            this.startX = startX;
            this.endX = endX;
            dx = endX - startX;
        }

        @Override
        protected void onUpdate(double progress) {
            int left = 0;
            if (progress == 1) {
                left = endX;
            } else {
                left = startX + (int)(dx * progress);
            }
            DOM.setStyleAttribute(getTabBar().getElement(), "left", "-" + left + "px");
        }

    }

    private class TabBarPositionCorrector {

        private int containerLeft;

        private int selectedTabIndex;

        private Widget selectedTabWidget;

        public void correctTabBarPosition() {
            selectedTabIndex = getTabBar().getSelectedTab();
            if (selectedTabIndex < 0) {
                return;
            }

            selectedTabWidget = (Widget)getTabBar().getTab(selectedTabIndex);
            containerLeft = getTabBar().getElement().getParentElement().getAbsoluteLeft();

            if (isScrollTabBarRight()) {
                return;
            }

            if (isScrollTabBarLeft()) {
                return;
            }

            if (isRestoreTabBarposition()) {
                return;
            }

        }

        public void defferredCorrection() {
            new Timer() {
                @Override
                public void run() {
                    updateScrollersVisibility();
                    correctTabBarPosition();
                }
            }.schedule(1);
        }

        private boolean isRestoreTabBarposition() {
            int tabPanelWidth = getTabPanelWidth();
            int controlsWidth = controlsPanel.getOffsetWidth();
            int tabsWidth = getTabsWidth();
            int tabContainerVisibleWidth = tabPanelWidth - controlsWidth;
            int tabsVisibleWidth = tabsWidth - offset;

            int restOfScreen = tabContainerVisibleWidth - tabsVisibleWidth;

            if (offset > 0) {
                int startX = offset;

                offset -= restOfScreen;
                if (offset < 0) {
                    offset = 0;
                }

                new MoveAnimation(startX, offset).run(500);
                return true;
            }

            return false;
        }

        private boolean isScrollTabBarLeft() {
            if (selectedTabWidget.getAbsoluteLeft() < containerLeft) {
                int dx = containerLeft - selectedTabWidget.getAbsoluteLeft();

                int startX = offset;
                offset -= dx;

                new MoveAnimation(startX, offset).run(500);
                return true;
            }

            return false;
        }

        private boolean isScrollTabBarRight() {
            int tabContainerWidth = getTabContainerWidth();
            int selectedTabWidgetRightPoint = selectedTabWidget.getAbsoluteLeft() + selectedTabWidget.getOffsetWidth();
            int tabContainerRight = containerLeft + tabContainerWidth;

            if (selectedTabWidgetRightPoint > tabContainerRight) {
                int dx = selectedTabWidgetRightPoint - tabContainerRight;
                int startX = offset;
                offset += dx;

                new MoveAnimation(startX, offset).run(500);
                return true;
            }

            return false;
        }

    }

    /** List of CloseTabHandlers */
    private List<CloseTabHandler> closeTabHandlers = new ArrayList<CloseTabHandler>();

    /** List of TabClosedHandlers */
    private List<TabClosedHandler> tabClosedHandlers = new ArrayList<TabClosedHandler>();

    /** Controls Panel uses for storing Scroll buttons and any controls defined by user. */
    private TabControlsPanel controlsPanel;

    /** Left offset of TabBar */
    private int offset = 0;

    /** Instance of TabBar Position Corrector, uses for animate of tab bar after adding, removing or selecting tabs. */
    private TabBarPositionCorrector tabBarPositionCorrector;

    /** List of opened tabs */
    private List<String> tabIds = new ArrayList<String>();

    /** Instance of Tab Scroller conponent. */
    private TabScroller tabScroller;

    /** Map of Tab Titles. */
    private Map<String, TabTitle> tabTitles = new HashMap<String, TabTitle>();

    /** Creates new instance of TabPanel. */
    public TabPanel() {
        try {
            Element tabBarElement = ((Widget)getTabBar()).getElement();
            tabBarElement.getParentElement().addClassName("gwt-DecoratedTabBarPanel");

            Element trElement = DOM.getChild(DOM.getChild(getElement(), 0), 0);

            Element firstTD = DOM.getChild(trElement, 0);
            DOM.setStyleAttribute(firstTD, "overflow", "hidden");

            Element tdScroller = DOM.createTD();
            trElement.appendChild(tdScroller);

            Element tdElement = DOM.getChild(DOM.getChild(DOM.getChild(getElement(), 0), 1), 0);
//         DOM.setElementAttribute(tdElement, "colspan", "2");
            tdElement.setAttribute("colSpan", "2");

            controlsPanel = new TabControlsPanel(tdScroller);

            tabScroller = new TabScroller(this);
            tabScroller.setVisible(false);
            controlsPanel.add(tabScroller);

            tabBarPositionCorrector = new TabBarPositionCorrector();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                tabBarPositionCorrector.defferredCorrection();
            }
        });

    }

    /**
     * To add a new Tab to this TabPanel use addTab(...) method.
     *
     * @see com.google.gwt.user.client.ui.TabPanel#add(com.google.gwt.user.client.ui.Widget)
     */
    @Override
    public void add(Widget w) {
        throw new IllegalStateException("Widgets can be added only by using addTab(...) method.");
    }

    /**
     * To add a new Tab to this TabPanel use addTab(...) method.
     *
     * @see com.google.gwt.user.client.ui.TabPanel#add(com.google.gwt.user.client.ui.Widget, java.lang.String)
     */
    @Override
    public void add(Widget w, String tabText) {
        throw new IllegalStateException("Widgets can be added only by using addTab(...) method.");
    }

    /**
     * To add a new Tab to this TabPanel use addTab(...) method.
     *
     * @see com.google.gwt.user.client.ui.TabPanel#add(com.google.gwt.user.client.ui.Widget, java.lang.String, boolean)
     */
    @Override
    public void add(Widget w, String tabText, boolean asHTML) {
        throw new IllegalStateException("Widgets can be added only by using addTab(...) method.");
    }

    /**
     * To add a new Tab to this TabPanel use addTab(...) method.
     *
     * @see com.google.gwt.user.client.ui.TabPanel#add(com.google.gwt.user.client.ui.Widget, com.google.gwt.user.client.ui.Widget)
     */
    @Override
    public void add(Widget w, Widget tabWidget) {
        throw new IllegalStateException("Widgets can be added only by using addTab(...) method.");
    }

    /**
     * Adds CloseTabHandler.
     *
     * @see org.exoplatform.gwtframework.ui.client.tab.event.HasCloseTabHandler#addCloseTabHandler(org.exoplatform.gwtframework.ui.client
     * .tab.event.CloseTabHandler)
     */
    @Override
    public HandlerRegistration addCloseTabHandler(CloseTabHandler closeTabHandler) {
        closeTabHandlers.add(closeTabHandler);
        return new CloseTabHandlerRegistration(closeTabHandler);
    }

    /**
     * Adds user defined button to this TabPanel.
     *
     * @param button
     *         new button
     */
    public void addTabButton(Widget button) {
        controlsPanel.add(button);
        updateScrollersVisibility();
        tabBarPositionCorrector.defferredCorrection();
    }

    private int wrapperBorderSize = 3;

    public void setWrapperBorderSize(int wrapperBorderSize) {
        this.wrapperBorderSize = wrapperBorderSize;
    }

    /**
     * @param tabId
     * @param icon
     * @param tabText
     * @param widget
     * @param canClose
     */
    public void addTab(String tabId, Image icon, String tabText, Widget widget, boolean canClose) {
        TabTitle tabTitle = new TabTitle(tabId, icon, tabText, this);
        tabTitle.setCanClose(canClose);
        tabTitles.put(tabId, tabTitle);

        Wrapper wrapper = new Wrapper(wrapperBorderSize);
        wrapper.add(widget);
        super.add(wrapper, tabTitle);

        tabIds.add(tabId);

        removeTabIndexAttribute();
        refreshTabIndexes();
    }

    /**
     *
     */
    private void refreshTabIndexes() {
        Element element = getTabBar().getElement();
        element = DOM.getChild(element, 0);
        element = DOM.getChild(element, 0);

        int tdCount = DOM.getChildCount(element);
        for (int i = 1; i < tdCount - 1; i++) {
            Element tdElement = DOM.getChild(element, i);
            DOM.setElementAttribute(tdElement, "tab-bar-index", "" + (i - 1));
        }

        Element deckElement = getDeckPanel().getElement();
        int elements = DOM.getChildCount(deckElement);
        for (int i = 0; i < elements; i++) {
            Element tabContentElement = DOM.getChild(deckElement, i);
            DOM.setElementAttribute(tabContentElement, "tab-index", "" + i);
        }
    }

    /** @see org.exoplatform.gwtframework.ui.client.tab.event.HasTabClosedHandler#addTabClosedHandler(org.exoplatform.gwtframework.ui
     * .client.tab.event.TabClosedHandler) */
    @Override
    public HandlerRegistration addTabClosedHandler(TabClosedHandler tabClosedHandler) {
        tabClosedHandlers.add(tabClosedHandler);
        return new TabClosedHandlerRegistration(tabClosedHandler);
    }

    /**
     * Gets the ID of selected tab.
     *
     * @return
     */
    public String getSelectedTabID() {
        int tabIndex = getTabBar().getSelectedTab();
        return tabIds.get(tabIndex);
    }

    /**
     * Determines the width of the Tab Bar.
     *
     * @return
     */
    private int getTabContainerWidth() {
        return getTabBar().getElement().getParentElement().getOffsetWidth();
    }

    /**
     * Gets tab's ID through its index.
     *
     * @param tabIndex
     * @return
     */
    public String getTabIdByIndex(int tabIndex) {
        return tabIds.get(tabIndex);
    }

    /**
     * Determines the width of this Tab Panel.
     *
     * @return
     */
    private int getTabPanelWidth() {
        return getOffsetWidth();
    }

    /**
     * Determines the width of all tab headers.
     *
     * @return
     */
    private int getTabsWidth() {
        int TAB_LEFT_DELIMITER = 5;
        int tabsWidth = TAB_LEFT_DELIMITER;

        if (getTabBar().getTabCount() == 0) {
            return tabsWidth;
        }

        for (int i = 0; i < getTabBar().getTabCount(); i++) {
            Widget w = (Widget)getTabBar().getTab(i);
            Element e = DOM.getParent(w.getElement());
            tabsWidth += e.getOffsetWidth();
        }

        return tabsWidth;
    }

    /** @see org.exoplatform.gwtframework.ui.client.tab.event.CloseTabHandler#onCloseTab(org.exoplatform.gwtframework.ui.client.tab.event
     * .CloseTabEvent) */
    @Override
    public void onCloseTab(CloseTabEvent event) {
        for (CloseTabHandler handler : closeTabHandlers) {
            handler.onCloseTab(event);
        }

        if (!event.isClosingCanceled()) {
            removeTab(event.getTabId());
        }
    }

    /** @see com.google.gwt.user.client.ui.TabPanel#remove(int) */
    @Override
    public boolean remove(int index) {
        if (getTabBar().getTabCount() > 0) {
            removeTab(tabIds.get(0));
            return true;
        }

        return false;
    }

    /** @see com.google.gwt.user.client.ui.TabPanel#remove(com.google.gwt.user.client.ui.Widget) */
    @Override
    public boolean remove(Widget widget) {
        throw new IllegalStateException(
                "Widgets can be removed only by using remove(int index) or remove(String tabId) methods.");
    }

    /**
     * Removes user defined button from this TabPanel.
     *
     * @param tabButton
     */
    public void removeTabButton(Widget tabButton) {
        controlsPanel.remove(tabButton);
        updateScrollersVisibility();
        tabBarPositionCorrector.defferredCorrection();
    }

    /**
     * Removes tab with specified Tab Id.
     *
     * @param tabId
     */
    public void removeTab(String tabId) {
        if (!tabIds.contains(tabId)) {
            return;
        }

        int tabIndex = tabIds.indexOf(tabId);

        if (getTabBar().getSelectedTab() != tabIndex) {
            tabIds.remove(tabId);
            tabTitles.remove(tabId);
            super.remove(tabIndex);
        } else {
            tabIds.remove(tabId);
            tabTitles.remove(tabId);
            super.remove(tabIndex);

            if (getTabBar().getTabCount() > 0) {
                if (tabIndex >= getTabBar().getTabCount()) {
                    tabIndex = getTabBar().getTabCount() - 1;
                }
                selectTab(tabIndex);
            }
        }

        updateScrollersVisibility();
        tabBarPositionCorrector.defferredCorrection();

        TabClosedEvent tabClosedEvent = new TabClosedEvent(tabId);
        for (TabClosedHandler tabClosedHandler : tabClosedHandlers) {
            tabClosedHandler.onTabClosed(tabClosedEvent);
        }

        refreshTabIndexes();
    }

    /**
     * This small hack removes the "tabindex" attribute from TabBar elements.
     * This action allows to remove the focus from the tab when clicking the mouse.
     */
    private void removeTabIndexAttribute() {
        Widget p = (Widget)getTabBar().getTab(getTabBar().getTabCount() - 1);

        Element e = DOM.getChild(p.getElement(), 0);
        e = DOM.getChild(e, 1);
        e = DOM.getChild(e, 1);
        e = DOM.getChild(e, 0);
        e = DOM.getChild(e, 0);
        DOM.removeElementAttribute(e, "tabindex");
    }

    /**
     * Scroll TabBar to the left.
     *
     * @see org.exoplatform.gwtframework.ui.client.tab.Scrollable#scrollLeft()
     */
    public void scrollLeft() {
        int startX = offset;

        int tabBarWidth = getTabBar().getOffsetWidth();
        int containerWidth = getTabBar().getElement().getParentElement().getOffsetWidth();

        if (tabBarWidth <= containerWidth) {
            offset = 0;
            DOM.setStyleAttribute(getTabBar().getElement(), "left", "0px");
            return;
        }

        int dOffset = containerWidth - 20;
        offset -= dOffset;

        if (offset < 0) {
            offset = 0;
        }

        new MoveAnimation(startX, offset).run(500);
    }

    /**
     * Scroll TabBar to the right.
     *
     * @see org.exoplatform.gwtframework.ui.client.tab.Scrollable#scrollRight()
     */
    public void scrollRight() {
        int startX = offset;

        int tabBarWidth = getTabBar().getOffsetWidth();
        int containerWidth = getTabBar().getElement().getParentElement().getOffsetWidth();

        if (tabBarWidth <= containerWidth) {
            offset = 0;
            DOM.setStyleAttribute(getTabBar().getElement(), "left", "0px");
            return;
        }

        int dOffset = containerWidth - 20;
        offset += dOffset;
        if ((offset + containerWidth) > tabBarWidth) {
            offset = tabBarWidth - containerWidth;
        }

        new MoveAnimation(startX, offset).run(500);
    }

    /**
     * Selects tab with specified ID.
     *
     * @param tabId
     *         ID of tab which will be selected.
     */
    public void selectTab(String tabId) {
        if (tabIds.contains(tabId)) {
            int tabIndex = tabIds.indexOf(tabId);
            selectTab(tabIndex);
        }
    }

    /**
     * @param tabIndex
     * @param isHighlited
     */
    public void setTabHighlited(int tabIndex, boolean isHighlited) {
        Wrapper w = (Wrapper)getDeckPanel().getWidget(getTabBar().getSelectedTab());
        w.setHighlighted(true);
    }

    /**
     * @param tabId
     * @param icon
     */
    public void setTabIcon(String tabId, Image icon) {
        TabTitle tabTitle = tabTitles.get(tabId);
        if (tabTitle == null) {
            return;
        }

        tabTitle.setIcon(icon);
    }

    /**
     * @param tabId
     * @param title
     */
    public void setTabTitle(String tabId, String title) {
        TabTitle tabTitle = tabTitles.get(tabId);
        if (tabTitle == null) {
            return;
        }

        tabTitle.setText(title);
    }

    /**
     *
     */
    private void updateScrollersVisibility() {
        controlsPanel.onResize();

        int tabPanelWidth = getTabPanelWidth();
        int tabsWidth = getTabsWidth();
        int controlsPanelWidth = controlsPanel.getOffsetWidth();

        if (tabScroller.isVisible()) {
            if ((tabsWidth + controlsPanelWidth - tabScroller.getOffsetWidth()) < tabPanelWidth) {
                tabScroller.setVisible(false);
                controlsPanel.onResize();
                if (tabsWidth + controlsPanelWidth < tabPanelWidth) {
                    DOM.setStyleAttribute(getTabBar().getElement(), "left", "0px");
                }
            }

        } else {
            if ((tabsWidth + controlsPanelWidth) > tabPanelWidth) {
                tabScroller.setVisible(true);
                controlsPanel.onResize();
            }
        }
    }

    /** @see com.google.gwt.user.client.ui.RequiresResize#onResize() */
    @Override
    public void onResize() {
        int tabs = getTabBar().getTabCount();
        for (int i = 0; i < tabs; i++) {
            Widget w = getDeckPanel().getWidget(i);
            if (w instanceof RequiresResize) {
                ((RequiresResize)w).onResize();
            }
        }
    }

//   /**
//    * @see org.exoplatform.gwtframework.ui.client.Resizeable#resize(int, int)
//    */
//   @Override
//   public void resize(int width, int height)
//   {
//      int tabBarHeight = getTabBar().getOffsetHeight();
//
//      width = width - wrapperBorderSize;
//      if (width < 0)
//      {
//         width = 0;
//      }
//
//      height = height - tabBarHeight - wrapperBorderSize;
//      if (height < 0)
//      {
//         height = 0;
//      }
//
//      int tabs = getTabBar().getTabCount();
//      for (int i = 0; i < tabs; i++)
//      {
//         Widget w = getDeckPanel().getWidget(i);
//         w.setPixelSize(width, height);
//
//         if (w instanceof Resizeable)
//         {
//            ((Resizeable)w).resize(width, height);
//         }
//         else if (w instanceof RequiresResize)
//         {
//            ((RequiresResize)w).onResize();
//         }
//      }
//
//   }

}
