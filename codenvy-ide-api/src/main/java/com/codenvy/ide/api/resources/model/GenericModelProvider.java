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
