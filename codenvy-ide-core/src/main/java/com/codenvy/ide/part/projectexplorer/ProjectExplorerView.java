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
package com.codenvy.ide.part.projectexplorer;

import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.parts.base.BaseActionDelegate;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.api.ui.tree.AbstractTreeNode;

import javax.validation.constraints.NotNull;

/**
 * Interface of project tree view.
 *
 * @author Andrey Plotnikov
 * @author Artem Zatsarynnyy
 */
public interface ProjectExplorerView extends View<ProjectExplorerView.ActionDelegate> {
    /**
     * Sets items into tree.
     *
     * @param rootNodes
     *         The root resource item
     */
    void setItems(@NotNull Array<AbstractTreeNode<?>> rootNodes);

    /**
     * Updates the pointed item.
     *
     * @param resource
     */
    void updateItem(@NotNull AbstractTreeNode<?> oldResource, @NotNull AbstractTreeNode<?> newResource);

    /**
     * Sets title of part.
     *
     * @param title
     *         title of part
     */
    void setTitle(@NotNull String title);

    /**
     * Sets project's name and visibility icon.
     *
     * @param project
     */
    void setProjectHeader(@NotNull ProjectDescriptor project);

    /**
     * Hide the project's header panel.
     */
    void hideProjectHeader();

    /** Needs for delegate some function into ProjectTree view. */
    public interface ActionDelegate extends BaseActionDelegate {
        /**
         * Performs any actions in response to node selection.
         *
         * @param node
         *         selected node
         */
        void onResourceSelected(@NotNull AbstractTreeNode<?> node);

        /**
         * Performs any actions in response to node expanded (opened) action.
         *
         * @param node
         *         expanded node
         */
        void onResourceOpened(@NotNull AbstractTreeNode<?> node);

        /**
         * Performs any actions in response to some node action.
         *
         * @param node
         *         node
         */
        void onResourceAction(@NotNull AbstractTreeNode<?> node);

        /**
         * Performs any actions appropriate in response to the user having clicked right button on mouse.
         *
         * @param mouseX
         *         the mouse x-position within the browser window's client area.
         * @param mouseY
         *         the mouse y-position within the browser window's client area.
         */
        void onContextMenu(int mouseX, int mouseY);
    }
}