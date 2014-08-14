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
import com.codenvy.ide.api.event.FileEvent;
import com.codenvy.ide.api.projecttree.AbstractTreeNode;
import com.codenvy.ide.api.projecttree.AbstractTreeStructure;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

/**
 * {@link AbstractTreeStructure} for the hierarchical tree.
 *
 * @author Artem Zatsarynnyy
 */
class GenericTreeStructure extends AbstractTreeStructure {
    private EventBus               eventBus;
    private AppContext             appContext;
    private ProjectServiceClient   projectServiceClient;
    private DtoUnmarshallerFactory dtoUnmarshallerFactory;

    GenericTreeStructure(EventBus eventBus, AppContext appContext, ProjectServiceClient projectServiceClient,
                         DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.eventBus = eventBus;
        this.appContext = appContext;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void getRoots(AsyncCallback<Array<AbstractTreeNode<?>>> callback) {
        Array<AbstractTreeNode<?>> roots = Collections.<AbstractTreeNode<?>>createArray(
                new ProjectRootNode(null, appContext.getCurrentProject().getProjectDescription()));
        callback.onSuccess(roots);
    }

    /** {@inheritDoc} */
    @Override
    public void refreshChildren(AbstractTreeNode<?> node, AsyncCallback<AbstractTreeNode<?>> callback) {
        if (node instanceof ProjectRootNode) {
            final String path = ((ProjectRootNode)node).getData().getPath();
            refresh(node, path, callback);
        } else if (isFolder(node)) {
            final String path = ((ItemNode)node).getData().getPath();
            refresh(node, path, callback);
        } else {
            Log.warn(GenericTreeStructure.class, "Unsupported node to refresh children.");
            callback.onFailure(new Exception("Unsupported node type."));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void processNodeAction(AbstractTreeNode<?> node) {
        if (isFile(node)) {
            eventBus.fireEvent(new FileEvent(((ItemNode)node).getData(), FileEvent.FileOperation.OPEN));
        }
    }

    private boolean isFile(AbstractTreeNode<?> node) {
        return node instanceof ItemNode && "file".equals(((ItemNode)node).getData().getType());
    }

    private boolean isFolder(AbstractTreeNode<?> node) {
        return node instanceof ItemNode && "folder".equals(((ItemNode)node).getData().getType());
    }

    private void refresh(final AbstractTreeNode<?> parentNode, String path, final AsyncCallback<AbstractTreeNode<?>> callback) {
        final boolean isShowHiddenItems = getSettings().isShowHiddenItems();
        final Unmarshallable<Array<ItemReference>> unmarshaller = dtoUnmarshallerFactory.newArrayUnmarshaller(ItemReference.class);
        projectServiceClient.getChildren(path, new AsyncRequestCallback<Array<ItemReference>>(unmarshaller) {
            @Override
            protected void onSuccess(Array<ItemReference> result) {
                Array<AbstractTreeNode<?>> array = Collections.createArray();
                parentNode.setChildren(array);
                for (ItemReference itemReference : result.asIterable()) {
                    if (isShowHiddenItems || !itemReference.getName().startsWith(".")) {
                        array.add(new ItemNode(parentNode, itemReference));
                    }
                }
                callback.onSuccess(parentNode);
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }

    /** Node that represents root item of opened project. */
    private static class ProjectRootNode extends AbstractTreeNode<ProjectDescriptor> {
        ProjectRootNode(AbstractTreeNode parent, ProjectDescriptor data) {
            super(parent, data);
        }

        /** {@inheritDoc} */
        @Override
        public String getName() {
            return data.getName();
        }

        /** {@inheritDoc} */
        @Override
        public boolean isLeaf() {
            return false;
        }
    }

    /** Node that represents item (folder or file). */
    private static class ItemNode extends AbstractTreeNode<ItemReference> {
        ItemNode(AbstractTreeNode parent, ItemReference data) {
            super(parent, data);
        }

        /** {@inheritDoc} */
        @Override
        public String getName() {
            return data.getName();
        }

        /** {@inheritDoc} */
        @Override
        public boolean isLeaf() {
            return "file".equals(data.getType());
        }
    }
}
