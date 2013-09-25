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
package com.codenvy.ide.ext.cloudbees.client.apps;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesAsyncRequestCallback;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesClientService;
import com.codenvy.ide.ext.cloudbees.client.delete.DeleteApplicationPresenter;
import com.codenvy.ide.ext.cloudbees.client.info.ApplicationInfoPresenter;
import com.codenvy.ide.ext.cloudbees.client.login.LoggedInHandler;
import com.codenvy.ide.ext.cloudbees.client.login.LoginPresenter;
import com.codenvy.ide.ext.cloudbees.client.marshaller.ApplicationListUnmarshaller;
import com.codenvy.ide.ext.cloudbees.shared.ApplicationInfo;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * The applications presenter manager CloudBees application.
 * The presenter can delete application and show information about it.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Sep 21, 2011 evgen $
 */
@Singleton
public class ApplicationsPresenter implements ApplicationsView.ActionDelegate {
    private ApplicationsView           view;
    private EventBus                   eventBus;
    private LoginPresenter             loginPresenter;
    private CloudBeesClientService     service;
    private ApplicationInfoPresenter   applicationInfoPresenter;
    private DeleteApplicationPresenter deleteApplicationPresenter;
    private NotificationManager        notificationManager;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param loginPresenter
     * @param service
     * @param applicationInfoPresenter
     * @param deleteApplicationPresenter
     * @param notificationManager
     */
    @Inject
    protected ApplicationsPresenter(ApplicationsView view, EventBus eventBus, LoginPresenter loginPresenter,
                                    CloudBeesClientService service, ApplicationInfoPresenter applicationInfoPresenter,
                                    DeleteApplicationPresenter deleteApplicationPresenter, NotificationManager notificationManager) {
        this.view = view;
        this.view.setDelegate(this);
        this.eventBus = eventBus;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.applicationInfoPresenter = applicationInfoPresenter;
        this.deleteApplicationPresenter = deleteApplicationPresenter;
        this.notificationManager = notificationManager;
    }

    /** Show dialog. */
    public void showDialog() {
        getOrUpdateAppList();
    }

    /** Gets list of available application for current user. */
    private void getOrUpdateAppList() {
        ApplicationListUnmarshaller unmarshaller = new ApplicationListUnmarshaller();
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                getOrUpdateAppList();
            }
        };

        try {
            service.applicationList(
                    new CloudBeesAsyncRequestCallback<JsonArray<ApplicationInfo>>(unmarshaller, loggedInHandler, null, eventBus,
                                                                                  loginPresenter, notificationManager) {
                        @Override
                        protected void onSuccess(JsonArray<ApplicationInfo> result) {
                            view.setApplications(result);

                            if (!view.isShown()) {
                                view.showDialog();
                            }
                        }
                    });
        } catch (RequestException e) {
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onOkClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onInfoClicked(ApplicationInfo app) {
        applicationInfoPresenter.showDialog(app);
    }

    /** {@inheritDoc} */
    @Override
    public void onDeleteClicked(ApplicationInfo app) {
        deleteApplicationPresenter.deleteApp(app.getId(), app.getTitle(), new AsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                getOrUpdateAppList();
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(ApplicationsPresenter.class, "Can not delete application", caught);
            }
        });
    }
}