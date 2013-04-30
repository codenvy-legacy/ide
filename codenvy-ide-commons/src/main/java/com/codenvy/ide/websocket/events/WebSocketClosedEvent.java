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
package com.codenvy.ide.websocket.events;

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