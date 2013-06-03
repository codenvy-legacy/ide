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
package com.codenvy.ide.ext.openshift.client.start;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.openshift.client.OpenShiftAsyncRequestCallback;
import com.codenvy.ide.ext.openshift.client.OpenShiftClientServiceImpl;
import com.codenvy.ide.ext.openshift.client.OpenShiftLocalizationConstant;
import com.codenvy.ide.ext.openshift.client.login.LoggedInHandler;
import com.codenvy.ide.ext.openshift.client.login.LoginPresenter;
import com.codenvy.ide.resources.marshal.StringUnmarshaller;
import com.codenvy.ide.resources.model.Project;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class StartApplicationPresenter {
    private EventBus                      eventBus;
    private ResourceProvider              resourceProvider;
    private ConsolePart                   console;
    private OpenShiftLocalizationConstant constant;
    private AsyncCallback<String>         appInfoChangedCallback;
    private LoginPresenter                loginPresenter;
    private OpenShiftClientServiceImpl    service;

    @Inject
    protected StartApplicationPresenter(EventBus eventBus, ResourceProvider resourceProvider, ConsolePart console,
                                        OpenShiftLocalizationConstant constant, AsyncCallback<String> appInfoChangedCallback,
                                        LoginPresenter loginPresenter, OpenShiftClientServiceImpl service) {
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.constant = constant;
        this.appInfoChangedCallback = appInfoChangedCallback;
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
            checkIsStarted(null);
        }
    };

    /** If user is not logged in to AppFog, this handler will be called, after user logged in. */
    private LoggedInHandler checkIsStoppedLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            checkIsStopped(null);
        }
    };

    /**
     * Starts OpenShift application.
     *
     * @param appName
     * @param callback
     */
    public void startApp(String appName, AsyncCallback<String> callback) {
        appInfoChangedCallback = callback;

        if (appName == null) {
            checkIsStarted(appName);
        } else {
            startApplication(appName, callback);
        }
    }

    /** Gets information about active project and check its state. */
    private void checkIsStarted(String name) {
        final Project project = resourceProvider.getActiveProject();

        final String appName = (project != null && name == null) ? project.getProperty("openshift-application").getValue().get(0) : name;

        try {
            StringUnmarshaller unmarshaller = new StringUnmarshaller();

            service.getApplicationHealth(appName,
                                         new OpenShiftAsyncRequestCallback<StringBuilder>(unmarshaller, checkIsStartedLoggedInHandler,
                                                                                          null, eventBus, console, constant,
                                                                                          loginPresenter) {
                                             @Override
                                             protected void onSuccess(StringBuilder result) {
                                                 if ("STARTED".equals(result.toString())) {
                                                     String msg = constant.applicationAlreadyStarted(appName);
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

        final String appName = (project != null && name == null) ? project.getProperty("openshift-application").getValue().get(0) : name;
        final String projectId = project != null ? project.getId() : null;

        try {
            service.startApplication(appName, new OpenShiftAsyncRequestCallback<Void>(null, startLoggedInHandler, null, eventBus,
                                                                                      console, constant, loginPresenter) {
                @Override
                protected void onSuccess(Void result) {
                    String msg = constant.applicationStartedSuccessfully(appName);
                    console.print(msg);
                    callback.onSuccess(projectId);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    super.onFailure(exception);
                    String msg = constant.applicationWasNotStarted(appName);
                    console.print(msg);
                }
            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /**
     * Stops OpenShift application.
     *
     * @param appName
     * @param callback
     */
    public void stopApp(String appName, AsyncCallback<String> callback) {
        appInfoChangedCallback = callback;

        if (appName == null) {
            checkIsStopped(appName);
        } else {
            stopApplication(appName, callback);
        }
    }

    /** Gets information about active project and check its state. */
    private void checkIsStopped(String name) {
        final Project project = resourceProvider.getActiveProject();

        final String appName = (project != null && name == null) ? project.getProperty("openshift-application").getValue().get(0) : name;

        try {
            StringUnmarshaller unmarshaller = new StringUnmarshaller();

            service.getApplicationHealth(appName,
                                         new OpenShiftAsyncRequestCallback<StringBuilder>(unmarshaller, checkIsStoppedLoggedInHandler,
                                                                                          null, eventBus, console, constant,
                                                                                          loginPresenter) {

                                             @Override
                                             protected void onSuccess(StringBuilder result) {
                                                 if ("STOPPED".equals(result.toString())) {
                                                     String msg = constant.applicationAlreadyStopped(appName);
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

        final String appName = (project != null && name == null) ? project.getProperty("openshift-application").getValue().get(0) : name;
        final String projectId = project != null ? project.getId() : null;

        try {
            service.stopApplication(appName,
                                    new OpenShiftAsyncRequestCallback<Void>(null, stopLoggedInHandler, null, eventBus, console, constant,
                                                                            loginPresenter) {
                                        @Override
                                        protected void onSuccess(Void result) {
                                            String msg = constant.applicationStoppedSuccessfully(appName);
                                            console.print(msg);
                                            callback.onSuccess(projectId);
                                        }

                                        @Override
                                        protected void onFailure(Throwable exception) {
                                            super.onFailure(exception);
                                            String msg = constant.applicationWasNotStopped(appName);
                                            console.print(msg);
                                        }
                                    });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /**
     * Restarts OpenShift application.
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

        final String appName = (project != null && name == null) ? project.getProperty("openshift-application").getValue().get(0) : name;
        final String projectId = project != null ? project.getId() : null;

        try {

            service.restartApplication(appName,
                                       new OpenShiftAsyncRequestCallback<Void>(null, restartLoggedInHandler, null,
                                                                               eventBus, console, constant, loginPresenter) {
                                           @Override
                                           protected void onSuccess(Void result) {
                                               String msg = constant.applicationRestarted(appName);
                                               console.print(msg);
                                               callback.onSuccess(projectId);
                                           }

                                           @Override
                                           protected void onFailure(Throwable exception) {
                                               super.onFailure(exception);

                                               String msg = constant.applicationWasNotStarted(appName);
                                               console.print(msg);
                                           }
                                       });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }
}
