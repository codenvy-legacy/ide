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
package com.codenvy.ide.ext.aws.client.beanstalk.create;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.ext.aws.client.AWSExtension;
import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.client.AwsAsyncRequestCallback;
import com.codenvy.ide.ext.aws.client.beanstalk.BeanstalkClientService;
import com.codenvy.ide.ext.aws.client.beanstalk.environments.EnvironmentRequestStatusHandler;
import com.codenvy.ide.ext.aws.client.beanstalk.environments.EnvironmentStatusChecker;
import com.codenvy.ide.ext.aws.client.login.LoggedInHandler;
import com.codenvy.ide.ext.aws.client.login.LoginCanceledHandler;
import com.codenvy.ide.ext.aws.client.login.LoginPresenter;
import com.codenvy.ide.ext.aws.client.marshaller.ApplicationInfoUnmarshaller;
import com.codenvy.ide.ext.aws.client.marshaller.EnvironmentInfoUnmarshaller;
import com.codenvy.ide.ext.aws.client.marshaller.SolutionStackListUnmarshaller;
import com.codenvy.ide.ext.aws.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.aws.shared.beanstalk.ApplicationInfo;
import com.codenvy.ide.ext.aws.shared.beanstalk.EnvironmentInfo;
import com.codenvy.ide.ext.aws.shared.beanstalk.SolutionStack;
import com.codenvy.ide.extension.builder.client.event.BuildProjectEvent;
import com.codenvy.ide.extension.builder.client.event.ProjectBuiltEvent;
import com.codenvy.ide.extension.builder.client.event.ProjectBuiltHandler;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.RequestStatusHandler;
import com.codenvy.ide.ui.loader.Loader;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Status.PROGRESS;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;

/**
 * Presenter which allow user to create Elastic Beanstalk Application.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class CreateApplicationPresenter implements CreateApplicationView.ActionDelegate, ProjectBuiltHandler {
    private CreateApplicationView   view;
    private EventBus                eventBus;
    private AWSLocalizationConstant constant;
    private String                  warUrl;
    private Project                 openedProject;
    private ResourceProvider        resourceProvider;
    private BeanstalkClientService  service;
    private LoginPresenter          loginPresenter;
    private HandlerRegistration     projectBuildHandler;
    private Loader                  loader;
    private NotificationManager     notificationManager;
    private Notification            notification;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param constant
     * @param resourceProvider
     * @param service
     * @param loginPresenter
     * @param loader
     * @param notificationManager
     */
    @Inject
    public CreateApplicationPresenter(CreateApplicationView view, EventBus eventBus, AWSLocalizationConstant constant,
                                      ResourceProvider resourceProvider, BeanstalkClientService service, LoginPresenter loginPresenter,
                                      Loader loader, NotificationManager notificationManager) {
        this.view = view;
        this.eventBus = eventBus;
        this.constant = constant;
        this.resourceProvider = resourceProvider;
        this.service = service;
        this.loginPresenter = loginPresenter;
        this.loader = loader;
        this.notificationManager = notificationManager;

        this.view.setDelegate(this);
    }

    /** Show main dialog window. */
    public void showDialog() {
        if (!view.isShown()) {
            view.showDialog();
            view.showCreateApplicationStep();
            getSolutionStacks();
        }
    }

    /** Get solution stack technologies. */
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
                            }

                            view.setSolutionStacks(stack);
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
    public void onProjectBuilt(ProjectBuiltEvent event) {
        projectBuildHandler.removeHandler();
        if (event.getBuildStatus().getDownloadUrl() != null) {
            warUrl = event.getBuildStatus().getDownloadUrl();
            createApplication();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onNextButtonClicked() {
        final String appName = view.getApplicationName();
        if (appName == null || appName.isEmpty()) {
            com.google.gwt.user.client.Window.alert(constant.validationErrorSpecifyAppName());
            return;
        }

        view.showCreateEnvironmentStep();
    }

    /** {@inheritDoc} */
    @Override
    public void onBackButtonClicked() {
        view.showCreateApplicationStep();
    }

    /** {@inheritDoc} */
    @Override
    public void onFinishButtonClicked() {
        if (view.launchNewEnvironment()) {
            final String envName = view.getEnvironmentName();
            if (envName == null || envName.length() < 4 || envName.length() > 32) {
                com.google.gwt.user.client.Window.alert(constant.validationErrorEnvNameLength());
            } else if (envName.startsWith("-") || envName.endsWith("-")) {
                com.google.gwt.user.client.Window.alert(constant.validationErrorEnvNameHyphen());
            }
        }

        warUrl = null;
        beforeCreation();
    }

    /** Start building application before creation on Elastic Beanstalk. */
    private void beforeCreation() {
        openedProject = resourceProvider.getActiveProject();

        if (openedProject != null) {
            projectBuildHandler = eventBus.addHandler(ProjectBuiltEvent.TYPE, this);
            eventBus.fireEvent(new BuildProjectEvent(openedProject));
        } else {
            Window.alert("You must open AWS project to deploy it.");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelButtonClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onLaunchEnvironmentClicked(boolean enabled) {
        view.enableCreateEnvironmentStep(enabled);
    }

    /** Create new application. */
    private void createApplication() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                createApplication();
            }
        };

        loader.show();

        final String appName = view.getApplicationName();

        DtoClientImpls.CreateApplicationRequestImpl createApplicationRequest = DtoClientImpls.CreateApplicationRequestImpl.make();
        createApplicationRequest.setApplicationName(appName);
        createApplicationRequest.setDescription(view.getDescription());
        createApplicationRequest.setS3Bucket(view.getS3Bucket());
        createApplicationRequest.setS3Key(view.getS3Key());
        createApplicationRequest.setWar(warUrl);

        notification = new Notification(constant.creatingProject(), PROGRESS);
        notificationManager.showNotification(notification);

        ApplicationInfoUnmarshaller unmarshaller = new ApplicationInfoUnmarshaller();

        try {
            service.createApplication(resourceProvider.getVfsId(), openedProject.getId(), createApplicationRequest,
                                      new AwsAsyncRequestCallback<ApplicationInfo>(unmarshaller, loggedInHandler, null, loginPresenter) {
                                          @Override
                                          protected void processFail(Throwable exception) {
                                              loader.hide();
                                              String message = constant.createApplicationFailed(appName);
                                              if (exception instanceof ServerException && exception.getMessage() != null) {
                                                  message += "<br>" + exception.getMessage();
                                              }

                                              notification.setStatus(FINISHED);
                                              notification.setType(ERROR);
                                              notification.setMessage(message);
                                          }

                                          @Override
                                          protected void onSuccess(ApplicationInfo result) {
                                              loader.hide();
                                              notification.setStatus(FINISHED);
                                              notification.setMessage(constant.createApplicationSuccess(result.getName()));

                                              if (view.launchNewEnvironment()) {
                                                  createEnvironment(result.getName());
                                              } else {
                                                  view.close();
                                              }
                                          }
                                      });
        } catch (RequestException e) {
            loader.hide();
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            notification.setStatus(FINISHED);
            notification.setType(ERROR);
            notification.setMessage(e.getMessage());
        }
    }

    /**
     * Create new environment.
     *
     * @param appName
     *         name of newly created application.
     */
    private void createEnvironment(final String appName) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                createEnvironment(appName);
            }
        };

        loader.show();

        final String envName = view.getEnvironmentName();

        DtoClientImpls.CreateEnvironmentRequestImpl createEnvironmentRequest = DtoClientImpls.CreateEnvironmentRequestImpl.make();
        createEnvironmentRequest.setApplicationName(appName);
        createEnvironmentRequest.setDescription(view.getEnvironmentDescription());
        createEnvironmentRequest.setEnvironmentName(envName);
        createEnvironmentRequest.setVersionLabel(AWSExtension.INIT_VER_LABEL);
        createEnvironmentRequest.setSolutionStackName(view.getSolutionStack());

        EnvironmentInfoUnmarshaller unmarshaller = new EnvironmentInfoUnmarshaller();

        try {
            service.createEnvironment(resourceProvider.getVfsId(), openedProject.getId(), createEnvironmentRequest,
                                      new AwsAsyncRequestCallback<EnvironmentInfo>(unmarshaller, loggedInHandler, null, loginPresenter) {
                                          @Override
                                          protected void processFail(Throwable exception) {
                                              loader.hide();
                                              String message = constant.launchEnvironmentFailed(envName);
                                              if (exception instanceof ServerException && exception.getMessage() != null) {
                                                  message += "<br>" + exception.getMessage();
                                              }

                                              Notification notification = new Notification(message, ERROR);
                                              notificationManager.showNotification(notification);
                                          }

                                          @Override
                                          protected void onSuccess(EnvironmentInfo result) {
                                              loader.hide();
                                              Notification notification =
                                                      new Notification(constant.launchEnvironmentLaunching(envName), INFO);
                                              notificationManager.showNotification(notification);
                                              RequestStatusHandler environmentStatusHandler =
                                                      new EnvironmentRequestStatusHandler(
                                                              constant.launchEnvironmentLaunching(result.getName()),
                                                              constant.launchEnvironmentSuccess(result.getName()), eventBus);

                                              new EnvironmentStatusChecker(resourceProvider, openedProject, result, true,
                                                                           environmentStatusHandler, eventBus, service,
                                                                           loginPresenter, constant, notificationManager).startChecking();
                                          }
                                      });
        } catch (RequestException e) {
            loader.hide();
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }
}
