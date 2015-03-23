/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.part.projectexplorer;

import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.api.parts.base.BaseActionDelegate;
import org.eclipse.che.ide.api.project.tree.TreeNode;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.ui.tree.SelectionModel;

import javax.annotation.Nonnull;

/**
 * Interface of project tree view.
 *
 * @author Andrey Plotnikov
 * @author Artem Zatsarynnyy
 */
public interface ProjectExplorerView extends View<ProjectExplorerView.ActionDelegate> {
    /**
     * Sets root nodes into tree.
     *
     * @param rootNodes
     *         root nodes to set
     */
    void setRootNodes(@Nonnull Array<TreeNode<?>> rootNodes);

    /**
     * Updates the specified node.
     *
     * @param oldNode
     *         the node to be replaced
     * @param newNode
     *         the new node
     */
    void updateNode(@Nonnull TreeNode<?> oldNode, @Nonnull TreeNode<?> newNode);

    /**
     * Select the specified node in the view.
     *
     * @param node
     *         node to select
     */
    void selectNode(@Nonnull TreeNode<?> node);

    /**
     * Expand the tree to the specified {@code node} and select it.
     *
     * @param node
     *         node to expand and select
     */
    void expandAndSelectNode(@Nonnull TreeNode<?> node);

    /**
     * Sets title of part.
     *
     * @param title
     *         title of part
     */
    void setTitle(@Nonnull String title);

    /**
     * Sets project's name and visibility icon.
     *
     * @param project
     */
    void setProjectHeader(@Nonnull ProjectDescriptor project);

    /** Hide the project's header panel. */
    void hideProjectHeader();

    /** Returns the currently selected node. */
    @Nonnull
    TreeNode<?> getSelectedNode();

    Array<TreeNode<?>> getOpenedTreeNodes();

    /** Needs for delegate some function into ProjectTree view. */
    public interface ActionDelegate extends BaseActionDelegate {
        /**
         * Performs any actions in response to node selection.
         *
         * @param node
         *         selected node
         */
        void onNodeSelected(TreeNode< ? > node, SelectionModel< ? > model);

        /**
         * Performs any actions in response to node expanded action.
         *
         * @param node
         *         expanded node
         */
        void onNodeExpanded(@Nonnull TreeNode<?> node);

        /**
         * Performs any actions in response to some node action.
         *
         * @param node
         *         node
         */
        void onNodeAction(@Nonnull TreeNode<?> node);

        /**
         * Performs any actions appropriate in response to the user having clicked right button on mouse.
         *
         * @param mouseX
         *         the mouse x-position within the browser window's client area.
         * @param mouseY
         *         the mouse y-position within the browser window's client area.
         */
        void onContextMenu(int mouseX, int mouseY);

        /** Performs any actions appropriate in response to the user having pressed the DELETE key. */
        void onDeleteKey();

        /** Performs any actions appropriate in response to the user having pressed the ENTER key. */
        void onEnterKey();
    }
}
