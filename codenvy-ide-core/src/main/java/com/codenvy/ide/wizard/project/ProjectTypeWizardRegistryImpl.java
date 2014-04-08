/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.wizard.project;

import com.codenvy.ide.api.ui.wizard.ProjectTypeWizardRegistry;
import com.codenvy.ide.api.ui.wizard.ProjectWizard;
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
