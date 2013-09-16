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
package org.exoplatform.ide;

/**
 * Abstract template data.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: AbstractTemplate.java Jul 26, 2011 5:37:26 PM vereshchaka $
 */
public abstract class Template {
    private String name;

    private String description;

    /** Flag, is template default. If template is default, it can't be deleted, unlike user's template */
    private boolean defaultTemplate;

    /**
     * Auxiliary field. It is necessary for client, that while parsing json it will be able to detect the type of child (folder or
     * file).
     */
    private String childType;

    public Template() {
    }

    protected Template(String type) {
        this.childType = type;
    }

    ;

    /** @return the defaultTemplate */
    public boolean isDefault() {
        return defaultTemplate;
    }

    /**
     * @param defaultTemplate
     *         the defaultTemplate to set
     */
    public void setDefault(boolean defaultTemplate) {
        this.defaultTemplate = defaultTemplate;
    }

    /** @return the type */
    public String getChildType() {
        return childType;
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

    /** @return the description */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *         the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
