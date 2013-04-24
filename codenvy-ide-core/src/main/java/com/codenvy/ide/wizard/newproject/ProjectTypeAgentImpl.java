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
package com.codenvy.ide.wizard.newproject;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.ProjectTypeAgent;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
@Singleton
public class ProjectTypeAgentImpl implements ProjectTypeAgent {
    private final JsonArray<ProjectTypeData> projectTypes;
    private       ProjectTypeData            selectedProjectType;

    @Inject
    protected ProjectTypeAgentImpl() {
        this.projectTypes = JsonCollections.createArray();
    }

    /** {@inheritDoc} */
    @Override
    public void registerProjectType(String typeName, String title, ImageResource icon) {
        ProjectTypeData projectType = new ProjectTypeData(typeName, title, icon);
        projectTypes.add(projectType);
    }

    /** {@inheritDoc} */
    @Override
    public String getSelectedProjectType() {
        return selectedProjectType != null ? selectedProjectType.getTypeName() : null;
    }

    public void setSelectedProjectType(ProjectTypeData projectType) {
        selectedProjectType = projectType;
    }

    public JsonArray<ProjectTypeData> getProjectTypes() {
        return projectTypes;
    }
}