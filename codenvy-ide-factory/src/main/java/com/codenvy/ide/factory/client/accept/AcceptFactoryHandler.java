package com.codenvy.ide.factory.client.accept;

import com.codenvy.api.core.rest.shared.dto.ServiceError;
import com.codenvy.api.factory.dto.Factory;
import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.api.user.gwt.client.UserServiceClient;
import com.codenvy.api.user.shared.dto.User;
import com.codenvy.ide.Constants;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ProjectTypeDescriptorRegistry;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.factory.client.FactoryClientService;
import com.codenvy.ide.factory.client.FactoryLocalizationConstant;
import com.codenvy.ide.navigation.NavigateToFilePresenter;
import com.codenvy.ide.projecttype.SelectProjectTypePresenter;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.security.oauth.JsOAuthWindow;
import com.codenvy.ide.security.oauth.OAuthCallback;
import com.codenvy.ide.security.oauth.OAuthStatus;
import com.codenvy.ide.util.Utils;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.events.MessageHandler;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.codenvy.ide.websocket.rest.exceptions.UnauthorizedException;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;


/**
 * @author Vladyslav Zhukovskii
 */
@Singleton
public class AcceptFactoryHandler implements OAuthCallback {
    private final String                        restContext;
    private final DtoUnmarshallerFactory        dtoUnmarshallerFactory;
    private final FactoryClientService          factoryService;
    private final MessageBus                    messageBus;
    private final FactoryLocalizationConstant   localization;
    private final ResourceProvider              resourceProvider;
    private final SelectProjectTypePresenter    selectProjectTypePresenter;
    private final NotificationManager           notificationManager;
    private final ProjectTypeDescriptorRegistry projectTypeDescriptorRegistry;
    private final ProjectServiceClient          projectServiceClient;
    private final DtoFactory                    dtoFactory;
    private final UserServiceClient             userServiceClient;
    private final NavigateToFilePresenter       navigateToFilePresenter;

    private static final String ACCEPT_EVENTS_CHANNEL = "acceptFactoryEvents";

    @Inject
    public AcceptFactoryHandler(@Named("restContext") String restContext, DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                FactoryClientService factoryService, MessageBus messageBus,
                                FactoryLocalizationConstant localization, ResourceProvider resourceProvider,
                                SelectProjectTypePresenter selectProjectTypePresenter, NotificationManager notificationManager,
                                ProjectTypeDescriptorRegistry projectTypeDescriptorRegistry, ProjectServiceClient projectServiceClient,
                                DtoFactory dtoFactory, UserServiceClient userServiceClient,
                                NavigateToFilePresenter navigateToFilePresenter) {
        this.restContext = restContext;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.factoryService = factoryService;
        this.messageBus = messageBus;
        this.localization = localization;
        this.resourceProvider = resourceProvider;
        this.selectProjectTypePresenter = selectProjectTypePresenter;
        this.notificationManager = notificationManager;
        this.projectTypeDescriptorRegistry = projectTypeDescriptorRegistry;
        this.projectServiceClient = projectServiceClient;
        this.dtoFactory = dtoFactory;
        this.userServiceClient = userServiceClient;
        this.navigateToFilePresenter = navigateToFilePresenter;
    }

    public void processFactory() {
        if (getRawStartUpParams() != null) {
            checkWebSocketOpenState.scheduleRepeating(500);
        }
    }

    Timer checkWebSocketOpenState = new Timer() {
        @Override
        public void run() {
            if (messageBus.getReadyState() == MessageBus.ReadyState.OPEN) {
                getStartUpParams();
                cancel();
            }
        }
    };

    private void getStartUpParams() {
        StringMap<Array<String>> parameterMap = buildParameterMap(getRawStartUpParams());
        if (parameterMap.get("id") != null && parameterMap.get("id").get(0) != null) {
            getFactory(parameterMap.get("id").get(0), true);
        } else if (parameterMap.get("v") != null && parameterMap.get("v").get(0) != null) {
            getFactory(getRawStartUpParams(), false);
        }
    }

    private void getFactory(String queryStringOrId, boolean encoded) {
        notificationManager.showNotification(new Notification(localization.getInformationAboutFactory(), Notification.Type.INFO));

        try {
            factoryService.getFactory(queryStringOrId, encoded,
                                      new RequestCallback<Factory>(dtoUnmarshallerFactory.newWSUnmarshaller(Factory.class)) {
                                          @Override
                                          protected void onSuccess(Factory factory) {
                                              acceptFactory(factory);
                                          }

                                          @Override
                                          protected void onFailure(Throwable e) {
                                              notificationManager
                                                      .showNotification(new Notification(e.getMessage(), Notification.Type.ERROR));

                                          }
                                      });
        } catch (WebSocketException e) {
            notificationManager.showNotification(
                    new Notification(e.getMessage(), Notification.Type.ERROR));
        }
    }

    private void acceptFactory(final Factory factory) {
        subscribeToAcceptFactoryEvents();

        final Notification acceptNotification = new Notification(localization.acceptIncomingFactoryURL(), Notification.Status.PROGRESS);
        notificationManager.showNotification(acceptNotification);

        try {
            factoryService.acceptFactory(factory, new RequestCallback<Factory>(
                    dtoUnmarshallerFactory.newWSUnmarshaller(Factory.class)) {
                @Override
                protected void onSuccess(Factory acceptedFactory) {
                    acceptNotification.setStatus(Notification.Status.FINISHED);
                    acceptNotification.setMessage(localization.factoryURLAcceptedSuccessfully());

                    notificationManager.showNotification(
                            new Notification(localization.projectImported(acceptedFactory.getProjectattributes().getPname()),
                                             Notification.Type.INFO));

                    openProject(acceptedFactory);
                }

                @Override
                protected void onFailure(Throwable e) {
                    unSubscribeFromAcceptFactoryEvents();

                    acceptNotification.setStatus(Notification.Status.FINISHED);
                    acceptNotification.setType(Notification.Type.ERROR);
                    acceptNotification.setImportant(true);

                    if (e instanceof UnauthorizedException) {
                        acceptNotification.setMessage(localization.needToAuthorize());
                        getOAuthInformation(factory);
                    } else {
                        ServiceError serviceError = dtoFactory.createDtoFromJson(e.getMessage(), ServiceError.class);
                        acceptNotification.setMessage(serviceError.getMessage());
                    }

                }
            });
        } catch (WebSocketException e) {
            acceptNotification.setStatus(Notification.Status.FINISHED);
            acceptNotification.setType(Notification.Type.ERROR);
            acceptNotification.setImportant(true);
            acceptNotification.setMessage(e.getMessage());
            unSubscribeFromAcceptFactoryEvents();
        }
    }

    /**
     * *****************************************************
     * Events
     * *****************************************************
     */

    private void openProject(final Factory acceptedFactory) {
        resourceProvider.getProject(acceptedFactory.getProjectattributes().getPname(), new AsyncCallback<Project>() {
            @Override
            public void onFailure(Throwable caught) {
                updateProjectWithPreSettedProjectType(acceptedFactory);
            }

            @Override
            public void onSuccess(Project openedProject) {
                if (openedProject.getDescription() != null &&
                    Constants.NAMELESS_ID.equals(openedProject.getDescription().getProjectTypeId())) {
                    updateProjectWithPreSettedProjectType(acceptedFactory);
                    return;
                }

                openFile(acceptedFactory);
            }
        });
    }

    private void askAndSetCorrectProjectType(final Factory acceptedFactory) {
        Project project = new Project(null, null, null, null);
        project.setName(acceptedFactory.getProjectattributes().getPname());
        selectProjectTypePresenter.showDialog(project, new AsyncCallback<Project>() {
            @Override
            public void onSuccess(Project result) {
                openProject(acceptedFactory);
            }

            @Override
            public void onFailure(Throwable caught) {
                notificationManager.showNotification(new Notification(localization.unableToSetProjectType(), Notification.Type.ERROR));
            }
        });
    }

    private void openFile(Factory acceptedFactory) {
        if (acceptedFactory.getOpenfile() != null && !acceptedFactory.getOpenfile().isEmpty()) {
            navigateToFilePresenter.openFile(acceptedFactory.getOpenfile());
        }
    }

    private void subscribeToAcceptFactoryEvents() {
        try {
            if (!messageBus.isHandlerSubscribed(acceptFactoryEvents, ACCEPT_EVENTS_CHANNEL)) {
                messageBus.subscribe(ACCEPT_EVENTS_CHANNEL, acceptFactoryEvents);
            }
        } catch (WebSocketException e) {
            //handle websocket error
        }
    }

    private void unSubscribeFromAcceptFactoryEvents() {
        try {
            if (messageBus.isHandlerSubscribed(acceptFactoryEvents, ACCEPT_EVENTS_CHANNEL)) {
                messageBus.unsubscribe(ACCEPT_EVENTS_CHANNEL, acceptFactoryEvents);
            }
        } catch (WebSocketException e) {
            //handle websocket error
        }
    }

    private MessageHandler acceptFactoryEvents = new MessageHandler() {
        @Override
        public void onMessage(String message) {
            notificationManager.showNotification(new Notification(message, Notification.Type.INFO));
        }
    };


    /**
     * *****************************************************
     * oAuth
     * *****************************************************
     */

    @Override
    public void onAuthenticated(OAuthStatus authStatus) {
        if (authStatus == OAuthStatus.LOGGED_IN) {
            getStartUpParams();
        }
    }

    //TODO need to create server side method which will be return oauth scope and provider based on git url
    private void getOAuthInformation(Factory factory) {
        String scope;
        String provider;

        if (factory.getVcsurl().contains("github.com")) {
            scope = "user,repo,write:public_key";
            provider = "github";
        } else if (factory.getVcsurl().contains("wso2.com")) {
            scope = "";
            provider = "wso2";
        } else {
            return;
        }

        getCurrentLoggedInUser(provider, scope);
    }

    private void getCurrentLoggedInUser(final String provider, final String scope) {
        userServiceClient.getCurrentUser(new AsyncRequestCallback<User>(dtoUnmarshallerFactory.newUnmarshaller(User.class)) {
            @Override
            protected void onSuccess(User user) {
                askUserToAuthorize(user.getId(), provider, scope);
            }

            @Override
            protected void onFailure(Throwable exception) {
                notificationManager
                        .showNotification(new Notification(localization.oauthFailedToGetCurrentLoggedInUser(), Notification.Type.ERROR));
            }
        });
    }

    private void askUserToAuthorize(String userId, String provider, String scope) {
        boolean permitToRedirect = Window.confirm(localization.oauthLoginPrompt("github.com"));
        if (permitToRedirect) {
            String authUrl = restContext + "/oauth/authenticate?oauth_provider=" + provider + "&scope=" + scope + "&userId=" + userId +
                             "&redirect_after_login=/ide/" + Utils.getWorkspaceName();
            JsOAuthWindow authWindow = new JsOAuthWindow(authUrl, "error.url", 500, 980, this);
            authWindow.loginWithOAuth();
        }
    }

    /**
     * *****************************************************
     * Commons
     * *****************************************************
     */

    // @formatter:off
    private native String getRawStartUpParams() /*-{
        try {
            if (!$wnd["startUpParams"]) {
            } else {
                return $wnd["startUpParams"];
            }
        } catch (e) {
            console.log("ERROR > " + e.message);
        }
    }-*/;
    // @formatter:on

    private StringMap<Array<String>> buildParameterMap(String rawQueryString) {
        StringMap<Array<String>> parameterMap = Collections.createStringMap();

        if (rawQueryString == null || rawQueryString.isEmpty()) {
            return parameterMap;
        }

        final String queryString = rawQueryString.startsWith("?") ? rawQueryString.substring(1) : rawQueryString;
        for (String kvPair : queryString.split("&")) {
            String[] kv = kvPair.split("=", 2);
            if (kv[0].length() == 0) continue;

            Array<String> values = parameterMap.get(kv[0]);
            if (values == null) {
                values = Collections.createArray();
                parameterMap.put(kv[0], values);
            }
            values.add(kv.length > 1 ? URL.decodeQueryString(kv[1]) : "");
        }

        return parameterMap;
    }

    private void updateProjectWithPreSettedProjectType(final Factory acceptedFactory) {
        final String projectType = acceptedFactory.getProjectattributes().getPtype();
        final ProjectTypeDescriptor projectTypeDescriptor = projectTypeDescriptorRegistry.getDescriptor(projectType);

        if (projectType == null || projectTypeDescriptor == null) {
            askAndSetCorrectProjectType(acceptedFactory);
            return;
        }

        ProjectDescriptor projectDescriptor = dtoFactory.createDto(ProjectDescriptor.class)
                                                        .withProjectTypeId(projectTypeDescriptor.getProjectTypeId())
                                                        .withProjectTypeName(projectTypeDescriptor.getProjectTypeName());

        projectServiceClient.updateProject(acceptedFactory.getProjectattributes().getPname(), projectDescriptor,
                                           new AsyncRequestCallback<ProjectDescriptor>() {
                                               @Override
                                               protected void onSuccess(ProjectDescriptor result) {
                                                   openProject(acceptedFactory);
                                               }

                                               @Override
                                               protected void onFailure(Throwable exception) {
                                                   askAndSetCorrectProjectType(acceptedFactory);
                                               }
                                           });
    }
}
