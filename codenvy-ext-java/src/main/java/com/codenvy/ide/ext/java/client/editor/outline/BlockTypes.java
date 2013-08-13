/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
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
package com.codenvy.ide.ext.java.client.editor.outline;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
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
