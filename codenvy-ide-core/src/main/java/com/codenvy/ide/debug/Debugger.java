/*
 * Copyright (C) ${year} eXo Platform SAS.
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
package com.codenvy.ide.debug;

import com.codenvy.ide.resources.model.File;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The general class which provides to manage breakpoints on server.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface Debugger {
    /**
     * Adds new breakpoint on server.
     *
     * @param file
     * @param lineNumber
     * @param callback
     * @throws RequestException
     */
    void addBreakPoint(File file, int lineNumber, AsyncCallback<Breakpoint> callback) throws RequestException;

    /**
     * Deletes breakpoint on server.
     *
     * @param file
     * @param lineNumber
     * @param callback
     * @throws RequestException
     */
    void deleteBreakPoint(File file, int lineNumber, AsyncCallback<Void> callback) throws RequestException;
}