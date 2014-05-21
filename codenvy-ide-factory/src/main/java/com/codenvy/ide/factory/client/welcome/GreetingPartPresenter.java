/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.ide.factory.client.welcome;

import com.codenvy.api.factory.dto.Factory;
import com.codenvy.api.factory.dto.WelcomeConfiguration;
import com.codenvy.api.user.gwt.client.UserProfileServiceClient;
import com.codenvy.api.user.shared.dto.Attribute;
import com.codenvy.api.user.shared.dto.Profile;
import com.codenvy.api.workspace.shared.dto.Workspace;
import com.codenvy.ide.MimeType;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.base.BasePresenter;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.api.ui.workspace.PropertyListener;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.HTTPHeader;
import com.codenvy.ide.util.Config;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.MessageBuilder;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.impl.ImageResourcePrototype;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * @author Vitaliy Guliy
 */
@Singleton
public class GreetingPartPresenter extends BasePresenter implements GreetingPartView.ActionDelegate, GreetingPart {

    private static final String TITLE = "Greeting";

    private       GreetingPartView    view;

    private final DtoFactory               dtoFactory;
    private final MessageBus               messageBus;
    private final DtoUnmarshallerFactory   dtoUnmarshallerFactory;
    private final NotificationManager      notificationManager;
    private final ResourceProvider         resourceProvider;
    private final UserProfileServiceClient userProfileService;
    private final EventBus                 eventBus;
    private final WorkspaceAgent           workspaceAgent;

    private Workspace workspace;
    private Project   project;
    private Profile   profile;
    private Factory   factory;

    private HandlerRegistration projectActionHandler;

    @Inject
    public GreetingPartPresenter(GreetingPartView view,
                                 NotificationManager notificationManager,
                                 DtoFactory dtoFactory,
                                 MessageBus messageBus,
                                 EventBus eventBus,
                                 DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                 UserProfileServiceClient userProfileService,
                                 ResourceProvider resourceProvider,
                                 WorkspaceAgent workspaceAgent) {
        this.view = view;

        this.view.setTitle(TITLE);
        this.view.setDelegate(this);

        this.notificationManager = notificationManager;
        this.dtoFactory = dtoFactory;
        this.messageBus = messageBus;
        this.eventBus = eventBus;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.userProfileService = userProfileService;
        this.resourceProvider = resourceProvider;
        this.workspaceAgent = workspaceAgent;

        projectActionHandler = eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
                project = event.getProject();
                determineGreetingConfiguration();
                projectActionHandler.removeHandler();
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                project = null;
            }

            @Override
            public void onProjectDescriptionChanged(ProjectActionEvent event) {
            }
        });
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Nullable
    @Override
    public ImageResource getTitleImage() {
        return null;
    }

    @Nullable
    @Override
    public String getTitleToolTip() {
        return "Greeting the user";
    }

    @Override
    public int getSize() {
        return 320;
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }


    /**
     * Displays the welcome page depending on workspace and project types and current user.
     */
    @Override
    public void showGreeting() {
        getUserProfile();
    }

    /**
     * Display welcome, configured in factory.
     *
     * @param factory
     */
    @Override
    public void showGreeting(Factory factory) {
        this.factory = factory;
        getUserProfile();
    }


    /**
     * Fetches current user profile.
     */
    private void getUserProfile() {
        try {
            Message message = new MessageBuilder(RequestBuilder.GET, "/profile/")
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .build();

            messageBus.send(message,
                            new RequestCallback<Profile>(dtoUnmarshallerFactory.newWSUnmarshaller(Profile.class)) {
                                @Override
                                protected void onSuccess(Profile result) {
                                    profile = result;
                                    getWorkspace();
                                }

                                @Override
                                protected void onFailure(Throwable e) {
                                    notificationManager.showNotification(
                                            new Notification("Unable to get user profile. " + e.getMessage(),
                                                             Notification.Type.ERROR));
                                    Log.error(GreetingPartPresenter.class, "Unable to get user profile. " + e.getMessage());
                                }
                            }
                           );
        } catch (Exception e) {
            notificationManager.showNotification(
                    new Notification("Unable to get user profile. " + e.getMessage(), Notification.Type.ERROR));
            Log.error(GreetingPartPresenter.class, "Unable to get user profile. " + e.getMessage());
        }
    }


    /**
     * Determine greeting page URL, based on current workspace, opened project and current user.
     */
    private void determineGreetingConfiguration() {
        if (workspace == null || project == null) {
            return;
        }

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {

                if (factory != null && factory.getWelcome() != null) {
                    if (isUserPermanent() && factory.getWelcome().getAuthenticated() != null) {
                        WelcomeConfiguration welcome = factory.getWelcome().getAuthenticated();
                        showGreeting(welcome.getTitle(), welcome.getIconurl(), welcome.getContenturl(),
                                        welcome.getNotification());
                        return;

                    } else if (!isUserPermanent() && factory.getWelcome().getNonauthenticated() != null) {
                        WelcomeConfiguration welcome = factory.getWelcome().getNonauthenticated();
                        showGreeting(welcome.getTitle(), welcome.getIconurl(), welcome.getContenturl(),
                                        welcome.getNotification());
                        return;
                    }
                }

                String key = isUserPermanent() ? "authenticated" : "anonymous";
                key += workspace.isTemporary() ? "-workspace-temporary" : "";

                if (project != null) {
                    String projectType = project.getDescription().getProjectTypeId();

                    while (projectType.contains("/")) {
                        projectType = projectType.replace('/', '-');
                    }

                    while (projectType.contains(" ")) {
                        projectType = projectType.replace(' ', '-');
                    }

                    String url = findGreetingByKey(key + "-" + projectType);
                    if (url != null && !url.trim().isEmpty()) {
                        createGreetingFrame(url);
                        return;
                    }

                    url = findGreetingByKey(key);
                    if (url != null && !url.trim().isEmpty()) {
                        createGreetingFrame(url);
                    }
                }
            }
        });
    }



    /**
     * Fetches current workspace.
     */
    private void getWorkspace() {
        try {
            Message message = new MessageBuilder(RequestBuilder.GET, "/workspace/" + Config.getWorkspaceId())
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .build();

            messageBus.send(message,
                            new RequestCallback<Workspace>(dtoUnmarshallerFactory.newWSUnmarshaller(Workspace.class)) {
                                @Override
                                protected void onSuccess(Workspace result) {
                                    workspace = result;
                                    determineGreetingConfiguration();
                                }

                                @Override
                                protected void onFailure(Throwable e) {
                                    notificationManager.showNotification(
                                            new Notification("Unable to get workspace. " + e.getMessage(),
                                                             Notification.Type.ERROR));
                                    Log.error(GreetingPartPresenter.class, "Unable to get workspace. " + e.getMessage());
                                }
                            }
                           );
        } catch (Exception e) {
            notificationManager.showNotification(
                    new Notification("Unable to get workspace. " + e.getMessage(), Notification.Type.ERROR));
            Log.error(GreetingPartPresenter.class, "Unable to get workspace. " + e.getMessage());
        }
    }


    /**
     * Determines whether the user is permanent.
     *
     * @return <b>true</b> if user is permanent, <b>false</b> otherwise
     */
    private boolean isUserPermanent() {
        if (profile != null && profile.getAttributes() != null) {
            for (Attribute attribute : profile.getAttributes()) {
                if ("temporary".equals(attribute.getName()) && "true".equals(attribute.getValue())) {
                    return false;
                }
            }
        }

        return true;
    }


    /**
     * Returns greeting configuration element.
     *
     * @param key
     * @return
     */
    //@formatter:off
    public static native String findGreetingByKey(String key) /*-{
        try {
            return $wnd.IDE.config.greetings[key];
        } catch (e) {
        }
        return null;
    }-*/;
    //@formatter:on




    /**
     * Creates hidden Frame to fetch greeting parameters.
     *
     * @param url
     */
    private void createGreetingFrame(final String url) {
        final Frame frame = new Frame(url);
        Style style = frame.getElement().getStyle();

        style.setPosition(Style.Position.ABSOLUTE);
        style.setLeft(-1000, Style.Unit.PX);
        style.setTop(-1000, Style.Unit.PX);
        style.setWidth(1, Style.Unit.PX);
        style.setHeight(1, Style.Unit.PX);
        style.setOverflow(Style.Overflow.HIDDEN);

        frame.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                fetchGreetingParamsFromIFrame(IFrameElement.as(frame.getElement()), url);
                frame.removeFromParent();
            }
        });

        RootPanel.get().add(frame);
    }


    /**
     * Fetches greeting parameters from frame.
     *
     * @param element
     * @param greetingContentURL
     */
    // @formatter:off
    private native void fetchGreetingParamsFromIFrame(Element element, String greetingContentURL)/*-{
        try {
            var frameDocument = element.contentWindow.document;
            var head = frameDocument.getElementsByTagName('head')[0];

            var title = null;
            var icon = null;
            var notification = null;

            var children = head.childNodes;
            for (var i = 0; i < children.length; i++) {
                var child = children[i];

                if (child.nodeType != 1) {
                    continue;
                }

                if ("title" == child.nodeName.toLowerCase()) {
                    title = child.innerHTML;
                    continue;
                }

                if ("meta" == child.nodeName.toLowerCase() &&
                    child.getAttribute("name") != null &&
                    "icon" == child.getAttribute("name").toLowerCase()) {
                    icon = child.getAttribute("url");
                    continue;
                }

                if ("meta" == child.nodeName.toLowerCase() &&
                    child.getAttribute("name") != null &&
                    "notification" == child.getAttribute("name").toLowerCase()) {
                    notification = child.getAttribute("content");
                }
            }

            this.@com.codenvy.ide.factory.client.welcome.GreetingPartPresenter::showGreeting(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(title, icon, greetingContentURL, notification);
        } catch (e) {
            this.@com.codenvy.ide.factory.client.welcome.GreetingPartPresenter::fetchingGreetingParamsFailed(Ljava/lang/String;)(e.message);
        }
    }-*/;
    // @formatter:on


    /**
     * Notifies the user when fetching greeting parameters are failed.
     *
     * @param message message to display
     */
    private void fetchingGreetingParamsFailed(String message) {
        notificationManager.showNotification(new Notification(message, Notification.Type.ERROR));
        Log.error(GreetingPartPresenter.class, message);
    }


    /**
     * Opens Greeting part and displays the URL in Frame.
     *
     */
    @Override
    public void showGreeting(String title, String iconURL, String greetingContentURL, final String notification) {
        workspaceAgent.openPart(this, PartStackType.TOOLING);
        workspaceAgent.setActivePart(this);

        view.setTitle(title);
        view.showGreeting(greetingContentURL);

        if (notification != null) {
            new Timer() {
                @Override
                public void run() {
                    new TooltipHint(notification);
                }
            }.schedule(1000);
        }
    }

}
