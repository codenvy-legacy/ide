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
package com.codenvy.ide.resources.model;

import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringSet;

/**
 * Description of the project.
 *
 * @author Nikolay Zamosenchuk
 * @author Artem Zatsarynnyy
 */
public class ProjectDescription {

    public static final String PROPERTY_PROJECT_TYPE = "vfs:projectType";
    public static final String PROPERTY_LANGUAGE     = "language";
    protected Project project;

    public ProjectDescription(Project project) {
        this.project = project;
    }

    /** @return project type id */
    public String getProjectTypeId() {
        return (String)project.getPropertyValue(PROPERTY_PROJECT_TYPE);
    }

    /**
     * Get property values as {@link StringSet}.
     *
     * @param propertyName
     *         property name
     * @return {@link StringSet} of property values
     */
    protected StringSet asStringSet(String propertyName) {
        Property property = project.getProperty(propertyName);
        StringSet natures = Collections.createStringSet();
        if (property != null) {
            natures.addAll(property.getValue());
        }
        return natures;
    }
}
