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
package com.codenvy.ide.workspace;

import com.codenvy.ide.api.mvp.View;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.FlowPanel;


/**
 * The view of {@link WorkspacePresenter}.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface WorkspaceView extends View<WorkspaceView.ActionDelegate> {
    /** Required for delegating functions in the view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Login button */
        void onLoginClicked();

        /** Performs any actions in response to click on Update button */
        void onUpdateClicked();
    }

    /** @return central panel */
    AcceptsOneWidget getPerspectivePanel();

    /** @return menu panel */
    AcceptsOneWidget getMenuPanel();

    /** @return toolbar panel */
    AcceptsOneWidget getToolbarPanel();

    /** @return status panel */
    AcceptsOneWidget getStatusPanel();

    /**
     * Sets whether Login button is visible.
     *
     * @param visible
     *         <code>true</code> to visible the button, <code>false</code> to disable it
     */
    void setVisibleLoginButton(boolean visible);

    /**
     * Sets whether Logout button is visible.
     *
     * @param visible
     *         <code>true</code> to visible the button, <code>false</code> to disable it
     */
    void setVisibleLogoutButton(boolean visible);

    /**
     * Sets whether 'Update extension' button is visible.
     *
     * @param visible
     *         <code>true</code> to show the button, <code>false</code> to hide it
     */
    void setUpdateButtonVisibility(boolean visible);
}