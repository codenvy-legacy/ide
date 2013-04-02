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
package com.codenvy.ide.api.wizard.newproject;

import com.codenvy.ide.resources.model.Project;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * General interface for all classes which creates project.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface CreateProjectHandler {
    /**
     * Adds params which needs for creating project.
     *
     * @param name
     *         param's name
     * @param value
     *         param's value
     */
    public void addParam(String name, String value);

    /**
     * Returns param's value by name.
     *
     * @param name
     *         param's name
     * @return param's name
     */
    public String getParam(String name);

    /**
     * Returns project's name.
     *
     * @return project's name
     */
    public String getProjectName();

    /**
     * Sets project's name.
     *
     * @param name
     */
    public void setProjectName(String name);

    /**
     * Creates project.
     *
     * @param callback
     *         do something when project is created
     */
    public void create(AsyncCallback<Project> callback);
}