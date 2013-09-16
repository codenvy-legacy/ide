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
package com.codenvy.ide.ext.cloudbees.client.info;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesAsyncRequestCallback;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesClientService;
import com.codenvy.ide.ext.cloudbees.client.login.LoggedInHandler;
import com.codenvy.ide.ext.cloudbees.client.login.LoginPresenter;
import com.codenvy.ide.ext.cloudbees.client.marshaller.ApplicationInfoUnmarshaller;
import com.codenvy.ide.ext.cloudbees.shared.ApplicationInfo;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Presenter for showing application info.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ApplicationInfoPresenter.java Jun 30, 2011 5:02:31 PM vereshchaka $
 */
@Singleton
public class ApplicationInfoPresenter implements ApplicationInfoView.ActionDelegate {
    private ApplicationInfoView    view;
    private EventBus               eventBus;
    private ResourceProvider       resourceProvider;
    private LoginPresenter         loginPresenter;
    private CloudBeesClientService service;
    private NotificationManager    notificationManager;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param resourceProvider
     * @param loginPresenter
     * @param service
     */
    @Inject
    protected ApplicationInfoPresenter(ApplicationInfoView view, EventBus eventBus, ResourceProvider resourceProvider,
                                       LoginPresenter loginPresenter, CloudBeesClientService service,
                                       NotificationManager notificationManager) {
        this.view = view;
        this.view.setDelegate(this);
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.notificationManager = notificationManager;
    }

    /** {@inheritDoc} */
    @Override
    public void onOKClicked() {
        view.close();
    }

    /** Show dialog. */
    public void showDialog() {
        showApplicationInfo(resourceProvider.getActiveProject().getId());
    }

    /** Show dialog. */
    public void showDialog(ApplicationInfo appInfo) {
        showAppInfo(appInfo);
    }

    /**
     * Gets application info from current project.
     *
     * @param projectId
     */
    private void showApplicationInfo(final String projectId) {
        ApplicationInfoUnmarshaller unmarshaller = new ApplicationInfoUnmarshaller();
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                showApplicationInfo(projectId);
            }
        };

        try {
            service.getApplicationInfo(null, resourceProvider.getVfsId(), projectId,
                                       new CloudBeesAsyncRequestCallback<ApplicationInfo>(unmarshaller, loggedInHandler, null, eventBus,
                                                                                          loginPresenter, notificationManager) {
                                           @Override
                                           protected void onSuccess(ApplicationInfo result) {
                                               showAppInfo(result);
                                           }
                                       });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /**
     * Shows application info.
     *
     * @param appInfo
     */
    private void showAppInfo(ApplicationInfo appInfo) {
        view.setAppId(appInfo.getId());
        view.setAppTitle(appInfo.getTitle());
        view.setServerPool(appInfo.getServerPool());
        view.setAppStatus(appInfo.getStatus());
        view.setAppContainer(appInfo.getContainer());
        view.setIdleTimeout(appInfo.getIdleTimeout());
        view.setMaxMemory(appInfo.getMaxMemory());
        view.setSecurityMode(appInfo.getSecurityMode());
        view.setClusterSize(appInfo.getClusterSize());
        view.setUrl(appInfo.getUrl());

        view.showDialog();
    }
}