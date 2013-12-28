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
package com.codenvy.ide.wizard.newproject;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.resources.ProjectTypeAgent;
import com.codenvy.ide.resources.ProjectTypeData;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * The implementation of {@link ProjectTypeAgent}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class ProjectTypeAgentImpl implements ProjectTypeAgent {
    private final StringMap<ProjectTypeData> projectTypes;

    /** Create agent. */
    @Inject
    protected ProjectTypeAgentImpl() {
        this.projectTypes = Collections.createStringMap();
    }

    /** {@inheritDoc} */
    @Override
    public void register(@NotNull String typeName,
                         @NotNull String title,
                         @Nullable ImageResource icon,
                         @NotNull String primaryNature,
                         @NotNull Array<String> secondaryNature) {
        if (projectTypes.containsKey(typeName)) {
            Window.alert("Project type with " + typeName + " name already exists");
            return;
        }

        ProjectTypeData projectType = new ProjectTypeData(typeName, title, icon, primaryNature, secondaryNature);
        projectTypes.put(typeName, projectType);
    }

    /**
     * Returns all available project types.
     *
     * @return project types
     */
    public Array<ProjectTypeData> getProjectTypes() {
        return projectTypes.getValues();
    }
}