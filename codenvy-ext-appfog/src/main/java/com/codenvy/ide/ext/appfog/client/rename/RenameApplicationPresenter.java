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
package com.codenvy.ide.ext.appfog.client.rename;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.appfog.client.AppfogAsyncRequestCallback;
import com.codenvy.ide.ext.appfog.client.AppfogClientService;
import com.codenvy.ide.ext.appfog.client.AppfogLocalizationConstant;
import com.codenvy.ide.ext.appfog.client.login.LoggedInHandler;
import com.codenvy.ide.ext.appfog.client.login.LoginPresenter;
import com.codenvy.ide.ext.appfog.client.marshaller.AppFogApplicationUnmarshaller;
import com.codenvy.ide.ext.appfog.shared.AppfogApplication;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Presenter for rename operation with application.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 */
@Singleton
public class RenameApplicationPresenter implements RenameApplicationView.ActionDelegate {
    private RenameApplicationView      view;
    private EventBus                   eventBus;
    private ResourceProvider           resourceProvider;
    /** The name of application. */
    private String                     applicationName;
    private AppfogLocalizationConstant constant;
    private LoginPresenter             loginPresenter;
    private AppfogClientService        service;
    private NotificationManager        notificationManager;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param resourceProvider
     * @param applicationName
     * @param constant
     * @param loginPresenter
     * @param service
     * @param notificationManager
     */
    @Inject
    protected RenameApplicationPresenter(RenameApplicationView view, EventBus eventBus, ResourceProvider resourceProvider,
                                         String applicationName, AppfogLocalizationConstant constant, LoginPresenter loginPresenter,
                                         AppfogClientService service, NotificationManager notificationManager) {
        this.view = view;
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.applicationName = applicationName;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.notificationManager = notificationManager;
    }

    /** {@inheritDoc} */
    @Override
    public void onNameChanged() {
        String newName = view.getName();
        boolean enable = !applicationName.equals(newName) && newName != null && !newName.isEmpty();
        view.setEnableRenameButton(enable);
    }

    /** {@inheritDoc} */
    @Override
    public void onRenameClicked() {
        renameApplication();
    }

    /** If user is not logged in to AppFog, this handler will be called, after user logged in. */
    private LoggedInHandler renameAppLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            renameApplication();
        }
    };

    /** Renames application. */
    private void renameApplication() {
        final String newName = view.getName();
        String projectId = resourceProvider.getActiveProject().getId();

        try {
            service.renameApplication(resourceProvider.getVfsInfo().getId(), projectId, applicationName, null, newName,
                                      new AppfogAsyncRequestCallback<String>(null, renameAppLoggedInHandler, null, eventBus, constant,
                                                                             loginPresenter, notificationManager) {
                                          @Override
                                          protected void onSuccess(String result) {
                                              view.close();
                                              Notification notification = new Notification(constant.renameApplicationSuccess(
                                                      applicationName, newName), ERROR);
                                              notificationManager.showNotification(notification);
                                          }
                                      });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** Shows dialog. */
    public void showDialog() {
        view.setName(applicationName);
        view.selectValueInRenameField();
        view.setEnableRenameButton(false);

        getApplicationInfo();
    }

    /** If user is not logged in to AppFog, this handler will be called, after user logged in. */
    private LoggedInHandler appInfoLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            getApplicationInfo();
        }
    };

    /** Get the application's information. */
    private void getApplicationInfo() {
        String projectId = resourceProvider.getActiveProject().getId();
        AppFogApplicationUnmarshaller unmarshaller = new AppFogApplicationUnmarshaller();

        try {
            service.getApplicationInfo(resourceProvider.getVfsInfo().getId(), projectId, null, null,
                                       new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller, appInfoLoggedInHandler, null,
                                                                                         eventBus, constant, loginPresenter,
                                                                                         notificationManager) {
                                           @Override
                                           protected void onSuccess(AppfogApplication result) {
                                               applicationName = result.getName();
                                               view.showDialog();
                                           }
                                       });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }
}