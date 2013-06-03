/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.appfog.client.start;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.appfog.client.AppfogAsyncRequestCallback;
import com.codenvy.ide.ext.appfog.client.AppfogClientService;
import com.codenvy.ide.ext.appfog.client.AppfogLocalizationConstant;
import com.codenvy.ide.ext.appfog.client.login.LoggedInHandler;
import com.codenvy.ide.ext.appfog.client.login.LoginPresenter;
import com.codenvy.ide.ext.appfog.client.marshaller.AppFogApplicationUnmarshaller;
import com.codenvy.ide.ext.appfog.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.appfog.shared.AppfogApplication;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

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

    /**
     * Create presenter.
     *
     * @param eventBus
     * @param resourceProvider
     * @param console
     * @param constant
     * @param loginPresenter
     * @param service
     */
    @Inject
    protected StartApplicationPresenter(EventBus eventBus, ResourceProvider resourceProvider, ConsolePart console,
                                        AppfogLocalizationConstant constant, LoginPresenter loginPresenter, AppfogClientService service) {
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.service = service;
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
        DtoClientImpls.AppfogApplicationImpl appfogApplication = DtoClientImpls.AppfogApplicationImpl.make();
        AppFogApplicationUnmarshaller unmarshaller = new AppFogApplicationUnmarshaller(appfogApplication);

        try {
            service.getApplicationInfo(resourceProvider.getVfsId(), project.getId(), null, null,
                                       new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller, checkIsStartedLoggedInHandler,
                                                                                         null, eventBus, constant, console,
                                                                                         loginPresenter) {
                                           @Override
                                           protected void onSuccess(AppfogApplication result) {
                                               if ("STARTED".equals(result.getState()) &&
                                                   result.getInstances() == result.getRunningInstances()) {
                                                   String msg = constant.applicationAlreadyStarted(result.getName());
                                                   console.print(msg);
                                               } else {
                                                   startApplication(null, appInfoChangedCallback);
                                               }
                                           }
                                       });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
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

        DtoClientImpls.AppfogApplicationImpl appfogApplication = DtoClientImpls.AppfogApplicationImpl.make();
        AppFogApplicationUnmarshaller unmarshaller = new AppFogApplicationUnmarshaller(appfogApplication);

        try {
            service.startApplication(null, null, appName, server,
                                     new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller, startLoggedInHandler, null, eventBus,
                                                                                       constant, console, loginPresenter) {
                                         @Override
                                         protected void onSuccess(AppfogApplication result) {
                                             if ("STARTED".equals(result.getState()) &&
                                                 result.getInstances() == result.getRunningInstances()) {
                                                 String msg = constant.applicationCreatedSuccessfully(result.getName());
                                                 if (result.getUris().isEmpty()) {
                                                     msg += "<br>" + constant.applicationStartedWithNoUrls();
                                                 } else {
                                                     msg += "<br>" + constant.applicationStartedOnUrls(result.getName(),
                                                                                                       getAppUrisAsString(result));
                                                 }

                                                 console.print(msg);
                                                 callback.onSuccess(projectId);
                                             } else {
                                                 String msg = constant.applicationWasNotStarted(result.getName());
                                                 console.print(msg);
                                             }

                                         }
                                     });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
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
        DtoClientImpls.AppfogApplicationImpl appfogApplication = DtoClientImpls.AppfogApplicationImpl.make();
        AppFogApplicationUnmarshaller unmarshaller = new AppFogApplicationUnmarshaller(appfogApplication);

        try {
            service.getApplicationInfo(resourceProvider.getVfsId(), project.getId(), null, null,
                                       new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller, checkIsStoppedLoggedInHandler,
                                                                                         null, eventBus, constant, console,
                                                                                         loginPresenter) {

                                           @Override
                                           protected void onSuccess(AppfogApplication result) {
                                               if ("STOPPED".equals(result.getState())) {
                                                   String msg = constant.applicationAlreadyStopped(result.getName());
                                                   console.print(msg);
                                               } else {
                                                   stopApplication(null, appInfoChangedCallback);
                                               }
                                           }
                                       });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
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
                                    new AppfogAsyncRequestCallback<String>(null, stopLoggedInHandler, null, eventBus, constant, console,
                                                                           loginPresenter) {
                                        @Override
                                        protected void onSuccess(String result) {
                                            DtoClientImpls.AppfogApplicationImpl appfogApplication =
                                                    DtoClientImpls.AppfogApplicationImpl.make();
                                            AppFogApplicationUnmarshaller unmarshaller =
                                                    new AppFogApplicationUnmarshaller(appfogApplication);

                                            try {
                                                service.getApplicationInfo(resourceProvider.getVfsId(), projectId, name, null,
                                                                           new AppfogAsyncRequestCallback<AppfogApplication>(
                                                                                   unmarshaller, null, null, eventBus, constant, console,
                                                                                   loginPresenter) {
                                                                               @Override
                                                                               protected void
                                                                               onSuccess(AppfogApplication result) {
                                                                                   final String msg =
                                                                                           constant.applicationStopped(result.getName());
                                                                                   console.print(msg);
                                                                                   callback.onSuccess(projectId);
                                                                               }
                                                                           });
                                            } catch (RequestException e) {
                                                eventBus.fireEvent(new ExceptionThrownEvent(e));
                                                console.print(e.getMessage());
                                            }
                                        }
                                    });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
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
        DtoClientImpls.AppfogApplicationImpl appfogApplication = DtoClientImpls.AppfogApplicationImpl.make();
        AppFogApplicationUnmarshaller unmarshaller = new AppFogApplicationUnmarshaller(appfogApplication);

        try {
            service.restartApplication(null, null, appName, server,
                                       new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller, restartLoggedInHandler, null,
                                                                                         eventBus, constant, console, loginPresenter) {
                                           @Override
                                           protected void onSuccess(AppfogApplication result) {
                                               if (result.getInstances() == result.getRunningInstances()) {
                                                   final String appUris = getAppUrisAsString(result);
                                                   String msg;
                                                   if (appUris.isEmpty()) {
                                                       msg = constant.applicationRestarted(result.getName());
                                                   } else {
                                                       msg = constant.applicationRestartedUris(result.getName(), appUris);
                                                   }
                                                   console.print(msg);
                                                   callback.onSuccess(projectId);
                                               } else {
                                                   String msg = constant.applicationWasNotStarted(result.getName());
                                                   console.print(msg);
                                               }
                                           }
                                       });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }
}