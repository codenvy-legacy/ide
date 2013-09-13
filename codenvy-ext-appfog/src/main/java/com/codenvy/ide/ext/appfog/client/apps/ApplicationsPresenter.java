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
package com.codenvy.ide.ext.appfog.client.apps;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.appfog.client.AppFogExtension;
import com.codenvy.ide.ext.appfog.client.AppfogAsyncRequestCallback;
import com.codenvy.ide.ext.appfog.client.AppfogClientService;
import com.codenvy.ide.ext.appfog.client.AppfogLocalizationConstant;
import com.codenvy.ide.ext.appfog.client.delete.DeleteApplicationPresenter;
import com.codenvy.ide.ext.appfog.client.login.LoggedInHandler;
import com.codenvy.ide.ext.appfog.client.login.LoginPresenter;
import com.codenvy.ide.ext.appfog.client.marshaller.ApplicationListUnmarshaller;
import com.codenvy.ide.ext.appfog.client.marshaller.TargetsUnmarshaller;
import com.codenvy.ide.ext.appfog.client.start.StartApplicationPresenter;
import com.codenvy.ide.ext.appfog.shared.AppfogApplication;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * The applications presenter manager AppFog application.
 * The presenter can start, stop, update, delete application.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 */
@Singleton
public class ApplicationsPresenter implements ApplicationsView.ActionDelegate {
    private ApplicationsView           view;
    private EventBus                   eventBus;
    private AppfogLocalizationConstant constant;
    private LoginPresenter             loginPresenter;
    private StartApplicationPresenter  startApplicationPresenter;
    private DeleteApplicationPresenter deleteApplicationPresenter;
    private AppfogClientService        service;
    private NotificationManager        notificationManager;
    private JsonArray<String>          servers;
    private String                     currentServer;
    /** The callback what execute when some application's information was changed. */
    private AsyncCallback<String> appInfoChangedCallback = new AsyncCallback<String>() {
        @Override
        public void onSuccess(String result) {
            getApplicationList();
        }

        @Override
        public void onFailure(Throwable caught) {
            Log.error(ApplicationsPresenter.class, "Can not change information", caught);
        }
    };

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param constant
     * @param loginPresenter
     * @param service
     * @param startApplicationPresenter
     * @param deleteApplicationPresenter
     * @param notificationManager
     */
    @Inject
    protected ApplicationsPresenter(ApplicationsView view, EventBus eventBus, AppfogLocalizationConstant constant,
                                    LoginPresenter loginPresenter, AppfogClientService service,
                                    StartApplicationPresenter startApplicationPresenter,
                                    DeleteApplicationPresenter deleteApplicationPresenter, NotificationManager notificationManager) {
        this.view = view;
        this.view.setDelegate(this);
        this.eventBus = eventBus;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.startApplicationPresenter = startApplicationPresenter;
        this.deleteApplicationPresenter = deleteApplicationPresenter;
        this.notificationManager = notificationManager;
    }

    /** Show dialog. */
    public void showDialog() {
        checkLogginedToServer();
    }

    /** Gets target from AppFog server. If this works well then we will know we have connect to AppFog server. */
    private void checkLogginedToServer() {
        TargetsUnmarshaller unmarshaller = new TargetsUnmarshaller();
        try {
            service.getTargets(new AsyncRequestCallback<JsonArray<String>>(unmarshaller) {
                @Override
                protected void onSuccess(JsonArray<String> result) {
                    if (result.isEmpty()) {
                        servers = JsonCollections.createArray(AppFogExtension.DEFAULT_SERVER);
                    } else {
                        servers = result;
                    }
                    // open view
                    openView();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    Notification notification = new Notification(exception.getMessage(), Notification.Type.ERROR);
                    notificationManager.showNotification(notification);
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                }
            });
        } catch (RequestException e) {
            Notification notification = new Notification(e.getMessage(), Notification.Type.ERROR);
            notificationManager.showNotification(notification);
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Opens view. */
    private void openView() {
        view.setTarget(AppFogExtension.DEFAULT_SERVER);
        // fill the list of applications
        currentServer = servers.get(0);
        getApplicationList();
    }

    /** Gets list of available application for current user. */
    private void getApplicationList() {
        try {
            ApplicationListUnmarshaller unmarshaller = new ApplicationListUnmarshaller();
            LoggedInHandler loggedInHandler = new LoggedInHandler() {
                @Override
                public void onLoggedIn() {
                    getApplicationList();
                }
            };

            service.getApplicationList(view.getTarget(),
                                       new AppfogAsyncRequestCallback<JsonArray<AppfogApplication>>(unmarshaller, loggedInHandler, null,
                                                                                                    view.getTarget(), eventBus, constant,
                                                                                                    loginPresenter, notificationManager) {
                                           @Override
                                           protected void onSuccess(JsonArray<AppfogApplication> result) {
                                               view.setApplications(result);
                                               view.setTarget(AppFogExtension.DEFAULT_SERVER);

                                               if (!view.isShown()) {
                                                   view.showDialog();
                                               }
                                           }
                                       });
        } catch (RequestException e) {
            Notification notification = new Notification(e.getMessage(), Notification.Type.ERROR);
            notificationManager.showNotification(notification);
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onShowClicked() {
        checkLogginedToServer();
    }

    /** {@inheritDoc} */
    @Override
    public void onStartClicked(AppfogApplication app) {
        startApplicationPresenter.startApp(app.getName(), appInfoChangedCallback);
    }

    /** {@inheritDoc} */
    @Override
    public void onStopClicked(AppfogApplication app) {
        startApplicationPresenter.stopApp(app.getName(), appInfoChangedCallback);
    }

    /** {@inheritDoc} */
    @Override
    public void onRestartClicked(AppfogApplication app) {
        startApplicationPresenter.restartApp(app.getName(), appInfoChangedCallback);
    }

    /** {@inheritDoc} */
    @Override
    public void onDeleteClicked(AppfogApplication app) {
        deleteApplicationPresenter.deleteApp(app.getName(), currentServer, appInfoChangedCallback);
    }
}