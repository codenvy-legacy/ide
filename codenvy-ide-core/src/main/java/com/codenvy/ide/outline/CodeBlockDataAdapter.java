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
package com.codenvy.ide.outline;

import com.codenvy.ide.json.JsonArray;
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
        JsonArray<CodeBlock> jsonArray = data.getChildren();
        return jsonArray != null && !jsonArray.isEmpty();
    }

    /** {@inheritDoc} */
    @Override
    public JsonArray<CodeBlock> getChildren(CodeBlock data) {
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
    public JsonArray<String> getNodePath(CodeBlock data) {
        return PathUtils.getNodePath(this, data);
    }

    /** {@inheritDoc} */
    @Override
    public CodeBlock getNodeByPath(CodeBlock root, JsonArray<String> relativeNodePath) {
        // TODO Auto-generated method stub
        return null;
    }

}
