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

import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.api.ui.action.ActionGroup;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.ActionPlaces;
import com.codenvy.ide.api.ui.action.CustomComponentAction;
import com.codenvy.ide.api.ui.action.IdeActions;
import com.codenvy.ide.api.ui.action.Presentation;
import com.codenvy.ide.api.ui.keybinding.KeyBindingAgent;
import com.codenvy.ide.toolbar.ActionSelectedHandler;
import com.codenvy.ide.toolbar.CloseMenuHandler;
import com.codenvy.ide.toolbar.MenuLockLayer;
import com.codenvy.ide.toolbar.PresentationFactory;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.UIObject;
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
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class MainMenuViewImpl extends Composite implements MainMenuView, CloseMenuHandler, ActionSelectedHandler {

    static final MenuResources resources = GWT.create(MenuResources.class);

    static {
        resources.menuCss().ensureInjected();
    }

    private final MenuItemPresentationFactory presentationFactory;
    private       List<Action>                newMenuVisibleActions;
    private       List<Action>                menuVisibleActions;
    private       List<Action>                rightVisibleActions;
    private       List<Action>                newRightVisibleActions;
    private       ActionManager               actionManager;
    private       KeyBindingAgent             keyBindingAgent;
    /** Panel, which contains top menu. */
    private       FlowPanel                   rootPanel;
    /** Lock layer for displaying popup menus. */
    private       MenuLockLayer               lockLayer;
    /** List Menu Bar items. */
    private Map<Element, MenuBarItem> menuBarItems   = new LinkedHashMap<>();
    private Map<Action, MenuBarItem>  action2barItem = new HashMap<>();

    /** Store selected Menu Bar item. */
    private MenuBarItem selectedMenuBarItem;

    /** Working table, cells of which are contains element of Menu. */
    private MenuBarTable table;

    private FlowPanel rightPanel;

    /** Create new {@link MainMenuViewImpl} */
    @Inject
    public MainMenuViewImpl(ActionManager actionManager, KeyBindingAgent keyBindingAgent) {
        this.actionManager = actionManager;
        this.keyBindingAgent = keyBindingAgent;
        rootPanel = new FlowPanel();
        initWidget(rootPanel);
        rootPanel.setStyleName(resources.menuCss().menuBar());

        table = new MenuBarTable();
        table.setStyleName(resources.menuCss().menuBarTable());
        table.setCellPadding(0);
        table.setCellSpacing(0);
        table.getElement().setAttribute("border", "0");
        rootPanel.add(table);
        rightPanel = new FlowPanel();
        rightPanel.addStyleName(resources.menuCss().rightPanel());
        rootPanel.add(rightPanel);

        menuVisibleActions = new ArrayList<>();
        newMenuVisibleActions = new ArrayList<>();
        rightVisibleActions = new ArrayList<>();
        newRightVisibleActions = new ArrayList<>();
        presentationFactory = new MenuItemPresentationFactory();
    }

    /** {@inheritDoc} */
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
            final List<Action> temp = menuVisibleActions;
            menuVisibleActions = newMenuVisibleActions;
            newMenuVisibleActions = temp;

            removeAll();
            for (final Action action : menuVisibleActions) {
                add(ActionPlaces.MAIN_MENU, action, presentationFactory);
            }
        }
        newRightVisibleActions.clear();
        expandActionGroup(IdeActions.GROUP_RIGHT_MAIN_MENU, newRightVisibleActions, actionManager);
        if (!newRightVisibleActions.equals(rightVisibleActions)) {
            final List<Action> temp = rightVisibleActions;
            rightVisibleActions = newRightVisibleActions;
            newRightVisibleActions = temp;

            removeRights();

            for (Action action : rightVisibleActions) {
                add2Right(ActionPlaces.MAIN_MENU, action, presentationFactory);
            }
        }
    }

    private void add2Right(String place, Action action, PresentationFactory presentationFactory) {
        Presentation presentation = presentationFactory.getPresentation(action);
        // todo find way to render non custom actions
        if(action instanceof CustomComponentAction){
            CustomComponentAction customComponentAction = (CustomComponentAction)action;
            Widget component = customComponentAction.createCustomComponent(presentation);
            component.getElement().getStyle().setFloat(Style.Float.RIGHT);
            rightPanel.add(component);
        }
    }

    private void removeRights() {
        rightPanel.clear();
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
        }
    }

    /**
     * Create and add new item in menu.
     */
    private void add(String place, Action action, MenuItemPresentationFactory presentationFactory) {
        Presentation presentation = presentationFactory.getPresentation(action);
        if(action instanceof ActionGroup) {
            ActionGroup group = (ActionGroup)action;
            table.setText(0, menuBarItems.size(), presentation.getText());
            Element element = table.getCellFormatter().getElement(0, menuBarItems.size());
            UIObject.ensureDebugId(element, place + "/" + actionManager.getId(group));
            MenuBarItem item =
                    new MenuBarItem(group, actionManager, presentationFactory, place, element, this, keyBindingAgent, resources.menuCss());

            item.onMouseOut();
            menuBarItems.put(element, item);
            action2barItem.put(group, item);
        } else if (action instanceof CustomComponentAction) {
            Widget widget = ((CustomComponentAction)action).createCustomComponent(presentation);
            table.setWidget(0,menuBarItems.size(), widget);
            Element element = table.getCellFormatter().getElement(0, menuBarItems.size());
            menuBarItems.put(element, null);
        }

    }

    /**
     * Open Popup Menu.
     *
     * @param item
     *         - popup menu item.
     */
    public void openPopupMenu(MenuBarItem item) {
        if (lockLayer == null) {
            int top = getAbsoluteTop() + getOffsetHeight();
            lockLayer = new MenuLockLayer(this, top);
        }

        item.openPopupMenu(lockLayer);
    }

    /** {@inheritDoc} */
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

}