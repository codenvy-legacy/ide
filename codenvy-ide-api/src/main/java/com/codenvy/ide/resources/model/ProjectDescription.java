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
 * Description of the project containing nature set and it's specific properties
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class ProjectDescription {

    public static final String PROPERTY_PRIMARY_NATURE = "nature.primary";

    public static final String PROPERTY_MIXIN_NATURES = "nature.mixin";

    /** Properties. */
    protected Project project;

    public ProjectDescription(Project project) {
        this.project = project;
    }

    /** @return primary nature */
    public String getPrimaryNature() {
        return (String)project.getPropertyValue(PROPERTY_PRIMARY_NATURE);
    }

    /** @return The set of Mixin natures or empty set */
    public StringSet getNatures() {
        return asStringSet(PROPERTY_MIXIN_NATURES);
    }

    /**
     * @param property
     * @return
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
