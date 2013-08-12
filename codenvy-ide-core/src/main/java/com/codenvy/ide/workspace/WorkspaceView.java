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


/**
 * Workspace view interface.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface WorkspaceView extends View<WorkspaceView.ActionDelegate> {
    /**
     * Returns central panel.
     *
     * @return
     */
    AcceptsOneWidget getPerspectivePanel();

    /**
     * Returns menu panel.
     *
     * @return
     */
    AcceptsOneWidget getMenuPanel();

    /**
     * Returns toolbar panel.
     *
     * @return
     */
    AcceptsOneWidget getToolbarPanel();

    /** Needs for delegate some function into Workspace view. */
    public interface ActionDelegate {
    }
}