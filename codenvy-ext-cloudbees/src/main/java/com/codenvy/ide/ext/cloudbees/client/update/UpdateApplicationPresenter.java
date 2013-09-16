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
package com.codenvy.ide.ext.cloudbees.client.update;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesAsyncRequestCallback;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesClientService;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesLocalizationConstant;
import com.codenvy.ide.ext.cloudbees.client.login.LoggedInHandler;
import com.codenvy.ide.ext.cloudbees.client.login.LoginPresenter;
import com.codenvy.ide.ext.cloudbees.client.marshaller.ApplicationInfoUnmarshaller;
import com.codenvy.ide.ext.cloudbees.shared.ApplicationInfo;
import com.codenvy.ide.ext.jenkins.client.build.BuildApplicationPresenter;
import com.codenvy.ide.ext.jenkins.shared.JobStatus;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Presenter for updating application on CloudBees.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: UpdateApplicationPresenter.java Oct 10, 2011 5:07:40 PM vereshchaka $
 */
@Singleton
public class UpdateApplicationPresenter {
    /** Location of war file (Java only). */
    private String                        warUrl;
    private EventBus                      eventBus;
    private ResourceProvider              resourceProvider;
    private CloudBeesLocalizationConstant constant;
    private LoginPresenter                loginPresenter;
    private CloudBeesClientService        service;
    private BuildApplicationPresenter     buildApplicationPresenter;
    private NotificationManager           notificationManager;
    /** Message for git commit. */
    private String                        updateMessage;
    private Project                       project;
    private String                        appId;
    private String                        appTitle;

    /**
     * Create presenter.
     *
     * @param eventBus
     * @param resourceProvider
     * @param constant
     * @param loginPresenter
     * @param service
     * @param buildApplicationPresenter
     * @param notificationManager
     */
    @Inject
    protected UpdateApplicationPresenter(EventBus eventBus, ResourceProvider resourceProvider, CloudBeesLocalizationConstant constant,
                                         LoginPresenter loginPresenter, CloudBeesClientService service,
                                         BuildApplicationPresenter buildApplicationPresenter, NotificationManager notificationManager) {
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.buildApplicationPresenter = buildApplicationPresenter;
        this.notificationManager = notificationManager;
    }

    /** Updates CloudBees application. */
    public void updateApp(String id, String title) {
        this.project = resourceProvider.getActiveProject();
        appId = id;
        appTitle = title;

        if (appId != null && appTitle != null) {
            askForMessage();
        } else if (project != null) {
            getApplicationInfo();
        }
    }

    /** Shows update message. */
    private void askForMessage() {
        updateMessage = Window.prompt(constant.updateAppAskForMsgText(), "");
        buildApplication();
    }

    /** Builds application. */
    private void buildApplication() {
        buildApplicationPresenter.build(project, new AsyncCallback<JobStatus>() {
            @Override
            public void onSuccess(JobStatus result) {
                if (result.getArtifactUrl() != null) {
                    warUrl = result.getArtifactUrl();
                    doUpdate();
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(UpdateApplicationPresenter.class, "Can not build project on Jenkins", caught);
            }
        });
    }

    /** Updates application. */
    private void doUpdate() {
        String projectId = null;
        if (project != null) {
            projectId = project.getId();
        }

        ApplicationInfoUnmarshaller unmarshaller = new ApplicationInfoUnmarshaller();
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                doUpdate();
            }
        };

        try {
            service.updateApplication(appId, resourceProvider.getVfsId(), projectId, warUrl, updateMessage,
                                      new CloudBeesAsyncRequestCallback<ApplicationInfo>(unmarshaller, loggedInHandler, null, eventBus,
                                                                                         loginPresenter, notificationManager) {
                                          @Override
                                          protected void onSuccess(ApplicationInfo appInfo) {
                                              Notification notification = new Notification(constant.applicationUpdatedMsg(appTitle), ERROR);
                                              notificationManager.showNotification(notification);
                                          }
                                      });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /** Get information about application. */
    protected void getApplicationInfo() {
        String projectId = project.getId();
        ApplicationInfoUnmarshaller unmarshaller = new ApplicationInfoUnmarshaller();
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                getApplicationInfo();
            }
        };

        try {
            service.getApplicationInfo(null, resourceProvider.getVfsId(), projectId,
                                       new CloudBeesAsyncRequestCallback<ApplicationInfo>(unmarshaller, loggedInHandler, null, eventBus,
                                                                                          loginPresenter, notificationManager) {
                                           @Override
                                           protected void onSuccess(ApplicationInfo appInfo) {
                                               appId = appInfo.getId();
                                               appTitle = appInfo.getTitle();
                                               askForMessage();
                                           }
                                       });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }
}