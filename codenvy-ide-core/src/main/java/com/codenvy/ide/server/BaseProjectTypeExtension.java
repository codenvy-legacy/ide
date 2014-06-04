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
package com.codenvy.ide.server;

import com.codenvy.api.project.server.ProjectTypeDescriptionRegistry;
import com.codenvy.api.project.server.ProjectTypeExtension;
import com.codenvy.api.project.shared.Attribute;
import com.codenvy.api.project.shared.ProjectTemplateDescription;
import com.codenvy.api.project.shared.ProjectType;
import com.codenvy.ide.Constants;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;

/** @author Vitalii Parfonov */

/**
 * Add this project type for replace UnknownProjectType by user decision
 * (e.g after cloning project) for avoid cyclic asking if keep
 * only UnknownProjectType.
 * This is temporary solution maybe removed in future.
 */
@Singleton
public class BaseProjectTypeExtension implements ProjectTypeExtension {

    @Inject
    public BaseProjectTypeExtension(ProjectTypeDescriptionRegistry registry) {
        registry.registerProjectType(this);
    }

    @Override
    public ProjectType getProjectType() {
        return new ProjectType(Constants.UNKNOWN_ID, Constants.UNKNOWN_PROJECT_TYPE, Constants.UNKNOWN_CATEGORY);
    }

    @Override
    public List<Attribute> getPredefinedAttributes() {
        return Collections.singletonList(new Attribute(Constants.LANGUAGE, "unknown"));
    }

    @Override
    public List<ProjectTemplateDescription> getTemplates() {
        return Collections.emptyList();
    }
}
