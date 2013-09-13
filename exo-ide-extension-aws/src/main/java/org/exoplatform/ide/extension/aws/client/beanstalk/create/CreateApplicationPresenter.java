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
package org.exoplatform.ide.extension.aws.client.beanstalk.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.commons.rest.RequestStatusHandler;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.job.JobManager;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.Language;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.client.AwsAsyncRequestCallback;
import org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService;
import org.exoplatform.ide.extension.aws.client.beanstalk.SolutionStackListUnmarshaller;
import org.exoplatform.ide.extension.aws.client.beanstalk.environments.EnvironmentRequestStatusHandler;
import org.exoplatform.ide.extension.aws.client.beanstalk.environments.EnvironmentStatusChecker;
import org.exoplatform.ide.extension.aws.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.CreateApplicationRequest;
import org.exoplatform.ide.extension.aws.shared.beanstalk.CreateEnvironmentRequest;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.SolutionStack;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 17, 2012 11:54:00 AM anya $
 */
public class CreateApplicationPresenter implements ProjectOpenedHandler, ProjectClosedHandler,
        VfsChangedHandler, CreateApplicationHandler, ViewClosedHandler, ProjectBuiltHandler {

    interface Display extends IsView {
        // Create Application step

        TextFieldItem getNameField();

        TextFieldItem getDescriptionField();

        TextFieldItem getS3BucketField();

        TextFieldItem getS3KeyField();

        // Create Environment step

        TextFieldItem getEnvNameField();

        TextFieldItem getEnvDescriptionField();

        HasValue<String> getSolutionStackField();

        HasValue<Boolean> getLaunchEnvField();

        void setSolutionStackValues(String[] values);

        HasClickHandlers getNextButton();

        HasClickHandlers getBackButton();

        HasClickHandlers getFinishButton();

        HasClickHandlers getCancelButton();

        void enableCreateEnvironmentStep(boolean enabled);

        void focusInApplicationNameField();

        void showCreateApplicationStep();

        void showCreateEnvironmentStep();
    }

    private Display display;

    private ProjectModel openedProject;

    private VirtualFileSystemInfo vfsInfo;

    private String warUrl = null;

    private boolean launchEnvironment;

    /** Time of last received event. */
    protected long lastReceivedEventTime;

    public CreateApplicationPresenter() {
        IDE.getInstance().addControl(new CreateApplicationControl());

        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(CreateApplicationEvent.TYPE, this);
    }

    public void bindDisplay() {
        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getNextButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                String appName = display.getNameField().getValue();
                if (appName == null || appName.length() == 0) {
                    Dialogs.getInstance().showError(AWSExtension.LOCALIZATION_CONSTANT.validationErrorTitile(),
                                                    AWSExtension.LOCALIZATION_CONSTANT.validationErrorSpecifyAppName());
                    return;
                }

                display.showCreateEnvironmentStep();
            }
        });

        display.getBackButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                display.showCreateApplicationStep();
            }
        });

        display.getFinishButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (display.getLaunchEnvField().getValue()) {
                    String envName = display.getEnvNameField().getValue();
                    if (envName == null || envName.length() < 4 || envName.length() > 23) {
                        Dialogs.getInstance().showError(AWSExtension.LOCALIZATION_CONSTANT.validationErrorTitile(),
                                                        AWSExtension.LOCALIZATION_CONSTANT.validationErrorEnvNameLength());
                        return;
                    } else if (envName.startsWith("-") || envName.endsWith("-")) {
                        Dialogs.getInstance().showError(AWSExtension.LOCALIZATION_CONSTANT.validationErrorTitile(),
                                                        AWSExtension.LOCALIZATION_CONSTANT.validationErrorEnvNameHyphen());
                        return;
                    }
                }

                warUrl = null;
                launchEnvironment = display.getLaunchEnvField().getValue();
                beforeCreation();
            }
        });

        display.getLaunchEnvField().addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                display.enableCreateEnvironmentStep(event.getValue());
            }
        });
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.create.CreateApplicationHandler#onCreateApplication(org.exoplatform.ide
     * .extension.aws.client.beanstalk.create.CreateApplicationEvent) */
    @Override
    public void onCreateApplication(CreateApplicationEvent event) {
        if (display == null) {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bindDisplay();
        }
        display.showCreateApplicationStep();
        display.focusInApplicationNameField();
        display.getLaunchEnvField().setValue(true);

        getSolutionStacks();
    }

    /** @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     * .application.event.VfsChangedEvent) */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        this.vfsInfo = event.getVfsInfo();
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework
     * .project.ProjectClosedEvent) */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        this.openedProject = null;
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework
     * .project.ProjectOpenedEvent) */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        this.openedProject = event.getProject();
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    private void beforeCreation() {
        ProjectType projectType = ProjectType.fromValue(openedProject.getProjectType());
        if (ProjectResolver.getProjectTypesByLanguage(Language.JAVA).contains(projectType)) {
            IDE.addHandler(ProjectBuiltEvent.TYPE, this);
            JobManager.get().showJobSeparated();
            IDE.fireEvent(new BuildProjectEvent(openedProject));
        } else {
            createApplication();
        }
    }

    public void createApplication() {
        final String applicationName = display.getNameField().getValue();
        CreateApplicationRequest createApplicationRequest =
                AWSExtension.AUTO_BEAN_FACTORY.createApplicationRequest().as();
        createApplicationRequest.setApplicationName(applicationName);
        createApplicationRequest.setDescription(display.getDescriptionField().getValue());
        createApplicationRequest.setS3Bucket(display.getS3BucketField().getValue());
        createApplicationRequest.setS3Key(display.getS3KeyField().getValue());
        createApplicationRequest.setWar(warUrl);

        AutoBean<ApplicationInfo> autoBean = AWSExtension.AUTO_BEAN_FACTORY.applicationInfo();

        try {
            BeanstalkClientService.getInstance().createApplication(
                    vfsInfo.getId(),
                    openedProject.getId(),
                    createApplicationRequest,
                    new AwsAsyncRequestCallback<ApplicationInfo>(new AutoBeanUnmarshaller<ApplicationInfo>(autoBean),
                                                                 new LoggedInHandler() {
                                                                     @Override
                                                                     public void onLoggedIn() {
                                                                         createApplication();
                                                                     }
                                                                 }, null) {

                        @Override
                        protected void onSuccess(ApplicationInfo result) {
                            IDE.fireEvent(new OutputEvent(AWSExtension.LOCALIZATION_CONSTANT.createApplicationSuccess(result
                                                                                                                              .getName()),
                                                          Type.INFO));
                            if (launchEnvironment) {
                                createEnvironment(result.getName());
                            } else {
                                IDE.getInstance().closeView(display.asView().getId());
                            }
                            IDE.fireEvent(new RefreshBrowserEvent(openedProject));
                        }

                        @Override
                        protected void processFail(Throwable exception) {
                            String message = AWSExtension.LOCALIZATION_CONSTANT.createApplicationFailed(applicationName);
                            if (exception instanceof ServerException && ((ServerException)exception).getMessage() != null) {
                                message += "<br>" + ((ServerException)exception).getMessage();
                            }
                            Dialogs.getInstance().showError(message);
                            IDE.fireEvent(new OutputEvent(message, Type.ERROR));
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void getSolutionStacks() {
        try {
            BeanstalkClientService.getInstance().getAvailableSolutionStacks(
                    new AwsAsyncRequestCallback<List<SolutionStack>>(new SolutionStackListUnmarshaller(), new LoggedInHandler() {
                        @Override
                        public void onLoggedIn() {
                            getSolutionStacks();
                        }
                    }, null) {
                        @Override
                        protected void onSuccess(List<SolutionStack> result) {
                            List<String> values = new ArrayList<String>();
                            for (SolutionStack solutionStack : result) {
                                //For detail see https://jira.exoplatform.org/browse/IDE-1951
                                if (solutionStack.getPermittedFileTypes().contains("war"))
                                    values.add(solutionStack.getName());
                            }
                            display.setSolutionStackValues(values.toArray(new String[values.size()]));
                        }

                        @Override
                        protected void processFail(Throwable exception) {
                            IDE.fireEvent(new ExceptionThrownEvent(exception));
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    public void createEnvironment(final String applicationName) {
        final String environmentName = display.getEnvNameField().getValue();
        CreateEnvironmentRequest createEnvironmentRequest =
                AWSExtension.AUTO_BEAN_FACTORY.createEnvironmentRequest().as();
        createEnvironmentRequest.setApplicationName(applicationName);
        createEnvironmentRequest.setDescription(display.getEnvDescriptionField().getValue());
        createEnvironmentRequest.setEnvironmentName(environmentName);
        createEnvironmentRequest.setVersionLabel(AWSExtension.INIT_VER_LABEL);
        createEnvironmentRequest.setSolutionStackName(display.getSolutionStackField().getValue());

        AutoBean<EnvironmentInfo> autoBean = AWSExtension.AUTO_BEAN_FACTORY.environmentInfo();
        try {
            BeanstalkClientService.getInstance().createEnvironment(
                    vfsInfo.getId(),
                    openedProject.getId(),
                    createEnvironmentRequest,
                    new AwsAsyncRequestCallback<EnvironmentInfo>(new AutoBeanUnmarshaller<EnvironmentInfo>(autoBean),
                                                                 new LoggedInHandler() {
                                                                     @Override
                                                                     public void onLoggedIn() {
                                                                         createEnvironment(applicationName);
                                                                     }
                                                                 }, null) {

                        @Override
                        protected void processFail(Throwable exception) {
                            String message = AWSExtension.LOCALIZATION_CONSTANT.launchEnvironmentFailed(environmentName);
                            if (exception instanceof ServerException && ((ServerException)exception).getMessage() != null) {
                                message += "<br>" + ((ServerException)exception).getMessage();
                            }
                            IDE.fireEvent(new OutputEvent(message, Type.ERROR));
                        }

                        @Override
                        protected void onSuccess(EnvironmentInfo result) {
                            if (display != null) {
                                IDE.getInstance().closeView(display.asView().getId());
                            }
                            IDE.fireEvent(new OutputEvent(AWSExtension.LOCALIZATION_CONSTANT
                                                                      .launchEnvironmentLaunching(environmentName), Type.INFO));

                            RequestStatusHandler environmentStatusHandler =
                                    new EnvironmentRequestStatusHandler(AWSExtension.LOCALIZATION_CONSTANT
                                                                                    .launchEnvironmentLaunching(result.getName()),
                                                                        AWSExtension.LOCALIZATION_CONSTANT
                                                                                    .launchEnvironmentSuccess(result.getName()));

                            new EnvironmentStatusChecker(vfsInfo, openedProject, result, true, environmentStatusHandler)
                                    .startChecking();
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** @see org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler#onProjectBuilt(org.exoplatform.ide.extension.maven.client.event.ProjectBuiltEvent) */
    @Override
    public void onProjectBuilt(ProjectBuiltEvent event) {
        IDE.removeHandler(event.getAssociatedType(), this);
        if (event.getBuildStatus().getDownloadUrl() != null) {
            warUrl = event.getBuildStatus().getDownloadUrl();
            createApplication();
        }
    }

}
