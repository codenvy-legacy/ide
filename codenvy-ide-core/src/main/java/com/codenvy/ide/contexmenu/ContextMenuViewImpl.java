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
package com.codenvy.ide.contexmenu;

import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.ActionGroup;
import com.codenvy.ide.api.action.ActionManager;
import com.codenvy.ide.api.action.ActionPlaces;
import com.codenvy.ide.api.action.CustomComponentAction;
import com.codenvy.ide.api.action.DefaultActionGroup;
import com.codenvy.ide.api.action.IdeActions;
import com.codenvy.ide.api.action.Presentation;
import com.codenvy.ide.api.action.Separator;
import com.codenvy.ide.api.keybinding.KeyBindingAgent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.toolbar.ActionButton;
import com.codenvy.ide.toolbar.ActionPopupButton;
import com.codenvy.ide.toolbar.ActionSelectedHandler;
import com.codenvy.ide.toolbar.CloseMenuHandler;
import com.codenvy.ide.toolbar.DelimiterItem;
import com.codenvy.ide.toolbar.MenuLockLayer;
import com.codenvy.ide.toolbar.PopupMenu;
import com.codenvy.ide.toolbar.PresentationFactory;
import com.codenvy.ide.toolbar.ToolbarView;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;

/**
 * The implementation of {@link ToolbarView}
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class ContextMenuViewImpl extends Composite implements ContextMenuView, CloseMenuHandler, ActionSelectedHandler {
    private final DefaultActionGroup  secondaryActions;
    private       ActionManager       actionManager;
    private       KeyBindingAgent     keyBindingAgent;
    private       FlowPanel           panel;
    private       PresentationFactory presentationFactory;
    private       String              place;
    private       PopupMenu           popupMenu;
    private       MenuLockLayer       lockLayer;

    /**
     * Create view.
     *
     * @param actionManager
     * @param keyBindingAgent
     */
    @Inject
    protected ContextMenuViewImpl(ActionManager actionManager, KeyBindingAgent keyBindingAgent) {
        this.actionManager = actionManager;
        this.keyBindingAgent = keyBindingAgent;
        this.presentationFactory = new PresentationFactory();
        this.secondaryActions = new DefaultActionGroup(actionManager);

        this.panel = new FlowPanel();
        initWidget(this.panel);
    }

    /** {@inheritDoc} */
    @Override
    public void setPlace(@Nonnull String place) {
        this.place = place;
    }

    /** {@inheritDoc} */
    @Override
    public void show(int x, int y) {
        updateActions();

        lockLayer = new MenuLockLayer(this);
        popupMenu =
                new PopupMenu(secondaryActions, actionManager, place, presentationFactory, lockLayer, this, keyBindingAgent, "contextMenu");
        lockLayer.add(popupMenu);

        popupMenu.getElement().getStyle().setTop(y, com.google.gwt.dom.client.Style.Unit.PX);
        popupMenu.getElement().getStyle().setLeft(x, com.google.gwt.dom.client.Style.Unit.PX);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        // ok
        // there are no events for now
    }

    /** Update panel with actions. */
    private void updateActions() {
        secondaryActions.removeAll();

        Array<Action> visibleActions = actions();

        fillPanel(visibleActions);
    }

    /** @return available actions for now */
    private Array<Action> actions() {
        Array<Action> newVisibleActions = Collections.createArray();

        final ActionGroup mainActionGroup = (ActionGroup)actionManager.getAction(IdeActions.GROUP_MAIN_CONTEXT_MENU);
        if (mainActionGroup == null) {
            return newVisibleActions;
        }

        final Action[] children = mainActionGroup.getChildren(null);
        for (final Action action : children) {
            final Presentation presentation = presentationFactory.getPresentation(action);
            final ActionEvent e = new ActionEvent(ActionPlaces.MAIN_CONTEXT_MENU, presentation, actionManager, 0);

            action.update(e);
            if (presentation.isVisible()) { // add only visible items
                newVisibleActions.add(action);
            }
        }

        return newVisibleActions;
    }

    /**
     * Add actions into panel.
     *
     * @param actions
     *         actions which need to show
     */
    private void fillPanel(Array<Action> actions) {
        for (int i = 0; i < actions.size(); i++) {
            final Action action = actions.get(i);

            secondaryActions.add(action);

            if (action instanceof Separator) {
                if (i > 0 && i < actions.size() - 1) {
                    panel.add(new DelimiterItem());
                }
            } else if (action instanceof CustomComponentAction) {
                Presentation presentation = presentationFactory.getPresentation(action);
                Widget customComponent = ((CustomComponentAction)action).createCustomComponent(presentation);
                panel.add(customComponent);
            } else if (action instanceof ActionGroup && !(action instanceof CustomComponentAction) && ((ActionGroup)action).isPopup()) {
                ActionPopupButton button =
                        new ActionPopupButton((ActionGroup)action, actionManager, keyBindingAgent, presentationFactory, place);
                panel.add(button);
            } else {
                final ActionButton button = new ActionButton(action, actionManager, presentationFactory.getPresentation(action), place);
                panel.add(button);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseMenu() {
        closePopup();
    }

    /** Close popup. */
    private void closePopup() {
        if (popupMenu != null) {
            popupMenu.removeFromParent();
            popupMenu = null;
        }

        if (lockLayer != null) {
            lockLayer.removeFromParent();
            lockLayer = null;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onActionSelected(Action action) {
        closePopup();
    }
}