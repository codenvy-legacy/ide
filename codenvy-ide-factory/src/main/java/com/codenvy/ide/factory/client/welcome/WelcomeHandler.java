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
import com.codenvy.api.user.gwt.client.UserProfileServiceClient;
import com.codenvy.api.user.shared.dto.Attribute;
import com.codenvy.api.user.shared.dto.Profile;
import com.codenvy.api.workspace.shared.dto.Workspace;
import com.codenvy.ide.MimeType;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.HTTPHeader;
import com.codenvy.ide.util.Config;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.MessageBuilder;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

//import com.codenvy.ide.rest.AsyncRequestFactory;

/**
 * @author <a href="mailto:foo@bar.org">Foo Bar</a>
 * @version $Id: Body Header.java 34027 2009-07-15 23:26:43Z aheritier $
 */
@Singleton
public class WelcomeHandler {

    //    private AsyncRequestFactory asyncRequestFactory;
    private final DtoFactory               dtoFactory;
    private final MessageBus               messageBus;
    private final DtoUnmarshallerFactory   dtoUnmarshallerFactory;
    private final NotificationManager      notificationManager;
    private final ResourceProvider         resourceProvider;
    private final UserProfileServiceClient userProfileService;
    private final EventBus                 eventBus;
    private final WorkspaceAgent workspaceAgent;

    private final GreetingPart greetingPart;

    private Workspace workspace;

    private Project project;

    boolean userTemporary;

    @Inject
    public WelcomeHandler(DtoFactory dtoFactory,
                          MessageBus messageBus,
                          EventBus eventBus,
                          DtoUnmarshallerFactory dtoUnmarshallerFactory,
                          NotificationManager notificationManager,
                          UserProfileServiceClient userProfileService,
                          ResourceProvider resourceProvider,
                          WorkspaceAgent workspaceAgent,
                          GreetingPart greetingPart) {

        this.dtoFactory = dtoFactory;
        this.messageBus = messageBus;
        this.eventBus = eventBus;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.notificationManager = notificationManager;
        this.userProfileService = userProfileService;
        this.resourceProvider = resourceProvider;
        this.workspaceAgent = workspaceAgent;

        this.greetingPart = greetingPart;

        eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
                project = event.getProject();
                findGreetingPage();
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


    public void welcome() {
        getWorkspace();
    }


    public void welcome(Factory factory) {
        getWorkspace();
    }


    private void getWorkspace() {
        try {
            Message message = new MessageBuilder(RequestBuilder.GET, "/workspace/" + Config.getWorkspaceId())
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .build();

            messageBus.send(message,
                            new RequestCallback<Workspace>(dtoUnmarshallerFactory.newWSUnmarshaller(Workspace.class)) {
                                @Override
                                protected void onSuccess(Workspace ws) {
                                    workspace = ws;
                                    getUser();
                                }

                                @Override
                                protected void onFailure(Throwable e) {
                                    notificationManager.showNotification(
                                            new Notification(e.getMessage(), Notification.Type.ERROR));
                                }
                            });
        } catch (Exception e) {
            notificationManager.showNotification(new Notification(e.getMessage(), Notification.Type.ERROR));
        }
    }


    private void getUser() {
        userProfileService.getCurrentProfile(null,
            new AsyncRequestCallback<Profile>(dtoUnmarshallerFactory.newUnmarshaller(Profile.class)) {
                @Override
                protected void onSuccess(final Profile profile) {
                    if (profile.getAttributes() != null) {
                        for (Attribute attribute : profile.getAttributes()) {
                            if ("temporary".equals(attribute.getName()) &&
                                "true".equals(attribute.getValue())) {
                                userTemporary = true;
                            }
                        }
                    }

                    findGreetingPage();
                }

                @Override
                protected void onFailure(Throwable e) {
                    notificationManager.showNotification(new Notification(e.getMessage(), Notification.Type.ERROR));
                }
            }
        );
    }


    private static final native void log(String msg) /*-{
        console.log(msg);
    }-*/;


    private void findGreetingPage() {

        log("-------------------------- findGreetingPage ---------------------------");

        log("workspace > " + workspace);
        log("project > " + project);
        log("userTemporary > " + userTemporary);

        if (workspace == null || project == null) {
            return;
        }

        Window.alert("Workspace: " + workspace.getName() + ", Project: " + project.getName());

        workspaceAgent.openPart(greetingPart, PartStackType.TOOLING);
    }

}
