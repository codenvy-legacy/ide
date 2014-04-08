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
package com.codenvy.ide.api.ui.wizard;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Registry that store wizards for creating new projects
 * @author Evgen Vidolob
 */
public interface ProjectTypeWizardRegistry {

    /**
     * Add wizard for project type.
     *
     * @param projectTypeId the project type id
     * @param wizard the wizard
     */
    void addWizard(@NotNull String projectTypeId,@NotNull ProjectWizard wizard);

    /**
     * Gets wizard wizard for project type.
     *
     * @param projectTypeId the project type id
     * @return the wizard
     */
    @Nullable
    ProjectWizard getWizard(@NotNull String projectTypeId);
}
