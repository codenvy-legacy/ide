/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.server;

import com.codenvy.api.project.shared.ProjectTemplateDescription;
import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.dto.server.DtoFactory;
import com.codenvy.dto.shared.JsonArray;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Reads project template descriptions that may be described in separate .json files
 * for every project type. This file should be named as project_type_id.json.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ProjectTemplateDescriptionLoader {

    /**
     * Load project template descriptions for the specified project type.
     *
     * @param projectTypeId
     *         id of the project type for which templates should be loaded
     * @param list
     *         list to add {@link ProjectTemplateDescription}s
     * @throws IOException
     *         if i/o error occurs while reading file with templates
     */
    public void load(String projectTypeId, List<ProjectTemplateDescription> list) throws IOException {
        final URL url = Thread.currentThread().getContextClassLoader().getResource(projectTypeId + ".json");
        if (url != null) {
            try (InputStream inputStream = url.openStream()) {
                JsonArray<ProjectTemplateDescriptor> templates =
                        DtoFactory.getInstance().createListDtoFromJson(inputStream, ProjectTemplateDescriptor.class);
                for (ProjectTemplateDescriptor template : templates) {
                    list.add(new ProjectTemplateDescription(template.getCategory(),
                                                            template.getSources().getType(),
                                                            template.getDisplayName(),
                                                            template.getDescription(),
                                                            template.getSources().getLocation()));
                }
            }
        }
    }
}
