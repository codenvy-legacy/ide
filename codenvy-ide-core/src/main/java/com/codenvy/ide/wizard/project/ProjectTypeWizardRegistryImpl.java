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
package com.codenvy.ide.wizard.project;

import com.codenvy.ide.api.projecttype.wizard.ProjectTypeWizardRegistry;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizard;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Evgen Vidolob
 */
public class ProjectTypeWizardRegistryImpl implements ProjectTypeWizardRegistry {
    private static final String                     DEFAULT_CATEGORY = "Other";
    private              Map<String, ProjectWizard> map              = new HashMap<>();

    private Map<String, List<String>> categories = new HashMap<>();


    @Override
    public void addWizard(String projectTypeId, ProjectWizard wizard) {
        map.put(projectTypeId, wizard);
    }

    @Override
    public void addProjectTypeToCategory(@Nonnull String projectTypeCategory, @Nonnull String projectTypeId) {
        List<String> types = categories.get(projectTypeCategory);
        if (types == null) {
            categories.put(projectTypeCategory, types = new ArrayList<>());
        }
        types.add(projectTypeId);

    }

    @Override
    public String getCategoryForProjectType(@Nonnull String projectType) {
        for (String key : categories.keySet()) {
            if (categories.get(key) != null && categories.get(key).contains(projectType)) {
                return key;
            }
        }
        return DEFAULT_CATEGORY;
    }

    @Override
    public ProjectWizard getWizard(String projectTypeId) {
        return map.get(projectTypeId);
    }
}
