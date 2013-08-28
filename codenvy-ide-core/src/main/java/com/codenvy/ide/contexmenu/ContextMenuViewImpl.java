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
package com.codenvy.ide.contexmenu;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.ui.action.*;
import com.codenvy.ide.api.ui.keybinding.KeyBindingAgent;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.toolbar.*;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

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
    public void setPlace(@NotNull String place) {
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

        popupMenu.getElement().getStyle().setTop(x, com.google.gwt.dom.client.Style.Unit.PX);
        popupMenu.getElement().getStyle().setLeft(y, com.google.gwt.dom.client.Style.Unit.PX);
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

        JsonArray<Action> visibleActions = actions();

        fillPanel(visibleActions);
    }

    /** @return available actions for now */
    private JsonArray<Action> actions() {
        JsonArray<Action> newVisibleActions = JsonCollections.createArray();

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
    private void fillPanel(JsonArray<Action> actions) {
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