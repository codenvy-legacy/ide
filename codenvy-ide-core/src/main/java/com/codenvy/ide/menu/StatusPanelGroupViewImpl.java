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
import com.codenvy.ide.toolbar.ActionSelectedHandler;
import com.codenvy.ide.toolbar.CloseMenuHandler;
import com.codenvy.ide.toolbar.MenuLockLayer;
import com.codenvy.ide.toolbar.PresentationFactory;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements {@link StatusPanelGroupView}
 *
 * @author Oleksii Orel
 */
public class StatusPanelGroupViewImpl extends Composite implements StatusPanelGroupView, CloseMenuHandler, ActionSelectedHandler {
    private final MenuResources resources;

    private final MenuItemPresentationFactory presentationFactory = new MenuItemPresentationFactory();

    private final FlowPanel centerPanel = new FlowPanel();

    private final FlowPanel rightPanel = new FlowPanel();

    private final FlowPanel leftPanel = new FlowPanel();
    /** Panel, which contains top menu. */
    private final FlowPanel rootPanel = new FlowPanel();


    /** Lock layer for displaying popup menus. */
    private MenuLockLayer lockLayer;
    /** List Menu Bar items. */
    private Map<Action, MenuBarItem> action2barItem = new HashMap<>();

    /** Store selected Menu Bar item. */
    private MenuBarItem selectedMenuBarItem;


    private List<Action> rightVisibleActions  = new ArrayList<>();
    private List<Action> centerVisibleActions = new ArrayList<>();
    private List<Action> leftVisibleActions   = new ArrayList<>();
    private List<Action>  newLeftVisibleActions;
    private List<Action>  newCenterVisibleActions;
    private List<Action>  newRightVisibleActions;
    private ActionManager actionManager;

    /** Create new {@link MainMenuViewImpl} */
    @Inject
    public StatusPanelGroupViewImpl(MenuResources resources, ActionManager actionManager) {
        this.resources = resources;
        this.actionManager = actionManager;

        initWidget(rootPanel);

        rootPanel.setStyleName(resources.menuCss().menuBar());

        leftPanel.addStyleName(resources.menuCss().leftPanel());
        leftPanel.getElement().getStyle().setPropertyPx("marginLeft", 1);
        rootPanel.add(leftPanel);

        centerPanel.setStyleName(resources.menuCss().leftPanel());
        rootPanel.add(centerPanel);

        rightPanel.addStyleName(resources.menuCss().rightPanel());
        rightPanel.getElement().getStyle().setPropertyPx("marginRight", 1);
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
        newCenterVisibleActions = new ArrayList<>();
        expandActionGroup(IdeActions.GROUP_CENTER_STATUS_PANEL, newCenterVisibleActions, actionManager);
        if (!newCenterVisibleActions.equals(centerVisibleActions)) {
            centerPanel.clear();
            for (Action action : newCenterVisibleActions) {
                addToPanel(centerPanel, action, presentationFactory);
            }
            centerVisibleActions = newCenterVisibleActions;
        }
        newRightVisibleActions = new ArrayList<>();
        expandActionGroup(IdeActions.GROUP_RIGHT_STATUS_PANEL, newRightVisibleActions, actionManager);
        if (!newRightVisibleActions.equals(rightVisibleActions)) {
            rightPanel.clear();
            for (Action action : newRightVisibleActions) {
                addToPanel(rightPanel, action, presentationFactory);
            }
            rightVisibleActions = newRightVisibleActions;
        }
        newLeftVisibleActions = new ArrayList<>();
        expandActionGroup(IdeActions.GROUP_LEFT_STATUS_PANEL, newLeftVisibleActions, actionManager);
        if (!newLeftVisibleActions.equals(leftVisibleActions)) {
            leftPanel.clear();
            for (Action action : newLeftVisibleActions) {
                addToPanel(leftPanel, action, presentationFactory);
            }
            leftVisibleActions = newLeftVisibleActions;
        }
        centerPanel.getElement().getStyle().setPropertyPx("marginLeft", rootPanel.getOffsetWidth() / 2 - leftPanel.getOffsetWidth() -
                                                                        centerPanel.getOffsetWidth() / 2);
    }

    /**
     * Create a new widget and add it to panel menu.
     */
    private void addToPanel(FlowPanel panel, Action action, PresentationFactory presentationFactory) {
        Presentation presentation = presentationFactory.getPresentation(action);

        if (action instanceof Separator) {
            panel.add(new SeparatorItem(resources.menuCss().panelSeparator()));

            // todo find way to render non custom actions
        } else if (action instanceof CustomComponentAction) {
            CustomComponentAction customComponentAction = (CustomComponentAction)action;
            Widget component = customComponentAction.createCustomComponent(presentation);
            component.addStyleName(resources.menuCss().customComponent());
            panel.add(component);
        }
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

    private static class SeparatorItem extends Composite {
        public SeparatorItem(String styleName) {
            final FlowPanel widget = new FlowPanel();
            widget.addStyleName(styleName);
            Element separator = widget.getElement();
            for (int i = 0; i < 6; i++) {
                separator.appendChild(DOM.createDiv());
            }

            initWidget(widget);
        }
    }
}
