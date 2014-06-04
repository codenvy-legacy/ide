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
package com.codenvy.ide.actions;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.ui.dialogs.ask.Ask;
import com.codenvy.ide.ui.dialogs.ask.AskHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/** @author Andrey Plotnikov */
@Singleton
public class DeleteResourceAction extends Action {
    private final SelectionAgent           selectionAgent;
    private final ResourceProvider         resourceProvider;
    private final NotificationManager      notificationManager;
    private final CoreLocalizationConstant localization;
    private final AnalyticsEventLogger     eventLogger;

    @Inject
    public DeleteResourceAction(SelectionAgent selectionAgent, ResourceProvider resourceProvider, Resources resources,
                                NotificationManager notificationManager, CoreLocalizationConstant localization,
                                AnalyticsEventLogger eventLogger) {
        super("Delete", "Delete resource", null, resources.delete());

        this.selectionAgent = selectionAgent;
        this.resourceProvider = resourceProvider;
        this.notificationManager = notificationManager;
        this.localization = localization;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        Selection<?> s = selectionAgent.getSelection();
        if (s != null && s.getFirstElement() instanceof Resource) {
            Selection<Resource> selection = (Selection<Resource>)s;
            Resource resource = selection.getFirstElement();
            e.getPresentation().setEnabled(resource != null);
        } else
            e.getPresentation().setEnabled(false);
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Delete file");
        Selection<?> s = selectionAgent.getSelection();
        if (s != null && s.getFirstElement() instanceof Resource) {
            Selection<Resource> selection = (Selection<Resource>)s;
            Resource resource = selection.getFirstElement();
            delete(resource);
        }
    }

    /**
     * Delete resource item.
     *
     * @param resource
     *         resource that need to be deleted
     */
    private void delete(@NotNull final Resource resource) {
        Ask ask = new Ask(localization.delete(), localization.deleteResourceQuestion(resource.getName()), new AskHandler() {
            @Override
            public void onOk() {
                resourceProvider.delete(resource, new AsyncCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        // do nothing
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        showErrorMessage(caught);
                    }
                });
            }
        });
        ask.show();
    }

    /**
     * Show error message.
     *
     * @param throwable
     *         exception that happened
     */
    private void showErrorMessage(@NotNull Throwable throwable) {
        Notification notification = new Notification(throwable.getMessage(), ERROR);
        notificationManager.showNotification(notification);
    }
}