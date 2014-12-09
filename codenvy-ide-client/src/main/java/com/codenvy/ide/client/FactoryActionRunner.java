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
package com.codenvy.ide.client;

import com.codenvy.api.factory.dto.Action;
import com.codenvy.api.factory.dto.Factory;
import com.codenvy.api.factory.dto.Ide;
import com.codenvy.api.factory.dto.OnProjectOpened;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.ActionManager;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.toolbar.PresentationFactory;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * @author Sergii Leschenko
 */
@Singleton
public class FactoryActionRunner {

    private final EventBus      eventBus;
    private final ActionManager actionManager;

    private HandlerRegistration openProjectHandler;

    @Inject
    public FactoryActionRunner(EventBus eventBus,
                               ActionManager actionManager) {
        this.eventBus = eventBus;
        this.actionManager = actionManager;
    }

    public void runActions(final Factory factory) {
        if (factory.getIde() != null) {
            final Ide ide = factory.getIde();
            if (ide.getOnAppLoaded() != null && ide.getOnAppLoaded().getActions() != null) {
                startActions(factory.getIde().getOnAppLoaded().getActions());
            }

            if (ide.getOnProjectOpened() != null) {
                final OnProjectOpened onProjectOpened = ide.getOnProjectOpened();

                openProjectHandler = eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
                    @Override
                    public void onProjectOpened(ProjectActionEvent event) {
                        if (onProjectOpened.getActions() != null) {
                            startActions(onProjectOpened.getActions());
                        }

                        openProjectHandler.removeHandler();
                    }

                    @Override
                    public void onProjectClosed(ProjectActionEvent event) {

                    }
                });
            }
        }
    }

    private void startActions(List<Action> actions) {
        for (Action action : actions) {
            com.codenvy.ide.api.action.Action ideAction = actionManager.getAction(action.getId());
            if (ideAction != null) {
                ActionEvent e = new ActionEvent("", new PresentationFactory().getPresentation(ideAction), actionManager, 0,
                                                action.getProperties());
                ideAction.update(e);
                if (e.getPresentation().isEnabled() && e.getPresentation().isVisible()) {
                    ideAction.actionPerformed(e);
                }
            }
        }
    }
}
