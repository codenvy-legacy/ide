/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.part;

import com.codenvy.ide.api.ui.perspective.PartStackView;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class EditorPartStackView extends Composite implements PartStackView {
    private static PartStackUiBinder uiBinder = GWT.create(PartStackUiBinder.class);
    private ActionDelegate delegate;

    private final PartStackUIResources resources;

    private TabButton activeTab;

    private boolean focused;

    // DOM Handler
    private final FocusRequstDOMHandler focusRequstHandler = new FocusRequstDOMHandler();

    private HandlerRegistration focusRequstHandlerRegistration;

    // list of tabs
    private final JsonArray<TabButton> tabs = JsonCollections.createArray();

    @UiField
    DockLayoutPanel parent;

    @UiField
    FlowPanel tabsPanel;

    @UiField
    com.google.gwt.user.client.ui.SimplePanel contentPanel;

    interface PartStackUiBinder extends UiBinder<Widget, EditorPartStackView> {
    }

    /**
     * Create View
     *
     * @param partStackResources
     */
    @Inject
    public EditorPartStackView(PartStackUIResources partStackResources) {
        resources = partStackResources;
        initWidget(uiBinder.createAndBindUi(this));

        parent.setStyleName(resources.partStackCss().idePartStack());
        tabsPanel.setStyleName(resources.partStackCss().idePartStackTabs());
        contentPanel.setStyleName(resources.partStackCss().idePartStackEditorContent());

        addFocusRequestHandler();
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
            activeTab.removeStyleName(resources.partStackCss().idePartStackTabSelected());
        }

        if (index >= 0 && index < tabs.size()) {
            activeTab = tabs.get(index);
            activeTab.addStyleName(resources.partStackCss().idePartStackTabSelected());
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

    /** Special button for tab title. */
    private class TabButton extends Composite implements PartStackView.TabItem {

        private Image image;

        private FlowPanel tabItem;

        private InlineLabel tabItemTittle;

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
