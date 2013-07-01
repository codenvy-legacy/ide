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
package com.codenvy.ide.ext.aws.client.beanstalk.environments.launch;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.client.AwsAsyncRequestCallback;
import com.codenvy.ide.ext.aws.client.beanstalk.BeanstalkClientService;
import com.codenvy.ide.ext.aws.client.login.LoggedInHandler;
import com.codenvy.ide.ext.aws.client.login.LoginCanceledHandler;
import com.codenvy.ide.ext.aws.client.login.LoginPresenter;
import com.codenvy.ide.ext.aws.client.marshaller.ApplicationVersionInfoUnmarshaller;
import com.codenvy.ide.ext.aws.client.marshaller.ApplicationVersionListUnmarshaller;
import com.codenvy.ide.ext.aws.client.marshaller.EnvironmentInfoUnmarshaller;
import com.codenvy.ide.ext.aws.client.marshaller.SolutionStackListUnmarshaller;
import com.codenvy.ide.ext.aws.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.aws.shared.beanstalk.ApplicationVersionInfo;
import com.codenvy.ide.ext.aws.shared.beanstalk.CreateEnvironmentRequest;
import com.codenvy.ide.ext.aws.shared.beanstalk.EnvironmentInfo;
import com.codenvy.ide.ext.aws.shared.beanstalk.SolutionStack;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class LaunchEnvironmentPresenter implements LaunchEnvironmentView.ActionDelegate {
    private LaunchEnvironmentView          view;
    private EventBus                       eventBus;
    private ConsolePart                    console;
    private BeanstalkClientService         service;
    private LoginPresenter                 loginPresenter;
    private ResourceProvider               resourceProvider;
    private String                         appName;
    private AWSLocalizationConstant        constant;
    private AsyncCallback<EnvironmentInfo> callback;

    @Inject
    public LaunchEnvironmentPresenter(LaunchEnvironmentView view, EventBus eventBus, ConsolePart console,
                                      BeanstalkClientService service, LoginPresenter loginPresenter, ResourceProvider resourceProvider,
                                      AWSLocalizationConstant constant) {
        this.view = view;
        this.eventBus = eventBus;
        this.console = console;
        this.service = service;
        this.loginPresenter = loginPresenter;
        this.resourceProvider = resourceProvider;
        this.constant = constant;

        this.view.setDelegate(this);
    }

    public void showDialog(String versionLabel, String appName, AsyncCallback<EnvironmentInfo> callback) {
        this.appName = appName;
        this.callback = callback;

        if (!view.isShown()) {
            view.enableLaunchButton(false);
            view.showDialog();

            getSolutionStacks();
            getVersions(versionLabel);
        }
    }

    private void getSolutionStacks() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                getSolutionStacks();
            }
        };

        LoginCanceledHandler loginCanceledHandler = new LoginCanceledHandler() {
            @Override
            public void onLoginCanceled() {
                view.close();
            }
        };

        JsonArray<SolutionStack> solutions = JsonCollections.createArray();
        SolutionStackListUnmarshaller unmarshaller = new SolutionStackListUnmarshaller(solutions);

        try {
            service.getAvailableSolutionStacks(
                    new AwsAsyncRequestCallback<JsonArray<SolutionStack>>(unmarshaller, loggedInHandler, loginCanceledHandler,
                                                                          loginPresenter) {
                        @Override
                        protected void processFail(Throwable exception) {
                            eventBus.fireEvent(new ExceptionThrownEvent(exception));
                            console.print(exception.getMessage());
                        }

                        @Override
                        protected void onSuccess(JsonArray<SolutionStack> result) {
                            JsonArray<String> stack = JsonCollections.createArray();
                            for (int i = 0; i < result.size(); i++) {
                                SolutionStack solution = result.get(i);
                                if (solution.getPermittedFileTypes().contains("war")) {
                                    stack.add(solution.getName());
                                }

                                view.setSolutionStackValues(stack);
                            }
                        }
                    });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    private void getVersions(final String versionLabel) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                getVersions(versionLabel);
            }
        };

        JsonArray<ApplicationVersionInfo> versionList = JsonCollections.createArray();
        ApplicationVersionListUnmarshaller unmarshaller = new ApplicationVersionListUnmarshaller(versionList);

        try {
            service.getVersions(resourceProvider.getVfsId(), resourceProvider.getActiveProject().getId(),
                                new AwsAsyncRequestCallback<JsonArray<ApplicationVersionInfo>>(unmarshaller, loggedInHandler, null,
                                                                                               loginPresenter) {
                                    @Override
                                    protected void processFail(Throwable exception) {
                                        eventBus.fireEvent(new ExceptionThrownEvent(exception));
                                        console.print(exception.getMessage());
                                    }

                                    @Override
                                    protected void onSuccess(JsonArray<ApplicationVersionInfo> result) {
                                        JsonArray<String> versions = JsonCollections.createArray();
                                        for (int i = 0; i < result.size(); i++) {
                                            versions.add(result.get(i).getVersionLabel());
                                        }

                                        view.setVersionValues(versions, versionLabel);
                                    }
                                });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    @Override
    public void onLaunchButtonClicked() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onLaunchButtonClicked();
            }
        };

        final String envName = view.getEnvName();

        DtoClientImpls.CreateEnvironmentRequestImpl createEnvironmentRequest = DtoClientImpls.CreateEnvironmentRequestImpl.make();
        createEnvironmentRequest.setApplicationName(appName);
        createEnvironmentRequest.setVersionLabel(view.getVersionField());
        createEnvironmentRequest.setDescription(view.getEnvDescription());
        createEnvironmentRequest.setEnvironmentName(envName);
        createEnvironmentRequest.setSolutionStackName(view.getSolutionStack());

        DtoClientImpls.EnvironmentInfoImpl environmentInfo = DtoClientImpls.EnvironmentInfoImpl.make();
        EnvironmentInfoUnmarshaller unmarshaller = new EnvironmentInfoUnmarshaller(environmentInfo);

        try {
            service.createEnvironment(resourceProvider.getVfsId(), resourceProvider.getActiveProject().getId(), createEnvironmentRequest,
                                      new AwsAsyncRequestCallback<EnvironmentInfo>(unmarshaller, loggedInHandler, null, loginPresenter) {
                                          @Override
                                          protected void processFail(Throwable exception) {
                                              String message = constant.launchEnvironmentFailed(envName);
                                              if (exception instanceof ServerException &&
                                                  exception.getMessage() != null) {
                                                  message += "<br>" + exception.getMessage();
                                              }

                                              console.print(message);
                                          }

                                          @Override
                                          protected void onSuccess(EnvironmentInfo result) {
                                              view.close();

                                              if (callback != null) {
                                                  callback.onSuccess(result);
                                              }
                                          }
                                      });
        } catch (RequestException e) {

        }

    }

    @Override
    public void onCancelButtonClicked() {
        view.close();
    }

    @Override
    public void onNameFieldValueChanged() {
        view.enableLaunchButton(view.getEnvName() != null && !view.getEnvName().isEmpty());
    }
}
