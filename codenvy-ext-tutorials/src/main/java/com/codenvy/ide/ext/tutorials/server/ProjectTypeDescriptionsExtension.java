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
package com.codenvy.ide.ext.tutorials.server;

import com.codenvy.api.project.server.ProjectTypeDescriptionExtension;
import com.codenvy.api.project.server.ProjectTypeDescriptionRegistry;
import com.codenvy.api.project.shared.AttributeDescription;
import com.codenvy.api.project.shared.ProjectType;
import com.codenvy.ide.ext.tutorials.shared.Constants;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link ProjectTypeDescriptionExtension} to register project types.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ProjectTypeDescriptionsExtension implements ProjectTypeDescriptionExtension {
    @Inject
    public ProjectTypeDescriptionsExtension(ProjectTypeDescriptionRegistry registry) {
        registry.registerDescription(this);
    }

    @Override
    public List<ProjectType> getProjectTypes() {
        final List<ProjectType> list = new ArrayList<>(1);
        list.add(new ProjectType(Constants.TUTORIAL_ID, Constants.TUTORIAL_NAME,Constants.CODENVY_TUTORIAL_CATEGORY));
        return list;
    }

    @Override
    public List<AttributeDescription> getAttributeDescriptions() {
        final List<AttributeDescription> list = new ArrayList<>(6);
        list.add(new AttributeDescription(Constants.LANGUAGE));
        list.add(new AttributeDescription(Constants.LANGUAGE_VERSION));
        list.add(new AttributeDescription(Constants.FRAMEWORK));
        list.add(new AttributeDescription(Constants.BUILDER_NAME));
        list.add(new AttributeDescription(Constants.BUILDER_MAVEN_SOURCE_FOLDERS));
        list.add(new AttributeDescription(Constants.RUNNER_NAME));
        return list;
    }


}
