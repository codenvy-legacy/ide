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
package com.codenvy.ide.extension.cloudfoundry.client.start;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoginPresenter;
import com.codenvy.ide.extension.cloudfoundry.client.marshaller.CloudFoundryApplicationUnmarshaller;
import com.codenvy.ide.extension.cloudfoundry.dto.client.DtoClientImpls;
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
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
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: StartApplicationPresenter.java Jul 12, 2011 3:58:22 PM vereshchaka $
 */
@Singleton
public class StartApplicationPresenter {
    private EventBus                            eventBus;
    private ResourceProvider                    resourceProvider;
    private ConsolePart                         console;
    private CloudFoundryLocalizationConstant    constant;
    private AsyncCallback<String>               appInfoChangedCallback;
    private LoginPresenter                      loginPresenter;
    private CloudFoundryClientService           service;
    private CloudFoundryExtension.PAAS_PROVIDER paasProvider;

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
                                        CloudFoundryLocalizationConstant constant, LoginPresenter loginPresenter,
                                        CloudFoundryClientService service) {
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.service = service;
    }

    /** If user is not logged in to CloudFoundry, this handler will be called, after user logged in. */
    private LoggedInHandler startLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            startApplication(null, null, appInfoChangedCallback);
        }
    };

    /** If user is not logged in to CloudFoundry, this handler will be called, after user logged in. */
    private LoggedInHandler stopLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            stopApplication(null, null, appInfoChangedCallback);
        }
    };

    /** If user is not logged in to CloudFoundry, this handler will be called, after user logged in. */
    private LoggedInHandler restartLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            restartApplication(null, null, appInfoChangedCallback);
        }
    };

    /** If user is not logged in to CloudFoundry, this handler will be called, after user logged in. */
    private LoggedInHandler checkIsStartedLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            checkIsStarted();
        }
    };

    /** If user is not logged in to CloudFoundry, this handler will be called, after user logged in. */
    private LoggedInHandler checkIsStoppedLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            checkIsStopped();
        }
    };

    /**
     * Starts CloudFounry application.
     *
     * @param appName
     * @param callback
     * @param paasProvider
     */
    public void startApp(String appName, String server, CloudFoundryExtension.PAAS_PROVIDER paasProvider, AsyncCallback<String> callback) {
        this.appInfoChangedCallback = callback;
        this.paasProvider = paasProvider;

        if (appName == null) {
            checkIsStarted();
        } else {
            startApplication(appName, server, callback);
        }
    }

    /** Gets information about active project and check its state. */
    private void checkIsStarted() {
        Project project = resourceProvider.getActiveProject();
        DtoClientImpls.CloudFoundryApplicationImpl cloudFoundryApplication = DtoClientImpls.CloudFoundryApplicationImpl.make();
        CloudFoundryApplicationUnmarshaller unmarshaller = new CloudFoundryApplicationUnmarshaller(cloudFoundryApplication);

        try {
            service.getApplicationInfo(resourceProvider.getVfsId(), project.getId(), null, null,
                                       new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller,
                                                                                                     checkIsStartedLoggedInHandler,
                                                                                                     null, eventBus, console, constant,
                                                                                                     loginPresenter, paasProvider) {
                                           @Override
                                           protected void onSuccess(CloudFoundryApplication result) {
                                               if ("STARTED".equals(result.getState()) &&
                                                   result.getInstances() == result.getRunningInstances()) {
                                                   String msg = constant.applicationAlreadyStarted(result.getName());
                                                   console.print(msg);
                                               } else {
                                                   startApplication(null, null, appInfoChangedCallback);
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
     * @param server
     * @param callback
     */
    private void startApplication(String name, String server, final AsyncCallback<String> callback) {
        final String projectId =
                resourceProvider.getActiveProject() != null ? resourceProvider.getActiveProject().getId() : null;
        DtoClientImpls.CloudFoundryApplicationImpl cloudFoundryApplication = DtoClientImpls.CloudFoundryApplicationImpl.make();
        CloudFoundryApplicationUnmarshaller unmarshaller = new CloudFoundryApplicationUnmarshaller(cloudFoundryApplication);

        try {
            service.startApplication(resourceProvider.getVfsId(), projectId, name, server, paasProvider,
                                     new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller, startLoggedInHandler, null,
                                                                                                   eventBus, console, constant,
                                                                                                   loginPresenter, paasProvider) {
                                         @Override
                                         protected void onSuccess(CloudFoundryApplication result) {
                                             if ("STARTED".equals(result.getState()) &&
                                                 result.getInstances() == result.getRunningInstances()) {
                                                 String msg = constant.applicationCreatedSuccessfully(result.getName());
                                                 if (result.getUris().isEmpty()) {
                                                     msg += "<br>" + constant.applicationStartedWithNoUrls();
                                                 } else {
                                                     msg += "<br>" +
                                                            constant.applicationStartedOnUrls(result.getName(), getAppUrisAsString(result));
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
    private String getAppUrisAsString(CloudFoundryApplication application) {
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
     * Stops CloudFounry application.
     *
     * @param appName
     * @param callback
     * @param paasProvider
     */
    public void stopApp(String appName, String server, CloudFoundryExtension.PAAS_PROVIDER paasProvider, AsyncCallback<String> callback) {
        this.paasProvider = paasProvider;

        if (appName == null) {
            checkIsStopped();
        } else {
            stopApplication(appName, server, callback);
        }
    }

    /** Gets information about active project and check its state. */
    private void checkIsStopped() {
        Project project = resourceProvider.getActiveProject();
        DtoClientImpls.CloudFoundryApplicationImpl cloudFoundryApplication = DtoClientImpls.CloudFoundryApplicationImpl.make();
        CloudFoundryApplicationUnmarshaller unmarshaller = new CloudFoundryApplicationUnmarshaller(cloudFoundryApplication);

        try {
            service.getApplicationInfo(resourceProvider.getVfsId(), project.getId(), null, null,
                                       new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller,
                                                                                                     checkIsStoppedLoggedInHandler,
                                                                                                     null, eventBus, console, constant,
                                                                                                     loginPresenter, paasProvider) {
                                           @Override
                                           protected void onSuccess(CloudFoundryApplication result) {
                                               if ("STOPPED".equals(result.getState())) {
                                                   String msg = constant.applicationAlreadyStopped(result.getName());
                                                   console.print(msg);
                                               } else {
                                                   stopApplication(null, null, appInfoChangedCallback);
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
    private void stopApplication(final String name, String server, final AsyncCallback<String> callback) {
        final String projectId =
                resourceProvider.getActiveProject() != null ? resourceProvider.getActiveProject().getId() : null;

        try {
            service.stopApplication(resourceProvider.getVfsId(), projectId, name, server, paasProvider,
                                    new CloudFoundryAsyncRequestCallback<String>(null, stopLoggedInHandler, null, eventBus, console,
                                                                                 constant, loginPresenter, paasProvider) {
                                        @Override
                                        protected void onSuccess(String result) {
                                            DtoClientImpls.CloudFoundryApplicationImpl cloudFoundryApplication =
                                                    DtoClientImpls.CloudFoundryApplicationImpl.make();
                                            CloudFoundryApplicationUnmarshaller unmarshaller =
                                                    new CloudFoundryApplicationUnmarshaller(cloudFoundryApplication);

                                            try {
                                                service.getApplicationInfo(resourceProvider.getVfsId(), projectId, name, null,
                                                                           new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(
                                                                                   unmarshaller, null, null, eventBus, console, constant,
                                                                                   loginPresenter, paasProvider) {
                                                                               @Override
                                                                               protected void onSuccess(CloudFoundryApplication result) {
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
     * Restarts CloudFoundry application.
     *
     * @param appName
     * @param callback
     * @param paasProvider
     */
    public void restartApp(String appName, String server, CloudFoundryExtension.PAAS_PROVIDER paasProvider,
                           AsyncCallback<String> callback) {
        this.appInfoChangedCallback = callback;
        this.paasProvider = paasProvider;
        restartApplication(appName, server, callback);
    }

    /**
     * Restart application.
     *
     * @param name
     * @param callback
     */
    private void restartApplication(String name, String server, final AsyncCallback<String> callback) {
        final String projectId =
                resourceProvider.getActiveProject() != null ? resourceProvider.getActiveProject().getId() : null;
        DtoClientImpls.CloudFoundryApplicationImpl cloudFoundryApplication = DtoClientImpls.CloudFoundryApplicationImpl.make();
        CloudFoundryApplicationUnmarshaller unmarshaller = new CloudFoundryApplicationUnmarshaller(cloudFoundryApplication);

        try {
            service.restartApplication(resourceProvider.getVfsId(), projectId, name, server, paasProvider,
                                       new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller, restartLoggedInHandler,
                                                                                                     null, eventBus, console, constant,
                                                                                                     loginPresenter, paasProvider) {
                                           @Override
                                           protected void onSuccess(CloudFoundryApplication result) {
                                               if (result.getInstances() == result.getRunningInstances()) {
                                                   final String appUris = getAppUrisAsString(result);
                                                   String msg = "";
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