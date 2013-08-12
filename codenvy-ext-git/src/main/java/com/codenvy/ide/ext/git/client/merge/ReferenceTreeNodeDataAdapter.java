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