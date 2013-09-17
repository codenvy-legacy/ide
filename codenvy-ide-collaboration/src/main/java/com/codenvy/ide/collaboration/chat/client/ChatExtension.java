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
package com.codenvy.ide.collaboration.chat.client;

import com.codenvy.ide.client.util.logging.Log;
import com.codenvy.ide.collaboration.dto.GetChatParticipantsResponse;
import com.codenvy.ide.collaboration.dto.client.DtoClientImpls.GetChatParticipantsImpl;
import com.codenvy.ide.commons.shared.ListenerManager;
import com.codenvy.ide.dtogen.client.RoutableDtoClientImpl;
import com.codenvy.ide.dtogen.shared.ServerError.FailureReason;
import com.codenvy.ide.dtogen.shared.ServerToClientDto;
import com.codenvy.ide.json.client.Jso;
import com.codenvy.ide.json.shared.JsonArray;
import com.codenvy.ide.json.shared.JsonCollections;
import com.google.collide.client.CollabEditorExtension;
import com.google.collide.client.collaboration.CollaborationPropertiesUtil;
import com.google.gwt.core.client.GWT;

import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.userinfo.UserInfo;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler;
import org.exoplatform.ide.client.framework.websocket.FrontendApi.ApiCallback;
import org.exoplatform.ide.client.framework.websocket.MessageBus.ReadyState;
import org.exoplatform.ide.client.framework.websocket.MessageFilter;
import org.exoplatform.ide.client.framework.websocket.events.ConnectionOpenedHandler;
import org.exoplatform.ide.client.framework.websocket.events.MessageHandler;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 *
 */
public class ChatExtension extends Extension
        implements ConnectionOpenedHandler, ProjectOpenedHandler, ProjectClosedHandler, UserInfoReceivedHandler {

    public static final ChatResources resources = GWT.create(ChatResources.class);
    private static ChatExtension   instance;
    private        ShowChatControl chatControl;
    private MessageFilter  messageFilter = new MessageFilter();
    private MessageHandler handler       = new MessageHandler() {
        @Override
        public void onMessage(String message) {
            ServerToClientDto dto = (ServerToClientDto)Jso.deserialize(message).<RoutableDtoClientImpl>cast();
            messageFilter.dispatchMessage(dto);
        }
    };
    private ChatApi              chatApi;
    private ProjectChatPresenter chatPresenter;
    private UserInfo             userInfo;
    private ProjectModel         currentProject;
    private boolean subscribeOnReady = false;
    private SendCodePointerControl pointerControl;

    public static ChatExtension get() {
        return instance;
    }

    /** {@inheritDoc} */
    @Override
    public void initialize() {
        instance = this;
        resources.chatCss().ensureInjected();
        chatControl = new ShowChatControl(resources);
        IDE.getInstance().addControl(chatControl, Docking.TOOLBAR_RIGHT);
        pointerControl = new SendCodePointerControl(resources);
        IDE.getInstance().addControl(pointerControl);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(UserInfoReceivedEvent.TYPE, this);
    }

    private void createPresenter() {
        if (chatPresenter != null) {
            return;
        }
        chatApi = new ChatApi(IDE.messageBus());
        chatPresenter = new ProjectChatPresenter(chatApi, messageFilter, IDE.getInstance(), chatControl, pointerControl,
                                                 userInfo.getName(), CollabEditorExtension.get());
    }

    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        unsubscribe(event.getProject());

        currentProject = null;
        if (chatPresenter != null) {
            chatPresenter.projectClosed();
        }
    }

    private void unsubscribe(ProjectModel project) {
        try {
            IDE.messageBus().unsubscribe("project_chat." + project.getId(), handler);
        } catch (Exception e) {
            Log.error(ChatExtension.class, e);
        }
    }

    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        currentProject = event.getProject();
//        if (!CollaborationPropertiesUtil.isCollaborationEnabled(currentProject)) {
//            return;
//        }
        if (IDE.messageBus().getReadyState() != ReadyState.OPEN) {
            subscribeOnReady = true;
        } else {
            subscribeToChanel();
        }
    }

    private void subscribeToChanel() {
        final String projectId = currentProject.getId();
        IDE.messageBus().subscribe("project_chat." + projectId, handler);
        GetChatParticipantsImpl request = GetChatParticipantsImpl.make();
        request.setProjectId(projectId);
        chatApi.GET_CHAT_PARTISIPANTS.send(request, new ApiCallback<GetChatParticipantsResponse>() {
            @Override
            public void onFail(FailureReason reason) {

            }

            @Override
            public void onMessageReceived(GetChatParticipantsResponse message) {
                if (!CollaborationPropertiesUtil.isCollaborationEnabled(currentProject)) {
                    chatPresenter.setProjectId(currentProject);

                }
                chatPresenter.setChatParticipants(message.getParticipants());
            }
        });
    }

    @Override
    public void onUserInfoReceived(UserInfoReceivedEvent event) {
        userInfo = event.getUserInfo();
        if (IDE.messageBus().getReadyState() != ReadyState.OPEN) {
            IDE.messageBus().setOnOpenHandler(this);
            return;
        }

        createPresenter();
    }

    @Override
    public void onConnectionOpened() {
        createPresenter();
        if (subscribeOnReady && currentProject != null) {
            subscribeToChanel();
        }
    }

    public JsonArray<Participant> getCurrentProjectParticipants() {
        if (chatPresenter == null) {
            return JsonCollections.createArray();
        }

        return chatPresenter.getParticipants().getValues();
    }

    public ListenerManager<ProjectUsersListener> getProjectUserListeners() {
        return chatPresenter.getProjectUsersListeners();
    }
}
