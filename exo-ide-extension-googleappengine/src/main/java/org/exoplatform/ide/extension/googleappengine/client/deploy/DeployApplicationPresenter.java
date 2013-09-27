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
package org.exoplatform.ide.extension.googleappengine.client.deploy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.component.GWTLoader;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
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
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.client.framework.websocket.rest.AutoBeanUnmarshallerWS;
import org.exoplatform.ide.extension.googleappengine.client.GaeTools;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineAsyncRequestCallback;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEnginePresenter;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineWsRequestCallback;
import org.exoplatform.ide.extension.googleappengine.client.create.CreateApplicationEvent;
import org.exoplatform.ide.extension.googleappengine.client.login.OAuthLoginView;
import org.exoplatform.ide.extension.googleappengine.shared.ApplicationInfo;
import org.exoplatform.ide.extension.googleappengine.shared.GaeUser;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler;
import org.exoplatform.ide.vfs.client.marshal.ProjectUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Presenter for deploying application to Google App Engine, can be as a part of deployment step in wizard.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 16, 2012 5:51:08 PM anya $
 */
public class DeployApplicationPresenter extends GoogleAppEnginePresenter implements HasPaaSActions,
                                                                                    ProjectBuiltHandler, DeployApplicationHandler {
    interface Display {
        HasValue<String> getApplicationIdField();

        HasValue<Boolean> getUseExisting();

        void enableApplicationIdField(boolean enable);

        Composite getView();
    }

    private DeployResultHandler deployResultHandler;

    private Display display;

    /** Google App Engine application's id. */
    private String applicationId;

    /** Flag points, whether to use existed GAE application or create new one. */
    private boolean useExisted;

    /** Application's war URL (for Java only). */
    private String applicationUrl;

    private ProjectModel builtProject;

    private String projectName;

    private ProjectTemplate projectTemplate;

    public DeployApplicationPresenter() {
        IDE.addHandler(DeployApplicationEvent.TYPE, this);
        IDE.getInstance().addControl(new DeployApplicationControl());
    }

    /** Bind display with presenter. */
    public void bindDisplay() {
        display.getUseExisting().addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                useExisted = event.getValue();
                boolean enable = event.getValue();
                display.enableApplicationIdField(enable);
            }
        });

        display.getApplicationIdField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                applicationId = event.getValue();
            }
        });
    }

   /*  *//** @see org.exoplatform.ide.client.framework.paas.PaasComponent#validate() */
   /*
    * @Override public void validate() { Scheduler.get().scheduleDeferred(new ScheduledCommand() {
    * @Override public void execute() { applicationId = display.getApplicationIdField().getValue(); // Check user is logged to
    * Google App Engine. isUserLogged(true); } }); }
    */

    /** @see org.exoplatform.ide.extension.googleappengine.client.deploy.DeployApplicationHandler#onDeployApplication(org.exoplatform.ide
     * .extension.googleappengine.client.deploy.DeployApplicationEvent) */
    @Override
    public void onDeployApplication(DeployApplicationEvent event) {
        currentProject = (event.getProject() != null) ? event.getProject() : currentProject;
        isUserLogged(false);
    }

    /** Before deploying check application type. If it is Java - build it before deploy. */
    private void beforeDeploy(ProjectModel project) {
        if (isAppEngineProject()) {
            applicationUrl = null;
            if (ProjectType.JAVA.value().equals(project.getProjectType())
                || ProjectResolver.APP_ENGINE_JAVA.equals(project.getProjectType())
                || ProjectType.WAR.value().equals(project.getProjectType())
                || ProjectType.JSP.value().equals(project.getProjectType())) {
                buildProject(project);
            } else {
                deployApplication(project);
            }
        } else {
            Dialogs.getInstance().showError(GoogleAppEngineExtension.GAE_LOCALIZATION.notAppEngineProjectError());
        }
    }

    /** Perform deploying application to Google App Engine. */
    public void deployApplication(final ProjectModel project) {
        try {
            AutoBean<ApplicationInfo> applicationInfo = GoogleAppEngineExtension.AUTO_BEAN_FACTORY.applicationInfo();
            AutoBeanUnmarshallerWS<ApplicationInfo> unmarshaller =
                    new AutoBeanUnmarshallerWS<ApplicationInfo>(applicationInfo);

            IDE.fireEvent(new OutputEvent(GoogleAppEngineExtension.GAE_LOCALIZATION.deployApplicationMessage(project
                                                                                                                     .getName()),
                                          Type.INFO));
            GoogleAppEngineClientService.getInstance().update(currentVfs.getId(), project, applicationUrl,
                                                              new GoogleAppEngineWsRequestCallback<ApplicationInfo>(unmarshaller) {

                                                                  @Override
                                                                  protected void onSuccess(ApplicationInfo result) {
                                                                      StringBuilder link = new StringBuilder("<a href='");
                                                                      link.append(result.getWebURL()).append("' target='_blank'>")
                                                                          .append(result.getWebURL())
                                                                          .append("</a>");
                                                                      IDE.fireEvent(new OutputEvent(
                                                                              GoogleAppEngineExtension.GAE_LOCALIZATION
                                                                                                      .deployApplicationSuccess(
                                                                                                              project.getName(),
                                                                                                              link.toString()), Type.INFO));
                                                                      IDE.fireEvent(new RefreshBrowserEvent());
                                                                  }
                                                              });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Build Java project before deploy. */
    private void buildProject(ProjectModel project) {
        this.applicationUrl = null;
        this.builtProject = project;
        IDE.addHandler(ProjectBuiltEvent.TYPE, this);
        IDE.fireEvent(new BuildProjectEvent(project));
    }

    /** @see org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler#onProjectBuilt(org.exoplatform.ide.extension.maven
     * .client.event.ProjectBuiltEvent) */
    @Override
    public void onProjectBuilt(ProjectBuiltEvent event) {
        IDE.removeHandler(ProjectBuiltEvent.TYPE, this);
        if (event.getBuildStatus().getDownloadUrl() != null) {
            applicationUrl = event.getBuildStatus().getDownloadUrl();
            deployApplication(builtProject);
        }
    }

    /**
     * Sets the application's id to configuration file (appengine-web.xml or app.yaml).
     *
     * @param appId
     *         application's id
     */
    private void setApplicationId(String appId, final ProjectModel project) {
        try {
            GoogleAppEngineClientService.getInstance().setApplicationId(currentVfs.getId(), project.getId(), appId,
                                                                        new GoogleAppEngineAsyncRequestCallback<Object>() {

                                                                            @Override
                                                                            protected void onSuccess(Object result) {
                                                                                beforeDeploy(project);
                                                                            }
                                                                        });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Checks if user is logged to Google App Engine.
     *
     * @param wizardStep
     */
    private void isUserLogged(final boolean wizardStep) {
        AutoBean<GaeUser> user = GoogleAppEngineExtension.AUTO_BEAN_FACTORY.user();
        AutoBeanUnmarshaller<GaeUser> unmarshaller = new AutoBeanUnmarshaller<GaeUser>(user);
        try {
            GoogleAppEngineClientService.getInstance().getLoggedUser(
                    new GoogleAppEngineAsyncRequestCallback<GaeUser>(unmarshaller) {

                        @Override
                        protected void onSuccess(GaeUser result) {
                            if (!GaeTools.isAuthenticatedInAppEngine(result.getToken())) {
                                new OAuthLoginView();
                                return;
                            }
                            if (wizardStep) {
                                if (display.getUseExisting().getValue() && (applicationId == null || applicationId.isEmpty())) {
                                    Dialogs.getInstance().showError(
                                            GoogleAppEngineExtension.GAE_LOCALIZATION.deployApplicationEmptyIdMessage());
                                } else {
                                    createProject(projectTemplate);
                                }
                            } else {
                                beforeDeploy(currentProject);
                            }
                        }

                        /**
                         * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineAsyncRequestCallback#onFailure(java
                         * .lang.Throwable)
                         */
                        @Override
                        protected void onFailure(Throwable exception) {
                            super.onFailure(exception);
                        }
                    });
        } catch (RequestException e) {
        }
    }

    /**
     * @see org.exoplatform.ide.client.framework.paas.recent.HasPaaSActions#deploy(org.exoplatform.ide.client.framework.template
     * .ProjectTemplate,
     *      org.exoplatform.ide.client.framework.paas.recent.DeployResultHandler)
     */
    @Override
    public void deploy(ProjectTemplate projectTemplate, DeployResultHandler deployResultHandler) {
        this.projectTemplate = projectTemplate;
        this.deployResultHandler = deployResultHandler;
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                applicationId = display.getApplicationIdField().getValue();
                // Check user is logged to Google App Engine.
                isUserLogged(true);
            }
        });
    }

    /**
     * @see org.exoplatform.ide.client.framework.paas.HasPaaSActions#getDeployView(java.lang.String,
     *      org.exoplatform.ide.client.framework.project.ProjectType, org.exoplatform.ide.client.framework.paas.InitializeDeployViewHandler)
     */
    @Override
    public Composite getDeployView(String projectName, ProjectType projectType, InitializeDeployViewHandler initializeDeployViewHandler) {
        this.projectName = projectName;
        if (display == null) {
            display = GWT.create(Display.class);
            bindDisplay();
        }
        display.getUseExisting().setValue(false);
        display.enableApplicationIdField(false);
        display.getApplicationIdField().setValue("");
        return display.getView();
    }

    private void createProject(ProjectTemplate projectTemplate) {
        final Loader loader = new GWTLoader();
        // TODO
        loader.setMessage("Creating project...");
        loader.show();
        try {
            TemplateService.getInstance().createProjectFromTemplate(currentVfs.getId(), currentVfs.getRoot().getId(),
                                                                    projectName, projectTemplate.getName(),
                                                                    new AsyncRequestCallback<ProjectModel>(
                                                                            new ProjectUnmarshaller(new ProjectModel())) {

                                                                        @Override
                                                                        protected void onSuccess(final ProjectModel result) {
                                                                            loader.hide();
                                                                            deployResultHandler.onProjectCreated(result);

                                                                            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                                                                                @Override
                                                                                public void execute() {
                                                                                    if (useExisted) {
                                                                                        setApplicationId(applicationId, result);
                                                                                    } else {
                                                                                        IDE.fireEvent(new CreateApplicationEvent(result));
                                                                                    }
                                                                                }
                                                                            });
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

    /**
     * @see org.exoplatform.ide.client.framework.paas.recent.HasPaaSActions#deploy(org.exoplatform.ide.vfs.client.model.ProjectModel,
     *      org.exoplatform.ide.client.framework.paas.recent.DeployResultHandler)
     */
    @Override
    public void deploy(ProjectModel project, DeployResultHandler deployResultHandler) {
        this.deployResultHandler = deployResultHandler;
        if (useExisted) {
            setApplicationId(applicationId, project);
        } else {
            IDE.fireEvent(new CreateApplicationEvent(project));
        }
    }

    /** @see org.exoplatform.ide.client.framework.paas.HasPaaSActions#validate() */
    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public void deployFirstTime(String projectName, ProjectTemplate projectTemplate, final DeployResultHandler deployResultHandler) {
        this.projectTemplate = projectTemplate;
        this.deployResultHandler = deployResultHandler;
        this.projectName = projectName;

        if (display == null) {
            display = GWT.create(Display.class);
            bindDisplay();
        }

        display.getUseExisting().setValue(false);
        display.enableApplicationIdField(false);
        display.getApplicationIdField().setValue("");

        isUserLogged(true);
    }
}
