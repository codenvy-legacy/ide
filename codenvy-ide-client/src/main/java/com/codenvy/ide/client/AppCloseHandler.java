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
package com.codenvy.ide.client;

import com.codenvy.api.factory.dto.Action;
import com.codenvy.ide.api.action.ActionManager;
import com.codenvy.ide.api.action.AppCloseActionEvent;
import com.codenvy.ide.toolbar.PresentationFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Handler that performs actions before closing of application
 *
 * @author Sergii Leschenko
 */
public class AppCloseHandler {
    private final ActionManager actionManager;
    private List<Action> actions = new ArrayList<>();

    @Inject
    public AppCloseHandler(ActionManager actionManager) {
        this.actionManager = actionManager;
        addUnloadHandler();
    }

    /**
     * Register actions for perform before closing of application
     */
    public void performBeforeClose(List<Action> actions) {
        this.actions.addAll(actions);
    }

    /**
     * Adds before unload event listener.
     */
    private native void addUnloadHandler() /*-{
        var instance = this;

        $wnd.onbeforeunload = function () {
            return instance.@com.codenvy.ide.client.AppCloseHandler::performActions()();
        }
    }-*/;

    /**
     * Performs registered action
     *
     * @return null if all action is successfully performed
     * or string with message if some action sent cancel closing of application.
     */
    private String performActions() {
        for (Action action : actions) {
            com.codenvy.ide.api.action.Action ideAction = actionManager.getAction(action.getId());
            if (ideAction != null) {
                AppCloseActionEvent e = new AppCloseActionEvent("", new PresentationFactory().getPresentation(ideAction), actionManager,
                                                                0, action.getProperties());
                ideAction.update(e);
                if (e.getPresentation().isEnabled() && e.getPresentation().isVisible()) {
                    ideAction.actionPerformed(e);

                    if (e.getCancelMessage() != null) {
                        return e.getCancelMessage();
                    }
                }
            }
        }

        return null;
    }
}
