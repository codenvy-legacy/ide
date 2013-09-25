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
