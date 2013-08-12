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
package com.codenvy.ide.websocket.rest.exceptions;

import com.codenvy.ide.websocket.Message;

/**
 * Thrown when there was a HTTP Status-Code 401 (Unauthorized) was received.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: UnauthorizedException.java Nov 9, 2012 5:09:29 PM azatsarynnyy $
 */
@SuppressWarnings("serial")
public class UnauthorizedException extends Exception {
    private Message message;

    public UnauthorizedException(Message message) {
        this.message = message;
    }

    public int getHTTPStatus() {
        return message.getResponseCode();
    }
}