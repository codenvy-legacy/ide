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
package com.codenvy.ide.extension.cloudfoundry.client.wizard;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.event.RefreshBrowserEvent;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.wizard.paas.AbstractPaasPage;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.extension.cloudfoundry.client.*;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoginCanceledHandler;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoginPresenter;
import com.codenvy.ide.extension.cloudfoundry.client.marshaller.CloudFoundryApplicationUnmarshaller;
import com.codenvy.ide.extension.cloudfoundry.client.marshaller.CloudFoundryApplicationUnmarshallerWS;
import com.codenvy.ide.extension.cloudfoundry.client.marshaller.SystemInfoUnmarshaller;
import com.codenvy.ide.extension.cloudfoundry.client.marshaller.TargetsUnmarshaller;
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import com.codenvy.ide.extension.cloudfoundry.shared.SystemInfo;
import com.codenvy.ide.extension.maven.client.event.BuildProjectEvent;
import com.codenvy.ide.extension.maven.client.event.ProjectBuiltEvent;
import com.codenvy.ide.extension.maven.client.event.ProjectBuiltHandler;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.WebSocketException;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PROJECT;
import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PROJECT_NAME;
import static com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryExtension.ID;

/**
 * Presenter for creating application on CloudFoundry from New project wizard.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class CloudFoundryPagePresenter extends AbstractPaasPage implements CloudFoundryPageView.ActionDelegate, ProjectBuiltHandler {
    private CloudFoundryPageView             view;
    private EventBus                         eventBus;
    private String                           server;
    private String                           name;
    private String                           url;
    /** Public url to war file of application. */
    private String                           warUrl;
    private String                           projectName;
    private Project                          project;
    private ResourceProvider                 resourcesProvider;
    private ConsolePart                      console;
    private CloudFoundryLocalizationConstant constant;
    private HandlerRegistration              projectBuildHandler;
    private LoginPresenter                   loginPresenter;
    private CloudFoundryClientService        service;
    private CloudFoundryExtension.PAAS_PROVIDER paasProvider = CloudFoundryExtension.PAAS_PROVIDER.CLOUD_FOUNDRY;
    private boolean        isLogined;
    private CommitCallback callback;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param resourcesProvider
     * @param resources
     * @param console
     * @param constant
     * @param loginPresenter
     * @param service
     */
    @Inject
    protected CloudFoundryPagePresenter(CloudFoundryPageView view, EventBus eventBus, ResourceProvider resourcesProvider,
                                        CloudFoundryResources resources, ConsolePart console, CloudFoundryLocalizationConstant constant,
                                        LoginPresenter loginPresenter, CloudFoundryClientService service) {
        super("Deploy project to Cloud Foundry", resources.cloudFoundry48(), ID);

        this.view = view;
        this.view.setDelegate(this);
        this.eventBus = eventBus;
        this.resourcesProvider = resourcesProvider;
        this.console = console;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.service = service;
    }

    /** {@inheritDoc} */
    @Override
    public void onNameChanged() {
        name = view.getName();

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
    public void onServerChanged() {
        server = view.getServer();
        // if url set automatically, than try to create url using server and name
        String target = view.getServer();
        String sufix = target.substring(target.indexOf("."));
        String oldUrl = view.getUrl();
        String prefix = "<name>";
        if (!oldUrl.isEmpty() && oldUrl.contains(".")) {
            prefix = oldUrl.substring(0, oldUrl.indexOf("."));
        }
        String url = prefix + sufix;
        view.setUrl(url);

        delegate.updateControls();
    }

    /** Create application on CloudFoundry by sending request over WebSocket or HTTP. */
    private void createApplication() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                createApplication();
            }
        };

        // TODO Need to create some special service after this class
        // This class still doesn't have analog.
        //      JobManager.get().showJobSeparated();
        CloudFoundryApplicationUnmarshallerWS unmarshaller = new CloudFoundryApplicationUnmarshallerWS();
        boolean noStart = false;

        try {
            // Application will be started after creation (IDE-1618)
            service.createWS(server, name, null, url, 0, 0, noStart, resourcesProvider.getVfsId(),
                             resourcesProvider.getActiveProject().getId(), warUrl, paasProvider,
                             new CloudFoundryRESTfulRequestCallback<CloudFoundryApplication>(unmarshaller, loggedInHandler, null,
                                                                                             server, eventBus, console, constant,
                                                                                             loginPresenter, paasProvider) {
                                 @Override
                                 protected void onSuccess(final CloudFoundryApplication result) {
                                     project.refreshProperties(new AsyncCallback<Project>() {
                                         @Override
                                         public void onSuccess(Project project) {
                                             onAppCreatedSuccess(result);
                                         }

                                         @Override
                                         public void onFailure(Throwable caught) {
                                             Log.error(CloudFoundryPagePresenter.class, "Can not refresh properties", caught);
                                             callback.onFailure(caught);
                                         }
                                     });
                                 }

                                 @Override
                                 protected void onFailure(Throwable exception) {
                                     console.print(constant.applicationCreationFailed());
                                     super.onFailure(exception);
                                     callback.onFailure(exception);
                                 }
                             });
        } catch (WebSocketException e) {
            createApplicationREST(loggedInHandler);
        }
    }

    /**
     * Create application on CloudFoundry by sending request over HTTP.
     *
     * @param loggedInHandler
     *         handler that should be called after success login
     */
    private void createApplicationREST(LoggedInHandler loggedInHandler) {
        CloudFoundryApplicationUnmarshaller unmarshaller = new CloudFoundryApplicationUnmarshaller();
        boolean noStart = false;

        try {
            // Application will be started after creation (IDE-1618)
            service.create(server, name, null, url, 0, 0, noStart, resourcesProvider.getVfsId(),
                           resourcesProvider.getActiveProject().getId(), warUrl, paasProvider,
                           new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller, loggedInHandler, null, server,
                                                                                         eventBus, console, constant, loginPresenter,
                                                                                         paasProvider) {
                               @Override
                               protected void onSuccess(final CloudFoundryApplication result) {
                                   project.refreshProperties(new AsyncCallback<Project>() {
                                       @Override
                                       public void onSuccess(Project project) {
                                           onAppCreatedSuccess(result);
                                       }

                                       @Override
                                       public void onFailure(Throwable caught) {
                                           Log.error(CloudFoundryPagePresenter.class, "Can not refresh properties", caught);
                                           callback.onFailure(caught);
                                       }
                                   });
                               }

                               @Override
                               protected void onFailure(Throwable exception) {
                                   console.print(constant.applicationCreationFailed());
                                   super.onFailure(exception);
                                   callback.onFailure(exception);
                               }
                           });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
            callback.onFailure(e);
        }
    }

    /**
     * Performs action when application successfully created.
     *
     * @param app
     *         {@link CloudFoundryApplication} which is created
     */
    private void onAppCreatedSuccess(CloudFoundryApplication app) {
        warUrl = null;
        String msg = constant.applicationCreatedSuccessfully(app.getName());
        if ("STARTED".equals(app.getState())) {
            if (app.getUris().isEmpty()) {
                msg += "<br>" + constant.applicationStartedWithNoUrls();
            } else {
                msg += "<br>" + constant.applicationStartedOnUrls(app.getName(), getAppUrlsAsString(app));
            }
        }

        console.print(msg);
        eventBus.fireEvent(new RefreshBrowserEvent(project));
        callback.onSuccess();
    }

    /**
     * Returns application URLs as string.
     *
     * @param application
     *         {@link CloudFoundryApplication Cloud Foundry application}
     * @return application URLs
     */
    private String getAppUrlsAsString(CloudFoundryApplication application) {
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

    /** Get the list of server and put them to select field. */
    private void getServers() {
        TargetsUnmarshaller unmarshaller = new TargetsUnmarshaller();
        try {
            service.getTargets(paasProvider, new AsyncRequestCallback<JsonArray<String>>(unmarshaller) {
                @Override
                protected void onSuccess(JsonArray<String> result) {
                    if (result.isEmpty()) {
                        JsonArray<String> servers = JsonCollections.createArray(CloudFoundryExtension.DEFAULT_CF_SERVER);
                        view.setServerValues(servers);
                        view.setServer(CloudFoundryExtension.DEFAULT_CF_SERVER);
                    } else {
                        view.setServerValues(result);
                        view.setServer(result.get(0));
                    }
                    view.setName(projectName);
                    // don't forget to init values, that are stored, when
                    // values in form fields are changed.
                    name = projectName;
                    server = view.getServer();
                    String urlSufix = server.substring(server.indexOf("."));
                    view.setUrl(name + urlSufix);
                    url = view.getUrl();

                    delegate.updateControls();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                    console.print(exception.getMessage());
                }
            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
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
            service.validateAction("create", server, name, null, url, resourcesProvider.getVfsId(), null, paasProvider, 0, 0, true,
                                   new CloudFoundryAsyncRequestCallback<String>(null, validateHandler, null, server, eventBus, console,
                                                                                constant, loginPresenter, paasProvider) {
                                       @Override
                                       protected void onSuccess(String result) {
                                           beforeDeploy();
                                       }
                                   });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
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

    /** Deploy project to CloudFoundry. */
    private void deploy(Project project) {
        this.project = project;
        buildApplication();
    }

    /** Builds application. */
    private void buildApplication() {
        // TODO IDEX-57
        // Replace EventBus Events with direct method calls and DI
        projectBuildHandler = eventBus.addHandler(ProjectBuiltEvent.TYPE, this);
        eventBus.fireEvent(new BuildProjectEvent(project));
    }

    /** Checking entered information on view. */
    public boolean validate() {
        return !isLogined || view.getName() != null && !view.getName().isEmpty() && view.getUrl() != null && !view.getUrl().isEmpty();
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
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void removeOptions() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public String getNotice() {
        if (!isLogined) {
            return "This project will be created without deploy on CloudFoundry.";
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
        projectName = wizardContext.getData(PROJECT_NAME);
        isLogined = true;
        getServers();

        isLogged();

        container.setWidget(view);
    }

    /** Checks the user is logged. */
    private void isLogged() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                isLogined = true;
                delegate.updateControls();
            }
        };

        LoginCanceledHandler loginCanceledHandler = new LoginCanceledHandler() {
            @Override
            public void onLoginCanceled() {
                isLogined = false;
                delegate.updateControls();
            }
        };
        SystemInfoUnmarshaller unmarshaller = new SystemInfoUnmarshaller();

        try {
            service.getSystemInfo(server, paasProvider,
                                  new CloudFoundryAsyncRequestCallback<SystemInfo>(unmarshaller, loggedInHandler, loginCanceledHandler,
                                                                                   server, eventBus, console, constant, loginPresenter,
                                                                                   paasProvider) {
                                      @Override
                                      protected void onSuccess(SystemInfo result) {
                                          // do nothing
                                      }
                                  });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void commit(@NotNull CommitCallback callback) {
        this.callback = callback;

        if (!isLogined) {
            return;
        }

        Project project = wizardContext.getData(PROJECT);
        deploy(project);
    }
}