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
package com.codenvy.ide.workspace;

import com.codenvy.ide.api.mvp.View;
import com.google.gwt.user.client.ui.AcceptsOneWidget;


/**
 * The view of {@link WorkspacePresenter}.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface WorkspaceView extends View<WorkspaceView.ActionDelegate> {
    /** Required for delegating functions in the view. */
    public interface ActionDelegate {
        /** Performs any actions in response to click on Update button */
        void onUpdateClicked();
    }

    /** @return central panel */
    AcceptsOneWidget getPerspectivePanel();

    /** @return menu panel */
    AcceptsOneWidget getMenuPanel();

    /** @return toolbar panel */
    AcceptsOneWidget getToolbarPanel();

    /**
     * Shows or hides toolbar.
     *
     * @param visible
     *         set <b>true</b> to show the toolbar or <b>false</b> to hide it
     */
    void setToolbarVisible(boolean visible);

    /** @return actions panel */

    /**
     * Returns actions panel ( panel like toolbar but located at the bottom )
     *
     * @return actions panel
     */
    AcceptsOneWidget getActionsPanel();

    /**
     * Shows or hides actions panel.
     *
     * @param visible
     */
    void setActionsPanelVisible(boolean visible);

    /**
     * Returns status panel ( an information panel located under actions panel )
     *
     * @return status panel
     */
    AcceptsOneWidget getStatusPanel();

    /**
     * Shows or hides status panel
     *
     * @param visible
     */
    void setStatusPanelVisible(boolean visible);

    /**
     * Sets whether 'Update extension' button is visible.
     *
     * @param visible
     *         <code>true</code> to show the button, <code>false</code> to hide it
     */
    void setUpdateButtonVisibility(boolean visible);
}