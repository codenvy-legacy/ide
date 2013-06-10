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