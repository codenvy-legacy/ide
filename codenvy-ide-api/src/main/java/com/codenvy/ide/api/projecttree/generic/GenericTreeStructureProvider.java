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
package com.codenvy.ide.api.projecttree.generic;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.projecttree.AbstractTreeStructure;
import com.codenvy.ide.api.projecttree.TreeStructureProvider;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/** @author Artem Zatsarynnyy */
public class GenericTreeStructureProvider implements TreeStructureProvider {
    private EventBus               eventBus;
    private AppContext             appContext;
    private ProjectServiceClient   projectServiceClient;
    private DtoUnmarshallerFactory dtoUnmarshallerFactory;

    @Inject
    public GenericTreeStructureProvider(EventBus eventBus,
                                        AppContext appContext,
                                        ProjectServiceClient projectServiceClient,
                                        DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.eventBus = eventBus;
        this.appContext = appContext;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
    }

    @Override
    public AbstractTreeStructure getTreeStructure() {
        return new GenericTreeStructure(eventBus, appContext, projectServiceClient, dtoUnmarshallerFactory);
    }
}
