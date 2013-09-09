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

package org.exoplatform.ide.client.framework.websocket.rest.exceptions;

import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.ide.client.framework.websocket.rest.Pair;
import org.exoplatform.ide.client.framework.websocket.rest.ResponseMessage;

/**
 * Thrown when there was an any exception was received from the server over WebSocket.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: ServerException.java Nov 9, 2012 5:20:18 PM azatsarynnyy $
 */
@SuppressWarnings("serial")
public class ServerException extends Exception {
    private ResponseMessage response;

    private boolean errorMessageProvided;

    public ServerException(ResponseMessage response) {
        this.response = response;
        this.errorMessageProvided = checkErrorMessageProvided();
    }

    @Override
    public String getMessage() {
        if (response.getBody() == null || response.getBody().isEmpty())
            return null;
        return response.getBody();
    }

    public int getHTTPStatus() {
        return response.getResponseCode();
    }

    public String getHeader(String key) {
        for (Pair header : response.getHeaders()) {
            if (key.equals(header.getName())) {
                return header.getValue();
            }
        }

        return null;
    }

    private boolean checkErrorMessageProvided() {
        String value = getHeader(HTTPHeader.JAXRS_BODY_PROVIDED);
        if (value != null) {
            return true;
        }
        return false;
    }

    public boolean isErrorMessageProvided() {
        return errorMessageProvided;
    }
}
