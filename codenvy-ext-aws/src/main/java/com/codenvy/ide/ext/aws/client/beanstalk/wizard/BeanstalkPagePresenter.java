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
package com.codenvy.ide.ext.aws.client.beanstalk.wizard;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.wizard.paas.AbstractPaasPage;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.ext.aws.client.AWSExtension;
import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.client.AWSResource;
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
import com.codenvy.ide.extension.maven.client.event.BuildProjectEvent;
import com.codenvy.ide.extension.maven.client.event.ProjectBuiltEvent;
import com.codenvy.ide.extension.maven.client.event.ProjectBuiltHandler;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.RequestStatusHandler;
import com.codenvy.ide.ui.loader.Loader;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Status.PROGRESS;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;
import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PROJECT_NAME;
import static com.codenvy.ide.ext.aws.client.AWSExtension.ID;

/**
 * Presenter to allow user create application via wizard.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class BeanstalkPagePresenter extends AbstractPaasPage implements BeanstalkPageView.ActionDelegate, ProjectBuiltHandler {
    private BeanstalkPageView       view;
    private EventBus                eventBus;
    private String                  environmentName;
    private Project                 project;
    private ResourceProvider        resourceProvider;
    private AWSLocalizationConstant constant;
    private HandlerRegistration     projectBuildHandler;
    private LoginPresenter          loginPresenter;
    private BeanstalkClientService  service;
    private String                  warUrl;
    private String                  projectName;
    private Loader                  loader;
    private NotificationManager     notificationManager;
    private Notification            notification;
    private boolean                 isLogined;
    private CommitCallback          callback;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param resourceProvider
     * @param constant
     * @param loginPresenter
     * @param service
     * @param resource
     * @param loader
     * @param notificationManager
     */
    @Inject
    public BeanstalkPagePresenter(BeanstalkPageView view, EventBus eventBus, ResourceProvider resourceProvider,
                                  AWSLocalizationConstant constant, LoginPresenter loginPresenter, BeanstalkClientService service,
                                  AWSResource resource, Loader loader, NotificationManager notificationManager) {
        super("Deploy project to Elastic Beanstalk", resource.elasticBeanstalk48(), ID);

        this.view = view;
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.loader = loader;
        this.notificationManager = notificationManager;

        this.view.setDelegate(this);
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
    public boolean isCompleted() {
        return validate();
    }

    /** {@inheritDoc} */
    @Override
    public void focusComponent() {
        //do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void removeOptions() {
        //do nothing
    }

    /** {@inheritDoc} */
    @Override
    public String getNotice() {
        if (!isLogined) {
            return "This project will be created without deploy on Elastic Beanstalk.";
        } else if (view.getApplicationName().isEmpty()) {
            return "Please, enter a application's name.";
        } else if (view.getEnvironmentName().isEmpty()) {
            return "Please, enter a environment's name";
        } else if (view.getSolutionStack().isEmpty()) {
            return "Please, select solution stack technology.";
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        projectName = wizardContext.getData(PROJECT_NAME);

        view.setApplicationName(projectName);
        view.setEnvironmentName("");

        getSolutionStack();

        isLogined = true;

        container.setWidget(view);
    }

    /**
     * Validate filling application name, environment name and solution stack field.
     *
     * @return true if all fields are filled correctly.
     */
    public boolean validate() {
        return !isLogined ||
               view.getApplicationName() != null && !view.getApplicationName().isEmpty() && view.getEnvironmentName() != null &&
               !view.getEnvironmentName().isEmpty() && view.getSolutionStack() != null && !view.getSolutionStack().isEmpty();
    }

    /** Creates Beanstalk Application. */
    private void createApplication() {
        loader.setMessage(constant.creatingProject());
        loader.show();

        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                createApplication();
            }
        };

        DtoClientImpls.CreateApplicationRequestImpl createApplicationRequest = DtoClientImpls.CreateApplicationRequestImpl.make();
        createApplicationRequest.setApplicationName(projectName);
        createApplicationRequest.setDescription("");
        createApplicationRequest.setS3Bucket("");
        createApplicationRequest.setS3Key("");
        createApplicationRequest.setWar(warUrl);

        ApplicationInfoUnmarshaller unmarshaller = new ApplicationInfoUnmarshaller();
        notification = new Notification(constant.creatingProject(), PROGRESS);
        notificationManager.showNotification(notification);

        try {
            service.createApplication(resourceProvider.getVfsId(), project.getId(), createApplicationRequest,
                                      new AwsAsyncRequestCallback<ApplicationInfo>(unmarshaller, loggedInHandler, null, loginPresenter) {
                                          @Override
                                          protected void processFail(Throwable exception) {
                                              String message = constant.createApplicationFailed(projectName);
                                              if (exception instanceof ServerException && exception.getMessage() != null) {
                                                  message += "<br>" + exception.getMessage();
                                              }

                                              eventBus.fireEvent(new ExceptionThrownEvent(exception));
                                              notification.setStatus(FINISHED);
                                              notification.setMessage(message);
                                              notification.setType(ERROR);
                                              callback.onFailure(exception);
                                          }

                                          @Override
                                          protected void onSuccess(ApplicationInfo result) {
                                              notification.setStatus(FINISHED);
                                              notification.setMessage(constant.createApplicationSuccess(result.getName()));
                                              createEnvironment(result.getName());
                                          }
                                      });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            notification.setStatus(FINISHED);
            notification.setMessage(e.getMessage());
            notification.setType(ERROR);
            callback.onFailure(e);
        }
    }

    /**
     * Create new environment for Elastic Beanstalk Application.
     *
     * @param appName
     *         name for newly created application.
     */
    private void createEnvironment(final String appName) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                createEnvironment(appName);
            }
        };

        loader.setMessage("Creating environment...");
        loader.show();

        DtoClientImpls.CreateEnvironmentRequestImpl createEnvironmentRequest = DtoClientImpls.CreateEnvironmentRequestImpl.make();
        createEnvironmentRequest.setApplicationName(appName);
        createEnvironmentRequest.setDescription("");
        createEnvironmentRequest.setEnvironmentName(environmentName);
        createEnvironmentRequest.setVersionLabel(AWSExtension.INIT_VER_LABEL);
        createEnvironmentRequest.setSolutionStackName(view.getSolutionStack());

        EnvironmentInfoUnmarshaller unmarshaller = new EnvironmentInfoUnmarshaller();

        try {
            service.createEnvironment(resourceProvider.getVfsId(), project.getId(), createEnvironmentRequest,
                                      new AwsAsyncRequestCallback<EnvironmentInfo>(unmarshaller, loggedInHandler, null, loginPresenter) {
                                          @Override
                                          protected void processFail(Throwable exception) {
                                              loader.hide();
                                              String message = constant.launchEnvironmentFailed(environmentName);
                                              if (exception instanceof ServerException && exception.getMessage() != null) {
                                                  message += "<br>" + exception.getMessage();
                                              }

                                              Notification notification = new Notification(message, ERROR);
                                              notificationManager.showNotification(notification);

                                              callback.onFailure(exception);
                                          }

                                          @Override
                                          protected void onSuccess(EnvironmentInfo result) {
                                              loader.hide();
                                              Notification notification = new Notification(constant.launchEnvironmentLaunching(
                                                      environmentName), INFO);
                                              notificationManager.showNotification(notification);

                                              RequestStatusHandler environmentStatusHandler =
                                                      new EnvironmentRequestStatusHandler(
                                                              constant.launchEnvironmentLaunching(result.getName()),
                                                              constant.launchEnvironmentSuccess(result.getName()), eventBus);
                                              new EnvironmentStatusChecker(resourceProvider, project, result, true,
                                                                           environmentStatusHandler, eventBus, service,
                                                                           loginPresenter, constant, notificationManager).startChecking();

                                              callback.onSuccess();
                                          }
                                      });
        } catch (RequestException e) {
            loader.hide();
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
            callback.onFailure(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void commit(@NotNull CommitCallback callback) {
        if (!isLogined) {
            callback.onSuccess();
            return;
        }

        this.callback = callback;

        // TODO may be improve without getProject?
        resourceProvider.getProject(projectName, new AsyncCallback<Project>() {
            @Override
            public void onSuccess(Project result) {
                deploy(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                BeanstalkPagePresenter.this.callback.onFailure(caught);
            }
        });
    }

    /**
     * Start deploy application.
     *
     * @param project
     *         created project for deploying.
     */
    public void deploy(Project project) {
        this.project = project;

        buildApplication();
    }

    /** Start building application. */
    private void buildApplication() {
        projectBuildHandler = eventBus.addHandler(ProjectBuiltEvent.TYPE, this);
        eventBus.fireEvent(new BuildProjectEvent(project));
    }

    /** {@inheritDoc} */
    @Override
    public void onApplicationNameChange() {
        projectName = view.getApplicationName();
    }

    /** {@inheritDoc} */
    @Override
    public void onEnvironmentNameChange() {
        environmentName = view.getEnvironmentName();
    }

    /** Get list of solution stack technologies. */
    private void getSolutionStack() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                isLogined = true;
                getSolutionStack();
            }
        };
        LoginCanceledHandler loginCanceledHandler = new LoginCanceledHandler() {
            @Override
            public void onLoginCanceled() {
                isLogined = false;
                delegate.updateControls();
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
                            JsonArray<String> stackList = JsonCollections.createArray();
                            for (int i = 0; i < result.size(); i++) {
                                SolutionStack solution = result.get(i);
                                if (solution.getPermittedFileTypes().contains("war")) {
                                    stackList.add(solution.getName());
                                }
                            }

                            view.setSolutionStack(stackList);
                        }
                    });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }
}
