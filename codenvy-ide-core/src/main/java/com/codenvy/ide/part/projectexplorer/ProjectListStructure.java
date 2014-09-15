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
import com.codenvy.ide.api.projecttree.TreeNode;
import com.codenvy.ide.api.projecttree.TreeSettings;
import com.codenvy.ide.api.projecttree.generic.StorableNode;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;

/**
 * Structure for displaying list of all projects from the workspace.
 *
 * @author Artem Zatsarynnyy
 */
public class ProjectListStructure extends AbstractTreeStructure {
    private EventBus               eventBus;
    private ProjectServiceClient   projectServiceClient;
    private DtoUnmarshallerFactory dtoUnmarshallerFactory;

    ProjectListStructure(TreeSettings settings, EventBus eventBus, ProjectServiceClient projectServiceClient,
                         DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        super(settings);
        this.eventBus = eventBus;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void getRoots(final AsyncCallback<Array<TreeNode<?>>> callback) {
        Unmarshallable<Array<ProjectReference>> unmarshaller = dtoUnmarshallerFactory.newArrayUnmarshaller(ProjectReference.class);
        projectServiceClient.getProjects(new AsyncRequestCallback<Array<ProjectReference>>(unmarshaller) {
            @Override
            protected void onSuccess(Array<ProjectReference> result) {
                Array<TreeNode<?>> array = Collections.createArray();
                for (ProjectReference projectReference : result.asIterable()) {
                    array.add(new ProjectNode(null, projectReference, eventBus, projectServiceClient));
                }
                callback.onSuccess(array);
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }

    /** Node that represents project item. */
    public static class ProjectNode extends AbstractTreeNode<ProjectReference> implements StorableNode<ProjectReference> {
        private EventBus             eventBus;
        private ProjectServiceClient projectServiceClient;

        ProjectNode(TreeNode<?> parent, ProjectReference data, EventBus eventBus, ProjectServiceClient projectServiceClient) {
            super(parent, data, eventBus);
            this.eventBus = eventBus;
            this.projectServiceClient = projectServiceClient;
        }

        /** {@inheritDoc} */
        @Nonnull
        @Override
        public String getDisplayName() {
            return data.getName();
        }

        /** {@inheritDoc} */
        @Override
        public boolean isLeaf() {
            return true;
        }

        /** {@inheritDoc} */
        @Override
        public void refreshChildren(AsyncCallback<TreeNode<?>> callback) {
        }

        /** {@inheritDoc} */
        @Override
        public void processNodeAction() {
            eventBus.fireEvent(new OpenProjectEvent(getData().getName()));
        }

        /** {@inheritDoc} */
        @Override
        public boolean isRenamable() {
            return true;
        }

        /** {@inheritDoc} */
        @Override
        public String getName() {
            return data.getName();
        }

        /** {@inheritDoc} */
        @Override
        public String getPath() {
            return data.getPath();
        }

        /** {@inheritDoc} */
        @Override
        public com.codenvy.ide.api.projecttree.generic.ProjectNode getProject() {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public void rename(final String newName, final RenameCallback callback) {
            projectServiceClient.rename(data.getPath(), newName, null, new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    ProjectNode.super.rename(newName, new RenameCallback() {
                        @Override
                        public void onRenamed() {
                            callback.onRenamed();
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            callback.onFailure(caught);
                        }
                    });
                }

                @Override
                protected void onFailure(Throwable exception) {
                    callback.onFailure(exception);
                }
            });
        }

        /** {@inheritDoc} */
        @Override
        public boolean isDeletable() {
            return true;
        }

        /** {@inheritDoc} */
        @Override
        public void delete(final DeleteCallback callback) {
            projectServiceClient.delete(data.getPath(), new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    ProjectNode.super.delete(new DeleteCallback() {
                        @Override
                        public void onDeleted() {
                            callback.onDeleted();
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            callback.onFailure(caught);
                        }
                    });
                }

                @Override
                protected void onFailure(Throwable exception) {
                    callback.onFailure(exception);
                }
            });
        }
    }
}
