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
