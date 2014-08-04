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
package com.codenvy.ide.api.ui.projecttree.generic;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.ide.api.AppContext;
import com.codenvy.ide.api.ui.projecttree.AbstractTreeStructure;
import com.codenvy.ide.api.ui.projecttree.TreeStructureProvider;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/** @author Artem Zatsarynnyy */
public class GenericTreeStructureProvider implements TreeStructureProvider {
    private EventBus               eventBus;
    private ProjectServiceClient   projectServiceClient;
    private DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private AppContext             appContext;

    @Inject
    public GenericTreeStructureProvider(EventBus eventBus,
                                        ProjectServiceClient projectServiceClient,
                                        DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                        AppContext appContext) {
        this.eventBus = eventBus;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.appContext = appContext;
    }

    @Override
    public AbstractTreeStructure getTreeStructure() {
        return new GenericTreeStructure(eventBus, projectServiceClient, dtoUnmarshallerFactory, appContext);
    }
}
