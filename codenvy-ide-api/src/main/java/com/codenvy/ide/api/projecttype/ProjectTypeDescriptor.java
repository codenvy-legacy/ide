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
package com.codenvy.ide.api.projecttype;

import com.codenvy.api.project.server.type.ProjectType2;
import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;

import java.util.List;

/**
 * @author Vitaly Parfonov
 */
public class ProjectTypeDescriptor {

    private ProjectType2 projectType;

    private String category;

    private String projectTypeName;

    private List<ProjectTemplateDescriptor> templates;

    public String getProjectTypeName() {
        return projectTypeName;
    }

    public void setProjectTypeName(String projectTypeName) {
        this.projectTypeName = projectTypeName;
    }


    public ProjectType2 getProjectType() {
        return projectType;
    }

    public void setProjectType(ProjectType2 projectType) {
        this.projectType = projectType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<ProjectTemplateDescriptor> getTemplates() {
        return templates;
    }

    public void setTemplates(List<ProjectTemplateDescriptor> templates) {
        this.templates = templates;
    }
}
