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

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
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

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

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
    private BeanstalkClientService         service;
    private LoginPresenter                 loginPresenter;
    private ResourceProvider               resourceProvider;
    private String                         appName;
    private AWSLocalizationConstant        constant;
    private NotificationManager            notificationManager;
    private AsyncCallback<EnvironmentInfo> callback;

    /**
     * Create view.
     *
     * @param view
     * @param eventBus
     * @param service
     * @param loginPresenter
     * @param resourceProvider
     * @param constant
     * @param notificationManager
     */
    @Inject
    public LaunchEnvironmentPresenter(LaunchEnvironmentView view, EventBus eventBus, BeanstalkClientService service,
                                      LoginPresenter loginPresenter, ResourceProvider resourceProvider,
                                      AWSLocalizationConstant constant, NotificationManager notificationManager) {
        this.view = view;
        this.eventBus = eventBus;
        this.service = service;
        this.loginPresenter = loginPresenter;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.notificationManager = notificationManager;

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
                            Notification notification = new Notification(exception.getMessage(), ERROR);
                            notificationManager.showNotification(notification);
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
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
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
        ApplicationVersionListUnmarshaller unmarshaller = new ApplicationVersionListUnmarshaller();

        try {
            service.getVersions(resourceProvider.getVfsInfo().getId(), resourceProvider.getActiveProject().getId(),
                                new AwsAsyncRequestCallback<JsonArray<ApplicationVersionInfo>>(unmarshaller, loggedInHandler, null,
                                                                                               loginPresenter) {
                                    @Override
                                    protected void processFail(Throwable exception) {
                                        eventBus.fireEvent(new ExceptionThrownEvent(exception));
                                        Notification notification = new Notification(exception.getMessage(), ERROR);
                                        notificationManager.showNotification(notification);
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
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
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

        EnvironmentInfoUnmarshaller unmarshaller = new EnvironmentInfoUnmarshaller();

        try {
            service.createEnvironment(resourceProvider.getVfsInfo().getId(), resourceProvider.getActiveProject().getId(), createEnvironmentRequest,
                                      new AwsAsyncRequestCallback<EnvironmentInfo>(unmarshaller, loggedInHandler, null, loginPresenter) {
                                          @Override
                                          protected void processFail(Throwable exception) {
                                              String message = constant.launchEnvironmentFailed(envName);
                                              if (exception instanceof ServerException &&
                                                  exception.getMessage() != null) {
                                                  message += "<br>" + exception.getMessage();
                                              }

                                              Notification notification = new Notification(message, ERROR);
                                              notificationManager.showNotification(notification);
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
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
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
