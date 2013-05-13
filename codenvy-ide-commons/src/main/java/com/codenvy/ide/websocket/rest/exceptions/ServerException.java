/**
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
 *
 */

package com.codenvy.ide.websocket.rest.exceptions;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.rest.HTTPHeader;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.rest.Pair;

/**
 * Thrown when there was an any exception was received from the server over WebSocket.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: ServerException.java Nov 9, 2012 5:20:18 PM azatsarynnyy $
 */
@SuppressWarnings("serial")
public class ServerException extends Exception {
    private Message response;

    private boolean errorMessageProvided;

    public ServerException(Message response) {
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
        JsonArray<Pair> headers = response.getHeaders();
        for (int i = 0; i < headers.size(); i++) {
            Pair header = headers.get(i);
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
