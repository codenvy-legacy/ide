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
package com.codenvy.ide.ext.extruntime.server;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

/**
 * Signals that an error has occurred while managing Codenvy-extensions.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ExtensionLauncherException.java Jul 19, 2013 4:03:36 PM azatsarynnyy $
 */
@SuppressWarnings("serial")
public class ExtensionLauncherException extends Exception {
    private int responseStatus = INTERNAL_SERVER_ERROR.getStatusCode();

    /**
     * Constructs a ExtensionLauncherException with the specified detail message.
     * 
     * @param message the detail message
     */
    public ExtensionLauncherException(String message) {
        super(message);
    }

    public ExtensionLauncherException(int responseStatus, String message) {
        super(message);
        this.responseStatus = responseStatus;
    }

    public ExtensionLauncherException(Throwable cause) {
        super(cause);
    }

    public ExtensionLauncherException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getResponseStatus() {
        return responseStatus;
    }
}
