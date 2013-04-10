/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package com.codenvy.ide.part;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import static com.codenvy.ide.part.PartStackView.TabPosition.ABOVE;
import static com.codenvy.ide.part.PartStackView.TabPosition.BELOW;


/**
 * PartStack view class. Implements UI that manages Parts organized in a Tab-like widget.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class PartStackViewImpl extends Composite implements PartStackView {
    private static PartStackUiBinder uiBinder = GWT.create(PartStackUiBinder.class);
    private final PartStackUIResources resources;
    // DOM Handler
    private final FocusRequstDOMHandler focusRequstHandler = new FocusRequstDOMHandler();
    // list of tabs
    private final JsonArray<TabButton>  tabs               = JsonCollections.createArray();
    @UiField
    DockLayoutPanel parent;
    @UiField
    FlowPanel       tabsPanel;
    @UiField
    SimplePanel     contentPanel;
    private ActionDelegate      delegate;
    private TabButton           activeTab;
    private boolean             focused;
    private HandlerRegistration focusRequstHandlerRegistration;
    private TabPosition         tabPosition;

    /**
     * Create View
     *
     * @param partStackResources
     */
    @Inject
    public PartStackViewImpl(PartStackUIResources partStackResources) {
        resources = partStackResources;
        initWidget(uiBinder.createAndBindUi(this));

        parent.setStyleName(resources.partStackCss().idePartStack());
        tabsPanel.setStyleName(resources.partStackCss().idePartStackTabs());
        contentPanel.setStyleName(resources.partStackCss().idePartStackContent());

        addFocusRequestHandler();
        //DEFAULT
        tabPosition = ABOVE;
    }

    @Override
    public void setTabPosition(TabPosition tabPosition) {
        this.tabPosition = tabPosition;
        if (tabPosition == ABOVE)
            return;
        parent.clear();
        switch (tabPosition) {
            case BELOW:
                parent.addSouth(tabsPanel, 22);
                updateBelowTabs();
                break;
            case LEFT:
                parent.addWest(tabsPanel, 22);
                updateLeftTabs();
                break;
            case RIGHT:
                parent.addEast(tabsPanel, 22);
                updateRightTabs();
                break;
        }
        parent.add(contentPanel);

    }

    private void updateBelowTabs() {
        for (TabButton tabItem : tabs.asIterable()) {
            tabItem.setStyleName(resources.partStackCss().idePartStackTabBelow());
        }
    }

    private void updateRightTabs() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                int top = 16;
                for (TabButton tabItem : tabs.asIterable()) {
                    tabItem.getElement().getStyle().setHeight(16, Style.Unit.PX);
                    tabItem.getElement().getStyle().setWidth(tabItem.offsetWidth, Style.Unit.PX);
                    tabItem.getElement().getStyle().setTop(top, Style.Unit.PX);
                    tabItem.addStyleName(resources.partStackCss().idePartStackTabRight());
                    top += tabItem.offsetWidth - 16;

                }
            }
        });
    }

    private void updateLeftTabs() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                int top = 0;
                boolean first = true;
                for (TabButton tabItem : tabs.asIterable()) {
                    tabItem.getElement().getStyle().setHeight(16, Style.Unit.PX);
                    if (first) {
                        top += tabItem.offsetWidth + 8;
                        first = false;
                    } else {
                        top += tabItem.offsetWidth - 16;
                    }
                    tabItem.getElement().getStyle().setWidth(tabItem.offsetWidth, Style.Unit.PX);
                    tabItem.getElement().getStyle().setTop(top, Style.Unit.PX);
                    tabItem.addStyleName(resources.partStackCss().idePartStackTabLeft());
                }
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public TabItem addTabButton(Image icon, String title, String toolTip, boolean closable) {
        TabButton tabItem = new TabButton(icon, title, toolTip, closable);
        tabsPanel.add(tabItem);
        tabs.add(tabItem);
        return tabItem;
    }

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget getContentPanel() {
        return contentPanel;
    }

    /** {@inheritDoc} */
    @Override
    public void removeTabButton(int index) {
        if (index < tabs.size()) {
            TabButton removed = tabs.remove(index);
            tabsPanel.remove(removed);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setActiveTabButton(int index) {
        if (activeTab != null) {
            if (tabPosition == BELOW) {
                activeTab.removeStyleName(resources.partStackCss().idePartStackTabSelectedBelow());
            } else {
                activeTab.removeStyleName(resources.partStackCss().idePartStackTabSelected());
            }
        }

        if (index >= 0 && index < tabs.size()) {
            activeTab = tabs.get(index);
            if (tabPosition == BELOW) {
                activeTab.addStyleName(resources.partStackCss().idePartStackTabSelectedBelow());
            } else {
                activeTab.addStyleName(resources.partStackCss().idePartStackTabSelected());
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus(boolean focused) {
        if (this.focused == focused) {
            // already focused
            return;
        }

        this.focused = focused;

        // if focused already, then remove DOM handler
        if (focused) {
            parent.addStyleName(resources.partStackCss().idePartStackFocused());
            removeFocusRequestHandler();
        } else {
            parent.removeStyleName(resources.partStackCss().idePartStackFocused());
            addFocusRequestHandler();
        }
    }

    /** Add MouseDown DOM Handler */
    protected void addFocusRequestHandler() {
        focusRequstHandlerRegistration = addDomHandler(focusRequstHandler, MouseDownEvent.getType());
    }

    /** Remove MouseDown DOM Handler */
    protected void removeFocusRequestHandler() {
        if (focusRequstHandlerRegistration != null) {
            focusRequstHandlerRegistration.removeHandler();
            focusRequstHandlerRegistration = null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateTabItem(int index, ImageResource icon, String title, String toolTip) {
        TabButton tabButton = tabs.get(index);
        tabButton.tabItemTittle.setText(title);
        tabButton.setTitle(toolTip);
    }

    interface PartStackUiBinder extends UiBinder<Widget, PartStackViewImpl> {
    }

    /** Special button for tab title. */
    private class TabButton extends Composite implements PartStackView.TabItem {

        private Image       image;
        private FlowPanel   tabItem;
        private InlineLabel tabItemTittle;
        private int         offsetWidth;

        /**
         * Create button.
         *
         * @param icon
         * @param title
         * @param toolTip
         * @param closable
         */
        public TabButton(Image icon, String title, String toolTip, boolean closable) {
            tabItem = new FlowPanel();
            tabItem.setTitle(toolTip);
            initWidget(tabItem);
            this.setStyleName(resources.partStackCss().idePartStackTab());
            if (icon != null) {
                tabItem.add(icon);
            }
            tabItemTittle = new InlineLabel(title);
            tabItem.add(tabItemTittle);
            if (closable) {
                image = new Image(resources.close());
                image.setStyleName(resources.partStackCss().idePartStackTabCloseButton());
                tabItem.add(image);
                addHandlers();
            }
        }

        @Override
        public HandlerRegistration addClickHandler(ClickHandler handler) {
            return addDomHandler(handler, ClickEvent.getType());
        }

        @Override
        public HandlerRegistration addCloseHandler(CloseHandler<TabItem> handler) {
            return addHandler(handler, CloseEvent.getType());
        }

        private void addHandlers() {
            image.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    CloseEvent.fire(TabButton.this, TabButton.this);
                }
            });
        }

        @Override
        protected void onLoad() {
            super.onLoad();
            offsetWidth = getElement().getOffsetWidth();
        }
    }

    /** Notifies delegated handler */
    private final class FocusRequstDOMHandler implements MouseDownHandler {
        @Override
        public void onMouseDown(MouseDownEvent event) {
            if (delegate != null) {
                delegate.onRequestFocus();
            }
        }
    }
}