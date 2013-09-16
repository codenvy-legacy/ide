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
package org.exoplatform.ide.client.framework.websocket.events;

/**
 * Event is fired, when WebSocket connection is closed.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: WebSocketClosedEvent.java Jun 18, 2012 14:33:50 PM azatsarynnyy $
 */
public class WebSocketClosedEvent {
    /**
     * The WebSocket connection close code provided by the server.
     *
     * @see https://developer.mozilla.org/en/WebSockets/WebSockets_reference/CloseEvent#Close_codes
     */
    private int code;

    /** A string indicating the reason the server closed the connection. This is specific to the particular server and sub-protocol. */
    private String reason;

    /** Indicates whether or not the connection was cleanly closed. */
    private boolean wasClean;

    public WebSocketClosedEvent() {
    }

    public WebSocketClosedEvent(int code, String reason, boolean wasClean) {
        this.code = code;
        this.reason = reason;
        this.wasClean = wasClean;
    }

    /**
     * Returns close code.
     *
     * @return close code
     */
    public int getCode() {
        return code;
    }

    /**
     * Returns the reason closed the connection.
     *
     * @return reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * Checks weather the connection was cleanly closed.
     *
     * @return <code>true</code> when WebSocket connection was cleanly closed;
     *         <code>false</code> when WebSocket connection was not cleanly closed
     */
    public boolean wasClean() {
        return wasClean;
    }

}
