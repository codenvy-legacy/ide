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
package com.codenvy.ide.ext.java.jdi.client.debug;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.ext.java.jdi.shared.Variable;
import com.codenvy.ide.ui.tree.NodeDataAdapter;
import com.codenvy.ide.ui.tree.TreeNodeElement;

import java.util.HashMap;
import java.util.List;

/**
 * The adapter for debug variable node.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class VariableNodeDataAdapter implements NodeDataAdapter<Variable> {
    private HashMap<Variable, TreeNodeElement<Variable>> treeNodeElements = new HashMap<>();

    /** {@inheritDoc} */
    @Override
    public int compare(Variable a, Variable b) {
        List<String> pathA = a.getVariablePath().getPath();
        List<String> pathB = b.getVariablePath().getPath();

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
        return !data.isPrimitive();
    }

    /** {@inheritDoc} */
    @Override
    public Array<Variable> getChildren(Variable data) {
        List<Variable> variables = data.getVariables();
        return variables != null ? Collections.createArray(variables) : Collections.<Variable>createArray();
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
    public Array<String> getNodePath(Variable data) {
        return Collections.createArray(data.getVariablePath().getPath());
    }

    /** {@inheritDoc} */
    @Override
    public Variable getNodeByPath(Variable root, Array<String> relativeNodePath) {
        Variable localRoot = root;
        for (int i = 0; i < relativeNodePath.size(); i++) {
            String path = relativeNodePath.get(i);
            if (localRoot != null) {
                Array<Variable> variables = Collections.createArray(localRoot.getVariables());
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