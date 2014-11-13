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
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.projecttree.AbstractTreeNode;
import com.codenvy.ide.api.projecttree.AbstractTreeStructure;
import com.codenvy.ide.api.projecttree.TreeNode;
import com.codenvy.ide.api.projecttree.TreeSettings;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

/**
 * {@link AbstractTreeStructure} for the hierarchical tree.
 *
 * @author Artem Zatsarynnyy
 */
public class GenericTreeStructure extends AbstractTreeStructure {
    protected ProjectDescriptor      project;
    protected EventBus               eventBus;
    protected EditorAgent            editorAgent;
    protected AppContext             appContext;
    protected ProjectServiceClient   projectServiceClient;
    protected DtoUnmarshallerFactory dtoUnmarshallerFactory;

    protected GenericTreeStructure(TreeSettings settings, ProjectDescriptor project, EventBus eventBus, EditorAgent editorAgent,
                                   AppContext appContext, ProjectServiceClient projectServiceClient,
                                   DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        super(settings);
        this.project = project;
        this.eventBus = eventBus;
        this.editorAgent = editorAgent;
        this.appContext = appContext;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void getRoots(AsyncCallback<Array<TreeNode<?>>> callback) {
        AbstractTreeNode projectRoot =
                new ProjectNode(null, project, this, settings, eventBus, projectServiceClient, dtoUnmarshallerFactory);
        callback.onSuccess(Collections.<TreeNode<?>>createArray(projectRoot));
    }

    @Override
    public void getNodeByPath(final String path, final AsyncCallback<TreeNode<?>> callback) {
        getRoots(new AsyncCallback<Array<TreeNode<?>>>() {
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
                refreshAndGetChildByName(project, p.split("/"), 1, new AsyncCallback<TreeNode<?>>() {
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

    private void refreshAndGetChildByName(TreeNode<?> node, final String[] path, final int index,
                                          final AsyncCallback<TreeNode<?>> callback) {
        node.refreshChildren(new AsyncCallback<TreeNode<?>>() {
            @Override
            public void onSuccess(TreeNode<?> result) {
                for (TreeNode<?> childNode : result.getChildren().asIterable()) {
                    if (childNode.getId().equals(path[index])) {
                        if (index + 1 == path.length) {
                            callback.onSuccess(childNode);
                        } else {
                            refreshAndGetChildByName(childNode, path, index + 1, callback);
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

    public FileNode newFileNode(TreeNode parent, ItemReference data) {
        return new FileNode(parent, data, eventBus, projectServiceClient, dtoUnmarshallerFactory);
    }

    public FolderNode newFolderNode(TreeNode parent, ItemReference data) {
        return new FolderNode(parent, data, this, settings, eventBus, editorAgent, projectServiceClient, dtoUnmarshallerFactory);
    }
}
