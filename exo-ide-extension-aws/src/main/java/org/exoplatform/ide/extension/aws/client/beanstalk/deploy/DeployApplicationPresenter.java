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
package org.exoplatform.ide.extension.aws.client.beanstalk.deploy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.commons.rest.RequestStatusHandler;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.GWTLoader;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.paas.DeployResultHandler;
import org.exoplatform.ide.client.framework.paas.HasPaaSActions;
import org.exoplatform.ide.client.framework.paas.InitializeDeployViewHandler;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.template.ProjectTemplate;
import org.exoplatform.ide.client.framework.template.TemplateService;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.client.AWSLocalizationConstant;
import org.exoplatform.ide.extension.aws.client.AwsAsyncRequestCallback;
import org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService;
import org.exoplatform.ide.extension.aws.client.beanstalk.SolutionStackListUnmarshaller;
import org.exoplatform.ide.extension.aws.client.beanstalk.environments.EnvironmentRequestStatusHandler;
import org.exoplatform.ide.extension.aws.client.beanstalk.environments.EnvironmentStatusChecker;
import org.exoplatform.ide.extension.aws.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.aws.client.login.LoginCanceledHandler;
import org.exoplatform.ide.extension.aws.shared.beanstalk.*;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.ProjectUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: DeployApplicationPresenter.java Sep 25, 2012 11:55:34 AM azatsarynnyy $
 */
public class DeployApplicationPresenter implements HasPaaSActions, VfsChangedHandler, ProjectBuiltHandler {
    interface Display {
        TextFieldItem getNameField();

        TextFieldItem getEnvNameField();

        HasValue<String> getSolutionStackField();

        void setSolutionStackValues(String[] values);

        Composite getView();
    }

    private static final AWSLocalizationConstant LOCALIZATION_CONSTANT = AWSExtension.LOCALIZATION_CONSTANT;

    private VirtualFileSystemInfo vfsInfo;

    private ProjectModel project;

    private Display display;

    /** Public url to war file of application. */
    private String warUrl;

    private DeployResultHandler deployResultHandler;

    private InitializeDeployViewHandler initializeDeployViewHandler;

    private String projectName;

    public DeployApplicationPresenter() {
        IDE.addHandler(VfsChangedEvent.TYPE, this);
    }

    /** Bind display with presenter. */
    private void bindDisplay() {
    }

    /**
     * @see org.exoplatform.ide.client.framework.paas.HasPaaSActions#getDeployView(java.lang.String,
     *      org.exoplatform.ide.client.framework.project.ProjectType, org.exoplatform.ide.client.framework.paas.InitializeDeployViewHandler)
     */
    @Override
    public Composite getDeployView(String projectName, ProjectType projectType, InitializeDeployViewHandler initializeDeployViewHandler) {
        this.projectName = projectName;
        this.initializeDeployViewHandler = initializeDeployViewHandler;
        if (display == null) {
            display = GWT.create(Display.class);
        }
        bindDisplay();
        display.getNameField().setValue(projectName);
        getSolutionStacks(false);
        return display.getView();
    }

    /**
     * @see org.exoplatform.ide.client.framework.paas.HasPaaSActions#deploy(org.exoplatform.ide.client.framework.template.ProjectTemplate,
     *      org.exoplatform.ide.client.framework.paas.DeployResultHandler)
     */
    @Override
    public void deploy(ProjectTemplate projectTemplate, DeployResultHandler deployResultHandler) {
        // TODO using validate() method may be more correctly
        // Check App name
        String appName = display.getNameField().getValue();
        if (appName == null || appName.length() == 0) {
            Dialogs.getInstance().showError(AWSExtension.LOCALIZATION_CONSTANT.validationErrorTitile(),
                                            AWSExtension.LOCALIZATION_CONSTANT.validationErrorSpecifyAppName());
            return;
        }

        // Check Env. name
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

        this.deployResultHandler = deployResultHandler;
        createProject(projectTemplate);
    }

    /**
     * Creates a new project from template.
     *
     * @param projectTemplate
     *         template of the project
     */
    private void createProject(ProjectTemplate projectTemplate) {
        final Loader loader = new GWTLoader();
        loader.setMessage(LOCALIZATION_CONSTANT.creatingProject());
        loader.show();
        try {
            TemplateService.getInstance().createProjectFromTemplate(vfsInfo.getId(), vfsInfo.getRoot().getId(),
                                                                    projectName, projectTemplate.getName(),
                                                                    new AsyncRequestCallback<ProjectModel>(
                                                                            new ProjectUnmarshaller(new ProjectModel())) {
                                                                        @Override
                                                                        protected void onSuccess(ProjectModel result) {
                                                                            loader.hide();
                                                                            project = result;
                                                                            deployResultHandler.onProjectCreated(project);
                                                                            beforeDeploy();
                                                                        }

                                                                        @Override
                                                                        protected void onFailure(Throwable exception) {
                                                                            loader.hide();
                                                                            IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                                        }
                                                                    });
        } catch (RequestException e) {
            loader.hide();
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Builds project if it's a Maven project and creates application. */
    private void beforeDeploy() {
        try {
            VirtualFileSystem.getInstance().getChildren(project,
                                                        new AsyncRequestCallback<List<Item>>(
                                                                new ChildrenUnmarshaller(new ArrayList<Item>())) {
                                                            @Override
                                                            protected void onSuccess(List<Item> result) {
                                                                project.getChildren().setItems(result);
                                                                for (Item i : result) {
                                                                    if (i.getItemType() == ItemType.FILE && "pom.xml".equals(i.getName())) {
                                                                        buildApplication();
                                                                        return;
                                                                    }
                                                                }
                                                                createApplication();
                                                            }

                                                            @Override
                                                            protected void onFailure(Throwable exception) {
                                                                IDE.fireEvent(new ExceptionThrownEvent(exception,
                                                                                                       "Can't receive children of project "
                                                                                                       + project.getName()));
                                                            }
                                                        });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void createApplication() {
        final String applicationName = display.getNameField().getValue();
        CreateApplicationRequest createApplicationRequest =
                AWSExtension.AUTO_BEAN_FACTORY.createApplicationRequest().as();
        createApplicationRequest.setApplicationName(applicationName);
        createApplicationRequest.setDescription("");
        createApplicationRequest.setS3Bucket("");
        createApplicationRequest.setS3Key("");
        createApplicationRequest.setWar(warUrl);

        AutoBean<ApplicationInfo> autoBean = AWSExtension.AUTO_BEAN_FACTORY.applicationInfo();

        try {
            BeanstalkClientService.getInstance().createApplication(
                    vfsInfo.getId(),
                    project.getId(),
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
                            createEnvironment(result.getName());
                            IDE.fireEvent(new RefreshBrowserEvent(project));
                        }

                        @Override
                        protected void processFail(Throwable exception) {
                            deployResultHandler.onDeployFinished(false);

                            String message = AWSExtension.LOCALIZATION_CONSTANT.createApplicationFailed(applicationName);
                            if (exception instanceof ServerException && ((ServerException)exception).getMessage() != null) {
                                message += "<br>" + ((ServerException)exception).getMessage();
                            }
                            IDE.fireEvent(new OutputEvent(message, Type.ERROR));
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void createEnvironment(final String applicationName) {
        final String environmentName = display.getEnvNameField().getValue();
        CreateEnvironmentRequest createEnvironmentRequest =
                AWSExtension.AUTO_BEAN_FACTORY.createEnvironmentRequest().as();
        createEnvironmentRequest.setApplicationName(applicationName);
        createEnvironmentRequest.setDescription("");
        createEnvironmentRequest.setEnvironmentName(environmentName);
        createEnvironmentRequest.setVersionLabel(AWSExtension.INIT_VER_LABEL);
        createEnvironmentRequest.setSolutionStackName(display.getSolutionStackField().getValue());

        AutoBean<EnvironmentInfo> autoBean = AWSExtension.AUTO_BEAN_FACTORY.environmentInfo();
        try {
            BeanstalkClientService.getInstance().createEnvironment(
                    vfsInfo.getId(),
                    project.getId(),
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
                            deployResultHandler.onDeployFinished(false);
                            String message = AWSExtension.LOCALIZATION_CONSTANT.launchEnvironmentFailed(environmentName);
                            if (exception instanceof ServerException && ((ServerException)exception).getMessage() != null) {
                                message += "<br>" + ((ServerException)exception).getMessage();
                            }
                            IDE.fireEvent(new OutputEvent(message, Type.ERROR));
                        }

                        @Override
                        protected void onSuccess(EnvironmentInfo result) {
                            deployResultHandler.onDeployFinished(true);
                            IDE.fireEvent(new OutputEvent(AWSExtension.LOCALIZATION_CONSTANT
                                                                      .launchEnvironmentLaunching(environmentName), Type.INFO));

                            RequestStatusHandler environmentStatusHandler =
                                    new EnvironmentRequestStatusHandler(AWSExtension.LOCALIZATION_CONSTANT
                                                                                    .launchEnvironmentLaunching(result.getName()),
                                                                        AWSExtension.LOCALIZATION_CONSTANT
                                                                                    .launchEnvironmentSuccess(result.getName()));
                            new EnvironmentStatusChecker(vfsInfo, project, result, true, environmentStatusHandler)
                                    .startChecking();
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void buildApplication() {
        IDE.addHandler(ProjectBuiltEvent.TYPE, this);
        IDE.fireEvent(new BuildProjectEvent(project));
    }

    /**
     * @see org.exoplatform.ide.client.framework.paas.HasPaaSActions#deploy(org.exoplatform.ide.vfs.client.model.ProjectModel,
     *      org.exoplatform.ide.client.framework.paas.DeployResultHandler)
     */
    @Override
    public void deploy(ProjectModel project, DeployResultHandler deployResultHandler) {
        // TODO Auto-generated method stub

    }

    /** Get the list of solution stack and put them to the appropriate field. */
    private void getSolutionStacks(final boolean getStartedWizard) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                getSolutionStacks(getStartedWizard);
            }
        };

        LoginCanceledHandler loginCanceledHandler = new LoginCanceledHandler() {
            @Override
            public void onLoginCanceled() {
                initializeDeployViewHandler.onInitializeDeployViewError();
            }
        };

        try {
            BeanstalkClientService.getInstance().getAvailableSolutionStacks(
                    new AwsAsyncRequestCallback<List<SolutionStack>>(new SolutionStackListUnmarshaller(), loggedInHandler, loginCanceledHandler) {
                        @Override
                        protected void onSuccess(List<SolutionStack> result) {
                            List<String> values = new ArrayList<String>();
                            for (SolutionStack solutionStack : result) {
                                //For detail see https://jira.exoplatform.org/browse/IDE-1951
                                if (solutionStack.getPermittedFileTypes().contains("war")) {
                                    values.add(solutionStack.getName());
                                }
                            }
                            display.setSolutionStackValues(values.toArray(new String[values.size()]));
                            if (getStartedWizard) {
                                beforeDeploy();
                            }
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

    /** @see org.exoplatform.ide.client.framework.paas.HasPaaSActions#validate() */
    @Override
    public boolean validate() {
        return display.getNameField().getValue() != null && !display.getNameField().getValue().isEmpty()
               && display.getEnvNameField().getValue() != null && !display.getEnvNameField().getValue().isEmpty()
               && display.getSolutionStackField().getValue() != null && !display.getSolutionStackField().getValue().isEmpty();
    }

    /** @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent) */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        vfsInfo = event.getVfsInfo();
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


    @Override
    public void deployFirstTime(String projectName, ProjectTemplate projectTemplate, final DeployResultHandler deployResultHandler) {
        this.projectName = projectName;
        this.deployResultHandler = deployResultHandler;

        if (display == null) {
            display = GWT.create(Display.class);
        }
        bindDisplay();
        display.getNameField().setValue(projectName + "-" + rand());
        display.getEnvNameField().setValue(projectName + "-env-" + rand());

        final Loader loader = new GWTLoader();
        loader.setMessage(LOCALIZATION_CONSTANT.creatingProject());
        loader.show();
        try {
            TemplateService.getInstance().createProjectFromTemplate(vfsInfo.getId(), vfsInfo.getRoot().getId(),
                                                                    projectName, projectTemplate.getName(),
                                                                    new AsyncRequestCallback<ProjectModel>(
                                                                            new ProjectUnmarshaller(new ProjectModel())) {
                                                                        @Override
                                                                        protected void onSuccess(ProjectModel result) {
                                                                            loader.hide();
                                                                            project = result;
                                                                            deployResultHandler.onProjectCreated(project);
                                                                            getSolutionStacks(true);
                                                                        }

                                                                        @Override
                                                                        protected void onFailure(Throwable exception) {
                                                                            loader.hide();
                                                                            IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                                        }
                                                                    });
        } catch (RequestException e) {
            loader.hide();
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private int rand() {
        return (int)(Math.floor(Math.random() * 999 - 100) + 100);
    }
}
