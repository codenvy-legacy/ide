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
package com.codenvy.ide.collaboration.chat.client;

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
    private static ChatExtension instance;

    private ShowChatControl chatControl;

    private MessageFilter messageFilter = new MessageFilter();

    private MessageHandler handler = new MessageHandler() {
        @Override
        public void onMessage(String message) {
            ServerToClientDto dto = (ServerToClientDto)Jso.deserialize(message).<RoutableDtoClientImpl>cast();
            messageFilter.dispatchMessage(dto);
        }
    };

    private ChatApi chatApi;

    private ProjectChatPresenter chatPresenter;

    private UserInfo userInfo;

    private ProjectModel currentProject;

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
        try {
            IDE.messageBus().unsubscribe("project_chat." + event.getProject().getId(), handler);
        } catch (Exception e) {
            e.printStackTrace();
        }

        currentProject = null;
        if (chatPresenter != null) {
            chatPresenter.projectClosed();
        }
    }

    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        currentProject = event.getProject();
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
                chatPresenter.setProjectId(currentProject);
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
        if (subscribeOnReady && currentProject != null) {
            subscribeToChanel();
        }
        createPresenter();
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
