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
package com.codenvy.ide.ext.appfog.client.wizard;

import com.codenvy.ide.api.event.RefreshBrowserEvent;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.template.CreateProjectProvider;
import com.codenvy.ide.api.template.TemplateAgent;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPagePresenter;
import com.codenvy.ide.api.ui.wizard.WizardPagePresenter;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.appfog.client.*;
import com.codenvy.ide.ext.appfog.client.login.LoggedInHandler;
import com.codenvy.ide.ext.appfog.client.login.LoginCanceledHandler;
import com.codenvy.ide.ext.appfog.client.login.LoginPresenter;
import com.codenvy.ide.ext.appfog.client.marshaller.AppFogApplicationUnmarshaller;
import com.codenvy.ide.ext.appfog.client.marshaller.AppFogApplicationUnmarshallerWS;
import com.codenvy.ide.ext.appfog.client.marshaller.InfrasUnmarshaller;
import com.codenvy.ide.ext.appfog.shared.AppfogApplication;
import com.codenvy.ide.ext.appfog.shared.InfraDetail;
import com.codenvy.ide.extension.maven.client.event.BuildProjectEvent;
import com.codenvy.ide.extension.maven.client.event.ProjectBuiltEvent;
import com.codenvy.ide.extension.maven.client.event.ProjectBuiltHandler;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.WebSocketException;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;

/**
 * Presenter for creating application on AppFog from New project wizard.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class AppFogPagePresenter extends AbstractWizardPagePresenter implements AppFogPageView.ActionDelegate, ProjectBuiltHandler {
    private AppFogPageView             view;
    private EventBus                   eventBus;
    private String                     server;
    private String                     url;
    /** Public url to war file of application. */
    private String                     warUrl;
    private String                     projectName;
    private Project                    project;
    private ResourceProvider           resourcesProvider;
    private ConsolePart                console;
    private AppfogLocalizationConstant constant;
    private HandlerRegistration        projectBuildHandler;
    private LoginPresenter             loginPresenter;
    private AppfogClientService        service;
    private TemplateAgent              templateAgent;
    private CreateProjectProvider      createProjectProvider;
    private NotificationManager        notificationManager;
    private InfraDetail                currentInfra;
    private Notification               notification;
    private JsonArray<InfraDetail>     infras;
    private boolean                    isLogined;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param resourcesProvider
     * @param console
     * @param resources
     * @param constant
     * @param loginPresenter
     * @param service
     * @param templateAgent
     * @param notificationManager
     */
    @Inject
    protected AppFogPagePresenter(AppFogPageView view, EventBus eventBus, ResourceProvider resourcesProvider, ConsolePart console,
                                  AppfogResources resources, AppfogLocalizationConstant constant, LoginPresenter loginPresenter,
                                  AppfogClientService service, TemplateAgent templateAgent, NotificationManager notificationManager) {

        super("Deploy project to AppFog", resources.appfog48());

        this.view = view;
        this.view.setDelegate(this);
        this.eventBus = eventBus;
        this.resourcesProvider = resourcesProvider;
        this.console = console;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.templateAgent = templateAgent;
        this.notificationManager = notificationManager;
    }

    /** {@inheritDoc} */
    @Override
    public void onNameChanged() {
        projectName = view.getName();

        delegate.updateControls();
    }

    /** {@inheritDoc} */
    @Override
    public void onUrlChanged() {
        url = view.getUrl();

        delegate.updateControls();
    }

    /** {@inheritDoc} */
    @Override
    public void onInfraChanged() {
        currentInfra = findInfraByName(view.getInfra());
        updateUrlField();
    }

    /**
     * Finds infrastructure by name.
     *
     * @param infraName
     *         infrastructure's name
     * @return infrastructure
     */
    private InfraDetail findInfraByName(String infraName) {
        for (int i = 0; i < infras.size(); i++) {
            InfraDetail infra = infras.get(i);
            if (infraName.equals(infra.getName())) {
                return infra;
            }
        }
        return null;
    }

    /** Updates url's field. */
    private void updateUrlField() {
        url = view.getName() + '.' + currentInfra.getBase();
        view.setUrl(url);
    }

    /** {@inheritDoc} */
    @Override
    public WizardPagePresenter flipToNext() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean canFinish() {
        return validate();
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        return validate();
    }

    /** Checking entered information on view. */
    public boolean validate() {
        if (isLogined) {
            return view.getName() != null && !view.getName().isEmpty() && view.getUrl() != null && !view.getUrl().isEmpty() &&
                   currentInfra != null;
        }
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public String getNotice() {
        if (!isLogined) {
            return "This project will be created without deploy on AppFog.";
        } else if (view.getName().isEmpty()) {
            return "Please, enter a application's name.";
        } else if (view.getUrl().isEmpty()) {
            return "Please, enter application's url.";
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        createProjectProvider = templateAgent.getSelectedTemplate().getCreateProjectProvider();
        projectName = createProjectProvider.getProjectName();

        server = AppFogExtension.DEFAULT_SERVER;
        view.setTarget(server);
        view.setName(projectName);
        getInfras(AppFogExtension.DEFAULT_SERVER);
        isLogined = true;

        container.setWidget(view);
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

    /** Create application on AppFog by sending request over WebSocket or HTTP. */
    private void createApplication() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                createApplication();
            }
        };
        // TODO Need to create some special service after this class
        // This class still doesn't have analog.
        // JobManager.get().showJobSeparated();
        AppFogApplicationUnmarshallerWS unmarshaller = new AppFogApplicationUnmarshallerWS();
        String message = constant.createApplicationStarted(project.getName());
        notification = new Notification(message, Notification.Status.PROGRESS);
        notificationManager.showNotification(notification);


        try {
            // Application will be started after creation (IDE-1618)
            boolean noStart = false;
            service.createWS(server, projectName, null, url, 0, 0, noStart, resourcesProvider.getVfsId(), project.getId(), warUrl,
                             currentInfra.getInfra(), new AppfogRESTfulRequestCallback<AppfogApplication>(unmarshaller, loggedInHandler,
                                                                                                          null, server, eventBus, constant,
                                                                                                          loginPresenter,
                                                                                                          notificationManager) {
                @Override
                protected void onSuccess(final AppfogApplication appFogApplication) {
                    project.refreshProperties(new AsyncCallback<Project>() {
                        @Override
                        public void onSuccess(Project project) {
                            onAppCreatedSuccess(appFogApplication);
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            Log.error(AppFogPagePresenter.class, "Can not refresh properties", caught);
                        }
                    });
                }

                @Override
                protected void onFailure(Throwable exception) {
                    notification.setStatus(FINISHED);
                    notification.setType(ERROR);
                    notification.setMessage(constant.applicationCreationFailed());
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
        AppFogApplicationUnmarshaller unmarshaller = new AppFogApplicationUnmarshaller();

        try {
            // Application will be started after creation (IDE-1618)
            boolean noStart = false;
            service.create(server, projectName, null, url, 0, 0, noStart, resourcesProvider.getVfsId(), project.getId(), warUrl,
                           currentInfra.getInfra(), new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller, loggedInHandler, null,
                                                                                                      server, eventBus, constant,
                                                                                                      loginPresenter, notificationManager) {
                @Override
                protected void onSuccess(final AppfogApplication appFogApplication) {
                    project.refreshProperties(new AsyncCallback<Project>() {
                        @Override
                        public void onSuccess(Project project) {
                            onAppCreatedSuccess(appFogApplication);
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            Log.error(AppFogPagePresenter.class, "Can not refresh properties", caught);
                        }
                    });
                }

                @Override
                protected void onFailure(Throwable exception) {
                    notification.setStatus(FINISHED);
                    notification.setType(ERROR);
                    notification.setMessage(constant.applicationCreationFailed());
                    super.onFailure(exception);
                }
            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            notification.setStatus(FINISHED);
            notification.setType(ERROR);
            notification.setMessage(constant.applicationCreationFailed());
        }
    }

    /**
     * Performs action when application successfully created.
     *
     * @param app
     *         {@link AppfogApplication} which is created
     */
    private void onAppCreatedSuccess(AppfogApplication app) {
        warUrl = null;
        String msg = constant.applicationCreatedSuccessfully(app.getName());
        if ("STARTED".equals(app.getState())) {
            if (app.getUris().isEmpty()) {
                msg += "<br>" + constant.applicationStartedWithNoUrls();
            } else {
                msg += "<br>" + constant.applicationStartedOnUrls(app.getName(), getAppUrlsAsString(app));
            }
        }
        notification.setStatus(FINISHED);
        notification.setType(INFO);
        notification.setMessage(msg);
        console.print(msg);
        eventBus.fireEvent(new RefreshBrowserEvent(project));
    }

    /**
     * Returns application URLs as string.
     *
     * @param application
     *         {@link AppfogApplication AppFog application}
     * @return application URLs
     */
    private String getAppUrlsAsString(AppfogApplication application) {
        String appUris = "";
        JsonArray<String> uris = application.getUris();
        for (int i = 0; i < uris.size(); i++) {
            String uri = uris.get(i);
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

    /** Get the list of infrastructure and put them to select field. */
    private void getInfras(final String server) {
        LoggedInHandler getInfrasHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                isLogined = true;
                getInfras(server);
            }
        };
        LoginCanceledHandler loginCanceledHandler = new LoginCanceledHandler() {
            @Override
            public void onLoginCanceled() {
                isLogined = false;
                delegate.updateControls();
            }
        };
        InfrasUnmarshaller unmarshaller = new InfrasUnmarshaller();

        try {
            service.infras(server, null, null,
                           new AppfogAsyncRequestCallback<JsonArray<InfraDetail>>(unmarshaller, getInfrasHandler, loginCanceledHandler,
                                                                                  server, eventBus, constant, loginPresenter,
                                                                                  notificationManager) {
                               @Override
                               protected void onSuccess(JsonArray<InfraDetail> result) {
                                   if (result.isEmpty()) {
                                       eventBus.fireEvent(new ExceptionThrownEvent(constant.errorGettingInfras()));
                                       Notification notification = new Notification(constant.errorGettingInfras(), ERROR);
                                       notificationManager.showNotification(notification);
                                   } else {
                                       infras = result;

                                       JsonArray<String> infraNames = JsonCollections.createArray();
                                       for (int i = 0; i < result.size(); i++) {
                                           InfraDetail infra = result.get(i);
                                           infraNames.add(infra.getName());
                                       }

                                       view.setInfras(infraNames);
                                       view.setInfra(infraNames.get(0));

                                       currentInfra = infras.get(0);

                                       updateUrlField();
                                       url = view.getUrl();

                                       delegate.updateControls();
                                   }
                               }
                           });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /** Validate action before build project. */
    public void performValidation() {
        LoggedInHandler validateHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                performValidation();
            }
        };

        try {
            service.validateAction("create", server, projectName, null, url, resourcesProvider.getVfsId(), null, 0, 0, true,
                                   new AppfogAsyncRequestCallback<String>(null, validateHandler, null, server, eventBus, constant,
                                                                          loginPresenter, notificationManager) {
                                       @Override
                                       protected void onSuccess(String result) {
                                           beforeDeploy();
                                       }
                                   });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /** Check current project is maven project. */
    private void beforeDeploy() {
        JsonArray<Resource> children = project.getChildren();

        for (int i = 0; i < children.size(); i++) {
            Resource child = children.get(i);
            if (child.isFile() && "pom.xml".equals(child.getName())) {
                buildApplication();
                return;
            }
        }

        createApplication();
    }

    /** Builds application. */
    private void buildApplication() {
        // TODO IDEX-57
        // Replace EventBus Events with direct method calls and DI
        projectBuildHandler = eventBus.addHandler(ProjectBuiltEvent.TYPE, this);
        eventBus.fireEvent(new BuildProjectEvent(project));
    }

    /** {@inheritDoc} */
    @Override
    public void doFinish() {
        createProjectProvider.create(new AsyncCallback<Project>() {
            @Override
            public void onSuccess(Project result) {
                if (isLogined) {
                    deploy(result);
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(AppFogPagePresenter.class, caught);
            }
        });
    }

    /** Deploy project to AppFog. */
    public void deploy(Project project) {
        this.project = project;

        buildApplication();
    }
}