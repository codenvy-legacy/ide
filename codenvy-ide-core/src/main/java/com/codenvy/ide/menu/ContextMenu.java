/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.ide.menu;

import elemental.dom.Element;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.EventRemover;
import elemental.events.MouseEvent;

import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.ActionGroup;
import com.codenvy.ide.api.action.ActionManager;
import com.codenvy.ide.api.action.ActionPlaces;
import com.codenvy.ide.api.action.DefaultActionGroup;
import com.codenvy.ide.api.action.IdeActions;
import com.codenvy.ide.api.action.Presentation;
import com.codenvy.ide.api.keybinding.KeyBindingAgent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.toolbar.ActionSelectedHandler;
import com.codenvy.ide.toolbar.CloseMenuHandler;
import com.codenvy.ide.toolbar.MenuLockLayer;
import com.codenvy.ide.toolbar.PopupMenu;
import com.codenvy.ide.toolbar.PresentationFactory;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Vitaliy Guliy
 */
@Singleton
public class ContextMenu implements CloseMenuHandler, ActionSelectedHandler {

    private final ActionManager   actionManager;
    private final KeyBindingAgent keyBindingAgent;

    private final PresentationFactory presentationFactory;
    private final DefaultActionGroup  actions;

    private PopupMenu     popupMenu;
    private MenuLockLayer lockLayer;

    private static final String place = ActionPlaces.MAIN_CONTEXT_MENU;


    @Inject
    public ContextMenu(ActionManager actionManager, KeyBindingAgent keyBindingAgent) {
        this.actionManager = actionManager;
        this.keyBindingAgent = keyBindingAgent;

        presentationFactory = new PresentationFactory();
        actions = new DefaultActionGroup(actionManager);

        blockBrowserMenu();
    }

    private void blockBrowserMenu() {
        com.google.gwt.user.client.Event.sinkEvents(RootPanel.getBodyElement(), com.google.gwt.user.client.Event.ONCONTEXTMENU);
        DOM.setEventListener(RootPanel.getBodyElement(), new com.google.gwt.user.client.EventListener() {
            @Override
            public void onBrowserEvent(com.google.gwt.user.client.Event event) {
                if (com.google.gwt.user.client.Event.ONCONTEXTMENU == event.getTypeInt()) {
                    event.stopPropagation();
                    event.preventDefault();
                    return;
                }
            }
        });
    }

    private final native void trace(String msg) /*-{
        console.log(msg);
    }-*/;

    public void show(int x, int y) {
        hide();
        updateActions();

        lockLayer = new MenuLockLayer(this);
        popupMenu =
                new PopupMenu(actions, actionManager, place, presentationFactory, lockLayer, this, keyBindingAgent, "contextMenu");
        lockLayer.add(popupMenu);

        popupMenu.getElement().getStyle().setTop(y, com.google.gwt.dom.client.Style.Unit.PX);
        popupMenu.getElement().getStyle().setLeft(x, com.google.gwt.dom.client.Style.Unit.PX);
    }

    /** Update action list. */
    private void updateActions() {
        actions.removeAll();

        final ActionGroup mainActionGroup = (ActionGroup)actionManager.getAction(IdeActions.GROUP_MAIN_CONTEXT_MENU);
        if (mainActionGroup == null) {
            return;
        }

        final Action[] children = mainActionGroup.getChildren(null);
        for (final Action action : children) {
            final Presentation presentation = presentationFactory.getPresentation(action);
            final ActionEvent e = new ActionEvent(ActionPlaces.MAIN_CONTEXT_MENU, presentation, actionManager, 0);

            action.update(e);
            if (presentation.isVisible()) {
                actions.add(action);
            }
        }
    }

    @Override
    public void onActionSelected(Action action) {
        hide();
    }

    @Override
    public void onCloseMenu() {
        hide();
    }

    public void hide() {
        if (popupMenu != null) {
            popupMenu.removeFromParent();
            popupMenu = null;
        }

        if (lockLayer != null) {
            lockLayer.removeFromParent();
            lockLayer = null;
        }
    }

}
