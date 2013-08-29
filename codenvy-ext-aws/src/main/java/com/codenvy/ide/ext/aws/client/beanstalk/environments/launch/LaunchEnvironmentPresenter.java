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
import com.codenvy.ide.ext.aws.client.marshaller.ApplicationVersionListUnmarshaller;
import com.codenvy.ide.ext.aws.client.marshaller.EnvironmentInfoUnmarshaller;
import com.codenvy.ide.ext.aws.client.marshaller.SolutionStackListUnmarshaller;
import com.codenvy.ide.ext.aws.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.aws.shared.beanstalk.ApplicationVersionInfo;
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
 * Presenter that allow user to launch environment.
 *
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

    /**
     * Create view.
     *
     * @param view
     * @param eventBus
     * @param console
     * @param service
     * @param loginPresenter
     * @param resourceProvider
     * @param constant
     */
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

    /** Show main dialog window. */
    public void showDialog(String versionLabel, String appName, AsyncCallback<EnvironmentInfo> callback) {
        this.appName = appName;
        this.callback = callback;

        if (!view.isShown()) {
            view.enableLaunchButton(false);
            view.showDialog();
            view.focusInEnvNameField();

            getSolutionStacks();
            getVersions(versionLabel);
        }
    }

    /** Get available solution stack technologies. */
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
        SolutionStackListUnmarshaller unmarshaller = new SolutionStackListUnmarshaller();

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

    /**
     * Get available version labels for the application.
     *
     * @param versionLabel
     *         version label.
     */
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

    /** {@inheritDoc} */
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
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }

    }

    /** {@inheritDoc} */
    @Override
    public void onCancelButtonClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onNameFieldValueChanged() {
        view.enableLaunchButton(view.getEnvName() != null && !view.getEnvName().isEmpty());
    }
}
