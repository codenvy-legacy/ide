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

import com.codenvy.api.project.gwt.client.ProjectTemplateServiceClient;
import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.ide.api.projecttype.ProjectTemplateRegistry;
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
public class ProjectTemplatesComponent implements Component {

    private final ProjectTemplateServiceClient projectTemplateServiceClient;
    private final ProjectTemplateRegistry      projectTemplateRegistry;
    private final DtoUnmarshallerFactory       dtoUnmarshallerFactory;

    @Inject
    public ProjectTemplatesComponent(ProjectTemplateServiceClient projectTemplateServiceClient,
                                     ProjectTemplateRegistry projectTemplateRegistry,
                                     DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.projectTemplateServiceClient = projectTemplateServiceClient;
        this.projectTemplateRegistry = projectTemplateRegistry;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
    }

    @Override
    public void start(final Callback<Component, Exception> callback) {
        projectTemplateServiceClient.getProjectTemplates(new AsyncRequestCallback<Array<ProjectTemplateDescriptor>>(
                dtoUnmarshallerFactory.newArrayUnmarshaller(ProjectTemplateDescriptor.class)) {
            @Override
            protected void onSuccess(Array<ProjectTemplateDescriptor> result) {
                for (ProjectTemplateDescriptor template : result.asIterable()) {
                    projectTemplateRegistry.register(template);
                }
                callback.onSuccess(ProjectTemplatesComponent.this);
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(new Exception("Can't load project templates", exception));
            }
        });
    }
}
