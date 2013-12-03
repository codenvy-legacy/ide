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
package com.codenvy.ide.part;

import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.api.ui.workspace.PartStackView;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import static com.codenvy.ide.api.ui.workspace.PartStackView.TabPosition.BELOW;
import static com.codenvy.ide.api.ui.workspace.PartStackView.TabPosition.LEFT;
import static com.codenvy.ide.api.ui.workspace.PartStackView.TabPosition.RIGHT;


/**
 * PartStack view class. Implements UI that manages Parts organized in a Tab-like widget.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class PartStackViewImpl extends Composite implements PartStackView {
    private final PartStackUIResources resources;
    // DOM Handler
    private final FocusRequestDOMHandler focusRequstHandler = new FocusRequestDOMHandler();
    // list of tabs
    private final Array<TabButton>       tabs               = Collections.createArray();
    private InsertPanel         tabsPanel;
    private SimplePanel         contentPanel;
    private ActionDelegate      delegate;
    private TabButton           activeTab;
    private boolean             focused;
    private HandlerRegistration focusRequstHandlerRegistration;
    private TabPosition         tabPosition;
    private int                 top;
    private boolean first = true;

    /**
     * Create View
     *
     * @param partStackResources
     */
    @Inject
    public PartStackViewImpl(PartStackUIResources partStackResources, @Assisted TabPosition tabPosition, @Assisted InsertPanel tabsPanel) {
        resources = partStackResources;
        this.tabPosition = tabPosition;
//        parent = new DockLayoutPanel(Style.Unit.PX);
        this.tabsPanel = tabsPanel;
        contentPanel = new SimplePanel();

        if (tabPosition == BELOW) {
            HTML w = new HTML();
            w.setWidth("21px");
            w.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
            tabsPanel.add(w);
        }
        if (tabPosition == RIGHT) {
            top = 6;
        }

        contentPanel.setStyleName(resources.partStackCss().idePartStackContent());
        initWidget(contentPanel);

        addFocusRequestHandler();
        //DEFAULT
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
            tabsPanel.remove(tabsPanel.getWidgetIndex(removed));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setActiveTabButton(int index) {
        if (activeTab != null) {
            activeTab.removeStyleName(resources.partStackCss().idePartStackToolTabSelected());
        }

        if (index >= 0 && index < tabs.size()) {
            activeTab = tabs.get(index);
            activeTab.addStyleName(resources.partStackCss().idePartStackToolTabSelected());
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

    /** Special button for tab title. */
    private class TabButton extends Composite implements PartStackView.TabItem {

        private Image       image;
        private FlowPanel   tabItem;
        private InlineLabel tabItemTittle;
        private Image       icon;

        /**
         * Create button.
         *
         * @param icon
         * @param title
         * @param toolTip
         * @param closable
         */
        public TabButton(Image icon, String title, String toolTip, boolean closable) {
            this.icon = icon;
            tabItem = new FlowPanel();
            tabItem.setTitle(toolTip);
            initWidget(tabItem);
            this.setStyleName(resources.partStackCss().idePartStackToolTab());
            if (icon != null) {
                tabItem.add(icon);
            }
            tabItemTittle = new InlineLabel(title);
            tabItemTittle.addStyleName(resources.partStackCss().idePartStackTabLabel());
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


            int offsetWidth = getElement().getOffsetWidth();
            if (tabPosition == RIGHT) {
                int padding;
                if (icon != null) {
                    getElement().getStyle().setWidth(offsetWidth + 16, Style.Unit.PX);
                    padding = 0;
                } else {
                    padding = 16;
                    getElement().getStyle().setWidth(offsetWidth, Style.Unit.PX);
                }

                getElement().getStyle().setTop(top, Style.Unit.PX);

                top += offsetWidth - padding;
                tabItem.addStyleName(resources.partStackCss().idePartStackTabRight());
            } else if (tabPosition == LEFT) {
                int topPadding;
                if (icon != null) {
                    getElement().getStyle().setWidth(offsetWidth + 15, Style.Unit.PX);
                    topPadding = 6;
                } else {
                    topPadding = 16;
                }
                if (first) {
                    if (icon != null) {
                        top += offsetWidth + 2;
                    } else {
                        top += offsetWidth - 15;
                    }
                    first = false;
                } else {
                    top += offsetWidth - topPadding;
                }

                tabItem.addStyleName(resources.partStackCss().idePartStackTabLeft());
//                tabItem.getElement().getStyle().setWidth(offsetWidth, Style.Unit.PX);
                tabItem.getElement().getStyle().setTop(top, Style.Unit.PX);
            } else {
                tabItem.addStyleName(resources.partStackCss().idePartStackTabBelow());

            }
        }


    }

    /** Notifies delegated handler */
    private final class FocusRequestDOMHandler implements MouseDownHandler {
        @Override
        public void onMouseDown(MouseDownEvent event) {
            if (delegate != null) {
                delegate.onRequestFocus();
            }
        }
    }
}