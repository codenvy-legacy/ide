/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/

package com.codenvy.ide.bootstrap;

import com.codenvy.api.project.gwt.client.ProjectTypeServiceClient;
import com.codenvy.api.project.shared.dto.ProjectTypeDefinition;
import com.codenvy.ide.api.projecttype.ProjectTypeRegistry;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.core.Component;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.google.gwt.core.client.Callback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class ProjectTypeComponent implements Component {
    private final ProjectTypeServiceClient projectTypeService;
    private final ProjectTypeRegistry      projectTypeRegistry;
    private final DtoUnmarshallerFactory   dtoUnmarshallerFactory;

    @Inject
    public ProjectTypeComponent(ProjectTypeServiceClient projectTypeService,
                                ProjectTypeRegistry projectTypeRegistry, DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.projectTypeService = projectTypeService;
        this.projectTypeRegistry = projectTypeRegistry;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
    }

    @Override
    public void start(final Callback<Component, Exception> callback) {
        projectTypeService.getProjectTypes(
                new AsyncRequestCallback<Array<ProjectTypeDefinition>>(
                        dtoUnmarshallerFactory.newArrayUnmarshaller(ProjectTypeDefinition.class)) {

                    @Override
                    protected void onSuccess(Array<ProjectTypeDefinition> result) {
                        for (ProjectTypeDefinition projectType : result.asIterable()) {
                            projectTypeRegistry.register(projectType);
                        }
                        callback.onSuccess(ProjectTypeComponent.this);
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        callback.onFailure(new Exception("Can't load project types", exception));
                    }
                });
    }
}
