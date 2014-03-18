/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */

package com.codenvy.ide.tree;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.ui.tree.NodeDataAdapter;
import com.codenvy.ide.ui.tree.TreeNodeElement;


public class ResourceTreeNodeDataAdapter implements NodeDataAdapter<Resource> {
    @Override
    public int compare(Resource a, Resource b) {
        return a.getPath().compareTo(b.getPath());
    }

    @Override
    public boolean hasChildren(Resource data) {
//        return data.isFolder() && ((Folder)data).getChildren().size() > 0;
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
        return data.getId();
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
        Array<String> list = Collections.<String>createArray();
        Array<String> result = Collections.<String>createArray();
        list.add(data.getId());

        Resource localData = data;
        while (localData.getParent() != null) {
            localData.getParent().getId();
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
                if (localRoot != null) {
                    Resource foundResource = localRoot.findResourceById(relativeNodePath.get(i));
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
        }
        return null;
    }
}
