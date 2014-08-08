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

import com.codenvy.ide.api.projecttype.wizard.ProjectTypeWizardRegistry;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizard;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;

/**
 * @author Evgen Vidolob
 */
public class ProjectTypeWizardRegistryImpl implements ProjectTypeWizardRegistry {
    private StringMap<ProjectWizard> map = Collections.createStringMap();

    @Override
    public void addWizard(String projectTypeId, ProjectWizard wizard) {
        map.put(projectTypeId, wizard);
    }

    @Override
    public ProjectWizard getWizard(String projectTypeId) {
        return map.get(projectTypeId);
    }
}
