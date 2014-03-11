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
package com.codenvy.ide.actions;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/** @author Andrey Plotnikov */
@Singleton
public class DeleteResourceAction extends Action {
    private SelectionAgent           selectionAgent;
    private ResourceProvider         resourceProvider;
    private NotificationManager      notificationManager;
    private CoreLocalizationConstant localization;

    @Inject
    public DeleteResourceAction(SelectionAgent selectionAgent,
                                ResourceProvider resourceProvider,
                                Resources resources,
                                NotificationManager notificationManager, CoreLocalizationConstant localization) {
        super("Delete", "Delete resource", resources.delete());

        this.selectionAgent = selectionAgent;
        this.resourceProvider = resourceProvider;
        this.notificationManager = notificationManager;
        this.localization = localization;
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        Project activeProject = resourceProvider.getActiveProject();
        Selection<Resource> selection = (Selection<Resource>)selectionAgent.getSelection();
        if (activeProject != null && selection != null) {
            Resource resource = selection.getFirstElement();
            e.getPresentation().setEnabled(resource != null);
        } else if (activeProject == null && selection != null) {
            Resource resource = selection.getFirstElement();
            e.getPresentation().setEnabled(resource != null);
        } else {
            e.getPresentation().setEnabled(false);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        Selection<Resource> selection = (Selection<Resource>)selectionAgent.getSelection();
        Resource resource = selection.getFirstElement();
        delete(resource);
    }

    /**
     * Delete resource item.
     *
     * @param resource
     *         resource that need to be deleted
     */
    private void delete(@NotNull Resource resource) {
        boolean isDelete = Window.confirm(localization.deleteResourceQuestion(resource.getName()));
        if (isDelete) {
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