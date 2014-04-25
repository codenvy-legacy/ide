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
import com.google.gwt.user.client.Window;
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