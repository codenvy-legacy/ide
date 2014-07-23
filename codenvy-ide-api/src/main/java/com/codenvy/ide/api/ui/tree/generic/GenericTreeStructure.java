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
package com.codenvy.ide.api.ui.tree.generic;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.AppContext;
import com.codenvy.ide.api.resources.FileEvent;
import com.codenvy.ide.api.ui.tree.AbstractTreeNode;
import com.codenvy.ide.api.ui.tree.TreeStructure;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Simple structure for the hierarchical tree.
 *
 * @author Artem Zatsarynnyy
 */
public class GenericTreeStructure implements TreeStructure {
    private Array<AbstractTreeNode<?>> roots;
    private EventBus                   eventBus;
    private ProjectServiceClient       projectServiceClient;
    private DtoUnmarshallerFactory     dtoUnmarshallerFactory;
    private AppContext                 appContext;

    public GenericTreeStructure(EventBus eventBus,
                                ProjectServiceClient projectServiceClient,
                                DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                AppContext appContext) {
        this.eventBus = eventBus;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.appContext = appContext;
        roots = Collections.createArray();
    }

    // TODO: find node by absolute path
    AbstractTreeNode<?> getNodeByPath(String path) {
        String[] splittedPath = path.split("/");
        for (AbstractTreeNode<?> rootNode : roots.asIterable()) {
            if (rootNode.getName().equals("???")) {
                // get children recursively
                // find node
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void getRoots(AsyncCallback<Array<AbstractTreeNode<?>>> callback) {
        if (roots.isEmpty()) {
            AbstractTreeNode<?> extLibrariesNode = new AbstractTreeNode<String>(null, null) {
                @Override
                public String getName() {
                    return "External Libraries";
                }

                @Override
                public boolean isAlwaysLeaf() {
                    return false;
                }
            };
            roots = Collections.createArray(new ProjectRootTreeNode(null, appContext.getCurrentProject()), extLibrariesNode);
        }
        callback.onSuccess(roots);
    }

    /** {@inheritDoc} */
    @Override
    public void refreshChildren(final AbstractTreeNode<?> node, final AsyncCallback<AbstractTreeNode<?>> callback) {
        if (node instanceof ProjectRootTreeNode) {
            final String path = ((ProjectRootTreeNode)node).getData().getPath();
            refresh(node, path, callback);
        } else if (node instanceof ItemTreeNode && "folder".equals(((ItemTreeNode)node).getData().getType())) {
            final String path = ((ItemTreeNode)node).getData().getPath();
            refresh(node, path, callback);
        } else {
            Log.warn(GenericTreeStructure.class, "Unsupported node to refresh children.");
            callback.onFailure(new Exception("Unsupported node type."));
        }
    }

    private void refresh(final AbstractTreeNode<?> parentNode, String path, final AsyncCallback<AbstractTreeNode<?>> callback) {
        final Unmarshallable<Array<ItemReference>> unmarshaller = dtoUnmarshallerFactory.newArrayUnmarshaller(ItemReference.class);
        projectServiceClient.getChildren(path, new AsyncRequestCallback<Array<ItemReference>>(unmarshaller) {
            @Override
            protected void onSuccess(Array<ItemReference> result) {
                Array<AbstractTreeNode<?>> array = Collections.createArray();
                parentNode.setChildren(array);
                for (ItemReference itemReference : result.asIterable()) {
                    array.add(new ItemTreeNode(parentNode, itemReference));
                }
                callback.onSuccess(parentNode);
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void processNodeAction(AbstractTreeNode<?> node) {
        if (node instanceof ItemTreeNode && "file".equals(((ItemTreeNode)node).getData().getType())) {
            // open file
            eventBus.fireEvent(new FileEvent(((ItemTreeNode)node).getData(), FileEvent.FileOperation.OPEN));
        }
    }
}
