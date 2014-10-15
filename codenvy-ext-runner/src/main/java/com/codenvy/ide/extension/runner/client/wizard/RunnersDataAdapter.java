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

package com.codenvy.ide.extension.runner.client.wizard;

import com.codenvy.api.project.shared.dto.RunnerEnvironment;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentLeaf;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.ui.tree.NodeDataAdapter;
import com.codenvy.ide.ui.tree.TreeNodeElement;

import java.util.Comparator;
import java.util.HashMap;

/**
 * @author Evgen Vidolob
 */
public class RunnersDataAdapter implements NodeDataAdapter<Object> {
    private static final Comparator<Object>                       COMPARATOR       = new Comparator<Object>() {
        @Override
        public int compare(Object o1, Object o2) {
            if (o1 instanceof RunnerEnvironmentTree && o2 instanceof RunnerEnvironmentLeaf) {
                return 1;
            }
            if (o2 instanceof RunnerEnvironmentTree && o1 instanceof RunnerEnvironmentLeaf) {
                return -1;
            }
            if (o1 instanceof RunnerEnvironmentTree && o2 instanceof RunnerEnvironmentTree) {
                return ((RunnerEnvironmentTree)o1).getDisplayName().compareTo(((RunnerEnvironmentTree)o2).getDisplayName());
            }
            if (o1 instanceof RunnerEnvironmentLeaf && o2 instanceof RunnerEnvironmentLeaf) {
                return ((RunnerEnvironmentLeaf)o1).getDisplayName().compareTo(((RunnerEnvironmentLeaf)o2).getDisplayName());
            }
            return 0;
        }
    };
    private              HashMap<Object, TreeNodeElement<Object>> treeNodeElements = new HashMap<>();

    @Override
    public int compare(Object a, Object b) {
        return COMPARATOR.compare(a, b);
    }

    @Override
    public boolean hasChildren(Object data) {
        if (data instanceof RunnerEnvironmentTree) {
            RunnerEnvironmentTree environmentTree = (RunnerEnvironmentTree)data;
            return !(environmentTree.getNodes().isEmpty() && environmentTree.getLeaves().isEmpty());
        }
        return false;


    }

    @Override
    public Array<Object> getChildren(Object data) {
        Array<Object> res = Collections.createArray();
        if (data instanceof RunnerEnvironmentTree) {
            RunnerEnvironmentTree environmentTree = (RunnerEnvironmentTree)data;
            for (RunnerEnvironmentTree runnerEnvironmentTree : environmentTree.getNodes()) {
                res.add(runnerEnvironmentTree);
            }

            for (RunnerEnvironmentLeaf leaf : environmentTree.getLeaves()) {
                RunnerEnvironment environment = leaf.getEnvironment();
                if (environment != null) {
                    res.add(leaf);
                }
            }

        }
        res.sort(COMPARATOR);
        return res;
    }

    @Override
    public String getNodeId(Object data) {
        return null;
    }

    @Override
    public String getNodeName(Object data) {
        return null;
    }

    @Override
    public Object getParent(Object data) {
        return null;
    }

    @Override
    public TreeNodeElement<Object> getRenderedTreeNode(Object data) {
        return treeNodeElements.get(data);
    }

    @Override
    public void setNodeName(Object data, String name) {

    }

    @Override
    public void setRenderedTreeNode(Object data, TreeNodeElement<Object> renderedNode) {
        treeNodeElements.put(data, renderedNode);
    }

    @Override
    public Object getDragDropTarget(Object data) {
        return null;
    }

    @Override
    public Array<String> getNodePath(Object data) {
        return null;
    }

    @Override
    public Object getNodeByPath(Object root, Array<String> relativeNodePath) {
        return null;
    }
}
