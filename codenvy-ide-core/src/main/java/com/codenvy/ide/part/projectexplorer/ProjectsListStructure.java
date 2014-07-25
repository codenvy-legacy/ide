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
package com.codenvy.ide.part.projectexplorer;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectReference;
import com.codenvy.ide.api.event.ProjectActionEvent_2;
import com.codenvy.ide.api.ui.tree.AbstractTreeNode;
import com.codenvy.ide.api.ui.tree.TreeStructure;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Structure for displaying list of projects.
 *
 * @author Artem Zatsarynnyy
 */
public class ProjectsListStructure implements TreeStructure {
    private Array<AbstractTreeNode<?>> roots;
    private ProjectServiceClient       projectServiceClient;
    private DtoUnmarshallerFactory     dtoUnmarshallerFactory;
    private EventBus                   eventBus;

    public ProjectsListStructure(ProjectServiceClient projectServiceClient,
                                 DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                 EventBus eventBus) {
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.eventBus = eventBus;

        roots = Collections.createArray();
    }

    /** {@inheritDoc} */
    @Override
    public void getRoots(final AsyncCallback<Array<AbstractTreeNode<?>>> callback) {
        if (!roots.isEmpty()) {
            callback.onSuccess(roots);
        } else {
            Unmarshallable<Array<ProjectReference>> unmarshaller = dtoUnmarshallerFactory.newArrayUnmarshaller(ProjectReference.class);

            projectServiceClient.getProjects(new AsyncRequestCallback<Array<ProjectReference>>(unmarshaller) {
                @Override
                protected void onSuccess(Array<ProjectReference> result) {
                    Array<AbstractTreeNode<?>> array = Collections.createArray();
                    for (ProjectReference projectReference : result.asIterable()) {
                        array.add(new ProjectNode(null, projectReference));
                    }
                    callback.onSuccess(array);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    callback.onFailure(exception);
                }
            });
        }
    }

    /** {@inheritDoc} */
    @Override
    public void refreshChildren(AbstractTreeNode<?> node, AsyncCallback<AbstractTreeNode<?>> callback) {
        callback.onSuccess(node);
    }

    /** {@inheritDoc} */
    @Override
    public void processNodeAction(AbstractTreeNode<?> node) {
        // open project
        if (node instanceof ProjectNode) {
            eventBus.fireEvent(ProjectActionEvent_2.createOpenProjectEvent(((ProjectNode)node).getData()));
        }
    }
}
