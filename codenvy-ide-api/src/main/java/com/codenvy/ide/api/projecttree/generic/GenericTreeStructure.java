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
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.projecttree.TreeNode;
import com.codenvy.ide.api.projecttree.TreeSettings;
import com.codenvy.ide.api.projecttree.TreeStructure;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;

/**
 * Serves as a 'generic' tree and as the factory for creating new tree nodes owned by that tree.
 * <p/>
 * Builds a currently opened project's tree structure that reflects the project's physical structure.
 *
 * @author Artem Zatsarynnyy
 * @see NodeFactory
 * @see com.codenvy.ide.api.projecttree.TreeSettings
 */
public class GenericTreeStructure implements TreeStructure {
    protected final NodeFactory            nodeFactory;
    protected       EventBus               eventBus;
    protected       AppContext             appContext;
    protected       ProjectServiceClient   projectServiceClient;
    protected       DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private         ProjectNode            projectNode;

    protected GenericTreeStructure(NodeFactory nodeFactory, EventBus eventBus, AppContext appContext,
                                   ProjectServiceClient projectServiceClient, DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.nodeFactory = nodeFactory;
        this.eventBus = eventBus;
        this.appContext = appContext;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void getRootNodes(@Nonnull AsyncCallback<Array<TreeNode<?>>> callback) {
        if (projectNode == null) {
            final CurrentProject currentProject = appContext.getCurrentProject();
            if (currentProject != null) {
                projectNode = newProjectNode(currentProject.getRootProject());
            } else {
                callback.onFailure(new IllegalStateException("No project is opened."));
                return;
            }
        }
        callback.onSuccess(Collections.<TreeNode<?>>createArray(projectNode));
    }

    @Nonnull
    @Override
    public TreeSettings getSettings() {
        return TreeSettings.DEFAULT;
    }

    public NodeFactory getNodeFactory() {
        return nodeFactory;
    }

    @Override
    public void getNodeByPath(@Nonnull final String path, @Nonnull final AsyncCallback<TreeNode<?>> callback) {
        getRootNodes(new AsyncCallback<Array<TreeNode<?>>>() {
            @Override
            public void onSuccess(Array<TreeNode<?>> result) {
                ProjectNode project = null;
                for (TreeNode<?> node : result.asIterable()) {
                    if (node instanceof ProjectNode) {
                        project = (ProjectNode)node;
                        break;
                    }
                }

                String p = path;
                if (path.startsWith("/")) {
                    p = path.substring(1);
                }
                getNodeByPathRecursively(project, p, project.getId().length() + 1, new AsyncCallback<TreeNode<?>>() {
                    @Override
                    public void onSuccess(TreeNode<?> result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }
                });
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }

    private void getNodeByPathRecursively(TreeNode<?> node, final String path, final int offset,
                                          final AsyncCallback<TreeNode<?>> callback) {
        node.refreshChildren(new AsyncCallback<TreeNode<?>>() {
            @Override
            public void onSuccess(TreeNode<?> result) {
                for (TreeNode<?> childNode : result.getChildren().asIterable()) {
                    if (path.startsWith(childNode.getId(), offset)) {
                        final int nextOffset = offset + childNode.getId().length() + 1;
                        if (nextOffset > path.length()) {
                            callback.onSuccess(childNode);
                        } else {
                            getNodeByPathRecursively(childNode, path, nextOffset, callback);
                        }
                        return;
                    }
                }
                callback.onSuccess(null);
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }

    /**
     * Creates a new {@link ProjectNode} owned by this tree with the specified associated {@code data}.
     *
     * @param data
     *         the associated {@link ProjectDescriptor}
     * @return a new {@link ProjectNode}
     */
    public ProjectNode newProjectNode(@Nonnull ProjectDescriptor data) {
        return getNodeFactory().newProjectNode(null, data, this);
    }

    /**
     * Creates a new {@link FileNode} owned by this tree
     * with the specified {@code parent} and associated {@code data}.
     *
     * @param parent
     *         the parent node
     * @param data
     *         the associated {@link ItemReference}
     * @return a new {@link FileNode}
     */
    public FileNode newFileNode(@Nonnull TreeNode parent, @Nonnull ItemReference data) {
        if (!"file".equals(data.getType())) {
            throw new IllegalArgumentException("The associated ItemReference type must be - file.");
        }
        return getNodeFactory().newFileNode(parent, data, this);
    }

    /**
     * Creates a new {@link FolderNode} owned by this tree
     * with the specified {@code parent} and associated {@code data}.
     *
     * @param parent
     *         the parent node
     * @param data
     *         the associated {@link ItemReference}
     * @return a new {@link FolderNode}
     */
    public FolderNode newFolderNode(@Nonnull TreeNode parent, @Nonnull ItemReference data) {
        if (!"folder".equals(data.getType())) {
            throw new IllegalArgumentException("The associated ItemReference type must be - folder.");
        }
        return getNodeFactory().newFolderNode(parent, data, this);
    }
}
