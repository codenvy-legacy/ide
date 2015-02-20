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
package com.codenvy.ide.api.projecttype.wizard;

import javax.annotation.Nonnull;

/**
 * Defines modes used to open a project wizard.
 *
 * @author Artem Zatsarynnyy
 */
public enum ProjectWizardMode {

    /** Project wizard opened for creating new project. */
    CREATE("create"),
    /** Project wizard opened for creating module from existing folder. */
    CREATE_MODULE("create_module"),
    /** Project wizard opened for updating existing project. */
    UPDATE("update"),
    /** Project wizard opened for creating new project from template. */
    IMPORT("import");

    private final String value;

    private ProjectWizardMode(String value) {
        this.value = value;
    }

    public static ProjectWizardMode parse(@Nonnull String mode) {
        for (ProjectWizardMode wizardMode : values()) {
            if (mode.equals(wizardMode.toString())) {
                return wizardMode;
            }
        }

        throw new IllegalArgumentException("Unknown value: " + mode);
    }

    @Override
    public String toString() {
        return value;
    }
}
