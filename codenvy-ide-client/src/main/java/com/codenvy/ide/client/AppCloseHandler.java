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
import com.codenvy.ide.api.action.Presentation;
import com.codenvy.ide.api.event.WindowActionEvent;
import com.codenvy.ide.api.event.WindowActionHandler;
import com.codenvy.ide.toolbar.PresentationFactory;
import com.google.web.bindery.event.shared.EventBus;

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
    public AppCloseHandler(ActionManager actionManager, EventBus eventBus) {
        this.actionManager = actionManager;

        eventBus.addHandler(WindowActionEvent.TYPE, new WindowActionHandler() {
            @Override
            public void onWindowClosing(final WindowActionEvent event) {
                String message = performActions();
                if (message != null) {
                    event.setMessage(message);
                }
            }

            @Override
            public void onWindowClosed(WindowActionEvent event) {
            }
        });
    }

    /**
     * Register actions for perform before closing of application
     */
    public void performBeforeClose(List<Action> actions) {
        this.actions.addAll(actions);
    }

    /**
     * Performs registered action
     *
     * @return null if all action is successfully performed
     * or string with message if some action sent cancel closing of application.
     */
    private String performActions() {
        String cancelMessage = null;
        for (Action action : actions) {
            com.codenvy.ide.api.action.Action ideAction = actionManager.getAction(action.getId());

            if (ideAction == null) {
                continue;
            }

            Presentation presentation = new PresentationFactory().getPresentation(ideAction);
            AppCloseActionEvent e = new AppCloseActionEvent("", presentation, actionManager, 0, action.getProperties());
            ideAction.update(e);

            if (!presentation.isEnabled() || !presentation.isVisible()) {
                continue;
            }

            ideAction.actionPerformed(e);

            if (e.getCancelMessage() != null) {
                cancelMessage = e.getCancelMessage();
            }
        }

        return cancelMessage;
    }
}
