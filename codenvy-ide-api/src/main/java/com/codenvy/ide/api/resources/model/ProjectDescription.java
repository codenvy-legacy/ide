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
