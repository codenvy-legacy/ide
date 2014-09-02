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
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.event.FileEvent;
import com.codenvy.ide.api.projecttree.AbstractTreeNode;
import com.codenvy.ide.api.projecttree.TreeSettings;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

/**
 * A node that represents a folder.
 *
 * @author Artem Zatsarynnyy
 */
public class FolderNode extends AbstractTreeNode<ItemReference> implements StorableNode {
    protected final GenericTreeStructure   treeStructure;
    protected final EventBus               eventBus;
    protected final EditorAgent            editorAgent;
    protected final ProjectServiceClient   projectServiceClient;
    protected final DtoUnmarshallerFactory dtoUnmarshallerFactory;
    protected       TreeSettings           settings;

    public FolderNode(AbstractTreeNode parent, ItemReference data, GenericTreeStructure treeStructure, TreeSettings settings,
                      EventBus eventBus, EditorAgent editorAgent, ProjectServiceClient projectServiceClient,
                      DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        super(parent, data, data.getName());
        this.treeStructure = treeStructure;
        this.settings = settings;
        this.eventBus = eventBus;
        this.editorAgent = editorAgent;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
    }

    /** Tests if the specified item is a file. */
    protected static boolean isFile(ItemReference item) {
        return "file".equals(item.getType());
    }

    /** Tests if the specified item is a folder. */
    protected static boolean isFolder(ItemReference item) {
        return "folder".equals(item.getType());
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
    public ProjectRootNode getProject() {
        AbstractTreeNode<?> parent = getParent();
        while (!(parent instanceof ProjectRootNode)) {
            parent = parent.getParent();
        }
        return (ProjectRootNode)parent;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isLeaf() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void refreshChildren(final AsyncCallback<AbstractTreeNode<?>> callback) {
        final boolean isShowHiddenItems = settings.isShowHiddenItems();
        final Unmarshallable<Array<ItemReference>> unmarshaller = dtoUnmarshallerFactory.newArrayUnmarshaller(ItemReference.class);
        projectServiceClient.getChildren(data.getPath(), new AsyncRequestCallback<Array<ItemReference>>(unmarshaller) {
            @Override
            protected void onSuccess(Array<ItemReference> children) {
                Array<AbstractTreeNode<?>> newChildren = Collections.createArray();
                setChildren(newChildren);
                for (ItemReference item : children.asIterable()) {
                    if (isShowHiddenItems || !item.getName().startsWith(".")) {
                        if (isFile(item)) {
                            newChildren.add(treeStructure.newFileNode(FolderNode.this, item));
                        } else if (isFolder(item)) {
                            newChildren.add(treeStructure.newFolderNode(FolderNode.this, item));
                        }
                    }
                }
                callback.onSuccess(FolderNode.this);
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public boolean isRenemable() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void rename(String newName, final AsyncCallback<Void> callback) {
        projectServiceClient.rename(getPath(), newName, null, new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
                callback.onSuccess(result);
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
    public void delete(final AsyncCallback<Void> callback) {
        projectServiceClient.delete(getPath(), new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
                // close all opened child files
                for (EditorPartPresenter editor : editorAgent.getOpenedEditors().getValues().asIterable()) {
                    if (editor.getEditorInput().getFile().getPath().startsWith(getPath())) {
                        eventBus.fireEvent(new FileEvent(editor.getEditorInput().getFile(), FileEvent.FileOperation.CLOSE));
                    }
                }
                callback.onSuccess(result);
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }
}
