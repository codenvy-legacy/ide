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
package com.codenvy.ide.extension.ant.client.template;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;

/**
 * Client service for creating projects.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface CreateProjectClientService {

    /**
     * Create spring project.
     *
     * @param projectName
     * @param properties
     * @param callback
     * @throws RequestException
     */
    void createSpringProject(String projectName, JsonArray<Property> properties, AsyncRequestCallback<Void> callback)
            throws RequestException;

    /**
     * Creates java project.
     *
     * @param projectName
     * @param properties
     * @param callback
     * @throws RequestException
     */
    void createJavaProject(String projectName, JsonArray<Property> properties, AsyncRequestCallback<Void> callback) throws RequestException;
}