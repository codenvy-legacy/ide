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
package org.eclipse.che.ide.part.projectexplorer;

import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.project.shared.dto.ProjectReference;
import org.eclipse.che.ide.api.event.OpenProjectEvent;
import org.eclipse.che.ide.api.project.tree.AbstractTreeNode;
import org.eclipse.che.ide.api.project.tree.TreeNode;
import org.eclipse.che.ide.api.project.tree.TreeSettings;
import org.eclipse.che.ide.api.project.tree.TreeStructure;
import org.eclipse.che.ide.api.project.tree.generic.StorableNode;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.collections.Collections;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.rest.Unmarshallable;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;

/**
 * Structure for displaying list of all projects from the workspace.
 *
 * @author Artem Zatsarynnyy
 */
public class ProjectListStructure implements TreeStructure {
    private EventBus               eventBus;
    private ProjectServiceClient   projectServiceClient;
    private DtoUnmarshallerFactory dtoUnmarshallerFactory;

    @Inject
    ProjectListStructure(EventBus eventBus, ProjectServiceClient projectServiceClient, DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.eventBus = eventBus;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void getRootNodes(@Nonnull final AsyncCallback<Array<TreeNode<?>>> callback) {
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

    @Nonnull
    @Override
    public TreeSettings getSettings() {
        return TreeSettings.DEFAULT;
    }

    @Override
    public void getNodeByPath(@Nonnull String path, @Nonnull AsyncCallback<TreeNode<?>> callback) {
    }

    /** Node that represents project item. */
    public class ProjectNode extends AbstractTreeNode<ProjectReference> implements StorableNode<ProjectReference> {
        private EventBus             eventBus;
        private ProjectServiceClient projectServiceClient;

        ProjectNode(TreeNode<?> parent, ProjectReference data, EventBus eventBus, ProjectServiceClient projectServiceClient) {
            super(parent, data, ProjectListStructure.this, eventBus);
            this.eventBus = eventBus;
            this.projectServiceClient = projectServiceClient;
        }

        /** {@inheritDoc} */
        @Nonnull
        @Override
        public String getDisplayName() {
            return getData().getName();
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
            return getData().getName();
        }

        /** {@inheritDoc} */
        @Override
        public String getPath() {
            return getData().getPath();
        }

        /** {@inheritDoc} */
        @Override
        public boolean canContainsFolder() {
            return false;
        }

        /** {@inheritDoc} */
        @Nonnull
        @Override
        public String getId() {
            return getData().getName();
        }

        /** {@inheritDoc} */
        @Nonnull
        @Override
        public org.eclipse.che.ide.api.project.tree.generic.ProjectNode getProject() {
            return null;
        }

        /** {@inheritDoc} */
        @Override
        public void rename(final String newName, final RenameCallback callback) {
            projectServiceClient.rename(getPath(), newName, null, new AsyncRequestCallback<Void>() {
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
            projectServiceClient.delete(getPath(), new AsyncRequestCallback<Void>() {
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
