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
package com.codenvy.ide.ext.openshift.client.project;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.openshift.client.OpenShiftAsyncRequestCallback;
import com.codenvy.ide.ext.openshift.client.OpenShiftAutoBeanFactory;
import com.codenvy.ide.ext.openshift.client.OpenShiftClientServiceImpl;
import com.codenvy.ide.ext.openshift.client.OpenShiftLocalizationConstant;
import com.codenvy.ide.ext.openshift.client.info.ApplicationInfoPresenter;
import com.codenvy.ide.ext.openshift.client.login.LoggedInHandler;
import com.codenvy.ide.ext.openshift.client.login.LoginPresenter;
import com.codenvy.ide.ext.openshift.shared.AppInfo;
import com.codenvy.ide.resources.marshal.StringUnmarshaller;
import com.codenvy.ide.rest.AutoBeanUnmarshaller;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

/**
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
    private OpenShiftAutoBeanFactory      autoBeanFactory;
    private ApplicationInfoPresenter      applicationInfoPresenter;
    private AppInfo                       application;

    @Inject
    protected ProjectPresenter(ProjectView view, EventBus eventBus, ConsolePart console, OpenShiftClientServiceImpl service,
                               OpenShiftLocalizationConstant constant, LoginPresenter loginPresenter, ResourceProvider resourceProvider,
                               OpenShiftAutoBeanFactory autoBeanFactory, ApplicationInfoPresenter applicationInfoPresenter) {
        this.view = view;
        this.eventBus = eventBus;
        this.console = console;
        this.service = service;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.resourceProvider = resourceProvider;
        this.autoBeanFactory = autoBeanFactory;
        this.applicationInfoPresenter = applicationInfoPresenter;

        this.view.setDelegate(this);
    }

    public void showDialog() {
        if (!view.isShown()) {
            getApplicationInfo();
        }
    }

    private void getApplicationInfo() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                getApplicationInfo();
            }
        };

        final String projectId = resourceProvider.getActiveProject().getId();
        final String vfsId = resourceProvider.getVfsId();

        AutoBean<AppInfo> appInfo = autoBeanFactory.appInfo();
        AutoBeanUnmarshaller<AppInfo> unmarshaller = new AutoBeanUnmarshaller<AppInfo>(appInfo);

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

    @Override
    public void onCloseClicked() {
        application = null;
        view.close();
    }

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

    @Override
    public void onShowApplicationPropertiesClicked(AppInfo application) {
        applicationInfoPresenter.showDialog(application);
    }

    @Override
    public void onDeleteApplicationDeleted(final AppInfo application) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onDeleteApplicationDeleted(application);
            }
        };

        final String projectId = resourceProvider.getActiveProject().getId();
        final String vfsId = resourceProvider.getVfsId();

        try {
            service.destroyApplication(application.getName(), vfsId, projectId,
                                       new OpenShiftAsyncRequestCallback<String>(null, loggedInHandler, null, eventBus, console, constant,
                                                                                 loginPresenter) {
                                           @Override
                                           protected void onSuccess(String result) {
                                                String msg = "Application deleted";
                                               console.print(msg);
                                           }
                                       });
        } catch (RequestException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }
}
