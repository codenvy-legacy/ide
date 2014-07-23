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
package com.codenvy.ide.tree;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectReference;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Structure for the tree that displays list of projects.
 *
 * @author Artem Zatsarynnyy
 */
public class ProjectsListTreeStructure implements TreeStructure {
    private Array<AbstractTreeNode<?>> roots;
    private ProjectServiceClient       projectServiceClient;
    private DtoUnmarshallerFactory     dtoUnmarshallerFactory;
    private EventBus                   eventBus;

    public ProjectsListTreeStructure(ProjectServiceClient projectServiceClient,
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
                        array.add(new ProjectTreeNode(null, projectReference));
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
        if (node instanceof ProjectTreeNode) {
            final String projectName = ((ProjectTreeNode)node).getData().getName();
            final Unmarshallable<ProjectDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class);

            projectServiceClient.getProject(projectName, new AsyncRequestCallback<ProjectDescriptor>(unmarshaller) {
                @Override
                protected void onSuccess(ProjectDescriptor result) {
                    eventBus.fireEvent(ProjectActionEvent.createProjectOpenedEvent(result));
                }

                @Override
                protected void onFailure(Throwable exception) {
                    Log.error(ProjectsListTreeStructure.class, exception);
                }
            });
        }
    }
}
