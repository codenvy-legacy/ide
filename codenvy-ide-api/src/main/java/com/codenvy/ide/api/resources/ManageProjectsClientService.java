/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.api.resources;

import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;

import java.util.List;
import java.util.Map;

/**
 * Client service for managing projects.
 *
 * @author Artem Zatsarynnyy
 */
public interface ManageProjectsClientService {
    /**
     * Creates a new project based on the specified {@link ProjectTypeDescriptor} with the provided attributes.
     *
     * @param projectName
     *         name of the created project
     * @param projectTypeDescriptor
     *         descriptor of type of project to create
     * @param attributes
     *         {@link Map} of project's attributes
     * @param callback
     *         callback
     * @throws RequestException
     */
    public void createProject(String projectName, ProjectTypeDescriptor projectTypeDescriptor, Map<String, List<String>> attributes,
                              AsyncRequestCallback<Void> callback) throws RequestException;
}
