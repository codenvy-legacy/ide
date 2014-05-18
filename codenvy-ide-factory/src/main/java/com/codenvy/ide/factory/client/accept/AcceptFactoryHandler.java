package com.codenvy.ide.factory.client.accept;

import com.codenvy.api.core.rest.shared.dto.ServiceError;
import com.codenvy.api.factory.dto.Factory;
import com.codenvy.api.factory.gwt.client.FactoryServiceClient;
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
import com.codenvy.ide.factory.client.FactoryLocalizationConstant;
import com.codenvy.ide.factory.client.welcome.WelcomeHandler;
import com.codenvy.ide.navigation.NavigateToFilePresenter;
import com.codenvy.ide.projecttype.SelectProjectTypePresenter;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.security.oauth.JsOAuthWindow;
import com.codenvy.ide.security.oauth.OAuthCallback;
import com.codenvy.ide.security.oauth.OAuthStatus;
import com.codenvy.ide.ui.dialogs.ask.Ask;
import com.codenvy.ide.ui.dialogs.ask.AskHandler;
import com.codenvy.ide.util.Config;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.events.ConnectionOpenedHandler;
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
    private final FactoryServiceClient          factoryService;
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
    private final WelcomeHandler                welcomeHandler;

    private static final String ACCEPT_EVENTS_CHANNEL = "acceptFactoryEvents";

    @Inject
    public AcceptFactoryHandler(@Named("restContext") String restContext, DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                FactoryServiceClient factoryService, MessageBus messageBus,
                                FactoryLocalizationConstant localization, ResourceProvider resourceProvider,
                                SelectProjectTypePresenter selectProjectTypePresenter, NotificationManager notificationManager,
                                ProjectTypeDescriptorRegistry projectTypeDescriptorRegistry, ProjectServiceClient projectServiceClient,
                                DtoFactory dtoFactory, UserServiceClient userServiceClient,
                                NavigateToFilePresenter navigateToFilePresenter, WelcomeHandler welcomeHandler) {
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
        this.welcomeHandler = welcomeHandler;
    }

    /**
     * Start checking the startup params for a factory parameters.
     * Begins accepting a factory after opening a Message Bus.
     *
     */
    public void process() {
        if (messageBus.getReadyState() == MessageBus.ReadyState.OPEN) {
            checkStartupParams();
        } else {
            messageBus.addOnOpenHandler(new ConnectionOpenedHandler() {
                @Override
                public void onOpen() {
                    checkStartupParams();
                }
            });
        }
    }


    /**
     * Checks the startup params for encoded or not encoded factory.
     */
    private void checkStartupParams() {
        if (Config.getStartupParam("id") != null) {
            getFactory(Config.getStartupParam("id"), true);
            return;
        }

        if (Config.getStartupParam("v") != null) {
            getFactory(Config.getStartupParams(), false);
            return;
        }

        welcomeHandler.welcome();
    }

    /**
     * Get a valid Factory object.
     *
     * @param queryStringOrId
     * @param encoded
     */
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
                          notificationManager.showNotification(new Notification(e.getMessage(), Notification.Type.ERROR));
                          welcomeHandler.welcome();
                      }
                  });
        } catch (WebSocketException e) {
            notificationManager.showNotification(new Notification(e.getMessage(), Notification.Type.ERROR));
            welcomeHandler.welcome();
        }
    }

    /**
     * Accepts a Factory.
     *
     * @param factory
     */
    private void acceptFactory(final Factory factory) {
        subscribeToAcceptFactoryEvents();

        final Notification acceptNotification = new Notification(localization.acceptIncomingFactoryURL(), Notification.Status.PROGRESS);
        notificationManager.showNotification(acceptNotification);

        try {
            factoryService.acceptFactory(factory, new RequestCallback<Factory>(dtoUnmarshallerFactory.newWSUnmarshaller(Factory.class)) {
                @Override
                protected void onSuccess(Factory acceptedFactory) {
                    acceptNotification.setStatus(Notification.Status.FINISHED);
                    acceptNotification.setMessage(localization.factoryURLAcceptedSuccessfully());
                    notificationManager.showNotification(
                            new Notification(localization.projectImported(acceptedFactory.getProjectattributes().getPname()),Notification.Type.INFO));
                    openProject(acceptedFactory);
                }

                @Override
                protected void onFailure(Throwable e) {
                    unSubscribeFromAcceptFactoryEvents();

                    welcomeHandler.welcome();

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
                welcomeHandler.welcome();
                updateProjectWithPreSettedProjectType(acceptedFactory);
            }

            @Override
            public void onSuccess(Project openedProject) {
                if (openedProject.getDescription() != null &&
                    Constants.NAMELESS_ID.equals(openedProject.getDescription().getProjectTypeId())) {
                    welcomeHandler.welcome();
                    updateProjectWithPreSettedProjectType(acceptedFactory);
                    return;
                }

                welcomeHandler.welcome(acceptedFactory);
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
            checkStartupParams();
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

    private void askUserToAuthorize(final String userId, final String provider, final String scope) {
        Ask ask = new Ask(localization.oAuthLoginTitle(), localization.oAuthLoginPrompt("github.com"), new AskHandler() {
            @Override
            public void onOk() {
                showPopUp(userId, provider, scope);
            }
        });
        ask.show();

    }

    private void showPopUp(String userId, String provider, String scope) {
        String authUrl = restContext + "/oauth/authenticate?oauth_provider=" + provider + "&scope=" + scope + "&userId=" + userId +
                         "&redirect_after_login=" + Window.Location.getProtocol() + "//" + Window.Location.getHost() + "/ide/" + Config.getWorkspaceName();
        JsOAuthWindow authWindow = new JsOAuthWindow(authUrl, "error.url", 500, 980, this);
        authWindow.loginWithOAuth();
    }

    /**
     * *****************************************************
     * Commons
     * *****************************************************
     */

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
                                           }
                                          );
    }
}
