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
package com.codenvy.ide.extension.maven.client.template;

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
     * Creates web project.
     *
     * @param projectName
     * @param properties
     * @param callback
     * @throws RequestException
     */
    void createWarProject(String projectName, JsonArray<Property> properties, AsyncRequestCallback<Void> callback)
            throws RequestException;

    /**
     * Creates java project.
     *
     * @param projectName
     * @param sourceFolder
     * @param properties
     * @param callback
     * @throws RequestException
     */
    void createJavaProject(String projectName, String sourceFolder, JsonArray<Property> properties,
                           AsyncRequestCallback<Void> callback) throws RequestException;
}