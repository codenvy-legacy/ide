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
package com.codenvy.ide.ext.appfog.client.create;

import com.codenvy.ide.api.event.RefreshBrowserEvent;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.appfog.client.*;
import com.codenvy.ide.ext.appfog.client.login.LoggedInHandler;
import com.codenvy.ide.ext.appfog.client.login.LoginPresenter;
import com.codenvy.ide.ext.appfog.client.marshaller.AppFogApplicationUnmarshaller;
import com.codenvy.ide.ext.appfog.client.marshaller.AppFogApplicationUnmarshallerWS;
import com.codenvy.ide.ext.appfog.client.marshaller.FrameworksUnmarshaller;
import com.codenvy.ide.ext.appfog.client.marshaller.InfrasUnmarshaller;
import com.codenvy.ide.ext.appfog.shared.AppfogApplication;
import com.codenvy.ide.ext.appfog.shared.Framework;
import com.codenvy.ide.ext.appfog.shared.InfraDetail;
import com.codenvy.ide.extension.builder.client.event.BuildProjectEvent;
import com.codenvy.ide.extension.builder.client.event.ProjectBuiltEvent;
import com.codenvy.ide.extension.builder.client.event.ProjectBuiltHandler;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.WebSocketException;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Presenter for creating application on AppFog.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 */
@Singleton
public class CreateApplicationPresenter implements CreateApplicationView.ActionDelegate, ProjectBuiltHandler {
    /** Needs for contain information about application. */
    private class AppData {
        String  server;
        String  name;
        String  type;
        String  url;
        String  infra;
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
         * @param infra
         */
        public AppData(String server, String name, String type, String url, int instances, int memory, boolean nostart,
                       String infra) {
            this.server = server;
            this.name = name;
            this.type = type;
            this.url = url;
            this.instances = instances;
            this.memory = memory;
            this.nostart = nostart;
            this.infra = infra;
        }
    }

    private CreateApplicationView      view;
    private JsonArray<Framework>       frameworks;
    private JsonArray<InfraDetail>     infras;
    /** Public url to war file of application. */
    private String                     warUrl;
    /** Store application data in format, that convenient to send to server. */
    private AppData                    appData;
    private boolean                    isMavenProject;
    private ResourceProvider           resourceProvider;
    private HandlerRegistration        projectBuildHandler;
    private EventBus                   eventBus;
    private ConsolePart                console;
    private AppfogLocalizationConstant constant;
    private LoginPresenter             loginPresenter;
    private AppfogClientService        service;
    private NotificationManager        notificationManager;
    private InfraDetail                currentInfra;
    private Notification               notification;

    /**
     * Create presenter.
     *
     * @param view
     * @param resourceProvider
     * @param eventBus
     * @param console
     * @param constant
     * @param loginPresenter
     * @param service
     * @param notificationManager
     */
    @Inject
    protected CreateApplicationPresenter(CreateApplicationView view, ResourceProvider resourceProvider, EventBus eventBus,
                                         ConsolePart console, AppfogLocalizationConstant constant, LoginPresenter loginPresenter,
                                         AppfogClientService service, NotificationManager notificationManager) {
        this.frameworks = JsonCollections.createArray();
        this.infras = JsonCollections.createArray();
        this.resourceProvider = resourceProvider;
        this.view = view;
        this.view.setDelegate(this);
        this.eventBus = eventBus;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.notificationManager = notificationManager;
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
            server = AppFogExtension.DEFAULT_SERVER;
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
                Notification notification = new Notification(constant.errorMemoryFormat(), ERROR);
                notificationManager.showNotification(notification);
                eventBus.fireEvent(new ExceptionThrownEvent(constant.errorMemoryFormat()));
            }
        }

        String url;

        InfraDetail infra = findInfraByName(view.getInfra());

        if (view.isCustomUrl()) {
            url = view.getUrl();
            if (url == null || url.isEmpty()) {
                url = null;
            }
        } else {
            url = name + '.' + infra.getBase();
        }

        int instances = 0;
        try {
            instances = Integer.parseInt(view.getInstances());
        } catch (NumberFormatException e) {
            Notification notification = new Notification(constant.errorInstancesFormat(), ERROR);
            notificationManager.showNotification(notification);
            eventBus.fireEvent(new ExceptionThrownEvent(constant.errorInstancesFormat()));
        }
        boolean nostart = !view.isStartAfterCreation();

        return new AppData(server, name, type, url, instances, memory, nostart, infra.getInfra());
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
            service.validateAction("create", app.server, app.name, app.type, app.url, resourceProvider.getVfsId(), project.getId(),
                                   app.instances, app.memory, app.nostart,
                                   new AppfogAsyncRequestCallback<String>(null, validateHandler, null, app.server, eventBus, constant,
                                                                          loginPresenter, notificationManager) {
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
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
            eventBus.fireEvent(new ExceptionThrownEvent(e));
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
     * Create application on AppFog by sending request over WebSocket or HTTP.
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
        AppFogApplicationUnmarshallerWS unmarshaller = new AppFogApplicationUnmarshallerWS();
        String message = constant.createApplicationStarted(project.getName());
        notification = new Notification(message, Notification.Status.PROGRESS);
        notificationManager.showNotification(notification);

        try {
            service.createWS(appData.server, appData.name, appData.type, appData.url, appData.instances, appData.memory, appData.nostart,
                             resourceProvider.getVfsId(), project.getId(), warUrl, appData.infra,
                             new AppfogRESTfulRequestCallback<AppfogApplication>(unmarshaller, loggedInHandler, null, appData.server,
                                                                                 eventBus, constant, loginPresenter, notificationManager) {
                                 @Override
                                 protected void onSuccess(final AppfogApplication appfogApplication) {
                                     project.refreshProperties(new AsyncCallback<Project>() {
                                         @Override
                                         public void onSuccess(Project result) {
                                             onAppCreatedSuccess(appfogApplication);
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
                                     notification.setStatus(Notification.Status.FINISHED);
                                     notification.setType(ERROR);
                                     notification.setMessage(constant.applicationCreationFailed());
                                     super.onFailure(exception);
                                 }
                             });
        } catch (WebSocketException e) {
            createApplicationREST(appData, project, loggedInHandler);
        }
    }

    /**
     * Create application on AppFog by sending request over HTTP.
     *
     * @param appData
     *         data to create new application
     * @param project
     *         {@link org.exoplatform.ide.vfs.client.model.ProjectModel}
     * @param loggedInHandler
     *         handler that should be called after success login
     */
    private void createApplicationREST(final AppData appData, final Project project, LoggedInHandler loggedInHandler) {
        AppFogApplicationUnmarshaller unmarshaller = new AppFogApplicationUnmarshaller();

        try {
            service.create(appData.server, appData.name, appData.type, appData.url, appData.instances, appData.memory, appData.nostart,
                           resourceProvider.getVfsId(), project.getId(), warUrl, appData.infra,
                           new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller, loggedInHandler, null, appData.server, eventBus,
                                                                             constant, loginPresenter, notificationManager) {
                               @Override
                               protected void onSuccess(final AppfogApplication appfogApplication) {
                                   project.refreshProperties(new AsyncCallback<Project>() {
                                       @Override
                                       public void onSuccess(Project result) {
                                           onAppCreatedSuccess(appfogApplication);
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
                                   notification.setStatus(Notification.Status.FINISHED);
                                   notification.setType(ERROR);
                                   notification.setMessage(constant.applicationCreationFailed());
                                   super.onFailure(exception);
                               }
                           });
        } catch (RequestException e) {
            notification.setStatus(Notification.Status.FINISHED);
            notification.setType(ERROR);
            notification.setMessage(constant.applicationCreationFailed());
        }
    }

    /**
     * Performs action when application successfully created.
     *
     * @param app
     * @link AppfogApplication} which is created
     */
    private void onAppCreatedSuccess(AppfogApplication app) {
        warUrl = null;
        notification.setStatus(Notification.Status.FINISHED);
        notification.setType(Notification.Type.INFO);

        String msg;
        if ("STARTED".equals(app.getState()) && app.getInstances() == app.getRunningInstances()) {
            msg = constant.applicationCreatedSuccessfully(app.getName());
            if (app.getUris().isEmpty()) {
                msg += "<br>" + constant.applicationStartedWithNoUrls();
            } else {
                msg += "<br>" + constant.applicationStartedOnUrls(app.getName(), getAppUrlsAsString(app));
            }
        } else if ("STARTED".equals(app.getState()) && app.getInstances() != app.getRunningInstances()) {
            msg = constant.applicationWasNotStarted(app.getName());
        } else {
            msg = constant.applicationCreatedSuccessfully(app.getName());
        }
        notification.setMessage(msg);
        console.print(msg);
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
     * Get the list of available frameworks for AppFog.
     *
     * @param server
     */
    private void getFrameworks(final String server) {
        LoggedInHandler getFrameworksLoggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                getFrameworks(server);
            }
        };
        FrameworksUnmarshaller unmarshaller = new FrameworksUnmarshaller();

        try {
            service.getFrameworks(server,
                                  new AppfogAsyncRequestCallback<JsonArray<Framework>>(unmarshaller, getFrameworksLoggedInHandler, null,
                                                                                       eventBus, constant, loginPresenter,
                                                                                       notificationManager) {
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
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
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
        final String url = getUrlByServerAndName(currentInfra.getBase(), view.getName());
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
        return "http://" + name.toLowerCase() + '.' + serverUrl;
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
    public void onTypeChanged() {
        Framework framework = findFrameworkByName(view.getType());
        if (framework != null) {
            view.setMemory(String.valueOf(framework.getMemory()));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onInfraChanged() {
        currentInfra = findInfraByName(view.getInfra());
        updateUrlField();
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

    /** Shows dialog. */
    public void showDialog() {
        Project selectedProject = resourceProvider.getActiveProject();
        if (selectedProject != null) {
            checkIsProject(selectedProject);

            // set the state of fields
            this.view.setEnableTypeField(false);
            this.view.setEnableUrlField(false);
            this.view.setEnableMemoryField(false);
            this.view.focusInNameField();

            // set default values to fields
            this.view.setServer(AppFogExtension.DEFAULT_SERVER);
            this.view.setName(resourceProvider.getActiveProject().getName());
            this.view.setTypeValues(JsonCollections.<String>createArray());
            this.view.setInstances("1");
            this.view.setAutodetectType(true);
            view.setStartAfterCreation(true);

            view.focusInNameField();
            getInfras(AppFogExtension.DEFAULT_SERVER);

            view.showDialog();
        } else {
            String msg = constant.createApplicationNotFolder(view.getName());
            eventBus.fireEvent(new ExceptionThrownEvent(msg));
            Notification notification = new Notification(msg, ERROR);
            notificationManager.showNotification(notification);
        }
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

    /** Get the list of infrastructure and put them to select field. */
    private void getInfras(final String server) {
        LoggedInHandler getInfrasHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                getInfras(server);
            }
        };
        InfrasUnmarshaller unmarshaller = new InfrasUnmarshaller();

        try {
            service.infras(server, null, null,
                           new AppfogAsyncRequestCallback<JsonArray<InfraDetail>>(unmarshaller, getInfrasHandler, null, server, eventBus,
                                                                                  constant, loginPresenter, notificationManager) {
                               @Override
                               protected void onSuccess(JsonArray<InfraDetail> result) {
                                   if (result.isEmpty()) {
                                       Notification notification = new Notification(constant.errorGettingInfras(), ERROR);
                                       notificationManager.showNotification(notification);
                                       eventBus.fireEvent(new ExceptionThrownEvent(constant.errorGettingInfras()));
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

                                       currentInfra = infras.get(0);
                                   }
                                   updateUrlField();
                               }
                           });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }
}