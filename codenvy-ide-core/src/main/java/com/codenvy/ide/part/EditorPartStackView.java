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

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.api.ui.workspace.PartStackView;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class EditorPartStackView extends ResizeComposite implements PartStackView {
    private static final int          COUNTING_ERROR      = 10;
    
    private static PartStackUiBinder     uiBinder            = GWT.create(PartStackUiBinder.class);
    private ActionDelegate               delegate;

    private final PartStackUIResources   partStackUIResources;

    private final Resources              resources;

    private TabButton                    activeTab;

    private boolean                      focused;

    // DOM Handler
    private final FocusRequestDOMHandler focusRequestHandler = new FocusRequestDOMHandler();

    private HandlerRegistration          focusRequestHandlerRegistration;

    // list of tabs
    private final Array<TabButton>       tabs                = Collections.createArray();

    @UiField
    DockLayoutPanel                      parent;

    @UiField
    FlowPanel                            tabsPanel;

    @UiField
    SimpleLayoutPanel                    contentPanel;

    private ListButton                   listTabsButton;

    private ShowListButtonClickHandler   showListButtonClickHandler;


    interface PartStackUiBinder extends UiBinder<Widget, EditorPartStackView> {
    }

    /**
     * Create View
     * 
     * @param partStackResources
     */
    @Inject
    public EditorPartStackView(PartStackUIResources partStackResources, Resources resources) {
        this.resources = resources;
        this.partStackUIResources = partStackResources;
        initWidget(uiBinder.createAndBindUi(this));

        parent.setStyleName(partStackResources.partStackCss().idePartStack());
        tabsPanel.setStyleName(partStackResources.partStackCss().idePartStackTabs());
        contentPanel.setStyleName(partStackResources.partStackCss().idePartStackEditorContent());

        listTabsButton = new ListButton(new Image(resources.listOpenedEditors()), "Show list");

        listTabsButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                listTabsButton.addStyleName(partStackUIResources.partStackCss().idePartStackTabButtonSelected());
                int x = listTabsButton.getAbsoluteLeft() + listTabsButton.getOffsetWidth();
                int y = listTabsButton.getAbsoluteTop() + listTabsButton.getOffsetHeight();
                showListButtonClickHandler.onShowListClicked(x, y, new AsyncCallback<Void>() {

                    @Override
                    public void onSuccess(Void result) {
                        listTabsButton.removeStyleName(partStackUIResources.partStackCss().idePartStackTabButtonSelected());
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        listTabsButton.removeStyleName(partStackUIResources.partStackCss().idePartStackTabButtonSelected());
                    }
                });
            }
        });


        tabsPanel.add(listTabsButton);
        listTabsButton.setVisible(false);


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
            activeTab.removeStyleName(partStackUIResources.partStackCss().idePartStackTabSelected());
        }

        if (index >= 0 && index < tabs.size()) {
            activeTab = tabs.get(index);
            activeTab.addStyleName(partStackUIResources.partStackCss().idePartStackTabSelected());
        }
        processPanelSize();
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
            parent.addStyleName(partStackUIResources.partStackCss().idePartStackFocused());
            removeFocusRequestHandler();
        } else {
            parent.removeStyleName(partStackUIResources.partStackCss().idePartStackFocused());
            addFocusRequestHandler();
        }
    }

    /** Add MouseDown DOM Handler */
    protected void addFocusRequestHandler() {
        focusRequestHandlerRegistration = addDomHandler(focusRequestHandler, MouseDownEvent.getType());
    }

    /** Remove MouseDown DOM Handler */
    protected void removeFocusRequestHandler() {
        if (focusRequestHandlerRegistration != null) {
            focusRequestHandlerRegistration.removeHandler();
            focusRequestHandlerRegistration = null;
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
            this.setStyleName(partStackUIResources.partStackCss().idePartStackTab());
            if (icon != null) {
                tabItem.add(icon);
            }
            tabItemTittle = new InlineLabel(title);
            tabItem.add(tabItemTittle);
            if (closable) {
                image = new Image(partStackUIResources.close());
                image.setStyleName(partStackUIResources.partStackCss().idePartStackTabCloseButton());
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

    /** Button for listing all opened tabs. */
    private class ListButton extends Composite implements PartStackView.TabItem {

        private FlowPanel tabItem;

        public ListButton(Image icon, String toolTip) {
            tabItem = new FlowPanel();
            tabItem.setTitle(toolTip);
            initWidget(tabItem);
            this.setStyleName(partStackUIResources.partStackCss().idePartStackTabButton());
            this.addStyleName(partStackUIResources.partStackCss().idePartStackTabRightButton());
            setWidth("32px");
            if (icon != null) {
                tabItem.add(icon);
            }
        }

        @Override
        public HandlerRegistration addClickHandler(ClickHandler handler) {
            return addDomHandler(handler, ClickEvent.getType());
        }

        /** {@inheritDoc} */
        @Override
        public HandlerRegistration addCloseHandler(CloseHandler<TabItem> handler) {
            return null;
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

    /** {@inheritDoc} */
    @Override
    public void onResize() {
        super.onResize();
        processPanelSize();
    }

    /**
     * This method analyzes the tabs panel size and the size of its components and 
     * displays the show list button, when all tabs can not be placed.
     */
    private void processPanelSize() {
        boolean activeTabIsVisible = true;
        int width = listTabsButton.isVisible() ? listTabsButton.getOffsetWidth() : COUNTING_ERROR;
        
        for (int i = 0; i < tabsPanel.getWidgetCount(); i++) {
            //Do not count list buttons width
            if (tabsPanel.getWidget(i) instanceof ListButton) {
                continue;
            }
            width += tabsPanel.getWidget(i).getOffsetWidth();
            //Check whether active tab is visible
            if (tabsPanel.getWidget(i) instanceof TabButton && ((TabButton)tabsPanel.getWidget(i)) == activeTab
                && width > tabsPanel.getOffsetWidth()) {
                activeTabIsVisible = false;
            }
        }
        
        //Move not visible active tab to the first place
        if (!activeTabIsVisible) {
            tabsPanel.insert(activeTab, 0);
        }
        listTabsButton.setVisible(width > tabsPanel.getOffsetWidth());

        width = COUNTING_ERROR;
        if (listTabsButton.isVisible()) {
            for (int i = 0; i < tabsPanel.getWidgetCount(); i++) {
                width += tabsPanel.getWidget(i).getOffsetWidth();
                if (width > tabsPanel.getOffsetWidth() && i >= 1) {
                    tabsPanel.insert(listTabsButton, i - 1);
                    break;
                }
            }
        }
    }

    /**
     * Sets the handler for list all tabs button click event.
     * 
     * @param handler
     */
    public void setShowListButtonHandler(ShowListButtonClickHandler handler) {
        this.showListButtonClickHandler = handler;
    }

    /** {@inheritDoc} */
    @Override
    public void clearContentPanel() {
        getContentPanel().setWidget(null);
    }
}
