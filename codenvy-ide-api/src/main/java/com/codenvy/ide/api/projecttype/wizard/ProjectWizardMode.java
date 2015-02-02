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

/**
 * Defines modes used to open a project wizard.
 *
 * @author Artem Zatsarynnyy
 */
public enum ProjectWizardMode {

    /** Project wizard opened for creating new project. */
    CREATE("create"),
    /** Project wizard opened for updating existing project. */
    UPDATE("update"),
    /** Project wizard opened for creating new project from template. */
    IMPORT("import");

    private final String value;

    private ProjectWizardMode(String value) {
        this.value = value;
    }

    public static ProjectWizardMode parse(String mode) {
        switch (mode) {
            case "create":
                return CREATE;
            case "update":
                return UPDATE;
            case "import":
                return IMPORT;
            default:
                throw new IllegalArgumentException("");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
