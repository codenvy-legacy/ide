/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.git.client.merge;

import com.codenvy.ide.ext.git.shared.Reference;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.ui.tree.NodeDataAdapter;
import com.codenvy.ide.ui.tree.TreeNodeElement;

import java.util.HashMap;

/**
 * The adapter for reference node.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class ReferenceTreeNodeDataAdapter implements NodeDataAdapter<Reference> {
    private HashMap<Reference, TreeNodeElement<Reference>> treeNodeElements = new HashMap<Reference, TreeNodeElement<Reference>>();

    /** {@inheritDoc} */
    @Override
    public int compare(Reference a, Reference b) {
        return a.getDisplayName().compareTo(b.getDisplayName());
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasChildren(Reference data) {
        JsonArray<Reference> branches = data.getBranches();
        return branches != null && !branches.isEmpty();
    }

    /** {@inheritDoc} */
    @Override
    public JsonArray<Reference> getChildren(Reference data) {
        return data.getBranches();
    }

    /** {@inheritDoc} */
    @Override
    public String getNodeId(Reference data) {
        return data.getFullName();
    }

    /** {@inheritDoc} */
    @Override
    public String getNodeName(Reference data) {
        return data.getDisplayName();
    }

    /** {@inheritDoc} */
    @Override
    public Reference getParent(Reference data) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public TreeNodeElement<Reference> getRenderedTreeNode(Reference data) {
        return treeNodeElements.get(data);
    }

    /** {@inheritDoc} */
    @Override
    public void setNodeName(Reference data, String name) {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void setRenderedTreeNode(Reference data, TreeNodeElement<Reference> renderedNode) {
        treeNodeElements.put(data, renderedNode);
    }

    /** {@inheritDoc} */
    @Override
    public Reference getDragDropTarget(Reference data) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public JsonArray<String> getNodePath(Reference data) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Reference getNodeByPath(Reference root, JsonArray<String> relativeNodePath) {
        return null;
    }
}