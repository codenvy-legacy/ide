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
package com.codenvy.ide.ext.extruntime.client;

import com.codenvy.ide.ext.extruntime.shared.ApplicationInstance;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestException;

/**
 * Client service to work with Codenvy extensions (creating/launching/getting logs/stopping).
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ExtRuntimeClientService.java Jul 3, 2013 12:48:08 PM azatsarynnyy $
 */
public interface ExtRuntimeClientService {

    /**
     * Create empty Codenvy extension project.
     *
     * @param projectName name of the project to create
     * @param properties properties to set to a newly created project
     * @param groupId group id to set to the projects pom.xml
     * @param artifactId artifact id to set to the projects pom.xml
     * @param version version to set to the projects pom.xml
     * @param callback callback
     * @throws RequestException
     */
    void createEmptyCodenvyExtensionProject(String projectName,
                                       JsonArray<Property> properties,
                                       String groupId,
                                       String artifactId,
                                       String version,
                                       AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Create sample Codenvy extension project.
     *
     * @param projectName name of the project to create
     * @param properties properties to set to a newly created project
     * @param groupId group id to set to the projects pom.xml
     * @param artifactId artifact id to set to the projects pom.xml
     * @param version version to set to the projects pom.xml
     * @param callback callback
     * @throws RequestException
     */
    void createSampleCodenvyExtensionProject(String projectName,
                                       JsonArray<Property> properties,
                                       String groupId,
                                       String artifactId,
                                       String version,
                                       AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Launch Codenvy application with custom extension.
     *
     * @param vfsId identifier of the virtual file system
     * @param projectId identifier of the custom's extension project we want to launch
     * @param callback callback
     * @throws WebSocketException
     */
    public void launch(String vfsId, String projectId, RequestCallback<ApplicationInstance> callback) throws WebSocketException;

    /**
     * Get logs of launched Codenvy application.
     *
     * @param appId identifier of launched Codenvy application to get its logs
     * @param callback callback
     * @throws RequestException
     */
    public void getLogs(String appId, AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Stop Codenvy application.
     *
     * @param appId identifier of Codenvy application to stop
     * @param callback callback
     * @throws RequestException
     */
    public void stop(String appId, AsyncRequestCallback<Void> callback) throws RequestException;
}
