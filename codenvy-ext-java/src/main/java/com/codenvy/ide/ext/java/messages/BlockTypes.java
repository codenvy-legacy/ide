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
package com.codenvy.ide.ext.java.messages;

/**
 * Java code blocks types
 *
 * @author Evgen Vidolob
 */
public enum BlockTypes {
    PACKAGE("Package"), //
    IMPORTS("Imports"), //
    IMPORT("Import"), //
    CLASS("Class"), //
    INTERFACE("Interface"), //
    ENUM("Enum"), //
    ANNOTATION("Annotation"), //
    FIELD("Field"), //
    METHOD("Method");

    private String type;

    BlockTypes(String type) {
        this.type = type;
    }

    /** @return the type */
    public String getType() {
        return type;
    }
}
