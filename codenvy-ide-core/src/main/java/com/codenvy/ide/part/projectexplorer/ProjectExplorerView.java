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

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.parts.base.BaseActionDelegate;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.resources.model.Resource;

import javax.validation.constraints.NotNull;

/**
 * Interface of project tree view.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface ProjectExplorerView extends View<ProjectExplorerView.ActionDelegate> {
    /**
     * Sets items into tree.
     *
     * @param resource
     *         The root resource item
     */
    void setItems(@NotNull Resource resource);
    
    /**
     * Updates the pointed item.
     * 
     * @param resource
     */
    void updateItem(@NotNull Resource oldResource, @NotNull Resource newResource);

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
    void setProjectHeader(@NotNull Project project);
    
    /**
     * Hide the project's header panel.
     */
    void hideProjectHeader();

    /** Needs for delegate some function into ProjectTree view. */
    public interface ActionDelegate extends BaseActionDelegate {
        /**
         * Performs any actions in response to node selection.
         *
         * @param resource
         *         node
         */
        void onResourceSelected(@NotNull Resource resource);
        
        /**
         * Performs any actions in response to node expanded (opened) action.
         * 
         * @param resource
         */
        void onResourceOpened(@NotNull Resource resource);

        /**
         * Performs any actions in response to some node action.
         *
         * @param resource
         *         node
         */
        void onResourceAction(@NotNull Resource resource);

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