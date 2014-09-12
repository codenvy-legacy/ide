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

import javax.annotation.Nullable;

/**
 * A node that represents a folder.
 *
 * @author Artem Zatsarynnyy
 */
public class FolderNode extends ItemNode {
    protected final GenericTreeStructure   treeStructure;
    protected final EditorAgent            editorAgent;
    protected       TreeSettings           settings;

    public FolderNode(AbstractTreeNode parent, ItemReference data, GenericTreeStructure treeStructure, TreeSettings settings,
                      EventBus eventBus, EditorAgent editorAgent, ProjectServiceClient projectServiceClient,
                      DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        super(parent, data, eventBus, projectServiceClient, dtoUnmarshallerFactory);
        this.treeStructure = treeStructure;
        this.settings = settings;
        this.editorAgent = editorAgent;
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
            protected void onSuccess(Array<ItemReference> childItems) {
                // remove child nodes for not existed items
                purgeNodes(childItems);
                // add child nodes for new items
                for (ItemReference item : filterNewItems(childItems).asIterable()) {
                    if (isShowHiddenItems || !item.getName().startsWith(".")) {
                        AbstractTreeNode node = createChildNode(item);
                        if (node != null) {
                            children.add(node);
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

    /** Throw away child nodes which was removed (for which we haven't appropriate item in {@code items}). */
    private void purgeNodes(Array<ItemReference> items) {
        Iterable<AbstractTreeNode<?>> it = getChildren().asIterable();
        for (AbstractTreeNode<?> node : it) {
            if (node.getData() instanceof ItemReference && !items.contains((ItemReference)node.getData())) {
                it.iterator().remove();
            }
        }
    }

    /**
     * Returns filtered {@code items} array that contains only items
     * for which we haven't appropriate node in this node's children.
     *
     * @param items
     *         array of {@link ItemReference} to filter
     * @return an array of new items, or an empty array if there are no new items
     */
    private Array<ItemReference> filterNewItems(Array<ItemReference> items) {
        Array<ItemReference> newItems = Collections.createArray(items.asIterable());
        Iterable<AbstractTreeNode<?>> it = getChildren().asIterable();
        for (AbstractTreeNode<?> node : it) {
            if (node.getData() instanceof ItemReference && items.contains((ItemReference)node.getData())) {
                newItems.remove((ItemReference)node.getData());
            }
        }
        return newItems;
    }

    /**
     * Creates node for the specified item. Method called for every child item in {@link #refreshChildren(AsyncCallback)} method.
     * <p/>
     * May be overridden in order to provide a way to create a node for the specified by.
     *
     * @param item
     *         {@link ItemReference} for which need to create node
     * @return new node instance or <code>null</code> if the specified item is not supported
     */
    @Nullable
    protected AbstractTreeNode<?> createChildNode(ItemReference item) {
        if (isFile(item)) {
            return treeStructure.newFileNode(this, item);
        } else if (isFolder(item)) {
            return treeStructure.newFolderNode(this, item);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void delete(final AsyncCallback<Void> callback) {
        super.delete(new AsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                for (EditorPartPresenter editor : editorAgent.getOpenedEditors().getValues().asIterable()) {
                    if (editor.getEditorInput().getFile().getPath().startsWith(getPath())) {
                        eventBus.fireEvent(new FileEvent(editor.getEditorInput().getFile(), FileEvent.FileOperation.CLOSE));
                    }
                }
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }
}
