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
