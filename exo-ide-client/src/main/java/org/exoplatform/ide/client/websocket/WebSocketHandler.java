/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.client.websocket;

import com.google.gwt.user.client.Window;

import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.application.event.ApplicationClosedEvent;
import org.exoplatform.ide.client.framework.application.event.ApplicationClosedHandler;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.client.framework.websocket.events.ConnectionErrorHandler;
import org.exoplatform.ide.client.framework.websocket.rest.RESTMessageBus;

/**
 * Handler that opens WebSocket connection when IDE loaded and close WebSocket on close IDE.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: WebSocketHandler.java Jun 19, 2012 12:33:42 PM azatsarynnyy $
 */
public class WebSocketHandler implements ApplicationClosedHandler {


    public WebSocketHandler() {
        IDE.addHandler(ApplicationClosedEvent.TYPE, this);
        IDE.setMessageBus(new RESTMessageBus(getWebSocketServerURL()));
        initialize();
    }

    private void initialize() {
        IDE.messageBus().setOnErrorHandler(new ConnectionErrorHandler() {
            @Override
            public void onConnectionError() {
                IDE.messageBus().close();
            }
        });
    }

    /** @see org.exoplatform.ide.client.framework.application.event.ApplicationClosedHandler#onApplicationClosed(org.exoplatform.ide
     * .client.framework.application.event.ApplicationClosedEvent) */
    @Override
    public void onApplicationClosed(ApplicationClosedEvent event) {
        if (IDE.messageBus() != null)
            IDE.messageBus().close();
    }

    /**
     * Returns WebSocket server URL.
     *
     * @return WebSocket server URL
     */
    private String getWebSocketServerURL() {
        boolean isSecureConnection = Window.Location.getProtocol().equals("https:");
        if (isSecureConnection)
            return "wss://" + Window.Location.getHost() + Utils.getWebSocketContext() + Utils.getWorkspaceName();
        else
            return "ws://" + Window.Location.getHost()  + Utils.getWebSocketContext() +  Utils.getWorkspaceName();
    }


}
