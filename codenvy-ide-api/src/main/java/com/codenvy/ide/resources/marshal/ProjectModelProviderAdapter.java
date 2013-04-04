/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package com.codenvy.ide.resources.marshal;

import com.codenvy.ide.api.resources.ModelProvider;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.ProjectDescription;
import com.codenvy.ide.resources.model.Property;


/**
 * This class is used during unmarshaling of the project. It encapsulates both
 * {@link ResourceProvider} used to get proper {@link ModelProvider} and the
 * new {@link Project} instance that is filled with response data.
 * <p/>
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 * exo@exoplatform.com
 * Sep 10, 2012
 */
public class ProjectModelProviderAdapter {
    private final ResourceProvider resourceProvider;

    private Project project;

    /** @param resourceProvider */
    public ProjectModelProviderAdapter(ResourceProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    /**
     * Initializes adapter by used just retrieved Project properties from backend.
     *
     * @param props
     * @return
     */
    public Project init(JsonArray<Property> props) {
        // find primary nature
        String primaryNature = null;
        if (props != null) {
            for (int i = 0; i < props.size(); i++) {
                Property p = props.get(i);
                if (ProjectDescription.PROPERTY_PRIMARY_NATURE.equals(p.getName())) {
                    if (p.getValue().get(0) != null) {
                        primaryNature = p.getValue().get(0).toString();
                    }
                }
            }
        }
        project = resourceProvider.getModelProvider(primaryNature).createProjectInstance();
        return project;
    }

    public Project getProject() {
        return project;
    }
}
