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
package com.codenvy.ide.resources.marshal;

import com.codenvy.ide.api.resources.ModelProvider;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.ProjectDescription;
import com.codenvy.ide.resources.model.Property;

/**
 * This class is used during unmarshalling of the project. It encapsulates both
 * {@link ResourceProvider} used to get proper {@link ModelProvider} and the
 * new {@link Project} instance that is filled with response data.
 *
 * @author Nikolay Zamosenchuk
 */
public class ProjectModelProviderAdapter {
    private final ResourceProvider resourceProvider;
    private       Project          project;

    public ProjectModelProviderAdapter(ResourceProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    /**
     * Initializes adapter by used just retrieved Project properties from backend.
     *
     * @param props
     *         project properties
     * @return new {@link Project} instance
     */
    public Project init(Array<Property> props) {
        String language = null;
        if (props != null) {
            for (int i = 0; i < props.size(); i++) {
                Property p = props.get(i);
                if (ProjectDescription.ATTR_LANGUAGE.equals(p.getName())) {
                    if (p.getValue().get(0) != null) {
                        language = p.getValue().get(0).toString();
                    }
                }
            }
        }
        project = resourceProvider.getModelProvider(language).createProjectInstance();
        return project;
    }

    public Project getProject() {
        return project;
    }
}
