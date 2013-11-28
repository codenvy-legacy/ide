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
package com.codenvy.ide.ext.appfog.client.update;

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
import com.codenvy.ide.extension.builder.client.event.BuildProjectEvent;
import com.codenvy.ide.extension.builder.client.event.ProjectBuiltEvent;
import com.codenvy.ide.extension.builder.client.event.ProjectBuiltHandler;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;

/**
 * Presenter for update application operation.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 */
@Singleton
public class UpdateApplicationPresenter implements ProjectBuiltHandler {
    /** Location of war file (Java only). */
    private String                     warUrl;
    private EventBus                   eventBus;
    private ResourceProvider           resourceProvider;
    private AppfogLocalizationConstant constant;
    private HandlerRegistration        projectBuildHandler;
    private LoginPresenter             loginPresenter;
    private AppfogClientService        service;
    private NotificationManager        notificationManager;

    /**
     * Create presenter.
     *
     * @param eventBus
     * @param resourceProvider
     * @param constant
     * @param loginPresenter
     * @param service
     * @param notificationManager
     */
    @Inject
    protected UpdateApplicationPresenter(EventBus eventBus, ResourceProvider resourceProvider, AppfogLocalizationConstant constant,
                                         LoginPresenter loginPresenter, AppfogClientService service,
                                         NotificationManager notificationManager) {
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.notificationManager = notificationManager;
    }

    /** If user is not logged in to AppFog, this handler will be called, after user logged in. */
    LoggedInHandler loggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            updateApplication();
        }
    };

    /** Updates AppFog application. */
    public void updateApp() {
        validateData();
    }

    /** Updates application. */
    private void updateApplication() {
        final String projectId = resourceProvider.getActiveProject().getId();

        try {
            service.updateApplication(resourceProvider.getVfsId(), projectId, null, null, warUrl,
                                      new AppfogAsyncRequestCallback<String>(null, loggedInHandler, null, eventBus, constant,
                                                                             loginPresenter, notificationManager) {
                                          @Override
                                          protected void onSuccess(String result) {
                                              AppFogApplicationUnmarshaller unmarshaller = new AppFogApplicationUnmarshaller();

                                              try {
                                                  service.getApplicationInfo(resourceProvider.getVfsId(), projectId, null, null,
                                                                             new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller,
                                                                                                                               null, null,
                                                                                                                               eventBus,
                                                                                                                               constant,
                                                                                                                               loginPresenter,
                                                                                                                               notificationManager) {
                                                                                 @Override
                                                                                 protected void onSuccess(AppfogApplication result) {
                                                                                     String message = constant.updateApplicationSuccess(
                                                                                             result.getName());
                                                                                     Notification notification =
                                                                                             new Notification(message, INFO);
                                                                                     notificationManager.showNotification(notification);
                                                                                 }
                                                                             });
                                              } catch (RequestException e) {
                                                  eventBus.fireEvent(new ExceptionThrownEvent(e));
                                                  Notification notification = new Notification(e.getMessage(), ERROR);
                                                  notificationManager.showNotification(notification);
                                              }
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
    public void onProjectBuilt(ProjectBuiltEvent event) {
        projectBuildHandler.removeHandler();
        if (event.getBuildStatus().getDownloadUrl() != null) {
            warUrl = event.getBuildStatus().getDownloadUrl();
            updateApplication();
        }
    }

    /** If user is not logged in to AppFog, this handler will be called, after user logged in. */
    private LoggedInHandler validateHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            validateData();
        }
    };

    /** Validate action before building project. */
    private void validateData() {
        final String projectId = resourceProvider.getActiveProject().getId();

        try {
            service.validateAction("update", null, null, null, null, resourceProvider.getVfsId(), projectId, 0, 0, false,
                                   new AppfogAsyncRequestCallback<String>(null, validateHandler, null, eventBus, constant,
                                                                          loginPresenter, notificationManager) {
                                       @Override
                                       protected void onSuccess(String result) {
                                           isBuildApplication();
                                       }
                                   });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /** Check, is work directory contains <code>pom.xml</code> file. */
    private void isBuildApplication() {
        final Project project = resourceProvider.getActiveProject();

        JsonArray<Resource> children = project.getChildren();

        for (int i = 0; i < children.size(); i++) {
            Resource child = children.get(i);
            if (child.isFile() && "pom.xml".equals(child.getName())) {
                buildApplication();
                return;
            }
        }
        warUrl = null;
        updateApplication();
    }

    /** Builds application. */
    private void buildApplication() {
        // TODO IDEX-57
        // Replace EventBus Events with direct method calls and DI
        projectBuildHandler = eventBus.addHandler(ProjectBuiltEvent.TYPE, this);
        eventBus.fireEvent(new BuildProjectEvent());
    }
}