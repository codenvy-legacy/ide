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
import com.codenvy.ide.api.event.CloseCurrentProjectEvent;
import com.codenvy.ide.api.event.ProjectDescriptorChangedEvent;
import com.codenvy.ide.api.event.ProjectDescriptorChangedHandler;
import com.codenvy.ide.api.projecttree.AbstractTreeNode;
import com.codenvy.ide.api.projecttree.TreeNode;
import com.codenvy.ide.api.projecttree.TreeSettings;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Node that represents project item.
 *
 * @author Artem Zatsarynnyy
 */
public class ProjectNode extends AbstractTreeNode<ProjectDescriptor> implements StorableNode<ProjectDescriptor>, Openable,
                                                                                ProjectDescriptorChangedHandler {
    protected final GenericTreeStructure   treeStructure;
    protected final ProjectServiceClient   projectServiceClient;
    protected final DtoUnmarshallerFactory dtoUnmarshallerFactory;
    protected final EventBus               eventBus;
    protected       TreeSettings           settings;
    private         boolean                opened;

    public ProjectNode(TreeNode<?> parent, ProjectDescriptor data, GenericTreeStructure treeStructure, TreeSettings settings,
                       EventBus eventBus, ProjectServiceClient projectServiceClient, DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        super(parent, data, eventBus);
        eventBus.addHandler(ProjectDescriptorChangedEvent.TYPE, this);

        this.treeStructure = treeStructure;
        this.settings = settings;
        this.eventBus = eventBus;
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
    public ProjectNode getProject() {
        return this;
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
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void refreshChildren(final AsyncCallback<TreeNode<?>> callback) {
        getChildren(data.getPath(), new AsyncCallback<Array<ItemReference>>() {
            @Override
            public void onSuccess(Array<ItemReference> childItems) {
                final boolean isShowHiddenItems = settings.isShowHiddenItems();
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
                callback.onSuccess(ProjectNode.this);
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public boolean isRenamable() {
        // Rename is not available for opened project.
        // Special message will be shown for user in this case (see RenameItemAction).
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void rename(String newName, RenameCallback callback) {
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
                eventBus.fireEvent(new CloseCurrentProjectEvent());
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

    /**
     * Method helps to retrieve children by the specified path using Codenvy Project API.
     *
     * @param path
     *         path to retrieve children
     * @param callback
     *         callback to return retrieved children
     */
    protected void getChildren(String path, final AsyncCallback<Array<ItemReference>> callback) {
        final Unmarshallable<Array<ItemReference>> unmarshaller = dtoUnmarshallerFactory.newArrayUnmarshaller(ItemReference.class);
        projectServiceClient.getChildren(path, new AsyncRequestCallback<Array<ItemReference>>(unmarshaller) {
            @Override
            protected void onSuccess(Array<ItemReference> result) {
                callback.onSuccess(result);
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }

    /** Throw away child nodes which was removed (for which we haven't appropriate item in {@code items}). */
    private void purgeNodes(Array<ItemReference> items) {
        Iterable<TreeNode<?>> it = getChildren().asIterable();
        for (TreeNode<?> node : it) {
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
        Iterable<TreeNode<?>> it = getChildren().asIterable();
        for (TreeNode<?> node : it) {
            if (node.getData() instanceof ItemReference && items.contains((ItemReference)node.getData())) {
                newItems.remove((ItemReference)node.getData());
            }
        }
        return newItems;
    }

    /** Get unique ID of type of project. */
    public String getProjectTypeId() {
        return data.getType();
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
            return treeStructure.newFileNode(ProjectNode.this, item);
        } else if (isFolder(item)) {
            return treeStructure.newFolderNode(ProjectNode.this, item);
        }
        return null;
    }

    /**
     * Returns value of the specified attribute.
     *
     * @param attributeName
     *         name of the attribute to get its value
     * @return value of the specified attribute or <code>null</code> if attribute does not exists
     */
    @Nullable
    public String getAttributeValue(String attributeName) {
        List<String> attributeValues = getAttributeValues(attributeName);
        if (attributeValues != null && !attributeValues.isEmpty()) {
            return attributeValues.get(0);
        }
        return null;
    }

    /**
     * Returns values list of the specified attribute.
     *
     * @param attributeName
     *         name of the attribute to get its values
     * @return {@link List} of attribute values or <code>null</code> if attribute does not exists
     * @see #getAttributeValue(String)
     */
    @Nullable
    public List<String> getAttributeValues(String attributeName) {
        return data.getAttributes().get(attributeName);
    }

    @Override
    public void close() {
        opened = false;
    }

    @Override
    public boolean isOpened() {
        return opened;
    }

    @Override
    public void open() {
        opened = true;
    }

    @Override
    public void onProjectDescriptorChanged(ProjectDescriptorChangedEvent event) {
        String path = event.getProjectDescriptor().getPath();
        if (getPath().equals(path)) {
            setData(event.getProjectDescriptor());
        }
    }
}
