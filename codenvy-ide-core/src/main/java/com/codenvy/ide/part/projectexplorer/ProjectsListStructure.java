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
import com.codenvy.ide.api.event.OpenProjectEvent;
import com.codenvy.ide.api.projecttree.AbstractTreeNode;
import com.codenvy.ide.api.projecttree.AbstractTreeStructure;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Structure for displaying projects list.
 *
 * @author Artem Zatsarynnyy
 */
class ProjectsListStructure extends AbstractTreeStructure {
    private EventBus               eventBus;
    private ProjectServiceClient   projectServiceClient;
    private DtoUnmarshallerFactory dtoUnmarshallerFactory;

    ProjectsListStructure(EventBus eventBus, ProjectServiceClient projectServiceClient, DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.eventBus = eventBus;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void getRoots(final AsyncCallback<Array<AbstractTreeNode<?>>> callback) {
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
            eventBus.fireEvent(new OpenProjectEvent(((ProjectNode)node).getData()));
        }
    }

    /** Node that represents project item. */
    private static class ProjectNode extends AbstractTreeNode<ProjectReference> {
        ProjectNode(AbstractTreeNode parent, ProjectReference data) {
            super(parent, data);
        }

        /** {@inheritDoc} */
        @Override
        public String getName() {
            return data.getName();
        }

        /** {@inheritDoc} */
        @Override
        public boolean isLeaf() {
            return true;
        }
    }
}
