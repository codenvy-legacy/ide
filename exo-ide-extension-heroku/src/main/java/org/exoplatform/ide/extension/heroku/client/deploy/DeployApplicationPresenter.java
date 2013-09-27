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
package org.exoplatform.ide.extension.heroku.client.deploy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.component.GWTLoader;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.job.JobManager;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.paas.DeployResultHandler;
import org.exoplatform.ide.client.framework.paas.HasPaaSActions;
import org.exoplatform.ide.client.framework.paas.InitializeDeployViewHandler;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.template.ProjectTemplate;
import org.exoplatform.ide.client.framework.template.TemplateService;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.extension.heroku.client.*;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.heroku.client.marshaller.Property;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.vfs.client.marshal.ProjectUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.List;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 26, 2012 5:41:46 PM anya $
 */
public class DeployApplicationPresenter implements HasPaaSActions, VfsChangedHandler, LoggedInHandler {
    interface Display {
        HasValue<String> getApplicationNameField();

        HasValue<String> getRemoteNameField();

        Composite getView();
    }

    private static final HerokuLocalizationConstant lb = HerokuExtension.LOCALIZATION_CONSTANT;

    private VirtualFileSystemInfo vfs;

    private Display display;

    private ProjectModel project;

    private DeployResultHandler deployResultHandler;

    private String projectName;

    public DeployApplicationPresenter() {
        IDE.addHandler(VfsChangedEvent.TYPE, this);
    }

    public void bindDisplay() {
    }

    /**
     * Form the message about application creation to display in output.
     *
     * @param properties
     *         application's properties
     * @return {@link String}
     */
    public String formApplicationCreatedMessage(List<Property> properties) {
        if (properties == null) {
            return HerokuExtension.LOCALIZATION_CONSTANT.createApplicationSuccess("");
        }
        StringBuilder message = new StringBuilder("<br> [");
        for (Property property : properties) {
            if ("webUrl".equals(property.getName())) {
                message.append("<b>").append(property.getName()).append("</b>").append(" : ").append("<a href='")
                       .append(property.getValue()).append("' target='_blank'>").append(property.getValue()).append("</a>")
                       .append("<br>");
            } else {
                message.append("<b>").append(property.getName()).append("</b>").append(" : ").append(property.getValue())
                       .append("<br>");
            }
        }
        message.append("] ");
        return HerokuExtension.LOCALIZATION_CONSTANT.createApplicationSuccess(message.toString());
    }

    /** Perform creation of application on Heroku by sending request over WebSocket or HTTP. */
    private void createApplication() {
        String applicationName =
                (display.getApplicationNameField().getValue() == null || display.getApplicationNameField().getValue()
                                                                                .isEmpty()) ? null
                                                                                            : display.getApplicationNameField().getValue();
        String remoteName =
                (display.getRemoteNameField().getValue() == null || display.getRemoteNameField().getValue().isEmpty()) ? null
                                                                                                                       : display
                        .getRemoteNameField().getValue();
        JobManager.get().showJobSeparated();

        try {
            HerokuClientService.getInstance().createApplicationWS(applicationName, vfs.getId(), project.getId(),
                                                                  remoteName, new HerokuRESTfulRequestCallback(this) {
                @Override
                protected void onSuccess(List<Property> properties) {
                    onAppCreatedSuccess(properties);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    super.onFailure(exception);
                    deployResultHandler.onDeployFinished(false);
                }
            });
        } catch (WebSocketException e) {
            createApplicationREST(applicationName, remoteName);
        }
    }

    /** Perform creation of application on Heroku by sending request over HTTP. */
    private void createApplicationREST(String applicationName, String remoteName) {
        try {
            HerokuClientService.getInstance().createApplication(applicationName, vfs.getId(), project.getId(), remoteName,
                                                                new HerokuAsyncRequestCallback(this) {
                                                                    @Override
                                                                    protected void onSuccess(List<Property> properties) {
                                                                        onAppCreatedSuccess(properties);
                                                                    }

                                                                    @Override
                                                                    protected void onFailure(Throwable exception) {
                                                                        super.onFailure(exception);
                                                                        deployResultHandler.onDeployFinished(false);
                                                                    }
                                                                });
        } catch (RequestException e) {
            deployResultHandler.onDeployFinished(false);
        }
    }

    /**
     * Performs action when application successfully created.
     *
     * @param properties
     *         {@link List} of application's {@link Property}
     */
    private void onAppCreatedSuccess(List<Property> properties) {
        IDE.fireEvent(new OutputEvent(formApplicationCreatedMessage(properties), Type.INFO));
        IDE.fireEvent(new RefreshBrowserEvent(project));
        deployResultHandler.onDeployFinished(true);
    }

    /** @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     * .application.event.VfsChangedEvent) */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        this.vfs = event.getVfsInfo();
    }

    /** @see org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler#onLoggedIn(org.exoplatform.ide.extension.heroku.client
     * .login.LoggedInEvent) */
    @Override
    public void onLoggedIn(LoggedInEvent event) {
        IDE.removeHandler(LoggedInEvent.TYPE, this);
        createApplication();
    }

    /** Initialize of the Git-repository by sending request over WebSocket or HTTP. */
    private void initRepository(final ProjectModel project) {
        try {
            GitClientService.getInstance().initWS(vfs.getId(), project.getId(), project.getName(), false,
                                                  new RequestCallback<String>() {
                                                      @Override
                                                      protected void onSuccess(String result) {
                                                          createApplication();
                                                      }

                                                      @Override
                                                      protected void onFailure(Throwable exception) {
                                                          handleGitError(exception);
                                                      }
                                                  });
        } catch (WebSocketException e) {
            initRepositoryREST(project);
        }
    }

    /** Initialize Git repository (sends request over HTTP). */
    private void initRepositoryREST(final ProjectModel project) {
        try {
            GitClientService.getInstance().init(vfs.getId(), project.getId(), project.getName(), false,
                                                new AsyncRequestCallback<String>() {
                                                    @Override
                                                    protected void onSuccess(String result) {
                                                        createApplication();
                                                    }

                                                    @Override
                                                    protected void onFailure(Throwable exception) {
                                                        handleGitError(exception);
                                                    }
                                                });
        } catch (RequestException e) {
            handleGitError(e);
        }
    }

    @Override
    public Composite getDeployView(String projectName, ProjectType projectType, InitializeDeployViewHandler initializeDeployViewHandler) {
        if (display == null) {
            display = GWT.create(Display.class);
            bindDisplay();
        }
        this.projectName = projectName;
        display.getApplicationNameField().setValue("");
        display.getRemoteNameField().setValue("");
        return display.getView();
    }

    @Override
    public void deploy(ProjectTemplate projectTemplate, DeployResultHandler deployResultHandler) {
        this.deployResultHandler = deployResultHandler;
        createProject(projectTemplate);
    }

    /**
     * Create new project from pointed template.
     *
     * @param projectTemplate
     */
    private void createProject(ProjectTemplate projectTemplate) {
        final Loader loader = new GWTLoader();
        loader.setMessage(lb.creatingProject());
        loader.show();
        try {
            TemplateService.getInstance().createProjectFromTemplate(vfs.getId(), vfs.getRoot().getId(), projectName,
                                                                    projectTemplate.getName(),
                                                                    new AsyncRequestCallback<ProjectModel>(
                                                                            new ProjectUnmarshaller(new ProjectModel())) {

                                                                        @Override
                                                                        protected void onSuccess(ProjectModel result) {
                                                                            loader.hide();
                                                                            project = result;
                                                                            deployResultHandler.onProjectCreated(project);
                                                                            initRepository(project);
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

    @Override
    public void deploy(ProjectModel project, DeployResultHandler deployResultHandler) {
        this.project = project;
        this.deployResultHandler = deployResultHandler;
        checkIsGitRepository(project);
    }

    private void checkIsGitRepository(final ProjectModel project) {

        if (project.hasProperty("isGitRepository") && project.getPropertyValue("isGitRepository").equals("true")) {
            createApplication();
        } else {
            initRepository(project);
        }
    }

    /** @see org.exoplatform.ide.client.framework.paas.HasPaaSActions#validate() */
    @Override
    public boolean validate() {
        return true;
    }

    private void handleGitError(Throwable e) {
        String errorMessage =
                (e.getMessage() != null && e.getMessage().length() > 0) ? e.getMessage() : GitExtension.MESSAGES.initFailed();
        IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
    }

    @Override
    public void deployFirstTime(String projectName, ProjectTemplate projectTemplate, DeployResultHandler deployResultHandler) {
        this.deployResultHandler = deployResultHandler;
        this.projectName = projectName;

        if (display == null) {
            display = GWT.create(Display.class);
            bindDisplay();
        }
        this.projectName = projectName;
        String name = projectName + "-" + rand();

        display.getApplicationNameField().setValue(name);
        display.getRemoteNameField().setValue(name);

        createProject(projectTemplate);
    }

    private int rand() {
        return (int)(Math.floor(Math.random() * 999 - 100) + 100);
    }
}
