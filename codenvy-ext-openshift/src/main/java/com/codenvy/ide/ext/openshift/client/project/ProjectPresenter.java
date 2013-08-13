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
package com.codenvy.ide.ext.openshift.client.project;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.openshift.client.OpenShiftAsyncRequestCallback;
import com.codenvy.ide.ext.openshift.client.OpenShiftClientServiceImpl;
import com.codenvy.ide.ext.openshift.client.OpenShiftLocalizationConstant;
import com.codenvy.ide.ext.openshift.client.info.ApplicationInfoPresenter;
import com.codenvy.ide.ext.openshift.client.login.LoggedInHandler;
import com.codenvy.ide.ext.openshift.client.login.LoginPresenter;
import com.codenvy.ide.ext.openshift.client.marshaller.ApplicationInfoUnmarshaller;
import com.codenvy.ide.ext.openshift.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.openshift.shared.AppInfo;
import com.codenvy.ide.resources.marshal.StringUnmarshaller;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Project preview window.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ProjectPresenter implements ProjectView.ActionDelegate {
    private ProjectView                   view;
    private EventBus                      eventBus;
    private ConsolePart                   console;
    private OpenShiftClientServiceImpl    service;
    private OpenShiftLocalizationConstant constant;
    private LoginPresenter                loginPresenter;
    private ResourceProvider              resourceProvider;
    private ApplicationInfoPresenter      applicationInfoPresenter;
    private AppInfo                       application;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param console
     * @param service
     * @param constant
     * @param loginPresenter
     * @param resourceProvider
     * @param applicationInfoPresenter
     */
    @Inject
    protected ProjectPresenter(ProjectView view, EventBus eventBus, ConsolePart console, OpenShiftClientServiceImpl service,
                               OpenShiftLocalizationConstant constant, LoginPresenter loginPresenter, ResourceProvider resourceProvider,
                               ApplicationInfoPresenter applicationInfoPresenter) {
        this.view = view;
        this.eventBus = eventBus;
        this.console = console;
        this.service = service;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.resourceProvider = resourceProvider;
        this.applicationInfoPresenter = applicationInfoPresenter;

        this.view.setDelegate(this);
    }

    /** Show main window. */
    public void showDialog() {
        if (!view.isShown()) {
            getApplicationInfo();
        }
    }

    /** Get application info and after that check health status. */
    private void getApplicationInfo() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                getApplicationInfo();
            }
        };

        final String projectId = resourceProvider.getActiveProject().getId();
        final String vfsId = resourceProvider.getVfsId();

        DtoClientImpls.AppInfoImpl appInfo = DtoClientImpls.AppInfoImpl.make();
        ApplicationInfoUnmarshaller unmarshaller = new ApplicationInfoUnmarshaller(appInfo);

        try {
            service.getApplicationInfo(null, vfsId, projectId,
                                       new OpenShiftAsyncRequestCallback<AppInfo>(unmarshaller, loggedInHandler, null, eventBus, console,
                                                                                  constant, loginPresenter) {
                                           @Override
                                           protected void onSuccess(AppInfo result) {
                                               application = result;
                                               getApplicationHealth();
                                               view.showDialog(application);
                                           }
                                       });
        } catch (RequestException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Get application status for specified application. */
    private void getApplicationHealth() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                getApplicationHealth();
            }
        };

        final StringUnmarshaller unmarshaller = new StringUnmarshaller();

        try {
            service.getApplicationHealth(application.getName(),
                                         new OpenShiftAsyncRequestCallback<StringBuilder>(unmarshaller, loggedInHandler, null, eventBus,
                                                                                          console, constant, loginPresenter) {
                                             @Override
                                             protected void onSuccess(StringBuilder result) {
                                                 view.setApplicationHealth(result.toString());
                                             }
                                         });
        } catch (RequestException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseClicked() {
        application = null;
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onStartApplicationClicked(final AppInfo application) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onStartApplicationClicked(application);
            }
        };

        try {
            service.startApplication(application.getName(),
                                     new OpenShiftAsyncRequestCallback<Void>(null, loggedInHandler, null, eventBus, console, constant,
                                                                             loginPresenter) {
                                         @Override
                                         protected void onSuccess(Void result) {
                                             String msg = "Application successfully started";
                                             console.print(msg);
                                             getApplicationHealth();
                                         }
                                     });
        } catch (RequestException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onStopApplicationClicked(final AppInfo application) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onStopApplicationClicked(application);
            }
        };

        try {
            service.stopApplication(application.getName(),
                                    new OpenShiftAsyncRequestCallback<Void>(null, loggedInHandler, null, eventBus, console, constant,
                                                                            loginPresenter) {
                                        @Override
                                        protected void onSuccess(Void result) {
                                            String msg = "Application successfully stopped";
                                            console.print(msg);
                                            getApplicationHealth();
                                        }
                                    });
        } catch (RequestException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onRestartApplicationClicked(final AppInfo application) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onRestartApplicationClicked(application);
            }
        };

        try {
            service.restartApplication(application.getName(),
                                       new OpenShiftAsyncRequestCallback<Void>(null, loggedInHandler, null, eventBus, console, constant,
                                                                               loginPresenter) {
                                           @Override
                                           protected void onSuccess(Void result) {
                                               String msg = "Application successfully restarted";
                                               console.print(msg);
                                               getApplicationHealth();
                                           }
                                       });
        } catch (RequestException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onShowApplicationPropertiesClicked(AppInfo application) {
        applicationInfoPresenter.showDialog(application);
    }

    /** {@inheritDoc} */
    @Override
    public void onDeleteApplicationDeleted(final AppInfo application) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onDeleteApplicationDeleted(application);
            }
        };

        final String projectId = resourceProvider.getActiveProject() != null ? resourceProvider.getActiveProject().getId() : null;
        final String vfsId = resourceProvider.getVfsId();

        try {
            service.destroyApplication(application.getName(), vfsId, projectId,
                                       new OpenShiftAsyncRequestCallback<String>(null, loggedInHandler, null, eventBus, console, constant,
                                                                                 loginPresenter) {
                                           @Override
                                           protected void onSuccess(String result) {
                                               String msg = "Application deleted";
                                               console.print(msg);
                                               view.close();
                                           }
                                       });
        } catch (RequestException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }
}
