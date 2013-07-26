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
package com.codenvy.ide.ext.extruntime.client;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestException;

/**
 * Client service to work with Codenvy extensions (create/launch/get logs/stop).
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ExtRuntimeClientService.java Jul 3, 2013 12:48:08 PM azatsarynnyy $
 */
public interface ExtRuntimeClientService {
    /**
     * Create Codenvy extension project.
     * 
     * @param projectName name of the project to create
     * @param properties properties to set to a newly created project
     * @param groupId group id to set to the projects pom.xml
     * @param artifactId artifact id to set to the projects pom.xml
     * @param version version to set to the projects pom.xml
     * @param callback callback
     * @throws RequestException
     */
    void createCodenvyExtensionProject(String projectName,
                                       JsonArray<Property> properties,
                                       String groupId,
                                       String artifactId,
                                       String version,
                                       AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Launch Codenvy extension.
     * 
     * @param vfsId identifier of the virtual file system
     * @param projectId identifier of the user's extension project we want to launch
     * @param callback callback
     * @throws WebSocketException
     */
    public void launch(String vfsId, String projectId, RequestCallback<StringBuilder> callback)
                                                                                               throws WebSocketException;

    /**
     * Get logs of launched Codenvy extension.
     * 
     * @param appId identifier of launched extension to get its logs
     * @param callback callback
     * @throws RequestException
     */
    public void getLogs(String appId, AsyncRequestCallback<StringBuilder> callback)
                                                                                   throws RequestException;

    /**
     * Stop Codenvy extension.
     * 
     * @param appId identifier of extension to stop
     * @param callback callback
     * @throws RequestException
     */
    public void stop(String appId, AsyncRequestCallback<Void> callback)
                                                                       throws RequestException;
}
