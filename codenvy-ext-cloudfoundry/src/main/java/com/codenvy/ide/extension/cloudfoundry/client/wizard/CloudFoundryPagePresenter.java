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
package com.codenvy.ide.extension.cloudfoundry.client.wizard;

import com.codenvy.ide.api.event.RefreshBrowserEvent;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.template.CreateProjectProvider;
import com.codenvy.ide.api.template.TemplateAgent;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPagePresenter;
import com.codenvy.ide.api.ui.wizard.WizardPagePresenter;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.extension.cloudfoundry.client.*;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoginPresenter;
import com.codenvy.ide.extension.cloudfoundry.client.marshaller.TargetsUnmarshaller;
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import com.codenvy.ide.extension.maven.client.event.BuildProjectEvent;
import com.codenvy.ide.extension.maven.client.event.ProjectBuiltEvent;
import com.codenvy.ide.extension.maven.client.event.ProjectBuiltHandler;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AutoBeanUnmarshaller;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.AutoBeanUnmarshallerWS;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Presenter for creating application on CloudFoundry from New project wizard.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class CloudFoundryPagePresenter extends AbstractWizardPagePresenter
        implements CloudFoundryPageView.ActionDelegate, ProjectBuiltHandler {
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
    private CloudFoundryAutoBeanFactory      autoBeanFactory;
    private HandlerRegistration              projectBuildHandler;
    private LoginPresenter                   loginPresenter;
    private CloudFoundryClientService        service;
    private TemplateAgent                    templateAgent;
    private CreateProjectProvider            createProjectProvider;
    private CloudFoundryExtension.PAAS_PROVIDER paasProvider = CloudFoundryExtension.PAAS_PROVIDER.CLOUD_FOUNDRY;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param resourcesProvider
     * @param resources
     * @param console
     * @param constant
     * @param autoBeanFactory
     * @param loginPresenter
     * @param service
     */
    @Inject
    protected CloudFoundryPagePresenter(CloudFoundryPageView view, EventBus eventBus, ResourceProvider resourcesProvider,
                                        CloudFoundryResources resources, ConsolePart console, CloudFoundryLocalizationConstant constant,
                                        CloudFoundryAutoBeanFactory autoBeanFactory, LoginPresenter loginPresenter,
                                        CloudFoundryClientService service, TemplateAgent templateAgent) {
        super("Deploy project to Cloud Foundry", resources.cloudFoundry48());

        this.view = view;
        this.view.setDelegate(this);
        this.eventBus = eventBus;
        this.resourcesProvider = resourcesProvider;
        this.console = console;
        this.constant = constant;
        this.autoBeanFactory = autoBeanFactory;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.templateAgent = templateAgent;
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

        AutoBean<CloudFoundryApplication> cloudFoundryApplication = autoBeanFactory.cloudFoundryApplication();
        AutoBeanUnmarshallerWS<CloudFoundryApplication> unmarshaller =
                new AutoBeanUnmarshallerWS<CloudFoundryApplication>(cloudFoundryApplication);

        try {
            // Application will be started after creation (IDE-1618)
            boolean noStart = false;
            service.createWS(server,
                             name,
                             null,
                             url,
                             0,
                             0,
                             noStart,
                             resourcesProvider.getVfsId(),
                             resourcesProvider.getActiveProject().getId(),
                             warUrl,
                             paasProvider,
                             new CloudFoundryRESTfulRequestCallback<CloudFoundryApplication>(unmarshaller, loggedInHandler, null,
                                                                                             server, eventBus, console, constant,
                                                                                             loginPresenter, paasProvider) {
                                 @Override
                                 protected void onSuccess(CloudFoundryApplication result) {
                                     onAppCreatedSuccess(result);
                                 }

                                 @Override
                                 protected void onFailure(Throwable exception) {
                                     console.print(constant.applicationCreationFailed());
                                     super.onFailure(exception);
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
        AutoBean<CloudFoundryApplication> cloudFoundryApplication = autoBeanFactory.cloudFoundryApplication();
        AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
                new AutoBeanUnmarshaller<CloudFoundryApplication>(cloudFoundryApplication);

        try {
            // Application will be started after creation (IDE-1618)
            boolean noStart = false;
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
                                       }
                                   });
                               }

                               @Override
                               protected void onFailure(Throwable exception) {
                                   console.print(constant.applicationCreationFailed());
                                   super.onFailure(exception);
                               }
                           });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
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

    /** Get the list of server and put them to select field. */
    private void getServers() {
        try {
            TargetsUnmarshaller unmarshaller = new TargetsUnmarshaller(JsonCollections.<String>createArray());
            service.getTargets(paasProvider,
                               new AsyncRequestCallback<JsonArray<String>>(unmarshaller) {
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
        return view.getName() != null && !view.getName().isEmpty() && view.getUrl() != null && !view.getUrl().isEmpty();
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

    /** {@inheritDoc} */
    @Override
    public String getNotice() {
        if (view.getName().isEmpty()) {
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
        getServers();
        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Override
    public void doFinish() {
        createProjectProvider.create(new AsyncCallback<Project>() {
            @Override
            public void onSuccess(Project result) {
                deploy(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(CloudFoundryPagePresenter.class, caught);
            }
        });
    }
}