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
public class FactoryActionManager {

    private final EventBus            eventBus;
    private final ActionManager       actionManager;
    private final AppClosedSubscriber appClosedSubscriber;

    private HandlerRegistration openProjectHandler;

    @Inject
    public FactoryActionManager(EventBus eventBus,
                                ActionManager actionManager,
                                AppClosedSubscriber appClosedSubscriber) {
        this.eventBus = eventBus;
        this.actionManager = actionManager;
        this.appClosedSubscriber = appClosedSubscriber;
    }

    public void processActions(final Factory factory) {
        if (factory.getIde() != null) {
            final Ide ide = factory.getIde();

            if (ide.getOnAppClosed() != null && ide.getOnAppClosed().getActions() != null) {
                appClosedSubscriber.startBeforeUnload(ide.getOnAppClosed().getActions());
            }

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
