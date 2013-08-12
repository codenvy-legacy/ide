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
package com.codenvy.ide.outline;

import com.codenvy.ide.Resources;
import com.codenvy.ide.outline.OutlineImpl.OutlineView;
import com.codenvy.ide.texteditor.api.outline.CodeBlock;
import com.codenvy.ide.ui.tree.NodeRenderer;
import com.codenvy.ide.ui.tree.Tree;
import com.codenvy.ide.ui.tree.Tree.Listener;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class OutlineViewImpl implements OutlineView {

    private Tree<CodeBlock> tree;

    /**
     * @param codeBlockDataAdapter
     * @param resources
     * @param renderer
     */
    public OutlineViewImpl(Resources resources, CodeBlockDataAdapter codeBlockDataAdapter, NodeRenderer<CodeBlock> renderer) {
        tree = Tree.create(resources, codeBlockDataAdapter, renderer);
    }

    /** {@inheritDoc} */
    @Override
    public Widget asWidget() {
        return tree.asWidget();
    }

    /** {@inheritDoc} */
    @Override
    public void renderTree() {
        tree.renderTree();
    }

    /** {@inheritDoc} */
    @Override
    public void rootChanged(CodeBlock newRoot) {
        tree.replaceSubtree(tree.getModel().getRoot(), newRoot, false);
    }

    /** {@inheritDoc} */
    @Override
    public void setTreeEventHandler(Listener<CodeBlock> listener) {
        tree.setTreeEventHandler(listener);
    }

    /** {@inheritDoc} */
    @Override
    public void selectAndExpand(CodeBlock block) {
        tree.autoExpandAndSelectNode(block, false);
    }

}
