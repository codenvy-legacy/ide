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
package com.codenvy.ide.texteditor.api.outline;

import com.codenvy.ide.json.JsonArray;
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
    public void setRootChildren(JsonArray<CodeBlock> nodes) {
        JsonArray<CodeBlock> rootChildren = root.getChildren();
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
