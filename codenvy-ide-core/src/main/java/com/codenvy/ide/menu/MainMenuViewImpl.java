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
import com.codenvy.ide.api.ui.action.IdeActions;
import com.codenvy.ide.api.ui.action.Presentation;
import com.codenvy.ide.api.ui.keybinding.KeyBindingAgent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.toolbar.ActionSelectedHandler;
import com.codenvy.ide.toolbar.CloseMenuHandler;
import com.codenvy.ide.toolbar.MenuLockLayer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.UIObject;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.LinkedHashMap;
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
    private       Array<Action>               newVisibleActions;
    private       Array<Action>               visibleActions;
    private       ActionManager               actionManager;
    private       KeyBindingAgent             keyBindingAgent;
    /** Panel, which contains top menu. */
    private       AbsolutePanel               absolutePanel;
    /** Lock layer for displaying popup menus. */
    private       MenuLockLayer               lockLayer;
    /** List Menu Bar items. */
    private Map<Element, MenuBarItem> menuBarItems   = new LinkedHashMap<>();
    private Map<Action, MenuBarItem>  action2barItem = new HashMap<>();
    /** Store selected Menu Bar item. */
    private MenuBarItem  selectedMenuBarItem;
    /** Working table, cells of which are contains element of Menu. */
    private MenuBarTable table;

    private Timer timer = new Timer() {
        @Override
        public void run() {
            updateMenuActions();
            schedule(1000);
        }
    };

    /** Create new {@link MainMenuViewImpl} */
    @Inject
    public MainMenuViewImpl(ActionManager actionManager, KeyBindingAgent keyBindingAgent) {
        this.actionManager = actionManager;
        this.keyBindingAgent = keyBindingAgent;
        absolutePanel = new AbsolutePanel();
        initWidget(absolutePanel);
        absolutePanel.setStyleName(resources.menuCss().menuBar());

        table = new MenuBarTable();
        table.setStyleName(resources.menuCss().menuBarTable());
        table.setCellPadding(0);
        table.setCellSpacing(0);
        DOM.setElementAttribute(table.getElement(), "border", "0");
        absolutePanel.add(table);
        visibleActions = Collections.createArray();
        newVisibleActions = Collections.createArray();
        presentationFactory = new MenuItemPresentationFactory();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        // ok
        // there are no events for now
        timer.schedule(2000);
    }

    /** Handle closing of all popup windows. */
    public void onCloseMenu() {
        selectedMenuBarItem.setNormalState();
        selectedMenuBarItem = null;
        lockLayer = null;
    }

    void updateMenuActions() {
        newVisibleActions.clear();

//        if (!myDisabled) {
//            DataContext dataContext = ((DataManagerImpl)myDataManager).getDataContextTest(this);
        expandActionGroup(newVisibleActions, actionManager);
//        }

        if (!newVisibleActions.equals(visibleActions)) {
            // should rebuild UI
            final boolean changeBarVisibility = newVisibleActions.isEmpty() || visibleActions.isEmpty();

            final Array<Action> temp = visibleActions;
            visibleActions = newVisibleActions;
            newVisibleActions = temp;

            removeAll();
            final boolean enableMnemonics = false; //!UISettings.getInstance().DISABLE_MNEMONICS;
            for (final Action action : visibleActions.asIterable()) {
                add(ActionPlaces.MAIN_MENU, (ActionGroup)action, presentationFactory);
            }
        }
    }

    private void removeAll() {
        table.clear();
        menuBarItems.clear();
        action2barItem.clear();
    }

    private void expandActionGroup(final Array<Action> newVisibleActions, ActionManager actionManager) {
        final ActionGroup mainActionGroup = (ActionGroup)actionManager.getAction(IdeActions.GROUP_MAIN_MENU);
        //CustomActionsSchema.getInstance().getCorrectedAction(IdeActions.GROUP_MAIN_MENU);
        if (mainActionGroup == null) return;
        final Action[] children = mainActionGroup.getChildren(null);
        for (final Action action : children) {
            if (!(action instanceof ActionGroup)) {
                continue;
            }
            final Presentation presentation = presentationFactory.getPresentation(action);
            final ActionEvent e = new ActionEvent(ActionPlaces.MAIN_MENU, presentation, actionManager, 0);
//            e.setInjectedContext(action.isInInjectedContext());
            action.update(e);
            if (presentation.isVisible()) { // add only visible items
                newVisibleActions.add(action);
            }
        }
    }

    /**
     * Create and add new item in menu.
     *
     * @return new instance of MenuBarItem which extends MenuItem
     */
    private MenuBarItem add(String place, ActionGroup group, MenuItemPresentationFactory presentationFactory) {
        table.setText(0, menuBarItems.size(), presentationFactory.getPresentation(group).getText());
        Element element = table.getCellFormatter().getElement(0, menuBarItems.size());
        UIObject.ensureDebugId(element, place + "/" + actionManager.getId(group));
        MenuBarItem item =
                new MenuBarItem(group, actionManager, presentationFactory, place, element, this, keyBindingAgent, resources.menuCss());

        item.onMouseOut();
        menuBarItems.put(element, item);
        action2barItem.put(group, item);
        return item;
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