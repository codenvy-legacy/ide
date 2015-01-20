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
package com.codenvy.ide.wizard.project;

import com.codenvy.ide.api.projecttype.wizard.ProjectWizardRegistrar;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizardRegistrarAgent;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.util.loging.Log;
import com.google.inject.Inject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation for {@link ProjectWizardRegistrarAgent}.
 *
 * @author Artem Zatsarynnyy
 */
public class ProjectWizardRegistrarAgentImpl implements ProjectWizardRegistrarAgent {
    private static final String DEFAULT_CATEGORY = "Other";

    private final StringMap<ProjectWizardRegistrar> projectRegistrars;
    private final Map<String, List<String>> categories = new HashMap<>();

    public ProjectWizardRegistrarAgentImpl() {
        projectRegistrars = Collections.createStringMap();
    }

    @Inject(optional = true)
    private void register(Set<ProjectWizardRegistrar> registrars) {
        for (ProjectWizardRegistrar registrar : registrars) {
            final String id = registrar.getProjectTypeId();
            if (projectRegistrars.containsKey(id)) {
                Log.warn(ProjectWizardRegistrarAgentImpl.class, "Wizard registrar for project type " + id + " already registered.");
            } else {
                projectRegistrars.put(id, registrar);
            }
        }
    }

    @Nullable
    @Override
    public ProjectWizardRegistrar getWizardRegistrar(@Nonnull String projectTypeId) {
        return projectRegistrars.get(projectTypeId);
    }
}
