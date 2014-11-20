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
package com.codenvy.ide.menu;

import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.ActionGroup;
import com.codenvy.ide.api.action.ActionManager;
import com.codenvy.ide.api.action.ActionPlaces;
import com.codenvy.ide.api.action.CustomComponentAction;
import com.codenvy.ide.api.action.IdeActions;
import com.codenvy.ide.api.action.Presentation;
import com.codenvy.ide.api.action.Separator;
import com.codenvy.ide.api.keybinding.KeyBindingAgent;
import com.codenvy.ide.toolbar.ActionSelectedHandler;
import com.codenvy.ide.toolbar.CloseMenuHandler;
import com.codenvy.ide.toolbar.MenuLockLayer;
import com.codenvy.ide.toolbar.PresentationFactory;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Implements {@link MainMenuView} using standard GWT Menu Widgets
 *
 * @author Nikolay Zamosenchuk
 * @author Oleksii Orel
 */
public class MainMenuViewImpl extends Composite implements MainMenuView, CloseMenuHandler, ActionSelectedHandler {

    static final MenuResources resources = GWT.create(MenuResources.class);

    static {
        resources.menuCss().ensureInjected();
    }

    private final MenuItemPresentationFactory presentationFactory = new MenuItemPresentationFactory();
    /** Working table, cells of which are contains element of Menu. */
    private final MenuBarTable                table               = new MenuBarTable();

    private final FlowPanel rightPanel = new FlowPanel();

    private final FlowPanel leftPanel = new FlowPanel();
    /** Panel, which contains top menu. */
    private final FlowPanel rootPanel = new FlowPanel();
    /** Lock layer for displaying popup menus. */
    private MenuLockLayer lockLayer;
    /** List Menu Bar items. */
    private Map<Element, MenuBarItem> menuBarItems   = new LinkedHashMap<>();
    private Map<Action, MenuBarItem>  action2barItem = new HashMap<>();

    /** Store selected Menu Bar item. */
    private MenuBarItem selectedMenuBarItem;

    private List<Action> leftVisibleActions     = new ArrayList<>();
    private List<Action> newLeftVisibleActions  = new ArrayList<>();
    private List<Action> newMenuVisibleActions  = new ArrayList<>();
    private List<Action> menuVisibleActions     = new ArrayList<>();
    private List<Action> rightVisibleActions    = new ArrayList<>();
    private List<Action> newRightVisibleActions = new ArrayList<>();
    private ActionManager   actionManager;
    private KeyBindingAgent keyBindingAgent;

    /** Create new {@link MainMenuViewImpl} */
    @Inject
    public MainMenuViewImpl(ActionManager actionManager, KeyBindingAgent keyBindingAgent) {
        this.actionManager = actionManager;
        this.keyBindingAgent = keyBindingAgent;

        initWidget(rootPanel);

        rootPanel.setStyleName(resources.menuCss().menuBar());
        leftPanel.addStyleName(resources.menuCss().leftPanel());
        rootPanel.add(leftPanel);
        table.setStyleName(resources.menuCss().menuBarTable());
        table.setCellPadding(0);
        table.setCellSpacing(0);
        rootPanel.add(table);
        rightPanel.addStyleName(resources.menuCss().rightPanel());
        rootPanel.add(rightPanel);
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        new Timer() {
            @Override
            public void run() {
                updateMenuActions();
            }
        }.scheduleRepeating(1000);
    }

    /** Handle closing of all popup windows. */
    public void onCloseMenu() {
        selectedMenuBarItem.setNormalState();
        selectedMenuBarItem = null;
        lockLayer = null;
    }

    void updateMenuActions() {
        if (selectedMenuBarItem != null) {
            return;
        }

        newMenuVisibleActions.clear();
        expandActionGroup(IdeActions.GROUP_MAIN_MENU, newMenuVisibleActions, actionManager);
        if (!newMenuVisibleActions.equals(menuVisibleActions)) {
            removeAll();
            for (final Action action : newMenuVisibleActions) {
                add(ActionPlaces.MAIN_MENU, action, presentationFactory);
            }
            menuVisibleActions = newMenuVisibleActions;
        }
        newRightVisibleActions.clear();
        expandActionGroup(IdeActions.GROUP_RIGHT_MAIN_MENU, newRightVisibleActions, actionManager);
        if (!newRightVisibleActions.equals(rightVisibleActions)) {
            rightPanel.clear();
            for (Action action : newRightVisibleActions) {
                addToPanel(rightPanel, action, presentationFactory);
            }
            rightVisibleActions = newRightVisibleActions;
        }
        newLeftVisibleActions.clear();
        expandActionGroup(IdeActions.GROUP_LEFT_MAIN_MENU, newLeftVisibleActions, actionManager);
        if (!newLeftVisibleActions.equals(leftVisibleActions)) {
            leftPanel.clear();
            for (Action action : newLeftVisibleActions) {
                addToPanel(leftPanel, action, presentationFactory);
            }
            leftVisibleActions = newLeftVisibleActions;
        }
    }

    /**
     * Create a new widget and add it to panel menu.
     */
    private void addToPanel(FlowPanel panel, Action action, PresentationFactory presentationFactory) {
        Presentation presentation = presentationFactory.getPresentation(action);

        if (action instanceof Separator) {
            panel.add(new SeparatorItem());

            // todo find way to render non custom actions
        } else if (action instanceof CustomComponentAction) {
            CustomComponentAction customComponentAction = (CustomComponentAction)action;
            Widget component = customComponentAction.createCustomComponent(presentation);
            component.addStyleName(resources.menuCss().customComponent());
            panel.add(component);
        }
    }

    private void removeAll() {
        table.clear();
        menuBarItems.clear();
        action2barItem.clear();
    }

    private void expandActionGroup(String actionGroupId, final List<Action> newVisibleActions, ActionManager actionManager) {
        final ActionGroup mainActionGroup = (ActionGroup)actionManager.getAction(actionGroupId);
        if (mainActionGroup == null) return;
        expandActionGroup(newVisibleActions, actionManager, mainActionGroup);
    }

    private void expandActionGroup(List<Action> newVisibleActions, ActionManager actionManager, ActionGroup mainActionGroup) {
        final Action[] children = mainActionGroup.getChildren(null);
        for (final Action action : children) {
            final Presentation presentation = presentationFactory.getPresentation(action);
            final ActionEvent e = new ActionEvent(ActionPlaces.MAIN_MENU, presentation, actionManager, 0);
            action.update(e);
            if (presentation.isVisible()) { // add only visible items
                newVisibleActions.add(action);
            }
            if (action2barItem.containsKey(action)) {
                action2barItem.get(action).update();
            }
        }
    }

    /**
     * Create and add new item in menu.
     */
    private void add(String place, Action action, MenuItemPresentationFactory presentationFactory) {
        Presentation presentation = presentationFactory.getPresentation(action);
        if (action instanceof ActionGroup) {
            ActionGroup group = (ActionGroup)action;
            table.setText(0, menuBarItems.size(), presentation.getText());
            Element element = table.getCellFormatter().getElement(0, menuBarItems.size());
            MenuBarItem item =
                    new MenuBarItem(group, actionManager, presentationFactory, place, element, this, keyBindingAgent, resources.menuCss());

            item.onMouseOut();
            menuBarItems.put(element, item);
            action2barItem.put(group, item);
        } else if (action instanceof CustomComponentAction) {
            Widget widget = ((CustomComponentAction)action).createCustomComponent(presentation);
            table.setWidget(0, menuBarItems.size(), widget);
            Element element = table.getCellFormatter().getElement(0, menuBarItems.size());
            menuBarItems.put(element, null);
        }

    }

    /**
     * Open Popup Menu.
     *
     * @param item
     *         popup menu item.
     */
    public void openPopupMenu(MenuBarItem item) {
        if (lockLayer == null) {
            int top = getAbsoluteTop() + getOffsetHeight();
            lockLayer = new MenuLockLayer(this, top);
        }

        item.openPopupMenu(lockLayer);
    }

    @Override
    public void onActionSelected(Action action) {
        if (action2barItem.containsKey(action)) {
            MenuBarItem item = action2barItem.get(action);
            if (selectedMenuBarItem != null && selectedMenuBarItem != item) {
                selectedMenuBarItem.setNormalState();
                selectedMenuBarItem.closePopupMenu();
            }

            selectedMenuBarItem = item;
        } else {
            lockLayer.close();
            lockLayer = null;
        }
    }

    /**
     * This is visual component.
     * Uses for handling mouse events on MenuBar.
     */
    private class MenuBarTable extends FlexTable {

        public MenuBarTable() {
            sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONMOUSEDOWN);
        }

        @Override
        public void onBrowserEvent(Event event) {
            Element td = getEventTargetCell(event);
            MenuBarItem item = menuBarItems.get(td);

            if (item == null) {
                return;
            }

            switch (DOM.eventGetType(event)) {
                case Event.ONMOUSEOVER:
                    if (selectedMenuBarItem != null && item != selectedMenuBarItem) {
                        if (item.onMouseDown()) {
                            openPopupMenu(item);
                        }
                    }

                    item.onMouseOver();
                    break;

                case Event.ONMOUSEOUT:
                    item.onMouseOut();
                    break;

                case Event.ONMOUSEDOWN:
                    if (item == selectedMenuBarItem) {
                        if (lockLayer != null) {
                            lockLayer.close();
                        }
                        return;
                    }

                    if (item.onMouseDown()) {
                        openPopupMenu(item);
                    }
                    break;

                default:
                    break;
            }

        }
    }

    private static class SeparatorItem extends Composite {
        public SeparatorItem() {
            final FlowPanel widget = new FlowPanel();
            widget.addStyleName(resources.menuCss().panelSeparator());
            Element separator = widget.getElement();
            for (int i = 0; i < 6; i++) {
                separator.appendChild(DOM.createDiv());
            }

            initWidget(widget);
        }
    }
}