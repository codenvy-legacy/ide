/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.ext.extensions.client;

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.websocket.WebSocketException;
import com.google.gwt.http.client.RequestException;

/**
 * Client service to work with Codenvy extensions.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ExtRuntimeClientService.java Jul 3, 2013 12:48:08 PM azatsarynnyy $
 */
public interface ExtRuntimeClientService {

    /**
     * Create empty Codenvy extension project.
     *
     * @param projectName
     *         name of the project to create
     * @param properties
     *         properties to set to a newly created project
     * @param callback
     *         callback
     * @throws RequestException
     */
    void createEmptyCodenvyExtensionProject(String projectName,
                                            Array<Property> properties,
                                            AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Create sample Codenvy extension project.
     *
     * @param projectName
     *         name of the project to create
     * @param properties
     *         properties to set to a newly created project
     * @param groupId
     *         group id to set to the projects pom.xml
     * @param artifactId
     *         artifact id to set to the projects pom.xml
     * @param version
     *         version to set to the projects pom.xml
     * @param callback
     *         callback
     * @throws RequestException
     */
    void createSampleCodenvyExtensionProject(String projectName,
                                             Array<Property> properties,
                                             String groupId,
                                             String artifactId,
                                             String version,
                                             AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Run a specified WAR, that contains Codenvy Platform with (or without) any extension.
     * <p/>
     * Hot update ability is supported.
     *
     * @param warUrl
     *         URL to Codenvy Platform WAR
     * @param enableHotUpdate
     *         whether to enable the ability hot update or not
     * @param vfsId
     *         identifier of the virtual file system (makes sense only when hot update is enabled)
     * @param projectId
     *         identifier of the extension project (makes sense only when hot update is enabled)
     * @param callback
     *         callback
     * @throws WebSocketException
     */
    public void launch(String projectName, AsyncRequestCallback<String> callback) throws RequestException;

    public void getStatus(Link link, AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Get logs of launched Codenvy application.
     *
     * @param link
     * @param callback
     *         callback
     * @throws RequestException
     */
    public void getLogs(Link link, AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Stop Codenvy application.
     *
     * @param link
     * @param callback
     *         callback
     * @throws RequestException
     */
    public void stop(Link link, AsyncRequestCallback<String> callback) throws RequestException;
}
