/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.ext.java.jdi.client.debug;

import com.codenvy.ide.ext.java.jdi.shared.Variable;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.ui.tree.NodeDataAdapter;
import com.codenvy.ide.ui.tree.TreeNodeElement;

import java.util.HashMap;

/**
 * The adapter for debug variable node.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class VariableTreeNodeDataAdapter implements NodeDataAdapter<Variable> {
    private HashMap<Variable, TreeNodeElement<Variable>> treeNodeElements = new HashMap<Variable, TreeNodeElement<Variable>>();

    /** {@inheritDoc} */
    @Override
    public int compare(Variable a, Variable b) {
        JsonArray<String> pathA = a.getVariablePath().getPath();
        JsonArray<String> pathB = b.getVariablePath().getPath();

        for (int i = 0; i < pathA.size(); i++) {
            String elementA = pathA.get(i);
            String elementB = pathB.get(i);

            int compare = elementA.compareTo(elementB);
            if (compare != 0) {
                return compare;
            }
        }

        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasChildren(Variable data) {
        return !data.primitive();
    }

    /** {@inheritDoc} */
    @Override
    public JsonArray<Variable> getChildren(Variable data) {
        JsonArray<Variable> variables = data.getVariables();
        return variables != null ? variables : JsonCollections.<Variable>createArray();
    }

    /** {@inheritDoc} */
    @Override
    public String getNodeId(Variable data) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getNodeName(Variable data) {
        return data.getName() + ": " + data.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public Variable getParent(Variable data) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public TreeNodeElement<Variable> getRenderedTreeNode(Variable data) {
        return treeNodeElements.get(data);
    }

    /** {@inheritDoc} */
    @Override
    public void setNodeName(Variable data, String name) {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void setRenderedTreeNode(Variable data, TreeNodeElement<Variable> renderedNode) {
        treeNodeElements.put(data, renderedNode);
    }

    /** {@inheritDoc} */
    @Override
    public Variable getDragDropTarget(Variable data) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public JsonArray<String> getNodePath(Variable data) {
        return data.getVariablePath().getPath();
    }

    /** {@inheritDoc} */
    @Override
    public Variable getNodeByPath(Variable root, JsonArray<String> relativeNodePath) {
        Variable localRoot = root;
        for (int i = 0; i < relativeNodePath.size(); i++) {
            String path = relativeNodePath.get(i);
            if (localRoot != null) {
                JsonArray<Variable> variables = localRoot.getVariables();
                localRoot = null;
                for (int j = 0; j < variables.size(); j++) {
                    Variable variable = variables.get(i);
                    if (variable.getName().equals(path)) {
                        localRoot = variable;
                        break;
                    }
                }

                if (i == (relativeNodePath.size() - 1)) {
                    return localRoot;
                }
            }
        }
        return null;
    }
}