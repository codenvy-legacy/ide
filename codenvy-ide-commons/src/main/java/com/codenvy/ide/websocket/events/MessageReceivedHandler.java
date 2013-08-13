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
package com.codenvy.ide.websocket.events;

/**
 * Handler for {@link MessageReceivedEvent} event.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: MessageReceivedHandler.java Jun 18, 2012 14:44:55 PM azatsarynnyy $
 */
public interface MessageReceivedHandler {
    /**
     * Perform actions, when a WebSocket message was received.
     *
     * @param event
     *         {@link MessageReceivedEvent}
     */
    void onMessageReceived(MessageReceivedEvent event);
}
