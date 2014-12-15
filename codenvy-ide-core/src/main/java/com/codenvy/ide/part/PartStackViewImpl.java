/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.part;

import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.api.parts.PartStackView;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.vectomatic.dom.svg.ui.SVGImage;

import static com.codenvy.ide.api.parts.PartStackView.TabPosition.BELOW;
import static com.codenvy.ide.api.parts.PartStackView.TabPosition.LEFT;
import static com.codenvy.ide.api.parts.PartStackView.TabPosition.RIGHT;
import static com.google.gwt.user.client.ui.InsertPanel.ForIsWidget;

/**
 * PartStack view class. Implements UI that manages Parts organized in a Tab-like widget.
 *
 * @author Nikolay Zamosenchuk
 */
public class PartStackViewImpl extends ResizeComposite implements PartStackView {
    private final PartStackUIResources resources;
    // DOM Handler
    private final FocusRequestDOMHandler focusRequestHandler = new FocusRequestDOMHandler();
    // list of tabs
    private final Array<TabButton>       tabButtons          = Collections.createArray();
    final         int                    margin              = 8;//tabButtons text margin
    private InsertPanel     tabsPanel;
    private DeckLayoutPanel contentPanel;
    private ActionDelegate  delegate;
    private TabButton       activeTabButton;
    private boolean         focused;

    private HandlerRegistration mouseDowntHandlerRegistration;
    private HandlerRegistration contextMenuHandlerRegistration;

    private TabPosition tabPosition;
    private int         top;

    /**
     * Create View.
     *
     * @param partStackResources
     * @param tabPosition
     * @param tabsPanel
     */
    @Inject
    public PartStackViewImpl(PartStackUIResources partStackResources, @Assisted TabPosition tabPosition, @Assisted InsertPanel tabsPanel) {
        resources = partStackResources;
        this.tabPosition = tabPosition;
        this.tabsPanel = tabsPanel;
        contentPanel = new DeckLayoutPanel();

        if (tabPosition == RIGHT) {
            top -= 1;
        }
        contentPanel.setStyleName(resources.partStackCss().idePartStackContent());
        initWidget(contentPanel);

        addFocusRequestHandler();
    }

    /** {@inheritDoc} */
    @Override
    public TabItem addTabButton(SVGImage icon, String title, String toolTip, IsWidget widget, boolean closable) {
        TabButton tabItem = new TabButton(icon, title, toolTip, widget, closable);
        tabItem.ensureDebugId("tabButton-" + title);
        tabsPanel.add(tabItem);
        tabButtons.add(tabItem);
        return tabItem;
    }

    /** {@inheritDoc} */
    @Override
    public ForIsWidget getContentPanel() {
        return contentPanel;
    }

    /** {@inheritDoc} */
    @Override
    public void removeTab(int index) {
        if (index < tabButtons.size()) {
            TabButton removed = tabButtons.remove(index);
            if (tabPosition != BELOW) {
                top -= removed.getElement().getOffsetWidth() - margin * 2 - 1;
                if (tabPosition == LEFT) {
                    top += 3;
                } else if (tabPosition == RIGHT) {
                    top += 1;
                }
            }
            tabsPanel.remove(tabsPanel.getWidgetIndex(removed));
            contentPanel.remove(contentPanel.getWidget(index));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setTabpositions(Array<Integer> partPositions) {
        // if this method is called, we reset the top position in case the tabitem is added after a first display of the part stack
        top = 0;
        if (tabPosition == RIGHT) {
            top -= 1;
        }

        for (int pos = 0; pos < partPositions.size(); pos++) {
            int realPartPos = partPositions.get(pos);
            if (realPartPos < tabButtons.size()) {
                tabsPanel.insert(tabButtons.get(realPartPos), pos);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setActiveTab(int index) {
        if (activeTabButton != null) {
            activeTabButton.removeStyleName(resources.partStackCss().idePartStackToolTabSelected());
        }

        if (index >= 0 && index < tabButtons.size()) {
            activeTabButton = tabButtons.get(index);
            activeTabButton.addStyleName(resources.partStackCss().idePartStackToolTabSelected());
            contentPanel.showWidget(index);
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
            contentPanel.addStyleName(resources.partStackCss().idePartStackFocused());
            removeFocusRequestHandler();
        } else {
            contentPanel.removeStyleName(resources.partStackCss().idePartStackFocused());
            addFocusRequestHandler();
        }
    }

    /** Add MouseDown DOM Handler */
    protected void addFocusRequestHandler() {
        mouseDowntHandlerRegistration = addDomHandler(focusRequestHandler, MouseDownEvent.getType());
        contextMenuHandlerRegistration = addDomHandler(focusRequestHandler, ContextMenuEvent.getType());
    }

    /** Remove MouseDown DOM Handler */
    protected void removeFocusRequestHandler() {
        if (mouseDowntHandlerRegistration != null) {
            mouseDowntHandlerRegistration.removeHandler();
            mouseDowntHandlerRegistration = null;
        }

        if (contextMenuHandlerRegistration != null) {
            contextMenuHandlerRegistration.removeHandler();
            contextMenuHandlerRegistration = null;
        }

    }

    /** {@inheritDoc} */
    @Override
    public void updateTabItem(int index, SVGImage icon, String title, String toolTip, IsWidget widget) {
        TabButton tabButton = tabButtons.get(index);
        tabButton.tabItemTitle.setText(title);
        tabButton.setTitle(toolTip);
        tabButton.update(icon, widget);
    }

    /** Special button for tab title. */
    private class TabButton extends Composite implements PartStackView.TabItem {

        private Image       image;
        private FlowPanel   tabItem;
        private InlineLabel tabItemTitle;
        private SVGImage    icon;
        private IsWidget    widget;

        /**
         * Create button.
         *
         * @param icon
         * @param title
         * @param toolTip
         * @param closable
         */
        public TabButton(SVGImage icon, String title, String toolTip, IsWidget widget, boolean closable) {
            this.icon = icon;
            this.widget = widget;
            tabItem = new FlowPanel();
            tabItem.setTitle(toolTip);
            initWidget(tabItem);
            this.setStyleName(resources.partStackCss().idePartStackToolTab());
            if (icon != null) {
                tabItem.add(icon);
            }
            tabItemTitle = new InlineLabel(title);
            tabItemTitle.addStyleName(resources.partStackCss().idePartStackTabLabel());
            tabItem.add(tabItemTitle);
            if (widget != null) {
                tabItem.add(widget);
            }

            if (closable) {
                image = new Image(resources.close());
                image.setStyleName(resources.partStackCss().idePartStackTabCloseButton());
                tabItem.add(image);
                tabItem.ensureDebugId("777");
                addHandlers();
            }
        }

        protected void update(SVGImage icon, IsWidget widget) {
            if (this.icon != null) {
                tabItem.remove(this.icon);
            }
            this.icon = icon;
            if (this.icon != null) {
                tabItem.add(this.icon);
            }

            if (this.widget != null) {
                tabItem.remove(this.widget);
            }
            this.widget = widget;
            if (this.widget != null) {
                tabItem.add(this.widget);
            }
        }

        /**
         * Create button.
         *
         * @param icon
         * @param title
         */
        public TabButton(SVGImage icon, String title) {
            tabItem = new FlowPanel();
            if (title != null) {
                tabItem.setTitle(title);
            }
            initWidget(tabItem);
            tabItem.add(icon);
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
            final int padding = 15;//div(button) padding
            final int marginPicture = 4;//picture margin
            int offsetWidth;
            super.onLoad();
            if (tabPosition == RIGHT) {
                tabItem.addStyleName(resources.partStackCss().idePartStackTabRight());
                offsetWidth = getElement().getOffsetWidth();
                if (icon != null) {
                    getElement().getStyle().setWidth(offsetWidth - padding * 2 - marginPicture, Style.Unit.PX);
                    offsetWidth -= marginPicture;
                } else {
                    getElement().getStyle().setWidth(offsetWidth - padding * 2, Style.Unit.PX);
                }
                getElement().getStyle().setTop(top, Style.Unit.PX);
                top += offsetWidth - margin * 2 - 1;
            } else if (tabPosition == LEFT) {
                tabItem.addStyleName(resources.partStackCss().idePartStackTabLeft());
                offsetWidth = getElement().getOffsetWidth();
                if (icon != null) {
                    getElement().getStyle().setWidth((offsetWidth - padding * 2), Style.Unit.PX);
                    offsetWidth -= marginPicture;
                } else {
                    getElement().getStyle().setWidth((offsetWidth - padding * 2), Style.Unit.PX);
                }
                top += offsetWidth - margin * 2 - 3;
                tabItem.getElement().getStyle().setTop(top, Style.Unit.PX);
            } else {
                tabItem.addStyleName(resources.partStackCss().idePartStackTabBelow());
            }
        }
    }

    /** Notifies delegated handler */
    private final class FocusRequestDOMHandler implements MouseDownHandler, ContextMenuHandler {
        @Override
        public void onMouseDown(MouseDownEvent event) {
            if (delegate != null) {
                delegate.onRequestFocus();
            }
        }

        @Override
        public void onContextMenu(ContextMenuEvent event) {
            if (delegate != null) {
                delegate.onRequestFocus();
            }
        }
    }
}