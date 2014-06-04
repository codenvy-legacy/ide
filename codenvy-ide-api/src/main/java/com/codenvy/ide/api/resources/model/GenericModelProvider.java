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

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.ide.api.resources.ModelProvider;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Model provider for generic Project.
 *
 * @author Nikolay Zamosenchuk
 */
public class GenericModelProvider implements ModelProvider {

    private final EventBus             eventBus;
    private final AsyncRequestFactory  asyncRequestFactory;
    private final ProjectServiceClient projectServiceClient;
    private final DtoUnmarshallerFactory dtoUnmarshallerFactory;

    /** Creates GenericModel provider. */
    @Inject
    public GenericModelProvider(EventBus eventBus,
                                AsyncRequestFactory asyncRequestFactory,
                                ProjectServiceClient projectServiceClient,
                                DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.eventBus = eventBus;
        this.asyncRequestFactory = asyncRequestFactory;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
    }

    /** {@inheritDoc} */
    @Override
    public Project createProjectInstance() {
        return new Project(eventBus, asyncRequestFactory, projectServiceClient, dtoUnmarshallerFactory);
    }

}
