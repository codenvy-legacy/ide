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
package org.exoplatform.ide.editor.java.client.model;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Dec 2, 2011 12:34:20 PM evgen $
 */
public class ShortTypeInfo {
    private Integer modifiers;

    private String name;

    /** Full Qualified Class Name */
    private String qualifiedName;

    /** Means this is CLASS, INTERFACE or ANNOTATION */
    private Types type;

    /**
     *
     */
    public ShortTypeInfo() {
    }

    /**
     * @param modifiers
     * @param name
     * @param qualifiedName
     * @param type
     */
    public ShortTypeInfo(Integer modifiers, String name, String qualifiedName, Types type) {
        super();
        this.modifiers = modifiers;
        this.name = name;
        this.qualifiedName = qualifiedName;
        this.type = type;
    }

    /** @return the modifiers */
    public Integer getModifiers() {
        return modifiers;
    }

    /**
     * @param modifiers
     *         the modifiers to set
     */
    public void setModifiers(Integer modifiers) {
        this.modifiers = modifiers;
    }

    /** @return the name */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *         the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /** @return the qualifiedName */
    public String getQualifiedName() {
        return qualifiedName;
    }

    /**
     * @param qualifiedName
     *         the qualifiedName to set
     */
    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    /** @return the type */
    public Types getType() {
        return type;
    }

    /**
     * @param type
     *         the type to set
     */
    public void setType(Types type) {
        this.type = type;
    }

}
