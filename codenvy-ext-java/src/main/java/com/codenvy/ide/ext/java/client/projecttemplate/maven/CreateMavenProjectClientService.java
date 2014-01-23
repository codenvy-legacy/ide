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
package com.codenvy.ide.ext.java.client.projecttemplate.maven;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;

import java.util.List;
import java.util.Map;

/**
 * Client service for creating projects.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface CreateMavenProjectClientService {
    /**
     * Creates java project.
     *
     * @param projectName
     * @param attributes
     * @param callback
     * @throws com.google.gwt.http.client.RequestException
     *
     */
    void createJarProject(String projectName, Map<String, List<String>> attributes, AsyncRequestCallback<Void> callback)
            throws RequestException;

    void unzipJarTemplate(String projectName, AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Creates web project.
     *
     * @param projectName
     * @param properties
     * @param callback
     * @throws com.google.gwt.http.client.RequestException
     *
     */
    void unzipWarTemplate(String projectName, Array<Property> properties, AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Create spring project.
     *
     * @param projectName
     * @param properties
     * @param callback
     * @throws com.google.gwt.http.client.RequestException
     *
     */
    void unzipSpringTemplate(String projectName, Array<Property> properties, AsyncRequestCallback<Void> callback) throws RequestException;

}