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

package com.codenvy.ide.ui.tree;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;

/**
 * Simple adapter that allows the Tree to traverse (get the children of) some
 * NodeData.
 * <p/>
 * Restrictions on the NodeData object. It must be able to provide sensible
 * implementations of the abstract methods in this class. Namely:
 * <p/>
 * 1. Each node must be able to return a String key that is unique amongst its
 * peers in the tree.
 * <p/>
 * 2. Each node must contain a back reference to its parent node.
 *
 * @param <D>
 *         The type of the data we want to traverse.
 */
public interface NodeDataAdapter<D> {

    static class PathUtils {
        public static <D> JsonArray<String> getNodePath(NodeDataAdapter<D> adapter, D data) {
            JsonArray<String> pathArray = JsonCollections.createArray();
            for (D node = data; adapter.getParent(node) != null; node = adapter.getParent(node)) {
                pathArray.add(adapter.getNodeId(node));
            }
            pathArray.reverse();
            return pathArray;
        }
    }

    /**
     * Compares two nodes for the purposes of sorting. Returns > 0 if a is larger
     * than b. Returns < 0 if a is smaller than b. Returns 0 if they are the same.
     */
    int compare(D a, D b);

    /**
     * @return true if the node has any child. The {@link #getChildren} may return
     *         an empty list for a node that has children, if those should be
     *         populated asynchronously
     */
    boolean hasChildren(D data);

    /** @return collection of child nodes */
    JsonArray<D> getChildren(D data);

    /**
     * @return node ID that is unique within its peers in a given level in the
     *         tree
     */
    String getNodeId(D data);

    /** @return String name for the node */
    String getNodeName(D data);

    /** @return node data that is the supplied node's parent. */
    D getParent(D data);

    /**
     * @return the rendered {@link TreeNodeElement} that is associated with the
     *         specified data node. If there is no rendered node in the tree, then
     *         {@code null} is returned.
     */
    TreeNodeElement<D> getRenderedTreeNode(D data);

    /**
     * Mutates the supplied data by setting the name to be the supplied name
     * String.
     */
    void setNodeName(D data, String name);

    /** Installs a reference to a rendered {@link TreeNodeElement}. */
    void setRenderedTreeNode(D data, TreeNodeElement<D> renderedNode);

    /**
     * @return the node that should be the drag-and-drop target for the given
     *         node. The returned node must already be rendered.
     */
    //@Nonnull D getDragDropTarget(D data);
    D getDragDropTarget(D data);

    /**
     * Returns an array of Strings representing the node IDs walking from the root
     * of the tree to the specified node data.
     */
    JsonArray<String> getNodePath(D data);

    /**
     * Looks up a node underneath the specified root using the specified relative
     * path.
     */
    D getNodeByPath(D root, JsonArray<String> relativeNodePath);
}
