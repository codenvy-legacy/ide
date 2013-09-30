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
package com.codenvy.ide.part.projectexplorer;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.parts.base.BaseActionDelegate;
import com.codenvy.ide.resources.model.Resource;

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
    void setItems(Resource resource);

    /**
     * Sets title of part.
     *
     * @param title
     *         title of part
     */
    void setTitle(String title);

    /** Needs for delegate some function into ProjectTree view. */
    public interface ActionDelegate extends BaseActionDelegate {
        /**
         * Performs any actions in response to node selection.
         *
         * @param resource
         *         node
         */
        void onResourceSelected(Resource resource);

        /**
         * Performs any actions in response to some node action.
         *
         * @param resource
         *         node
         */
        void onResourceAction(Resource resource);

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