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
package com.codenvy.ide.extension.cloudfoundry.client.create;

import com.codenvy.ide.api.event.RefreshBrowserEvent;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.extension.cloudfoundry.client.*;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoginPresenter;
import com.codenvy.ide.extension.cloudfoundry.client.marshaller.CloudFoundryApplicationUnmarshaller;
import com.codenvy.ide.extension.cloudfoundry.client.marshaller.CloudFoundryApplicationUnmarshallerWS;
import com.codenvy.ide.extension.cloudfoundry.client.marshaller.FrameworksUnmarshaller;
import com.codenvy.ide.extension.cloudfoundry.client.marshaller.TargetsUnmarshaller;
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import com.codenvy.ide.extension.cloudfoundry.shared.Framework;
import com.codenvy.ide.extension.builder.client.event.BuildProjectEvent;
import com.codenvy.ide.extension.builder.client.event.ProjectBuiltEvent;
import com.codenvy.ide.extension.builder.client.event.ProjectBuiltHandler;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.WebSocketException;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Presenter for creating application on CloudFoundry.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CreateApplicationPresenter.java Jul 8, 2011 11:57:36 AM vereshchaka $
 */
@Singleton
public class CreateApplicationPresenter implements CreateApplicationView.ActionDelegate, ProjectBuiltHandler {
    /** Needs for contain information about application. */
    private class AppData {
        String  server;
        String  name;
        String  type;
        String  url;
        int     instances;
        int     memory;
        boolean nostart;

        /**
         * Create application data.
         *
         * @param server
         * @param name
         * @param type
         * @param url
         * @param instances
         * @param memory
         * @param nostart
         */
        public AppData(String server, String name, String type, String url, int instances, int memory, boolean nostart) {
            this.server = server;
            this.name = name;
            this.type = type;
            this.url = url;
            this.instances = instances;
            this.memory = memory;
            this.nostart = nostart;
        }
    }

    private CreateApplicationView               view;
    private JsonArray<Framework>                frameworks;
    /** Public url to war file of application. */
    private String                              warUrl;
    /** Store application data in format, that convenient to send to server. */
    private AppData                             appData;
    private boolean                             isMavenProject;
    private ResourceProvider                    resourceProvider;
    private HandlerRegistration                 projectBuildHandler;
    private EventBus                            eventBus;
    private ConsolePart                         console;
    private CloudFoundryLocalizationConstant    constant;
    private LoginPresenter                      loginPresenter;
    private CloudFoundryClientService           service;
    private CloudFoundryExtension.PAAS_PROVIDER paasProvider;

    /**
     * Create presenter.
     *
     * @param resourceProvider
     * @param view
     * @param eventBus
     * @param console
     * @param constant
     * @param loginPresenter
     * @param service
     */
    @Inject
    protected CreateApplicationPresenter(ResourceProvider resourceProvider, CreateApplicationView view, EventBus eventBus,
                                         ConsolePart console, CloudFoundryLocalizationConstant constant, LoginPresenter loginPresenter,
                                         CloudFoundryClientService service) {
        this.frameworks = JsonCollections.createArray();

        this.resourceProvider = resourceProvider;
        this.view = view;
        this.view.setDelegate(this);
        this.console = console;
        this.eventBus = eventBus;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.service = service;
    }

    /** {@inheritDoc} */
    @Override
    public void onCreateClicked() {
        appData = getAppDataFromForm();

        validateData(appData);
    }

    /**
     * Process values from application create form, and store data in bean in format, that is convenient to send to server
     *
     * @return {@link AppData}
     */
    private AppData getAppDataFromForm() {
        String server = view.getServer();
        if (server == null || server.isEmpty()) {
            // is server is empty, set value to null
            // it is need for client service
            // if null, than service will not send this parameter
            server = null;
        } else if (server.endsWith("/")) {
            server = server.substring(0, server.length() - 1);
        }
        String name = view.getName();
        String type;
        int memory = 0;
        if (view.isAutodetectType()) {
            type = null;
            memory = 0;
        } else {
            Framework framework = findFrameworkByName(view.getType());
            type = framework.getName();
            try {
                memory = Integer.parseInt(view.getMemory());
            } catch (NumberFormatException e) {
                eventBus.fireEvent(new ExceptionThrownEvent(constant.errorMemoryFormat()));
                console.print(constant.errorMemoryFormat());
            }
        }

        String url;

        if (view.isCustomUrl()) {
            url = view.getUrl();
            if (url == null || url.isEmpty()) {
                url = null;
            }
        } else {
            url = null;
        }

        int instances = 0;
        try {
            instances = Integer.parseInt(view.getInstances());
        } catch (NumberFormatException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(constant.errorInstancesFormat()));
            console.print(constant.errorInstancesFormat());
        }
        boolean nostart = !view.isStartAfterCreation();

        return new AppData(server, name, type, url, instances, memory, nostart);
    }

    /** Check is selected item project and can be built. */
    private void checkIsProject(final Project project) {
        JsonArray<Resource> children = project.getChildren();

        isMavenProject = false;
        for (int i = 0; i < children.size(); i++) {
            Resource child = children.get(i);
            if (child.isFile() && "pom.xml".equals(child.getName())) {
                isMavenProject = true;
            }
        }
    }

    /**
     * Find framework from list by name.
     *
     * @param frameworkName
     * @return
     */
    private Framework findFrameworkByName(String frameworkName) {
        for (int i = 0; i < frameworks.size(); i++) {
            Framework framework = frameworks.get(i);
            String name = framework.getDisplayName() != null ? framework.getDisplayName() : framework.getName();
            if (frameworkName.equals(name)) {
                return framework;
            }
        }
        return null;
    }

    /**
     * Validate action before building project
     *
     * @param app
     */
    private void validateData(final AppData app) {
        LoggedInHandler validateHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                validateData(app);
            }
        };

        Project project = resourceProvider.getActiveProject();

        try {
            service.validateAction("create", app.server, app.name, app.type, app.url, resourceProvider.getVfsInfo().getId(), project.getId(),
                                   paasProvider, app.instances, app.memory, app.nostart,
                                   new CloudFoundryAsyncRequestCallback<String>(null, validateHandler, null, app.server, eventBus, console,
                                                                                constant, loginPresenter, paasProvider) {
                                       @Override
                                       protected void onSuccess(String result) {
                                           if (isMavenProject) {
                                               buildApplication();
                                           } else {
                                               createApplication(appData);
                                           }

                                           view.close();
                                       }
                                   });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /** Builds application. */
    private void buildApplication() {
        // TODO IDEX-57
        // Replace EventBus Events with direct method calls and DI
        projectBuildHandler = eventBus.addHandler(ProjectBuiltEvent.TYPE, this);
        eventBus.fireEvent(new BuildProjectEvent());
    }

    /**
     * Create application on CloudFoundry by sending request over WebSocket or HTTP.
     *
     * @param appData
     *         data to create new application
     */
    private void createApplication(final AppData appData) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                createApplication(appData);
            }
        };
        final Project project = resourceProvider.getActiveProject();
        CloudFoundryApplicationUnmarshallerWS unmarshaller = new CloudFoundryApplicationUnmarshallerWS();

        try {
            service.createWS(appData.server, appData.name, appData.type, appData.url, appData.instances, appData.memory, appData.nostart,
                             resourceProvider.getVfsInfo().getId(), project.getId(), warUrl, paasProvider,
                             new CloudFoundryRESTfulRequestCallback<CloudFoundryApplication>(unmarshaller, loggedInHandler, null,
                                                                                             appData.server, eventBus, console, constant,
                                                                                             loginPresenter, paasProvider) {
                                 @Override
                                 protected void onSuccess(final CloudFoundryApplication cloudFoundryApp) {
                                     project.refreshProperties(new AsyncCallback<Project>() {
                                         @Override
                                         public void onSuccess(Project result) {
                                             onAppCreatedSuccess(cloudFoundryApp);
                                             eventBus.fireEvent(new RefreshBrowserEvent(project));
                                         }

                                         @Override
                                         public void onFailure(Throwable caught) {
                                             Log.error(CreateApplicationPresenter.class, "Can not refresh properties", caught);
                                         }
                                     });
                                 }

                                 @Override
                                 protected void onFailure(Throwable exception) {
                                     console.print(constant.applicationCreationFailed());
                                     super.onFailure(exception);
                                 }
                             });
        } catch (WebSocketException e) {
            createApplicationREST(appData, project, loggedInHandler);
        }
    }

    /**
     * Create application on CloudFoundry by sending request over HTTP.
     *
     * @param appData
     *         data to create new application
     * @param project
     *         {@link Project}
     * @param loggedInHandler
     *         handler that should be called after success login
     */
    private void createApplicationREST(final AppData appData, final Project project, LoggedInHandler loggedInHandler) {
        CloudFoundryApplicationUnmarshaller unmarshaller = new CloudFoundryApplicationUnmarshaller();

        try {
            service.create(appData.server, appData.name, appData.type, appData.url, appData.instances, appData.memory, appData.nostart,
                           resourceProvider.getVfsInfo().getId(), project.getId(), warUrl, paasProvider,
                           new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller, loggedInHandler, null,
                                                                                         appData.server, eventBus, console, constant,
                                                                                         loginPresenter, paasProvider) {
                               @Override
                               protected void onSuccess(final CloudFoundryApplication cloudFoundryApp) {
                                   project.refreshProperties(new AsyncCallback<Project>() {
                                       @Override
                                       public void onSuccess(Project result) {
                                           onAppCreatedSuccess(cloudFoundryApp);
                                           eventBus.fireEvent(new RefreshBrowserEvent(project));
                                       }

                                       @Override
                                       public void onFailure(Throwable caught) {
                                           Log.error(CreateApplicationPresenter.class, "Can not refresh properties", caught);
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
            console.print(constant.applicationCreationFailed());
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

        if ("STARTED".equals(app.getState()) && app.getInstances() == app.getRunningInstances()) {
            String msg = constant.applicationCreatedSuccessfully(app.getName());
            if (app.getUris().isEmpty()) {
                msg += "<br>" + constant.applicationStartedWithNoUrls();
            } else {
                msg += "<br>" + constant.applicationStartedOnUrls(app.getName(), getAppUrlsAsString(app));
            }
            console.print(msg);
        } else if ("STARTED".equals(app.getState()) && app.getInstances() != app.getRunningInstances()) {
            String msg = constant.applicationWasNotStarted(app.getName());
            console.print(msg);
        } else {
            String msg = constant.applicationCreatedSuccessfully(app.getName());
            console.print(msg);
        }
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

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onAutoDetectTypeChanged() {
        boolean value = view.isAutodetectType();

        view.setEnableTypeField(!value);
        view.setEnableMemoryField(!value);

        if (value) {
            view.setTypeValues(JsonCollections.<String>createArray());
            view.setMemory("");
        } else {
            final JsonArray<String> frameworkArray = getApplicationTypes(frameworks);
            view.setTypeValues(frameworkArray);
            view.setSelectedIndexForTypeSelectItem(0);
            getFrameworks(view.getServer());
        }
    }

    /**
     * Get the array of application types from list of frameworks.
     *
     * @param frameworks
     *         - list of available frameworks
     * @return an array of types
     */
    private JsonArray<String> getApplicationTypes(JsonArray<Framework> frameworks) {
        JsonArray<String> frameworkNames = JsonCollections.createArray();
        for (int i = 0; i < frameworks.size(); i++) {
            Framework framework = frameworks.get(i);
            frameworkNames.add(framework.getDisplayName() != null ? framework.getDisplayName() : framework.getName());
        }

        return frameworkNames;
    }

    /**
     * Get the list of available frameworks for CloudFoundry.
     *
     * @param server
     */
    private void getFrameworks(final String server) {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                getFrameworks(server);
            }
        };
        FrameworksUnmarshaller unmarshaller = new FrameworksUnmarshaller();

        try {
            service.getFrameworks(server, paasProvider,
                                  new CloudFoundryAsyncRequestCallback<JsonArray<Framework>>(unmarshaller, loggedInHandler, null, eventBus,
                                                                                             console, constant, loginPresenter,
                                                                                             paasProvider) {
                                      @Override
                                      protected void onSuccess(JsonArray<Framework> result) {
                                          if (!result.isEmpty()) {
                                              frameworks = result;
                                              JsonArray<String> fw = getApplicationTypes(result);
                                              view.setTypeValues(fw);
                                              Framework framework = findFrameworkByName(fw.get(0));
                                              view.setMemory(String.valueOf(framework.getMemory()));
                                          }
                                      }
                                  });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCustomUrlChanged() {
        boolean value = view.isCustomUrl();

        view.setEnableUrlField(value);

        if (value) {
            view.focusInUrlField();
        } else {
            updateUrlField();
        }
    }

    /** Update the URL field, using values from server and name field. */
    private void updateUrlField() {
        final String url = getUrlByServerAndName(view.getServer(), view.getName());
        view.setUrl(url);
    }

    /**
     * Gets url by server and name.
     *
     * @param serverUrl
     * @param name
     * @return
     */
    private String getUrlByServerAndName(String serverUrl, String name) {
        int index = serverUrl.indexOf(".");
        if (index < 0) {
            return name.toLowerCase();
        }
        final String domain = serverUrl.substring(index, serverUrl.length());
        return "http://" + name.toLowerCase() + domain;
    }

    /** {@inheritDoc} */
    @Override
    public void onApplicationNameChanged() {
        // if url set automatically, than try to create url using server and name
        if (!view.isCustomUrl()) {
            updateUrlField();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onServerChanged() {
        // if url set automatically, than try to create url using server and name
        if (!view.isCustomUrl()) {
            updateUrlField();
            view.setEnableAutodetectTypeCheckItem(true);
        }
    }

    /** Shows dialog. */
    public void showDialog(CloudFoundryExtension.PAAS_PROVIDER paasProvider) {
        this.paasProvider = paasProvider;
        Project selectedProject = resourceProvider.getActiveProject();
        if (selectedProject != null) {
            checkIsProject(selectedProject);

            // set the state of fields
            this.view.setEnableTypeField(false);
            this.view.setEnableUrlField(false);
            this.view.setEnableMemoryField(false);
            this.view.focusInNameField();

            // set default values to fields
            this.view.setTypeValues(JsonCollections.<String>createArray());
            this.view.setInstances("1");
            this.view.setAutodetectType(true);
            view.setStartAfterCreation(true);

            view.focusInNameField();
            getServers();

            view.showDialog();
        } else {
            String msg = constant.createApplicationNotFolder(view.getName());
            eventBus.fireEvent(new ExceptionThrownEvent(msg));
            console.print(msg);
        }
    }

    /** Get the list of server and put them to select field. */
    private void getServers() {
        TargetsUnmarshaller unmarshaller = new TargetsUnmarshaller();

        try {
            service.getTargets(paasProvider,
                               new AsyncRequestCallback<JsonArray<String>>(unmarshaller) {
                                   @Override
                                   protected void onSuccess(JsonArray<String> result) {
                                       if (result.isEmpty()) {
                                           JsonArray<String> list = JsonCollections.createArray();
                                           list.add(CloudFoundryExtension.DEFAULT_CF_SERVER);
                                           view.setServerValues(list);
                                           view.setServer(CloudFoundryExtension.DEFAULT_CF_SERVER);
                                       } else {
                                           view.setServerValues(result);
                                           view.setServer(result.get(0));
                                           getFrameworks(result.get(0));
                                       }

                                       if (!result.isEmpty()) {
                                           JsonArray<String> list = JsonCollections.createArray(CloudFoundryExtension.DEFAULT_CF_SERVER);
                                           view.setServerValues(list);
                                           view.setServer(CloudFoundryExtension.DEFAULT_CF_SERVER);
                                           getFrameworks(CloudFoundryExtension.DEFAULT_CF_SERVER);
                                       } else if (paasProvider == CloudFoundryExtension.PAAS_PROVIDER.CLOUD_FOUNDRY) {
                                           JsonArray<String> list = JsonCollections.createArray(CloudFoundryExtension.DEFAULT_CF_SERVER);
                                           view.setServerValues(list);
                                           view.setServer(CloudFoundryExtension.DEFAULT_CF_SERVER);
                                       }

                                       view.setName(resourceProvider.getActiveProject().getName());
                                       updateUrlField();
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

    /** {@inheritDoc} */
    @Override
    public void onTypeChanged() {
        Framework framework = findFrameworkByName(view.getType());
        if (framework != null) {
            view.setMemory(String.valueOf(framework.getMemory()));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onProjectBuilt(ProjectBuiltEvent event) {
        projectBuildHandler.removeHandler();
        if (event.getBuildStatus().getDownloadUrl() != null) {
            warUrl = event.getBuildStatus().getDownloadUrl();
            createApplication(appData);
        }
    }
}