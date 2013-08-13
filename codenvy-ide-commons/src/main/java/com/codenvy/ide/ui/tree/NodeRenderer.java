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

import elemental.html.Element;
import elemental.html.SpanElement;

/**
 * Flyweight renderer whose job it is to take a NodeData and construct the
 * appropriate DOM structure for the tree node contents.
 *
 * @param <D>
 *         The type of data we want to render.
 */
public interface NodeRenderer<D> {

    /**
     * Takes in a {@link SpanElement} constructed via a call to
     * {@link #renderNodeContents} and returns an element whose contract is that
     * it contains only text corresponding to the key for the node's underlying
     * data.
     * <p/>
     * This ofcourse depends on the structure that was generated via the call to
     * {@link #renderNodeContents}.
     */
    Element getNodeKeyTextContainer(SpanElement treeNodeLabel);

    /**
     * Constructs the label portion of a {@link TreeNodeElement}. Labels can have
     * arbitrary DOM structure, with one constraint. At least one element MUST
     * contain only text that corresponds to the String key for the underlying
     * node's data.
     */
    SpanElement renderNodeContents(D data);

    /**
     * Updates the node's contents to reflect the current state of the node.
     *
     * @param treeNode
     *         the tree node that contains the rendered node contents
     */
    void updateNodeContents(TreeNodeElement<D> treeNode);
}
