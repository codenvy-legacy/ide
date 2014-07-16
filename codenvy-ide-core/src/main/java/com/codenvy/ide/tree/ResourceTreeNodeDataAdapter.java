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
package com.codenvy.ide.tree;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.resources.model.Folder;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.ui.tree.NodeDataAdapter;
import com.codenvy.ide.ui.tree.TreeNodeElement;

public class ResourceTreeNodeDataAdapter implements NodeDataAdapter<Resource> {
    @Override
    public int compare(Resource a, Resource b) {
        return a.getPath().compareTo(b.getPath());
    }

    @Override
    public boolean hasChildren(Resource data) {
        // hide 'expand arrow' for project when all projects are displayed
        if (data instanceof Project) {
            return data.getProject() != null;
        }
        return data.isFolder();
    }

    @Override
    public Array<Resource> getChildren(Resource data) {
        if (data instanceof Folder) {
            return ((Folder)data).getChildren();
        }
        return null;
    }

    @Override
    public String getNodeId(Resource data) {
        return data.getPath();
    }

    @Override
    public String getNodeName(Resource data) {
        return data.getName();
    }

    @Override
    public Resource getParent(Resource data) {
        return data.getParent();
    }

    @SuppressWarnings("unchecked")
    @Override
    public TreeNodeElement<Resource> getRenderedTreeNode(Resource data) {
        return (TreeNodeElement<Resource>)data.getTag();
    }

    @Override
    public void setNodeName(Resource data, String name) {
    }

    @Override
    public void setRenderedTreeNode(Resource data, TreeNodeElement<Resource> renderedNode) {
        data.setTag(renderedNode);
    }

    @Override
    public Resource getDragDropTarget(Resource data) {
        return null;
    }

    @Override
    public Array<String> getNodePath(Resource data) {
        Array<String> list = Collections.createArray();
        Array<String> result = Collections.createArray();
        list.add(data.getPath());

        Resource localData = data;
        while (localData.getParent() != null) {
            localData.getParent().getPath();
            localData = localData.getParent();
        }

        for (int i = list.size(); i > 0; i--) {
            result.add(list.get(i - 1));
        }
        return result;
    }

    @Override
    public Resource getNodeByPath(Resource root, Array<String> relativeNodePath) {
        if (root instanceof Folder) {
            Folder localRoot = (Folder)root;
            for (int i = 0; i < relativeNodePath.size(); i++) {
                Resource foundResource = localRoot.findResourceByPath(relativeNodePath.get(i));
                if (foundResource instanceof Folder) {
                    localRoot = (Folder)foundResource;
                }
                if (foundResource instanceof File) {
                    if (i == (relativeNodePath.size() - 1)) {
                        return foundResource;
                    }
                }
            }
        }
        return null;
    }
}
