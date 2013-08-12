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
package com.codenvy.ide.api.template;

import com.codenvy.ide.resources.model.Project;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The general interface of creating project from template. This interface needs when someone wants to create own create project template.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface CreateProjectProvider {
    /**
     * Creates project from template.
     *
     * @param callback
     */
    void create(AsyncCallback<Project> callback);

    /**
     * Returns project's name.
     *
     * @return project's name
     */
    String getProjectName();

    /**
     * Sets project's name.
     *
     * @param projectName
     */
    void setProjectName(String projectName);
}