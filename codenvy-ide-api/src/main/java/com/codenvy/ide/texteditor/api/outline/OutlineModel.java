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
package com.codenvy.ide.texteditor.api.outline;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.runtime.Assert;
import com.codenvy.ide.ui.tree.NodeRenderer;

/**
 * Model object that holds essential navigation structure data data and sends
 * notifications when data is changed.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class OutlineModel {
    /** OutlineModel notifications listener interface. */
    public interface OutlineModelListener {
        public void rootChanged(CodeBlock newRoot);

        void rootUpdated();
    }

    private OutlineModelListener listener;

    private CodeBlock root;

    private NodeRenderer<CodeBlock> renderer;

    /**
     * Instantiate Outline Model
     *
     * @param renderer
     */
    public OutlineModel(NodeRenderer<CodeBlock> renderer) {
        this.renderer = renderer;
    }

    /** @return Root Block */
    public CodeBlock getRoot() {
        return root;
    }

    /**
     * Set Listener
     *
     * @param listener
     */
    public void setListener(OutlineModelListener listener) {
        this.listener = listener;
    }

    /**
     * Set children's for root code block.
     *
     * @param nodes
     *         new children's
     */
    public void setRootChildren(Array<CodeBlock> nodes) {
        Array<CodeBlock> rootChildren = root.getChildren();
        rootChildren.clear();
        rootChildren.addAll(nodes);
        if (listener != null) {
            listener.rootUpdated();
        }
    }

    /**
     * Set new root code block
     *
     * @param root
     *         code block
     */
    public void updateRoot(CodeBlock root) {
        Assert.isNotNull(root);

        this.root = root;
        if (listener != null) {
            listener.rootChanged(root);
        }
    }

    /**
     * Provides CodeBlock Node Renderer
     *
     * @return Renderer for code blocks
     */
    public NodeRenderer<CodeBlock> getRenderer() {
        return renderer;
    }

}
