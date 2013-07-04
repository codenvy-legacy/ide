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
package com.codenvy.ide.collaboration.watcher.client;

import com.codenvy.ide.dtogen.client.RoutableDtoClientImpl;
import com.codenvy.ide.dtogen.shared.ServerToClientDto;
import com.codenvy.ide.json.client.Jso;

import com.codenvy.ide.notification.NotificationManager;

import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.userinfo.UserInfo;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler;
import org.exoplatform.ide.client.framework.websocket.MessageFilter;
import org.exoplatform.ide.client.framework.websocket.events.ConnectionOpenedHandler;
import org.exoplatform.ide.client.framework.websocket.events.MessageHandler;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class VfsWatcherExtension extends Extension implements ConnectionOpenedHandler, UserInfoReceivedHandler {

    private UserInfo userInfo;

    private MessageFilter messageFilter = new MessageFilter();

    CollaborationApi collaborationApi;

    private boolean connectionOpened = false;

    public static VfsWatcherExtension instance;

    public static VfsWatcherExtension get(){
        return instance;
    }

    /** {@inheritDoc} */
    @Override
    public void initialize() {
        instance = this;
        IDE.eventBus().addHandler(UserInfoReceivedEvent.TYPE, this);
        IDE.messageBus().setOnOpenHandler(this);
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
    }

    @Override
    public void onConnectionOpened() {
        connectionOpened = true;
        if (userInfo != null) {
            subscribe();
        }
    }
}
