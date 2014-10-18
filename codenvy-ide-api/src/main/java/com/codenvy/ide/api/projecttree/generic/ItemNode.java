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
import com.codenvy.ide.api.event.ItemEvent;
import com.codenvy.ide.api.projecttree.AbstractTreeNode;
import com.codenvy.ide.api.projecttree.TreeNode;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;

/**
 * A node that represents an {@link ItemReference}.
 *
 * @author Artem Zatsarynnyy
 */
public abstract class ItemNode extends AbstractTreeNode<ItemReference> implements StorableNode<ItemReference> {
    protected ProjectServiceClient   projectServiceClient;
    protected DtoUnmarshallerFactory dtoUnmarshallerFactory;

    /**
     * Creates new node.
     *
     * @param parent
     *         parent node
     * @param data
     *         an object this node encapsulates
     * @param eventBus
     * @param projectServiceClient
     * @param dtoUnmarshallerFactory
     */
    public ItemNode(TreeNode<?> parent, ItemReference data, EventBus eventBus, ProjectServiceClient projectServiceClient,
                    DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        super(parent, data, eventBus);
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getDisplayName() {
        return data.getName();
    }

    /** {@inheritDoc} */
    @Override
    public void refreshChildren(AsyncCallback<TreeNode<?>> callback) {
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
    public boolean isRenamable() {
        return true;
    }

    /** Rename appropriate {@link ItemReference} using Codenvy Project API. */
    @Override
    public void rename(final String newName, final RenameCallback callback) {
        projectServiceClient.rename(getPath(), newName, null, new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(final Void result) {
                // parent node should be StorableNode instance
                final String parentPath = ((StorableNode)getParent()).getPath();
                Unmarshallable<Array<ItemReference>> unmarshaller = dtoUnmarshallerFactory.newArrayUnmarshaller(ItemReference.class);

                // update inner ItemReference object
                projectServiceClient.getChildren(parentPath, new AsyncRequestCallback<Array<ItemReference>>(unmarshaller) {
                    @Override
                    protected void onSuccess(Array<ItemReference> items) {
                        for (ItemReference item : items.asIterable()) {
                            if (newName.equals(item.getName())) {
                                data = item;
                                break;
                            }
                        }
                        updateChildrenData(ItemNode.this);

                        ItemNode.super.rename(newName, new RenameCallback() {
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

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }

    /** Updates inner ItemReference object for all hierarchy of child nodes. */
    private void updateChildrenData(final ItemNode itemNode) {
        Unmarshallable<Array<ItemReference>> unmarshaller = dtoUnmarshallerFactory.newArrayUnmarshaller(ItemReference.class);
        projectServiceClient.getChildren(itemNode.getPath(), new AsyncRequestCallback<Array<ItemReference>>(unmarshaller) {
            @Override
            protected void onSuccess(Array<ItemReference> result) {
                for (TreeNode<?> childNode : itemNode.getChildren().asIterable()) {
                    if (childNode instanceof ItemNode) {
                        final ItemNode childItemNode = (ItemNode)childNode;
                        for (ItemReference itemReference : result.asIterable()) {
                            if (childItemNode.getName().equals(itemReference.getName())) {
                                childItemNode.setData(itemReference);

                                if (childNode instanceof FolderNode) {
                                    updateChildrenData(childItemNode);
                                }
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            protected void onFailure(Throwable exception) {
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDeletable() {
        return true;
    }

    /** Delete appropriate {@link ItemReference} using Codenvy Project API. */
    @Override
    public void delete(final DeleteCallback callback) {
        projectServiceClient.delete(getPath(), new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
                ItemNode.super.delete(new DeleteCallback() {
                    @Override
                    public void onDeleted() {
                        callback.onDeleted();
                    }

                    @Override
                    public void onFailure(Throwable exception) {
                        callback.onFailure(exception);
                    }
                });
                eventBus.fireEvent(new ItemEvent(ItemNode.this, ItemEvent.ItemOperation.DELETE));
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }
}
