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
package com.codenvy.ide.ext.appfog.client.delete;

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
import com.codenvy.ide.resources.model.Project;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for delete application operation.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 */
@Singleton
public class DeleteApplicationPresenter implements DeleteApplicationView.ActionDelegate {
    private DeleteApplicationView      view;
    /** The name of application. */
    private String                     appName;
    /** Name of the server. */
    private String                     serverName;
    private ResourceProvider           resourceProvider;
    private EventBus                   eventBus;
    private AppfogLocalizationConstant constant;
    private LoginPresenter             loginPresenter;
    private AsyncCallback<String>      appDeleteCallback;
    private AppfogClientService        service;
    private NotificationManager        notificationManager;


    /**
     * Create presenter.
     *
     * @param view
     * @param resourceProvider
     * @param eventBus
     * @param constant
     * @param loginPresenter
     * @param service
     * @param notificationManager
     */
    @Inject
    protected DeleteApplicationPresenter(DeleteApplicationView view, ResourceProvider resourceProvider, EventBus eventBus,
                                         AppfogLocalizationConstant constant, LoginPresenter loginPresenter,
                                         AppfogClientService service, NotificationManager notificationManager) {
        this.view = view;
        this.view.setDelegate(this);
        this.resourceProvider = resourceProvider;
        this.eventBus = eventBus;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.notificationManager = notificationManager;
    }

    /** If user is not logged in to AppFog, this handler will be called, after user logged in. */
    private LoggedInHandler appInfoLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            getApplicationInfo();
        }
    };

    /** If user is not logged in to AppFog, this handler will be called, after user logged in. */
    private LoggedInHandler deleteAppLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            deleteApplication(appName, serverName, appDeleteCallback);
        }
    };

    /**
     * Deletes AppFog application.
     *
     * @param appName
     * @param serverName
     * @param callback
     */
    public void deleteApp(String appName, String serverName, AsyncCallback<String> callback) {
        this.appName = appName;
        this.serverName = serverName;
        this.appDeleteCallback = callback;

        // If application name is absent then need to find it
        if (appName == null) {
            getApplicationInfo();
        } else {
            this.appName = appName;
            showDialog(appName);
        }
    }

    /** Get application's name and put it to the field. */
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
                                               appName = result.getName();
                                               showDialog(appName);
                                           }
                                       });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), Notification.Type.ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /**
     * Deletes application.
     *
     * @param appName
     * @param serverName
     * @param callback
     */
    private void deleteApplication(final String appName, String serverName, final AsyncCallback<String> callback) {
        boolean isDeleteServices = view.isDeleteServices();
        String projectId = null;
        final Project project = resourceProvider.getActiveProject();

        if (project != null && project.getPropertyValue("appfog-application") != null &&
            appName.equals(project.getPropertyValue("appfog-application"))) {
            projectId = project.getId();
        }

        try {
            service.deleteApplication(resourceProvider.getVfsInfo().getId(), projectId, appName, serverName, isDeleteServices,
                                      new AppfogAsyncRequestCallback<String>(null, deleteAppLoggedInHandler, null, eventBus, constant,
                                                                             loginPresenter, notificationManager) {
                                          @Override
                                          protected void onSuccess(final String result) {
                                              if (project != null) {
                                                  project.refreshProperties(new AsyncCallback<Project>() {
                                                      @Override
                                                      public void onSuccess(Project project) {
                                                          view.close();
                                                          Notification notification =
                                                                  new Notification(constant.applicationDeletedMsg(appName),
                                                                                   Notification.Type.INFO);
                                                          notificationManager.showNotification(notification);

                                                          callback.onSuccess(result);
                                                      }

                                                      @Override
                                                      public void onFailure(Throwable caught) {
                                                          callback.onFailure(caught);
                                                      }
                                                  });
                                              } else {
                                                  view.close();
                                                  Notification notification = new Notification(constant.applicationDeletedMsg(appName),
                                                                                               Notification.Type.INFO);
                                                  notificationManager.showNotification(notification);

                                                  callback.onSuccess(result);
                                              }
                                          }
                                      });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), Notification.Type.ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onDeleteClicked() {
        deleteApplication(appName, serverName, appDeleteCallback);
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /**
     * Shows dialog.
     *
     * @param appName
     *         application name which need to delete
     */
    private void showDialog(String appName) {
        view.setAskMessage(constant.deleteApplicationQuestion(appName));
        view.setDeleteServices(false);

        view.showDialog();
    }
}