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
package org.exoplatform.ide.extension.appfog.client.deploy;

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
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.paas.DeployResultHandler;
import org.exoplatform.ide.client.framework.paas.HasPaaSActions;
import org.exoplatform.ide.client.framework.paas.InitializeDeployViewHandler;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.template.ProjectTemplate;
import org.exoplatform.ide.client.framework.template.TemplateService;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.AutoBeanUnmarshallerWS;
import org.exoplatform.ide.extension.appfog.client.*;
import org.exoplatform.ide.extension.appfog.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.appfog.client.login.LoginCanceledHandler;
import org.exoplatform.ide.extension.appfog.client.marshaller.InfrasUnmarshaller;
import org.exoplatform.ide.extension.appfog.shared.AppfogApplication;
import org.exoplatform.ide.extension.appfog.shared.InfraDetail;
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
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class DeployApplicationPresenter implements ProjectBuiltHandler, HasPaaSActions, VfsChangedHandler {
    interface Display {
        HasValue<String> getNameField();

        HasValue<String> getUrlField();

        HasValue<String> getServerField();

        HasValue<String> getInfraField();

        void setServerValue(String server);

        void setInfraValues(String[] infras);

        Composite getView();
    }

    private static final AppfogLocalizationConstant lb = AppfogExtension.LOCALIZATION_CONSTANT;

    private VirtualFileSystemInfo vfs;

    private Display display;

    private String server;

    private String name;

    private String url;

    private InfraDetail currentInfra;

    private List<InfraDetail> infras;

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
            }
        });

        display.getUrlField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                url = event.getValue();
            }
        });

        display.getInfraField().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                currentInfra = findInfraByName(display.getInfraField().getValue());
                updateUrlField();
            }
        });

        name = display.getNameField().getValue();
    }

    @Override
    public void onProjectBuilt(ProjectBuiltEvent event) {
        IDE.removeHandler(event.getAssociatedType(), this);
        if (event.getBuildStatus().getDownloadUrl() != null) {
            warUrl = event.getBuildStatus().getDownloadUrl();
            createApplication();
        }
    }

    private InfraDetail findInfraByName(String infraName) {
        for (InfraDetail infra : infras) {
            if (infraName.equals(infra.getName())) {
                return infra;
            }
        }
        return null;
    }

    private void updateUrlField() {
        url = display.getNameField().getValue() + '.' + currentInfra.getBase();
        display.getUrlField().setValue(url);
    }

    // ----Implementation------------------------

    private void buildApplication() {
        IDE.addHandler(ProjectBuiltEvent.TYPE, this);
        IDE.fireEvent(new BuildProjectEvent(project));
    }

    /** Create application on AppFog by sending request over WebSocket or HTTP. */
    private void createApplication() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                createApplication();
            }
        };
        JobManager.get().showJobSeparated();

        try {
            AutoBean<AppfogApplication> appfogApplication = AppfogExtension.AUTO_BEAN_FACTORY.appfogApplication();
            AutoBeanUnmarshallerWS<AppfogApplication> unmarshaller =
                    new AutoBeanUnmarshallerWS<AppfogApplication>(appfogApplication);

            // Application will be started after creation (IDE-1618)
            boolean noStart = false;
            AppfogClientService.getInstance().createWS(server, name, null, url, 0, 0, noStart, vfs.getId(),
                                                       project.getId(), warUrl, currentInfra.getInfra(),
                                                       new AppfogRESTfulRequestCallback<AppfogApplication>(unmarshaller, loggedInHandler,
                                                                                                           null, server) {
                                                           @Override
                                                           protected void onSuccess(AppfogApplication result) {
                                                               onAppCreatedSuccess(result);
                                                           }

                                                           @Override
                                                           protected void onFailure(Throwable exception) {
                                                               deployResultHandler.onDeployFinished(false);
                                                               IDE.fireEvent(new OutputEvent(lb.applicationCreationFailed(),
                                                                                             OutputMessage.Type.INFO));
                                                               super.onFailure(exception);
                                                           }
                                                       });
        } catch (WebSocketException e) {
            createApplicationREST(loggedInHandler);
        }
    }

    /**
     * Create application on AppFog by sending request over HTTP.
     *
     * @param loggedInHandler
     *         handler that should be called after success login
     */
    private void createApplicationREST(LoggedInHandler loggedInHandler) {
        try {
            AutoBean<AppfogApplication> appfogApplication = AppfogExtension.AUTO_BEAN_FACTORY.appfogApplication();
            AutoBeanUnmarshaller<AppfogApplication> unmarshaller =
                    new AutoBeanUnmarshaller<AppfogApplication>(appfogApplication);

            // Application will be started after creation (IDE-1618)
            boolean noStart = false;
            AppfogClientService.getInstance().create(server, name, null, url, 0, 0, noStart, vfs.getId(), project.getId(),
                                                     warUrl, currentInfra.getInfra(),
                                                     new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller, loggedInHandler, null,
                                                                                                       server) {
                                                         @Override
                                                         protected void onSuccess(AppfogApplication result) {
                                                             onAppCreatedSuccess(result);
                                                         }

                                                         @Override
                                                         protected void onFailure(Throwable exception) {
                                                             deployResultHandler.onDeployFinished(false);
                                                             IDE.fireEvent(new OutputEvent(lb.applicationCreationFailed(),
                                                                                           OutputMessage.Type.INFO));
                                                             super.onFailure(exception);
                                                         }
                                                     });
        } catch (RequestException e) {
            deployResultHandler.onDeployFinished(false);
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Performs action when application successfully created.
     *
     * @param app
     *         @link AppfogApplication} which is created
     */
    private void onAppCreatedSuccess(AppfogApplication app) {
        warUrl = null;
        String msg = lb.applicationCreatedSuccessfully(app.getName());
        if ("STARTED".equals(app.getState())) {
            if (app.getUris().isEmpty()) {
                msg += "<br>" + lb.applicationStartedWithNoUrls();
            } else {
                msg += "<br>" + lb.applicationStartedOnUrls(app.getName(), getAppUrlsAsString(app));
            }
        }
        deployResultHandler.onDeployFinished(true);
        IDE.fireEvent(new OutputEvent(msg, OutputMessage.Type.INFO));
        IDE.fireEvent(new RefreshBrowserEvent(project));
    }

    private String getAppUrlsAsString(AppfogApplication application) {
        String appUris = "";
        for (String uri : application.getUris()) {
            if (!uri.startsWith("http")) {
                uri = "http://" + uri;
            }
            appUris += ", " + "<a href=\"" + uri + "\" target=\"_blank\">" + uri + "</a>";
        }
        if (!appUris.isEmpty()) {
            // crop unnecessary symbols
            appUris = appUris.substring(2);
        }
        return appUris;
    }

    private void getInfras(final String server, final boolean startedWizard) {
        LoggedInHandler getInfrasHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                getInfras(server, startedWizard);
            }
        };
        LoginCanceledHandler loginCanceledHandler = new LoginCanceledHandler() {
            @Override
            public void onLoginCanceled() {
                if (initializeDeployViewHandler != null) {
                    initializeDeployViewHandler.onInitializeDeployViewError();
                }
            }
        };

        try {
            AppfogClientService.getInstance().infras(
                    server,
                    null,
                    null,
                    new AppfogAsyncRequestCallback<List<InfraDetail>>(new InfrasUnmarshaller(new ArrayList<InfraDetail>()),
                                                                      getInfrasHandler, loginCanceledHandler, server) {
                        @Override
                        protected void onSuccess(List<InfraDetail> result) {
                            if (result.isEmpty()) {
                                IDE.fireEvent(new ExceptionThrownEvent(AppfogExtension.LOCALIZATION_CONSTANT.errorGettingInfras()));
                            } else {
                                infras = result;

                                List<String> infraNames = new ArrayList<String>(result.size());
                                for (InfraDetail infra : result) {
                                    infraNames.add(infra.getName());
                                }

                                display.getInfraField().setValue(infraNames.get(0));
                                display.setInfraValues(infraNames.toArray(new String[infraNames.size()]));

                                currentInfra = infras.get(0);

                                updateUrlField();
                                url = display.getUrlField().getValue();
                                if (startedWizard) {
                                    beforeDeploy();
                                }
                            }
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    public void performValidation() {
        LoggedInHandler validateHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                performValidation();
            }
        };

        try {
            AppfogClientService.getInstance().validateAction("create", server, name, null, url, vfs.getId(), null, 0, 0,
                                                             true,
                                                             new AppfogAsyncRequestCallback<String>(null, validateHandler, null, server) {
                                                                 @Override
                                                                 protected void onSuccess(String result) {
                                                                     beforeDeploy();
                                                                 }
                                                             });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        this.vfs = event.getVfsInfo();
    }

    @Override
    public void deploy(ProjectTemplate projectTemplate, DeployResultHandler deployResultHandler) {
        this.deployResultHandler = deployResultHandler;
        if (display.getInfraField().getValue() == null || display.getInfraField().getValue().isEmpty()
            || currentInfra == null) {
            Dialogs.getInstance().showError("Infrastructure field must be valid and not empty.");
        } else {
            createProject(display.getNameField().getValue(), projectTemplate, false);
        }
    }

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
                                                                                                       "Can't receive project children "
                                                                                                       + project.getName()));
                                                            }
                                                        });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
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
        display.setServerValue(AppfogExtension.DEFAULT_SERVER);
        display.getNameField().setValue(projectName);
        getInfras(AppfogExtension.DEFAULT_SERVER, false);
        server = display.getServerField().getValue();
        bindDisplay();
        return display.getView();
    }

    private void createProject(String name, ProjectTemplate projectTemplate, final boolean startedWizard) {
        final Loader loader = new GWTLoader();
        loader.setMessage(lb.creatingProject());
        loader.show();
        try {
            TemplateService.getInstance().createProjectFromTemplate(
                    vfs.getId(),
                    vfs.getRoot().getId(),
                    name,
                    projectTemplate.getName(),
                    new AsyncRequestCallback<ProjectModel>(new ProjectUnmarshaller(new ProjectModel())) {

                        @Override
                        protected void onSuccess(ProjectModel result) {
                            loader.hide();
                            project = result;
                            deployResultHandler.onProjectCreated(project);
                            if (startedWizard) {
                                getInfras(server, startedWizard);
                            } else {
                                beforeDeploy();
                            }
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
        beforeDeploy();
    }

    @Override
    public boolean validate() {
        return display.getNameField().getValue() != null && !display.getNameField().getValue().isEmpty()
               && display.getUrlField().getValue() != null && !display.getUrlField().getValue().isEmpty();
        //         && display.getInfraField().getValue() != null && !display.getInfraField().getValue().isEmpty();
    }

    @Override
    public void deployFirstTime(final String projectName, final ProjectTemplate projectTemplate, final DeployResultHandler deployResultHandler) {
        this.deployResultHandler = deployResultHandler;
        this.projectName = projectName;

        if (display == null) {
            display = GWT.create(Display.class);
        }

        server = AppfogExtension.DEFAULT_SERVER;
        name = projectName + "-" + rand();
        display.setServerValue(server);
        display.getNameField().setValue(name);

        bindDisplay();

        createProject(name, projectTemplate, true);
    }

    private int rand() {
        return (int)(Math.floor(Math.random() * 999 - 100) + 100);
    }
}
