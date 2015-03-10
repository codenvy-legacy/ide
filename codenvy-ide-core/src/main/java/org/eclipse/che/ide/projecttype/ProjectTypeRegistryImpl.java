/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.projecttype;

import org.eclipse.che.api.project.shared.dto.ProjectTypeDefinition;
import org.eclipse.che.ide.api.project.type.ProjectTypeRegistry;
import org.eclipse.che.ide.util.loging.Log;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vitaly Parfonov
 */
public class ProjectTypeRegistryImpl implements ProjectTypeRegistry {

    private List<ProjectTypeDefinition> types;

    @Override
    public void setProjectTypes(@Nonnull List<ProjectTypeDefinition> projectTypes) {
        types = projectTypes;
    }

    @Override
    public  ProjectTypeDefinition getProjectType(@Nonnull String id) {
        if (types == null || types.isEmpty()) {
            return null;
        }
        for (ProjectTypeDefinition type : types) {
            if (id.equals(type.getId())) {
                return type;
            }
        }
        return null;
    }

    @Override
    public List<ProjectTypeDefinition> getProjectTypes() {
        Log.info(this.getClass(),  types.size());
        return types;
    }

    @Override
    public void register(ProjectTypeDefinition projectType) {
        if (types == null) {
            types = new ArrayList<>();
        }
        types.add(projectType);
    }
}
