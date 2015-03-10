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
package org.eclipse.che.ide.projecttype.wizard;

import org.eclipse.che.ide.api.project.type.wizard.ProjectWizardRegistrar;
import org.eclipse.che.ide.api.project.type.wizard.ProjectWizardRegistry;
import org.eclipse.che.ide.collections.Collections;
import org.eclipse.che.ide.collections.StringMap;
import org.eclipse.che.ide.util.loging.Log;
import com.google.inject.Inject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 * Implementation for {@link ProjectWizardRegistry}.
 *
 * @author Artem Zatsarynnyy
 */
public class ProjectWizardRegistryImpl implements ProjectWizardRegistry {
    private static final String DEFAULT_CATEGORY = "Other";
    private final StringMap<ProjectWizardRegistrar> registrars;

    public ProjectWizardRegistryImpl() {
        registrars = Collections.createStringMap();
    }

    @Inject(optional = true)
    private void register(Set<ProjectWizardRegistrar> registrars) {
        for (ProjectWizardRegistrar registrar : registrars) {
            final String id = registrar.getProjectTypeId();
            if (this.registrars.containsKey(id)) {
                Log.warn(ProjectWizardRegistryImpl.class, "Wizard for project type " + id + " already registered.");
            } else {
                this.registrars.put(id, registrar);
            }
        }
    }

    @Nullable
    @Override
    public ProjectWizardRegistrar getWizardRegistrar(@Nonnull String projectTypeId) {
        return registrars.get(projectTypeId);
    }

    @Nullable
    @Override
    public String getWizardCategory(@Nonnull String projectTypeId) {
        ProjectWizardRegistrar registrar = registrars.get(projectTypeId);
        if (registrar != null) {
            final String category = registrar.getCategory();
            return category.isEmpty() ? DEFAULT_CATEGORY : category;
        }
        return null;
    }
}
