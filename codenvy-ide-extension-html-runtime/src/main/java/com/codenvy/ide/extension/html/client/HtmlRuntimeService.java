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
package com.codenvy.ide.extension.html.client;

import com.codenvy.ide.extension.html.shared.ApplicationInstance;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Service for operations with HTML applications.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: HtmlRuntimeService.java Jun 26, 2013 11:10:07 AM azatsarynnyy $
 *
 */
public abstract class HtmlRuntimeService {
    /** HTML service. */
    private static HtmlRuntimeService instance;

    public static HtmlRuntimeService getInstance() {
        return instance;
    }

    protected HtmlRuntimeService() {
        instance = this;
    }

    /**
     * Start HTML project.
     *
     * @param vfsId
     *         virtual file system id
     * @param projectId
     *         project's id
     * @param callback
     *         callback
     * @throws RequestException
     */
    public abstract void start(String vfsId, ProjectModel project, RequestCallback<ApplicationInstance> callback)
            throws WebSocketException;

    /**
     * Stop running HTML application.
     *
     * @param name
     *         application's name to stop
     * @param callback
     *         callback
     * @throws RequestException
     */
    public abstract void stop(String name, AsyncRequestCallback<Object> callback) throws RequestException;

    /**
     * Get HTML application's logs.
     * 
     * @param name
     *         application's name
     * @param callback
     *         callback
     * @throws RequestException
     */
    public abstract void getLogs(String name, AsyncRequestCallback<StringBuilder> callback) throws RequestException;
}
