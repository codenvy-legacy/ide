/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.gwtframework.ui.client.tablayout;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Style.*;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.SelectedEventImpl;
import org.exoplatform.gwtframework.ui.client.tab.Scrollable;
import org.exoplatform.gwtframework.ui.client.tab.TabControlsPanel;
import org.exoplatform.gwtframework.ui.client.tab.TabScroller;
import org.exoplatform.gwtframework.ui.client.tab.TabTitle;
import org.exoplatform.gwtframework.ui.client.tab.event.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wrapped and restyled {@link TabLayoutPanel}.
 * Since standard components do not provide a way to manipulate the tabs,
 * such as closing the tab by clicking the mouse and scrolling tabs when the tabs of more than can't fit Tab container.
 * This component was added this functionality.<br>
 * Also
 * Component can also draw additional controls in the tabs, see {@link TabPanel#addTabButton(Widget)}
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Oct 6, 2011 evgen $
 */
public class TabPanel extends ResizeComposite implements CloseTabHandler, HasCloseTabHandler, HasTabClosedHandler,
                                                         Scrollable, HasSelectionHandlers<Integer> {

    private TabLayoutPanel tab;

    /** List of opened tabs */
    private List<String> tabIds = new ArrayList<String>();

    /** Map of Tab Titles. */
    private Map<String, TabTitle> tabTitles = new HashMap<String, TabTitle>();

    /** List of CloseTabHandlers */
    private List<CloseTabHandler> closeTabHandlers = new ArrayList<CloseTabHandler>();

    /** List of TabClosedHandlers */
    private List<TabClosedHandler> tabClosedHandlers = new ArrayList<TabClosedHandler>();

    private TabControlsPanel controlsPanel;

    private TabScroller tabScroller;

    private TabBarPositionCorrector tabBarPositionCorrector;

    /** Left offset of TabBar */
    private int offset = 0;

    private Element tabsElement;

    private int selectedTab;

    /**
     *
     */
    public TabPanel() {
        tab = new TabLayoutPanel(24, Unit.PX);
        initWidget(tab);

        // this is hack of DOM structure of tab widget container
        // needs to correct add controls panel, with buttons and tab scroll buttons
        Element child1 = DOM.getChild(getElement(), 1);
        tabsElement = DOM.getChild(child1, 0);

        Element table = DOM.createTable();
        table.getStyle().setHeight(100, Unit.PCT);
        Element tr = DOM.createTR();
        DOM.appendChild(table, tr);
        table.getStyle().setTableLayout(TableLayout.FIXED);
        table.getStyle().setWidth(100, Unit.PCT);
        table.getStyle().setBorderStyle(BorderStyle.NONE);
        DOM.setElementAttribute(table, "cellpadding", "0");
        DOM.setElementAttribute(table, "cellspacing", "0");

        Element td1 = DOM.createTD();
        td1.getStyle().setOverflow(Overflow.HIDDEN);
        DOM.appendChild(tr, td1);
        DOM.appendChild(td1, tabsElement);
        DOM.setStyleAttribute(td1, "whiteSpace", "nowrap");
        tabsElement.getStyle().setHeight(24, Unit.PX);
        tabsElement.getStyle().setDisplay(Display.INLINE_BLOCK);
        tabsElement.getStyle().clearWidth();

        Element td2 = DOM.createTD();
        DOM.appendChild(tr, td2);
        DOM.appendChild(child1, table);
        controlsPanel = new TabControlsPanel(td2);

        tabScroller = new TabScroller(this);
        tabScroller.setVisible(false);
        controlsPanel.add(tabScroller);

        tabBarPositionCorrector = new TabBarPositionCorrector();
        tab.addSelectionHandler(new SelectionHandler<Integer>() {

            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                if (!event.getSelectedItem().equals(selectedTab)) {
                    selectedTab = event.getSelectedItem();
                    tabBarPositionCorrector.defferredCorrection();
                }
            }
        });

    }

    /** @see com.google.gwt.user.client.ui.ResizeComposite#onResize() */
    @Override
    public void onResize() {
        super.onResize();
        offset = 0;
        updateScrollersVisibility();
    }

    private ClickHandler tabClickHandler = new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
            SelectionEvent<Integer> ev = new SelectedEventImpl<Integer>(tab.getSelectedIndex());
            tab.fireEvent(ev);
        }
    };

    /**
     * Add new Tab
     *
     * @param tabId
     *         id of a tab
     * @param icon
     *         Icon of tab
     * @param tabText
     *         tab title
     * @param widget
     *         content of the tab
     * @param canClose
     *         is tab can close
     */
    public void addTab(String tabId, Image icon, String tabText, Widget widget, boolean canClose) {
        TabTitle tabTitle = new TabTitle(tabId, icon, tabText, this);
        tabTitle.addClickHandler(tabClickHandler);
        tabTitle.setCanClose(canClose);
        tabTitles.put(tabId, tabTitle);
        tabIds.add(tabId);

        tab.add(widget, tabTitle);

        refreshTabIndexes();
        updateScrollersVisibility();
    }

    /**
     * Add "tab-bar-index" attribute to all tabs.
     * Need for manipulate selections by Selenium tests.
     */
    private void refreshTabIndexes() {
        Element element = tabsElement;
        int tdCount = DOM.getChildCount(element);
        for (int i = 0; i < tdCount; i++) {
            Element tdElement = DOM.getChild(element, i);
            DOM.setElementAttribute(tdElement, "tab-bar-index", "" + (i));
        }
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

    /**
     * Selects tab with specified ID.
     *
     * @param tabId
     *         ID of tab which will be selected.
     */
    public void selectTab(String tabId) {
        if (tabIds.contains(tabId)) {
            int tabIndex = tabIds.indexOf(tabId);
            tab.selectTab(tabIndex);
        }
    }

    /**
     * Remove tab by this index
     *
     * @param index
     * @return
     */
    public boolean remove(int index) {
        if (tab.getWidgetCount() > 0) {
            removeTab(tabIds.get(index));
            return true;
        }

        return false;
    }

    /**
     * Change tab icon
     *
     * @param tabId
     *         id of the tab
     * @param icon
     *         new icon
     */
    public void setTabIcon(String tabId, Image icon) {
        TabTitle tabTitle = tabTitles.get(tabId);
        if (tabTitle == null) {
            return;
        }

        tabTitle.setIcon(icon);
    }

    /**
     * Change tab title
     *
     * @param tabId
     *         is of the tab
     * @param title
     *         new tab title
     */
    public void setTabTitle(String tabId, String title) {
        TabTitle tabTitle = tabTitles.get(tabId);
        if (tabTitle == null) {
            return;
        }

        tabTitle.setText(title);
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

        if (tab.getSelectedIndex() != tabIndex) {
            tabIds.remove(tabId);
            tabTitles.remove(tabId);
            tab.remove(tabIndex);
        } else {
            tabIds.remove(tabId);
            tabTitles.remove(tabId);
            tab.remove(tabIndex);

            if (tab.getWidgetCount() > 0) {
                if (tabIndex >= tab.getWidgetCount()) {
                    tabIndex = tab.getWidgetCount() - 1;
                }
                tab.selectTab(tabIndex);
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

    /** this method consider to show/hide tabs scroll buttons */
    private void updateScrollersVisibility() {
        controlsPanel.onResize();

        int tabPanelWidth = getTabPanelWidth();
        int tabsWidth = getTabsWidth();
        int controlsPanelWidth = controlsPanel.getOffsetWidth();

        if (tabScroller.isVisible()) {
            if ((tabsWidth + controlsPanelWidth - tabScroller.getOffsetWidth()) < tabPanelWidth) {
                tabScroller.setVisible(false);
                controlsPanel.onResize();

                DOM.setStyleAttribute((Element)tabsElement, "left", "0px");
            }

        } else {
            if ((tabsWidth + controlsPanelWidth) > tabPanelWidth) {
                tabScroller.setVisible(true);
                controlsPanel.onResize();
            }
        }
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
    }

    /**
     * Removes user defined button from this TabPanel.
     *
     * @param tabButton
     */
    public void removeTabButton(Widget tabButton) {
        controlsPanel.remove(tabButton);
        updateScrollersVisibility();
    }

    /**
     * Determines the width of all tab headers.
     *
     * @return
     */
    private int getTabsWidth() {
        int TAB_LEFT_DELIMITER = 5;
        int tabsWidth = TAB_LEFT_DELIMITER;

        if (tab.getWidgetCount() == 0) {
            return tabsWidth;
        }

        for (int i = 0; i < tab.getWidgetCount(); i++) {
            Widget w = tab.getTabWidget(i);
            Element e = DOM.getParent(w.getElement());
            tabsWidth += e.getOffsetWidth();
        }

        return tabsWidth;
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
     * Gets tab's ID through its index.
     *
     * @param tabIndex
     * @return
     */
    public String getTabIdByIndex(int tabIndex) {
        return tabIds.get(tabIndex);
    }

    /** @see org.exoplatform.gwtframework.ui.client.tab.event.HasTabClosedHandler#addTabClosedHandler(org.exoplatform.gwtframework.ui
     * .client.tab.event.TabClosedHandler) */
    @Override
    public HandlerRegistration addTabClosedHandler(TabClosedHandler tabClosedHandler) {
        tabClosedHandlers.add(tabClosedHandler);
        return new TabClosedHandlerRegistration(tabClosedHandler);
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

    /** @return  */
    public int getSelectedTab() {
        return tab.getSelectedIndex();
    }

    /** @see org.exoplatform.gwtframework.ui.client.tab.Scrollable#scrollLeft() */
    @Override
    public void scrollLeft() {
        int startX = offset;

        int tabBarWidth = tabsElement.getOffsetWidth();
        int containerWidth = getElement().getOffsetWidth();

        if (tabBarWidth <= containerWidth) {
            offset = 0;
            DOM.setStyleAttribute(tabsElement, "left", "0px");
            return;
        }

        int dOffset = containerWidth - 20;
        offset -= dOffset;

        if (offset < 0) {
            offset = 0;
        }

        new MoveAnimation(startX, offset).run(500);
    }

    /** @see org.exoplatform.gwtframework.ui.client.tab.Scrollable#scrollRight() */
    @Override
    public void scrollRight() {
        int startX = offset;

        int tabBarWidth = tabsElement.getOffsetWidth();
        int containerWidth = getElement().getOffsetWidth() - controlsPanel.getOffsetWidth();

        if (tabBarWidth <= containerWidth) {
            offset = 0;
            DOM.setStyleAttribute(tabsElement, "left", "0px");
            return;
        }

        int dOffset = containerWidth - 40;
        offset += dOffset;
        if ((offset + containerWidth) > tabBarWidth) {
            offset = tabBarWidth - containerWidth;
        }

        new MoveAnimation(startX, offset).run(500);
    }

    /**
     * Determines the width of the Tab Bar.
     *
     * @return
     */
    private int getTabContainerWidth() {
        return getElement().getOffsetWidth() - controlsPanel.getOffsetWidth();
    }

    private class TabBarPositionCorrector {

        private int containerLeft;

        private int selectedTabIndex;

        private Widget selectedTabWidget;

        public void correctTabBarPosition() {
            selectedTabIndex = tab.getSelectedIndex();
            if (selectedTabIndex < 0) {
                return;
            }

            selectedTabWidget = tab.getTabWidget(selectedTabIndex);
            containerLeft = getElement().getAbsoluteLeft();

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
            if (tabContainerWidth < 0) {
                offset = 0;
                return false;
            }
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
            DOM.setStyleAttribute(tabsElement, "left", "-" + left + "px");
        }

    }

    /** @see com.google.gwt.event.logical.shared.HasSelectionHandlers#addSelectionHandler(com.google.gwt.event.logical.shared
     * .SelectionHandler) */
    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<Integer> handler) {
        return tab.addSelectionHandler(handler);
    }

    /**
     * Get widget associate with tab index
     *
     * @param i
     *         tab index
     * @return widget
     */
    public Widget getWidget(int i) {
        return tab.getWidget(i);
    }
}
