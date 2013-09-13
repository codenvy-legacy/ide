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
package com.codenvy.ide.collaboration.watcher.client;

import com.codenvy.ide.collaboration.dto.DisableEnableCollaborationDto;
import com.codenvy.ide.collaboration.dto.RoutingTypes;
import com.codenvy.ide.collaboration.dto.client.DtoClientImpls;
import com.codenvy.ide.dtogen.client.RoutableDtoClientImpl;
import com.codenvy.ide.dtogen.shared.ServerError;
import com.codenvy.ide.dtogen.shared.ServerToClientDto;
import com.codenvy.ide.json.client.Jso;
import com.codenvy.ide.notification.NotificationManager;
import com.google.collide.client.bootstrap.BootstrapSession;
import com.google.collide.client.disable.DisableEnableCollaborationEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import org.exoplatform.ide.client.framework.event.CollaborationChangedEvent;
import org.exoplatform.ide.client.framework.event.CollaborationChangedHandler;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.userinfo.UserInfo;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler;
import org.exoplatform.ide.client.framework.websocket.FrontendApi;
import org.exoplatform.ide.client.framework.websocket.MessageFilter;
import org.exoplatform.ide.client.framework.websocket.events.ConnectionOpenedHandler;
import org.exoplatform.ide.client.framework.websocket.events.MessageHandler;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class VfsWatcherExtension extends Extension implements ConnectionOpenedHandler, UserInfoReceivedHandler,
                                                              CollaborationChangedHandler {

    private UserInfo userInfo;

    private MessageFilter messageFilter = new MessageFilter();

    CollaborationApi collaborationApi;

    private boolean connectionOpened = false;

    public static VfsWatcherExtension instance;
    private HandlerRegistration handlerRegistration;

    public static VfsWatcherExtension get() {
        return instance;
    }

    /** {@inheritDoc} */
    @Override
    public void initialize() {
        instance = this;
        IDE.eventBus().addHandler(UserInfoReceivedEvent.TYPE, this);
        IDE.messageBus().setOnOpenHandler(this);
        handlerRegistration = IDE.addHandler(CollaborationChangedEvent.TYPE, this);
    }

    /** {@inheritDoc} */
    @Override
    public void onUserInfoReceived(UserInfoReceivedEvent event) {
        userInfo = event.getUserInfo();
        if (connectionOpened) {
            subscribe();
        }
    }

    private void subscribe() {
        collaborationApi = new CollaborationApi(IDE.messageBus());
        new VfsWatcher(messageFilter, IDE.eventBus(), collaborationApi, NotificationManager.get());
        IDE.messageBus().subscribe("vfs_watcher." + userInfo.getClientId(), new MessageHandler() {
            @Override
            public void onMessage(String message) {
                ServerToClientDto dto = (ServerToClientDto)Jso.deserialize(message).<RoutableDtoClientImpl>cast();
                messageFilter.dispatchMessage(dto);
            }
        });
        messageFilter.registerMessageRecipient(RoutingTypes.DISABLE_ENABLE_COLLABORATION, new FrontendApi.ApiCallback<DisableEnableCollaborationDto>() {
            @Override
            public void onFail(ServerError.FailureReason reason) {
            }

            @Override
            public void onMessageReceived(DisableEnableCollaborationDto message) {
                handlerRegistration.removeHandler();
                IDE.fireEvent(new DisableEnableCollaborationEvent(message.isEnabled(), false));
                handlerRegistration = IDE.addHandler(CollaborationChangedEvent.TYPE, VfsWatcherExtension.this);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onConnectionOpened() {
        connectionOpened = true;
        if (userInfo != null) {
            subscribe();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCollaborationChanged(CollaborationChangedEvent event) {
        DtoClientImpls.DisableEnableCollaborationDtoImpl dto = DtoClientImpls.DisableEnableCollaborationDtoImpl.make();
        dto.setIsEnabled(event.isEnabled());
        dto.setProjectId(event.getProject().getId());
        dto.setClientId(BootstrapSession.getBootstrapSession().getActiveClientId());
        collaborationApi.DISABLE_ENABLE_COLLAB.send(dto);
    }
}
