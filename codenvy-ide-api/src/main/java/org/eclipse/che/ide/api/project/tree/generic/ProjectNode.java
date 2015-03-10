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
package org.eclipse.che.ide.api.project.tree.generic;

import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.project.shared.dto.ItemReference;
import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.ide.api.event.CloseCurrentProjectEvent;
import org.eclipse.che.ide.api.event.ProjectDescriptorChangedEvent;
import org.eclipse.che.ide.api.event.ProjectDescriptorChangedHandler;
import org.eclipse.che.ide.api.project.tree.AbstractTreeNode;
import org.eclipse.che.ide.api.project.tree.TreeNode;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.collections.Collections;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.rest.Unmarshallable;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Node that represents a project.
 *
 * @author Artem Zatsarynnyy
 */
public class ProjectNode extends AbstractTreeNode<ProjectDescriptor> implements StorableNode<ProjectDescriptor>, Openable,
                                                                                ProjectDescriptorChangedHandler {
    protected final ProjectServiceClient   projectServiceClient;
    protected final DtoUnmarshallerFactory dtoUnmarshallerFactory;
    protected final EventBus               eventBus;
    private final   GenericTreeStructure   treeStructure;
    private         boolean                opened;

    @Inject
    public ProjectNode(@Assisted TreeNode<?> parent,
                       @Assisted ProjectDescriptor data,
                       @Assisted GenericTreeStructure treeStructure,
                       EventBus eventBus,
                       ProjectServiceClient projectServiceClient,
                       DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        super(parent, data, treeStructure, eventBus);
        eventBus.addHandler(ProjectDescriptorChangedEvent.TYPE, this);

        this.treeStructure = treeStructure;
        this.eventBus = eventBus;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
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
        return true;
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
    public ProjectNode getProject() {
        return this;
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getDisplayName() {
        return getData().getName();
    }

    /** Returns {@link org.eclipse.che.ide.api.project.tree.TreeStructure} which this node belongs. */
    @Nonnull
    public GenericTreeStructure getTreeStructure() {
        return treeStructure;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isLeaf() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void refreshChildren(final AsyncCallback<TreeNode<?>> callback) {
        getChildren(getPath(), new AsyncCallback<Array<ItemReference>>() {
            @Override
            public void onSuccess(Array<ItemReference> childItems) {
                setChildren(getChildNodesForItems(childItems));
                callback.onSuccess(ProjectNode.this);
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }

    private Array<TreeNode<?>> getChildNodesForItems(Array<ItemReference> childItems) {
        Array<TreeNode<?>> oldChildren = Collections.createArray(getChildren().asIterable());
        Array<TreeNode<?>> newChildren = Collections.createArray();
        for (ItemReference item : childItems.asIterable()) {
            AbstractTreeNode node = createChildNode(item);
            if (node != null) {
                if (oldChildren.contains(node)) {
                    final int i = oldChildren.indexOf(node);
                    newChildren.add(oldChildren.get(i));
                } else {
                    newChildren.add(node);
                }
            }
        }
        return newChildren;
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
        projectServiceClient.delete(getPath(), new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
                if (isRootProject()) {
                    eventBus.fireEvent(new CloseCurrentProjectEvent());
                }
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
     * Method helps to retrieve child {@link ItemReference}s by the specified path using Codenvy Project API.
     * <p/>
     * It takes into account state of the 'show hidden items' setting.
     *
     * @param path
     *         path to retrieve children
     * @param callback
     *         callback to return retrieved children
     */
    protected void getChildren(String path, final AsyncCallback<Array<ItemReference>> callback) {
        final Array<ItemReference> children = Collections.createArray();
        final Unmarshallable<Array<ItemReference>> unmarshaller = dtoUnmarshallerFactory.newArrayUnmarshaller(ItemReference.class);
        projectServiceClient.getChildren(path, new AsyncRequestCallback<Array<ItemReference>>(unmarshaller) {
            @Override
            protected void onSuccess(Array<ItemReference> result) {
                final boolean isShowHiddenItems = getTreeStructure().getSettings().isShowHiddenItems();
                for (ItemReference item : result.asIterable()) {
                    if (!isShowHiddenItems && item.getName().startsWith(".")) {
                        continue;
                    }
                    children.add(item);
                }

                callback.onSuccess(children);
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }

    /** Get unique ID of type of project. */
    public String getProjectTypeId() {
        return getData().getType();
    }

    /**
     * Creates node for the specified item. Method called for every child item in {@link #refreshChildren(AsyncCallback)} method.
     * <p/>
     * May be overridden in order to provide a way to create a node for the specified by.
     *
     * @param item
     *         {@link ItemReference} for which need to create node
     * @return new node instance or {@code null} if the specified item is not supported
     */
    @Nullable
    protected AbstractTreeNode<?> createChildNode(ItemReference item) {
        if ("file".equals(item.getType())) {
            return treeStructure.newFileNode(ProjectNode.this, item);
        } else if ("folder".equals(item.getType()) || "project".equals(item.getType())) {
            return treeStructure.newFolderNode(ProjectNode.this, item);
        }
        return null;
    }

    /**
     * Returns value of the specified attribute.
     *
     * @param attributeName
     *         name of the attribute to get its value
     * @return value of the specified attribute or {@code null} if attribute does not exists
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
     * @return {@link List} of attribute values or {@code null} if attribute does not exists
     * @see #getAttributeValue(String)
     */
    @Nullable
    public List<String> getAttributeValues(String attributeName) {
        return getData().getAttributes().get(attributeName);
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

    private boolean isRootProject() {
        return getParent().getParent() == null;
    }
}
