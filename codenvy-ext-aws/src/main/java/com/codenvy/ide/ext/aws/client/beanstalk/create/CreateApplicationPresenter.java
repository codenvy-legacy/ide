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
package com.codenvy.ide.ext.aws.client.beanstalk.create;

import com.codenvy.ide.api.parts.ConsolePart;
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
import com.codenvy.ide.ext.aws.shared.beanstalk.*;
import com.codenvy.ide.extension.maven.client.event.BuildProjectEvent;
import com.codenvy.ide.extension.maven.client.event.ProjectBuiltEvent;
import com.codenvy.ide.extension.maven.client.event.ProjectBuiltHandler;
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
    private ConsolePart             console;
    private AWSLocalizationConstant constant;
    private String                  warUrl;
    private Project                 openedProject;
    private ResourceProvider        resourceProvider;
    private BeanstalkClientService  service;
    private LoginPresenter          loginPresenter;
    private HandlerRegistration     projectBuildHandler;
    private Loader                  loader;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param console
     * @param constant
     * @param resourceProvider
     * @param service
     * @param loginPresenter
     * @param loader
     */
    @Inject
    public CreateApplicationPresenter(CreateApplicationView view, EventBus eventBus, ConsolePart console,
                                      AWSLocalizationConstant constant, ResourceProvider resourceProvider, BeanstalkClientService service,
                                      LoginPresenter loginPresenter, Loader loader) {
        this.view = view;
        this.eventBus = eventBus;
        this.console = console;
        this.constant = constant;
        this.resourceProvider = resourceProvider;
        this.service = service;
        this.loginPresenter = loginPresenter;
        this.loader = loader;

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

        JsonArray<SolutionStack> solutionStack = JsonCollections.createArray();
        SolutionStackListUnmarshaller unmarshaller = new SolutionStackListUnmarshaller(solutionStack);

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
                            }

                            view.setSolutionStacks(stack);
                        }
                    });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
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


        DtoClientImpls.ApplicationInfoImpl applicationInfo = DtoClientImpls.ApplicationInfoImpl.make();
        ApplicationInfoUnmarshaller unmarshaller = new ApplicationInfoUnmarshaller(applicationInfo);

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

                                              console.print(message);
                                          }

                                          @Override
                                          protected void onSuccess(ApplicationInfo result) {
                                              loader.hide();
                                              console.print(constant.createApplicationSuccess(result.getName()));

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
            console.print(e.getMessage());
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

        DtoClientImpls.EnvironmentInfoImpl environmentInfo = DtoClientImpls.EnvironmentInfoImpl.make();
        EnvironmentInfoUnmarshaller unmarshaller = new EnvironmentInfoUnmarshaller(environmentInfo);

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

                                              console.print(message);
                                          }

                                          @Override
                                          protected void onSuccess(EnvironmentInfo result) {
                                              loader.hide();
                                              console.print(constant.launchEnvironmentLaunching(envName));
                                              RequestStatusHandler environmentStatusHandler =
                                                      new EnvironmentRequestStatusHandler(
                                                              constant.launchEnvironmentLaunching(result.getName()),
                                                              constant.launchEnvironmentSuccess(result.getName()), eventBus);

                                              new EnvironmentStatusChecker(resourceProvider, openedProject, result, true,
                                                                           environmentStatusHandler, eventBus, console, service,
                                                                           loginPresenter, constant).startChecking();
                                          }
                                      });
        } catch (RequestException e) {
            loader.hide();
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }
}
