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
import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectReference;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.event.RefreshProjectTreeEvent;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.ui.dialogs.ask.Ask;
import com.codenvy.ide.ui.dialogs.ask.AskHandler;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/** @author Andrey Plotnikov */
@Singleton
public class DeleteResourceAction extends Action {
    private final SelectionAgent           selectionAgent;
    private final NotificationManager      notificationManager;
    private final CoreLocalizationConstant localization;
    private final AnalyticsEventLogger     eventLogger;
    private final ProjectServiceClient     projectServiceClient;
    private final EventBus                 eventBus;

    @Inject
    public DeleteResourceAction(SelectionAgent selectionAgent,
                                Resources resources,
                                NotificationManager notificationManager,
                                CoreLocalizationConstant localization,
                                AnalyticsEventLogger eventLogger,
                                ProjectServiceClient projectServiceClient,
                                EventBus eventBus) {
        super("Delete", "Delete resource", null, resources.delete());

        this.selectionAgent = selectionAgent;
        this.notificationManager = notificationManager;
        this.localization = localization;
        this.eventLogger = eventLogger;
        this.projectServiceClient = projectServiceClient;
        this.eventBus = eventBus;
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        Selection<?> selection = selectionAgent.getSelection();
        if (selection != null) {
            Object firstElement = selection.getFirstElement();
            e.getPresentation().setEnabled(firstElement instanceof ItemReference ||
                                           firstElement instanceof ProjectReference ||
                                           firstElement instanceof ProjectDescriptor);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Delete file");
        Selection<?> selection = selectionAgent.getSelection();
        if (selection != null) {
            Object firstElement = selection.getFirstElement();
            String name = null;
            String path = null;
            if (firstElement instanceof ItemReference) {
                ItemReference item = ((Selection<ItemReference>)selection).getFirstElement();
                name = item.getName();
                path = item.getPath();
            } else if (firstElement instanceof ProjectReference) {
                ProjectReference item = ((Selection<ProjectReference>)selection).getFirstElement();
                name = item.getName();
                path = name;
            } else if (firstElement instanceof ProjectDescriptor) {
                ProjectDescriptor item = ((Selection<ProjectDescriptor>)selection).getFirstElement();
                name = item.getName();
                path = item.getPath();
            }
            if (name != null && path != null) {
                delete(name, path);
            }
        }
    }

    /**
     * Delete item by the specified path.
     *
     * @param itemName
     *         name of the item to delete
     * @param itemPath
     *         path of the item to delete
     */
    private void delete(@NotNull final String itemName, final String itemPath) {
        Ask ask = new Ask(localization.delete(), localization.deleteResourceQuestion(itemName), new AskHandler() {
            @Override
            public void onOk() {
                projectServiceClient.delete(itemPath, new AsyncRequestCallback<Void>() {
                    @Override
                    protected void onSuccess(Void result) {
                        eventBus.fireEvent(new RefreshProjectTreeEvent());
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        notificationManager.showNotification(new Notification(exception.getMessage(), ERROR));
                    }
                });
            }
        });
        ask.show();
    }
}
