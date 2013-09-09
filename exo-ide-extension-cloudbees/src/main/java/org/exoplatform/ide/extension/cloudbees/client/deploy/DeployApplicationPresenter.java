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
package org.exoplatform.ide.extension.cloudbees.client.deploy;

import com.google.gwt.core.client.GWT;
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
import org.exoplatform.ide.client.framework.websocket.rest.AutoBeanUnmarshallerWS;
import org.exoplatform.ide.extension.cloudbees.client.*;
import org.exoplatform.ide.extension.cloudbees.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudbees.client.login.LoginCanceledHandler;
import org.exoplatform.ide.extension.cloudbees.client.marshaller.DomainsUnmarshaller;
import org.exoplatform.ide.extension.cloudbees.shared.ApplicationInfo;
import org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltEvent;
import org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltHandler;
import org.exoplatform.ide.extension.jenkins.client.event.BuildApplicationEvent;
import org.exoplatform.ide.vfs.client.marshal.ProjectUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DeployApplicationPresenter.java Dec 5, 2011 1:58:22 PM vereshchaka $
 */
public class DeployApplicationPresenter implements ApplicationBuiltHandler, HasPaaSActions, VfsChangedHandler {
    interface Display {
        HasValue<String> getNameField();

        HasValue<String> getUrlField();

        HasValue<String> getDomainsField();

        /**
         * Set the list of domains.
         *
         * @param domains
         */
        void setDomainValues(String[] domains);

        Composite getView();
    }

    private static final CloudBeesLocalizationConstant lb = CloudBeesExtension.LOCALIZATION_CONSTANT;

    private VirtualFileSystemInfo vfs;

    private Display display;

    private String domain;

    private String name;

    /** Public url to war file of application. */
    private String warUrl;

    private String projectName;

    private ProjectModel project;

    private DeployResultHandler deployResultHandler;

    private InitializeDeployViewHandler initializeDeployViewHandler;

    public DeployApplicationPresenter() {
        IDE.addHandler(VfsChangedEvent.TYPE, this);
    }

    public void bindDisplay() {
        display.getNameField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                name = event.getValue();
                display.getUrlField().setValue(domain + "/" + name);
            }
        });

        display.getDomainsField().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                domain = display.getDomainsField().getValue();
                display.getUrlField().setValue(domain + "/" + name);
            }
        });

    }

    /** @see org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltHandler#onApplicationBuilt(org.exoplatform.ide.extension
     * .jenkins.client.event.ApplicationBuiltEvent) */
    @Override
    public void onApplicationBuilt(ApplicationBuiltEvent event) {
        IDE.removeHandler(event.getAssociatedType(), this);
        if (event.getJobStatus().getArtifactUrl() != null) {
            warUrl = event.getJobStatus().getArtifactUrl();
            createApplication();
        }
    }

    // ----Implementation------------------------

    private void buildApplication() {
        IDE.addHandler(ApplicationBuiltEvent.TYPE, this);
        IDE.fireEvent(new BuildApplicationEvent(project));
    }

    private void getDomains() {
        try {
            CloudBeesClientService.getInstance().getDomains(
                    new CloudBeesAsyncRequestCallback<List<String>>(new DomainsUnmarshaller(new ArrayList<String>()),
                                                                    new LoggedInHandler() {
                                                                        @Override
                                                                        public void onLoggedIn() {
                                                                            getDomains();
                                                                        }
                                                                    }, new LoginCanceledHandler() {
                                                                        @Override
                                                                        public void onLoginCanceled() {
                                                                            initializeDeployViewHandler.onInitializeDeployViewError();
                                                                        }
                                                                    }) {
                        @Override
                        protected void onSuccess(List<String> result) {
                            display.setDomainValues(result.toArray(new String[result.size()]));
                            domain = display.getDomainsField().getValue();
                            display.getNameField().setValue(projectName);
                            name = display.getNameField().getValue();
                            display.getUrlField().setValue(domain + "/" + name);
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Create application on Cloud Bees by sending request over WebSocket or HTTP. */
    private void createApplication() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                createApplication();
            }
        };
        JobManager.get().showJobSeparated();
        AutoBean<ApplicationInfo> autoBean = CloudBeesExtension.AUTO_BEAN_FACTORY.applicationInfo();

        try {
            CloudBeesClientService.getInstance().initializeApplicationWS(
                    domain + "/" + name,
                    vfs.getId(),
                    project.getId(),
                    warUrl,
                    null,
                    new CloudBeesRESTfulRequestCallback<ApplicationInfo>(new AutoBeanUnmarshallerWS<ApplicationInfo>(autoBean),
                                                                         loggedInHandler, null) {
                        @Override
                        protected void onSuccess(ApplicationInfo appInfo) {
                            onCreatedSuccess(appInfo);
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            IDE.fireEvent(new OutputEvent(CloudBeesExtension.LOCALIZATION_CONSTANT
                                                                            .deployApplicationFailureMessage(), Type.INFO));
                            deployResultHandler.onDeployFinished(false);
                            super.onFailure(exception);
                        }
                    });
        } catch (WebSocketException e) {
            createApplicationREST(loggedInHandler);
        }
    }

    /** Create application on Cloud Bees by sending request over HTTP. */
    private void createApplicationREST(LoggedInHandler loggedInHandler) {
        AutoBean<ApplicationInfo> autoBean = CloudBeesExtension.AUTO_BEAN_FACTORY.applicationInfo();
        try {
            CloudBeesClientService.getInstance().initializeApplication(
                    domain + "/" + name,
                    vfs.getId(),
                    project.getId(),
                    warUrl,
                    null,
                    new CloudBeesAsyncRequestCallback<ApplicationInfo>(new AutoBeanUnmarshaller<ApplicationInfo>(autoBean),
                                                                       loggedInHandler, null) {
                        @Override
                        protected void onSuccess(ApplicationInfo appInfo) {
                            onCreatedSuccess(appInfo);
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            IDE.fireEvent(new OutputEvent(CloudBeesExtension.LOCALIZATION_CONSTANT
                                                                            .deployApplicationFailureMessage(), Type.INFO));
                            deployResultHandler.onDeployFinished(false);
                            super.onFailure(exception);
                        }
                    });
        } catch (RequestException e) {
            deployResultHandler.onDeployFinished(false);
            IDE.fireEvent(new OutputEvent(CloudBeesExtension.LOCALIZATION_CONSTANT.deployApplicationFailureMessage(),
                                          Type.INFO));
        }
    }

    private void onCreatedSuccess(ApplicationInfo appInfo) {
        StringBuilder output =
                new StringBuilder(CloudBeesExtension.LOCALIZATION_CONSTANT.deployApplicationSuccess()).append("<br>");
        output.append(CloudBeesExtension.LOCALIZATION_CONSTANT.deployApplicationInfo()).append("<br>");
        output.append(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridId()).append(" : ")
              .append(appInfo.getId()).append("<br>");
        output.append(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridTitle()).append(" : ")
              .append(appInfo.getTitle()).append("<br>");
        output.append(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridServerPool()).append(" : ")
              .append(appInfo.getServerPool()).append("<br>");
        output.append(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridStatus()).append(" : ")
              .append(appInfo.getStatus()).append("<br>");
        output.append(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridContainer()).append(" : ")
              .append(appInfo.getContainer()).append("<br>");
        output.append(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridIdleTimeout()).append(" : ")
              .append(appInfo.getIdleTimeout()).append("<br>");
        output.append(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridMaxMemory()).append(" : ")
              .append(appInfo.getMaxMemory()).append("<br>");
        output.append(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridSecurityMode()).append(" : ")
              .append(appInfo.getSecurityMode()).append("<br>");
        output.append(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridClusterSize()).append(" : ")
              .append(appInfo.getClusterSize()).append("<br>");
        output.append(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridUrl()).append(" : ")
              .append("<a href='").append(appInfo.getUrl()).append("' target='_blank'>").append(appInfo.getUrl())
              .append("</a>").append("<br>");

        IDE.fireEvent(new OutputEvent(output.toString(), Type.INFO));
        IDE.fireEvent(new RefreshBrowserEvent(project));
        deployResultHandler.onDeployFinished(true);
    }

    /** @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     * .application.event.VfsChangedEvent) */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        this.vfs = event.getVfsInfo();
    }

    @Override
    public void deploy(ProjectTemplate projectTemplate, DeployResultHandler deployResultHandler) {
        this.deployResultHandler = deployResultHandler;
        name = display.getNameField().getValue();
        if (name == null || name.isEmpty()) {
            Dialogs.getInstance().showError("Name field must be not empty");
        } else {
            createProject(projectTemplate);
        }
    }

    @Override
    public Composite getDeployView(String projectName, ProjectType projectType, InitializeDeployViewHandler initializeDeployViewHandler) {
        this.projectName = projectName;
        this.initializeDeployViewHandler = initializeDeployViewHandler;
        if (display == null) {
            display = GWT.create(Display.class);
        }
        bindDisplay();
        getDomains();
        return display.getView();
    }

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
                                                                            buildApplication();
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
        buildApplication();
    }

    /** @see org.exoplatform.ide.client.framework.paas.HasPaaSActions#validate() */
    @Override
    public boolean validate() {
        return display.getNameField().getValue() != null && !display.getNameField().getValue().isEmpty()
               && display.getUrlField().getValue() != null && !display.getUrlField().getValue().isEmpty();
    }

    @Override
    public void deployFirstTime(final String projectName, final ProjectTemplate projectTemplate, final DeployResultHandler deployResultHandler) {
        this.projectName = projectName;
        this.deployResultHandler = deployResultHandler;
        if (display == null) {
            display = GWT.create(Display.class);
        }
        bindDisplay();

        display.getNameField().setValue(projectName);
        name = projectName + "_" + rand();

        try {
            TemplateService.getInstance().createProjectFromTemplate(vfs.getId(), vfs.getRoot().getId(), projectName,
                                                                    projectTemplate.getName(),
                                                                    new AsyncRequestCallback<ProjectModel>(
                                                                            new ProjectUnmarshaller(new ProjectModel())) {

                                                                        @Override
                                                                        protected void onSuccess(ProjectModel result) {
                                                                            project = result;
                                                                            deployResultHandler.onProjectCreated(project);
                                                                            getFirstDeployDomains();
//                                                                            buildApplication();
                                                                        }

                                                                        @Override
                                                                        protected void onFailure(Throwable exception) {
                                                                            IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                                        }
                                                                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void getFirstDeployDomains() {
        try {
            CloudBeesClientService.getInstance().getDomains(
                    new CloudBeesAsyncRequestCallback<List<String>>(new DomainsUnmarshaller(new ArrayList<String>()),
                                                                    new LoggedInHandler() {
                                                                        @Override
                                                                        public void onLoggedIn() {
                                                                            getFirstDeployDomains();
                                                                        }
                                                                    }, new LoginCanceledHandler() {
                        @Override
                        public void onLoginCanceled() {
                            if (initializeDeployViewHandler != null) {
                                initializeDeployViewHandler.onInitializeDeployViewError();
                            }
                        }
                    }) {
                        @Override
                        protected void onSuccess(List<String> result) {
                            display.setDomainValues(result.toArray(new String[result.size()]));
                            domain = display.getDomainsField().getValue();
                            display.getNameField().setValue(projectName);
                            name = projectName;
                            display.getUrlField().setValue(domain + "/" + name);
                            buildApplication();
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private int rand() {
        return (int)(Math.floor(Math.random() * 999 - 100) + 100);
    }
}
