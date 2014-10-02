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
package com.codenvy.ide.part.editor;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.api.parts.PartStackView;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.vectomatic.dom.svg.ui.SVGImage;

import static com.google.gwt.user.client.ui.InsertPanel.ForIsWidget;

/**
 * @author Evgen Vidolob
 */
public class EditorPartStackView extends ResizeComposite implements PartStackView {
    private static final int COUNTING_ERROR = 10;

    private static PartStackUiBinder uiBinder = GWT.create(PartStackUiBinder.class);
    private ActionDelegate delegate;

    private final PartStackUIResources partStackUIResources;

    private TabButton activeTab;

    private boolean focused;

    // DOM Handler
    private final FocusRequestDOMHandler focusRequestHandler = new FocusRequestDOMHandler();

    private HandlerRegistration focusRequestHandlerRegistration;

    // list of tabs
    private final Array<TabButton> tabs = Collections.createArray();

    @UiField
    DockLayoutPanel parent;

    @UiField
    FlowPanel tabsPanel;

    @UiField
    DeckLayoutPanel contentPanel;

    private ListButton listTabsButton;

    private ShowListButtonClickHandler showListButtonClickHandler;


    interface PartStackUiBinder extends UiBinder<Widget, EditorPartStackView> {
    }

    /**
     * Create View
     *
     * @param partStackResources
     */
    @Inject
    public EditorPartStackView(PartStackUIResources partStackResources, Resources resources) {
        this.partStackUIResources = partStackResources;
        initWidget(uiBinder.createAndBindUi(this));
        setWidth("100%");
        setHeight("100%");

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
        setVisible(false);

        addFocusRequestHandler();
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        Style style = getElement().getParentElement().getStyle();
        style.setHeight(100, Style.Unit.PCT);
        style.setWidth(100, Style.Unit.PCT);
    }

    /** {@inheritDoc} */
    @Override
    public TabItem addTabButton(SVGImage icon, String title, String toolTip, IsWidget widget, boolean closable) {
        setVisible(true);
        TabButton tabItem = new TabButton(icon, title, toolTip, closable);
        tabsPanel.add(tabItem);
        tabs.add(tabItem);
        return tabItem;
    }

    /** {@inheritDoc} */
    @Override
    public ForIsWidget getContentPanel() {
        return contentPanel;
    }

    /** {@inheritDoc} */
    @Override
    public void setTabpositions(Array<Integer> partPositions) {
        //TODO not implemented. need to add for sorting
    }

    /** {@inheritDoc} */
    @Override
    public void removeTab(int index) {
        if (index < tabs.size()) {
            TabButton removed = tabs.remove(index);
            tabsPanel.remove(removed);
            contentPanel.remove(contentPanel.getWidget(index));
        }
        setVisible(tabs.size() > 0);
    }

    /** {@inheritDoc} */
    @Override
    public void setActiveTab(int index) {
        if (activeTab != null) {
            activeTab.removeStyleName(partStackUIResources.partStackCss().idePartStackTabSelected());

            //This code is necessary to distinguish active from inactive content and tab when testing
            if (tabs.indexOf(activeTab) != -1) {
                contentPanel.getWidget(tabs.indexOf(activeTab)).ensureDebugId("inactiveContent");
                activeTab.ensureDebugId("inactiveTabButton-" + activeTab.tabItemTittle.getText());
            }
        }

        if (index >= 0 && index < tabs.size()) {
            activeTab = tabs.get(index);
            activeTab.addStyleName(partStackUIResources.partStackCss().idePartStackTabSelected());
            contentPanel.showWidget(index);

            //This code is necessary to distinguish active from inactive content and tab when testing
            contentPanel.getWidget(index).ensureDebugId("activeContent");
            activeTab.ensureDebugId("activeTabButton-" + activeTab.tabItemTittle.getText());
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
    public void updateTabItem(int index, SVGImage icon, String title, String toolTip, IsWidget widget) {
        TabButton tabButton = tabs.get(index);
        tabButton.tabItemTittle.setText(title);
        tabButton.setTitle(toolTip);
        tabButton.update(icon, widget);
    }

    /** Special button for tab title. */
    private class TabButton extends Composite implements TabItemWithMarks {

        private Image image;

        private FlowPanel tabItem;

        private InlineLabel tabItemTittle;

        private SVGImage icon;

        private boolean isVisibilityOfWarningMark;

        private boolean isVisibilityOfErrorMark;

        /**
         * Create button.
         *
         * @param icon
         * @param title
         * @param toolTip
         * @param closable
         */
        private TabButton(SVGImage icon, String title, String toolTip, boolean closable) {
            this.icon = icon;
            tabItem = new FlowPanel();
            tabItem.setTitle(toolTip);
            initWidget(tabItem);
            this.setStyleName(partStackUIResources.partStackCss().idePartStackTab());
            if (icon != null) {
                icon.setClassNameBaseVal(partStackUIResources.partStackCss().idePartStackTabIcon());
                tabItem.add(icon);
            }
            tabItemTittle = new InlineLabel("");
            tabItemTittle.getElement().setInnerHTML(title);
            tabItem.add(tabItemTittle);
            if (closable) {
                image = new Image(partStackUIResources.close());
                image.setStyleName(partStackUIResources.partStackCss().idePartStackTabCloseButton());
                tabItem.add(image);
                addHandlers();
            }
            this.ensureDebugId("tabButton-" + title);
        }

        protected void update(SVGImage icon, IsWidget widget) {
            if (this.icon != null) {
                tabItem.remove(this.icon);
            }
            this.icon = icon;
            if (this.icon != null) {
                icon.setClassNameBaseVal(partStackUIResources.partStackCss().idePartStackTabIcon());
                tabItem.add(this.icon);
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
        public void setErrorMark(boolean isVisible) {
            if (isVisibilityOfErrorMark != isVisible) {
                isVisibilityOfErrorMark = isVisible;
                if (isVisible) {
                    tabItemTittle.addStyleName(partStackUIResources.partStackCss().lineError());
                } else {
                    tabItemTittle.removeStyleName(partStackUIResources.partStackCss().lineError());
                }
            }
        }

        @Override
        public void setWarningMark(boolean isVisible) {
            if (isVisibilityOfWarningMark != isVisible) {
                isVisibilityOfWarningMark = isVisible;
                if (isVisible) {
                    tabItemTittle.addStyleName(partStackUIResources.partStackCss().lineWarning());
                } else {
                    tabItemTittle.removeStyleName(partStackUIResources.partStackCss().lineWarning());
                }
            }
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
            if (tabsPanel.getWidget(i) instanceof TabButton && (tabsPanel.getWidget(i)) == activeTab
                && width > tabsPanel.getOffsetWidth()) {
                activeTabIsVisible = false;
            }
        }

        //Move not visible active tab to the first place
        if (!activeTabIsVisible) {
            tabsPanel.insert(activeTab, 0);
        }
        listTabsButton.setVisible(width > tabsPanel.getOffsetWidth() && tabsPanel.getOffsetWidth() > 0);

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

        updateTabsAttributes();
    }

    /**
     * A special method, is needed for issue "IDEUI-40 Create rounded tabs for file in order to be conform to initial designs".
     * Method appends special attributes to editor tabs to be able to restyle these tabs in future.
     * Using these attributes we can access these tabs through CSS attribute selectors.
     */
    private void updateTabsAttributes() {
        try {
            TabButton left = null;
            TabButton right = null;

            int width = listTabsButton.isVisible() ? listTabsButton.getOffsetWidth() : COUNTING_ERROR;

            for (int i = 0; i < tabsPanel.getWidgetCount(); i++) {
                if (!(tabsPanel.getWidget(i) instanceof TabButton)) {
                    continue;
                }

                TabButton tab = (TabButton)tabsPanel.getWidget(i);
                width += tab.getOffsetWidth();

                if (width > tabsPanel.getOffsetWidth()) {
                    tab.getElement().removeAttribute("visible");
                    tab.getElement().removeAttribute("left");
                    tab.getElement().removeAttribute("right");
                    tab.getElement().removeAttribute("active");
                    continue;
                }

                tab.getElement().setAttribute("visible", "");

                if (left == null) {
                    left = tab;
                    tab.getElement().setAttribute("left", "");
                } else {
                    tab.getElement().removeAttribute("left");
                }

                if (right == null) {
                    right = tab;
                    tab.getElement().setAttribute("right", "");
                } else {
                    right.getElement().removeAttribute("right");
                    right = tab;
                    tab.getElement().setAttribute("right", "");
                }

                if (tab == activeTab) {
                    tab.getElement().setAttribute("active", "");
                } else {
                    tab.getElement().removeAttribute("active");
                }
            }
        } catch (Exception e) {
            Log.error(getClass(), "Error occurs while updating tab attributes. " + e.getMessage());
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
}
