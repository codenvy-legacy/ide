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
package com.codenvy.ide.ext.appfog.client.start;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.appfog.client.AppfogAsyncRequestCallback;
import com.codenvy.ide.ext.appfog.client.AppfogClientService;
import com.codenvy.ide.ext.appfog.client.AppfogLocalizationConstant;
import com.codenvy.ide.ext.appfog.client.login.LoggedInHandler;
import com.codenvy.ide.ext.appfog.client.login.LoginPresenter;
import com.codenvy.ide.ext.appfog.client.marshaller.AppFogApplicationUnmarshaller;
import com.codenvy.ide.ext.appfog.shared.AppfogApplication;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;

/**
 * Presenter for start and stop application commands.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 */
@Singleton
public class StartApplicationPresenter {
    private EventBus                   eventBus;
    private ResourceProvider           resourceProvider;
    private ConsolePart                console;
    private AppfogLocalizationConstant constant;
    private AsyncCallback<String>      appInfoChangedCallback;
    private LoginPresenter             loginPresenter;
    private AppfogClientService        service;
    private NotificationManager        notificationManager;

    /**
     * Create presenter.
     *
     * @param eventBus
     * @param resourceProvider
     * @param console
     * @param constant
     * @param loginPresenter
     * @param service
     * @param notificationManager
     */
    @Inject
    protected StartApplicationPresenter(EventBus eventBus, ResourceProvider resourceProvider, ConsolePart console,
                                        AppfogLocalizationConstant constant, LoginPresenter loginPresenter, AppfogClientService service,
                                        NotificationManager notificationManager) {
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.notificationManager = notificationManager;
    }

    /** If user is not logged in to AppFog, this handler will be called, after user logged in. */
    private LoggedInHandler startLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            startApplication(null, appInfoChangedCallback);
        }
    };

    /** If user is not logged in to AppFog, this handler will be called, after user logged in. */
    private LoggedInHandler stopLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            stopApplication(null, appInfoChangedCallback);
        }
    };

    /** If user is not logged in to AppFog, this handler will be called, after user logged in. */
    private LoggedInHandler restartLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            restartApplication(null, appInfoChangedCallback);
        }
    };

    /** If user is not logged in to AppFog, this handler will be called, after user logged in. */
    private LoggedInHandler checkIsStartedLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            checkIsStarted();
        }
    };

    /** If user is not logged in to AppFog, this handler will be called, after user logged in. */
    private LoggedInHandler checkIsStoppedLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            checkIsStopped();
        }
    };

    /**
     * Starts AppFog application.
     *
     * @param appName
     * @param callback
     */
    public void startApp(String appName, AsyncCallback<String> callback) {
        appInfoChangedCallback = callback;

        if (appName == null) {
            checkIsStarted();
        } else {
            startApplication(appName, callback);
        }
    }

    /** Gets information about active project and check its state. */
    private void checkIsStarted() {
        Project project = resourceProvider.getActiveProject();
        AppFogApplicationUnmarshaller unmarshaller = new AppFogApplicationUnmarshaller();

        try {
            service.getApplicationInfo(resourceProvider.getVfsInfo().getId(), project.getId(), null, null,
                                       new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller, checkIsStartedLoggedInHandler,
                                                                                         null, eventBus, constant, loginPresenter,
                                                                                         notificationManager) {
                                           @Override
                                           protected void onSuccess(AppfogApplication result) {
                                               if ("STARTED".equals(result.getState()) &&
                                                   result.getInstances() == result.getRunningInstances()) {
                                                   String msg = constant.applicationAlreadyStarted(result.getName());
                                                   Notification notification = new Notification(msg, ERROR);
                                                   notificationManager.showNotification(notification);
                                               } else {
                                                   startApplication(null, appInfoChangedCallback);
                                               }
                                           }
                                       });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /**
     * Starts application.
     *
     * @param name
     * @param callback
     */
    private void startApplication(String name, final AsyncCallback<String> callback) {
        Project project = resourceProvider.getActiveProject();

        final String server = project != null ? project.getProperty("appfog-target").getValue().get(0) : null;
        final String appName = (project != null && name == null) ? project.getProperty("appfog-application").getValue().get(0) : name;
        final String projectId = project != null ? project.getId() : null;

        AppFogApplicationUnmarshaller unmarshaller = new AppFogApplicationUnmarshaller();

        try {
            service.startApplication(null, null, appName, server,
                                     new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller, startLoggedInHandler, null, eventBus,
                                                                                       constant, loginPresenter, notificationManager) {
                                         @Override
                                         protected void onSuccess(AppfogApplication result) {
                                             Notification notification;
                                             if ("STARTED".equals(result.getState()) &&
                                                 result.getInstances() == result.getRunningInstances()) {
                                                 String msg = constant.applicationCreatedSuccessfully(result.getName());
                                                 if (result.getUris().isEmpty()) {
                                                     msg += "<br>" + constant.applicationStartedWithNoUrls();
                                                 } else {
                                                     msg += "<br>" + constant.applicationStartedOnUrls(result.getName(),
                                                                                                       getAppUrisAsString(result));
                                                 }

                                                 notification = new Notification(msg, INFO);
                                                 console.print(msg);
                                                 callback.onSuccess(projectId);
                                             } else {
                                                 String msg = constant.applicationWasNotStarted(result.getName());
                                                 notification = new Notification(msg, ERROR);
                                             }
                                             notificationManager.showNotification(notification);
                                         }
                                     });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /**
     * Creates application's url in HTML format.
     *
     * @param application
     * @return
     */
    private String getAppUrisAsString(AppfogApplication application) {
        String appUris = "";
        JsonArray<String> uris = application.getUris();
        for (int i = 0; i < uris.size(); i++) {
            String uri = uris.get(i);
            if (!uri.startsWith("http")) {
                uri = "http://" + uri;
            }
            appUris += ", " + "<a href=\"" + uri + "\" target=\"_blank\">" + uri + "</a>";
        }
        if (!appUris.isEmpty()) {
            // crop unnecessary symbols
            appUris = appUris.substring(2);
        }
        return appUris;
    }

    /**
     * Stops AppFog application.
     *
     * @param appName
     * @param callback
     */
    public void stopApp(String appName, AsyncCallback<String> callback) {
        appInfoChangedCallback = callback;

        if (appName == null) {
            checkIsStopped();
        } else {
            stopApplication(appName, callback);
        }
    }

    /** Gets information about active project and check its state. */
    private void checkIsStopped() {
        Project project = resourceProvider.getActiveProject();
        AppFogApplicationUnmarshaller unmarshaller = new AppFogApplicationUnmarshaller();

        try {
            service.getApplicationInfo(resourceProvider.getVfsInfo().getId(), project.getId(), null, null,
                                       new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller, checkIsStoppedLoggedInHandler,
                                                                                         null, eventBus, constant, loginPresenter,
                                                                                         notificationManager) {

                                           @Override
                                           protected void onSuccess(AppfogApplication result) {
                                               if ("STOPPED".equals(result.getState())) {
                                                   String msg = constant.applicationAlreadyStopped(result.getName());
                                                   Notification notification = new Notification(msg, ERROR);
                                                   notificationManager.showNotification(notification);
                                               } else {
                                                   stopApplication(null, appInfoChangedCallback);
                                               }
                                           }
                                       });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /**
     * Stops application.
     *
     * @param name
     * @param callback
     */
    private void stopApplication(final String name, final AsyncCallback<String> callback) {
        Project project = resourceProvider.getActiveProject();

        final String server = project != null ? project.getProperty("appfog-target").getValue().get(0) : null;
        final String appName = (project != null && name == null) ? project.getProperty("appfog-application").getValue().get(0) : name;
        final String projectId = project != null ? project.getId() : null;

        try {
            service.stopApplication(null, null, appName, server,
                                    new AppfogAsyncRequestCallback<String>(null, stopLoggedInHandler, null, eventBus, constant,
                                                                           loginPresenter, notificationManager) {
                                        @Override
                                        protected void onSuccess(String result) {
                                            AppFogApplicationUnmarshaller unmarshaller = new AppFogApplicationUnmarshaller();

                                            try {
                                                service.getApplicationInfo(resourceProvider.getVfsInfo().getId(), projectId, name, null,
                                                                           new AppfogAsyncRequestCallback<AppfogApplication>(
                                                                                   unmarshaller, null, null, eventBus, constant,
                                                                                   loginPresenter, notificationManager) {
                                                                               @Override
                                                                               protected void
                                                                               onSuccess(AppfogApplication result) {
                                                                                   final String msg =
                                                                                           constant.applicationStopped(result.getName());
                                                                                   Notification notification =
                                                                                           new Notification(msg, INFO);
                                                                                   notificationManager.showNotification(notification);
                                                                                   callback.onSuccess(projectId);
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

    /**
     * Restarts AppFog application.
     *
     * @param appName
     * @param callback
     */
    public void restartApp(String appName, AsyncCallback<String> callback) {
        appInfoChangedCallback = callback;

        restartApplication(appName, callback);
    }

    /**
     * Restart application.
     *
     * @param name
     * @param callback
     */
    private void restartApplication(String name, final AsyncCallback<String> callback) {
        Project project = resourceProvider.getActiveProject();

        final String server = project != null ? project.getProperty("appfog-target").getValue().get(0) : null;
        final String appName = (project != null && name == null) ? project.getProperty("appfog-application").getValue().get(0) : name;
        final String projectId = project != null ? project.getId() : null;
        AppFogApplicationUnmarshaller unmarshaller = new AppFogApplicationUnmarshaller();

        try {
            service.restartApplication(null, null, appName, server,
                                       new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller, restartLoggedInHandler, null,
                                                                                         eventBus, constant, loginPresenter,
                                                                                         notificationManager) {
                                           @Override
                                           protected void onSuccess(AppfogApplication result) {
                                               Notification notification;
                                               if (result.getInstances() == result.getRunningInstances()) {
                                                   final String appUris = getAppUrisAsString(result);
                                                   String msg;
                                                   if (appUris.isEmpty()) {
                                                       msg = constant.applicationRestarted(result.getName());
                                                   } else {
                                                       msg = constant.applicationRestartedUris(result.getName(), appUris);
                                                   }
                                                   notification = new Notification(msg, INFO);
                                                   callback.onSuccess(projectId);
                                               } else {
                                                   String msg = constant.applicationWasNotStarted(result.getName());
                                                   notification = new Notification(msg, ERROR);
                                               }
                                               notificationManager.showNotification(notification);
                                           }
                                       });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }
}