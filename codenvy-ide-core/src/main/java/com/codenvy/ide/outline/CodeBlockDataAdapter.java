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

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.texteditor.api.outline.CodeBlock;
import com.codenvy.ide.ui.tree.NodeDataAdapter;
import com.codenvy.ide.ui.tree.TreeNodeElement;

import java.util.HashMap;

/**
 * Default implementation of {@link NodeDataAdapter} for {@link CodeBlock}
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class CodeBlockDataAdapter implements NodeDataAdapter<CodeBlock> {

    private HashMap<CodeBlock, TreeNodeElement<CodeBlock>> renderNodes =
            new HashMap<CodeBlock, TreeNodeElement<CodeBlock>>();

    /** {@inheritDoc} */
    @Override
    public int compare(CodeBlock a, CodeBlock b) {
        // TODO Auto-generated method stub
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasChildren(CodeBlock data) {
        Array<CodeBlock> array = data.getChildren();
        return array != null && !array.isEmpty();
    }

    /** {@inheritDoc} */
    @Override
    public Array<CodeBlock> getChildren(CodeBlock data) {
        return data.getChildren();
    }

    /** {@inheritDoc} */
    @Override
    public String getNodeId(CodeBlock data) {
        return data.getId();
    }

    /** {@inheritDoc} */
    @Override
    public String getNodeName(CodeBlock data) {
        return data.getType();
    }

    /** {@inheritDoc} */
    @Override
    public CodeBlock getParent(CodeBlock data) {
        return data.getParent();
    }

    /** {@inheritDoc} */
    @Override
    public TreeNodeElement<CodeBlock> getRenderedTreeNode(CodeBlock data) {
        return renderNodes.get(data);
    }

    /** {@inheritDoc} */
    @Override
    public void setNodeName(CodeBlock data, String name) {
    }

    /** {@inheritDoc} */
    @Override
    public void setRenderedTreeNode(CodeBlock data, TreeNodeElement<CodeBlock> renderedNode) {
        renderNodes.put(data, renderedNode);
    }

    /** {@inheritDoc} */
    @Override
    public CodeBlock getDragDropTarget(CodeBlock data) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Array<String> getNodePath(CodeBlock data) {
        return PathUtils.getNodePath(this, data);
    }

    /** {@inheritDoc} */
    @Override
    public CodeBlock getNodeByPath(CodeBlock root, Array<String> relativeNodePath) {
        // TODO Auto-generated method stub
        return null;
    }

}
