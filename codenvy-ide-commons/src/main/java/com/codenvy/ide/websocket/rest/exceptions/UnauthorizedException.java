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
package com.codenvy.ide.websocket.rest.exceptions;

import com.codenvy.ide.websocket.rest.ResponseMessage;


/**
 * Thrown when there was a HTTP Status-Code 401 (Unauthorized) was received.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: UnauthorizedException.java Nov 9, 2012 5:09:29 PM azatsarynnyy $
 */
@SuppressWarnings("serial")
public class UnauthorizedException extends Exception {
    private ResponseMessage response;

    public UnauthorizedException(ResponseMessage response) {
        this.response = response;
    }

    public int getHTTPStatus() {
        return response.getResponseCode();
    }
}