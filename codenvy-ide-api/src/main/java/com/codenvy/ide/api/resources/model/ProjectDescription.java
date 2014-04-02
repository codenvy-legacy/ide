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
package com.codenvy.ide.api.resources.model;

import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringSet;

import java.util.List;

/**
 * Description of the project.
 *
 * @author Nikolay Zamosenchuk
 */
public class ProjectDescription {
    public static final String LANGUAGE_ATTRIBUTE = "language";
    protected Project project;

    public ProjectDescription(Project project) {
        this.project = project;
    }

    /** @return project type id */
    public String getProjectTypeId() {
        return project.getProjectTypeId();
    }

    /**
     * Get attribute values as {@link StringSet}.
     *
     * @param attributeName
     *         attribute name
     * @return {@link StringSet} of attribute values or empty {@link StringSet} if no values
     */
    protected StringSet asStringSet(String attributeName) {
        StringSet values = Collections.createStringSet();
        List<String> attributeValues = project.getAttributeValues(attributeName);
        if (attributeValues != null) {
            for (String value : attributeValues) {
                values.add(value);
            }
        }
        return values;
    }
}
