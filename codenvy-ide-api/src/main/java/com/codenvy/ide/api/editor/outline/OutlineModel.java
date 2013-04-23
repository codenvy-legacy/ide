/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package com.codenvy.ide.api.editor.outline;

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
