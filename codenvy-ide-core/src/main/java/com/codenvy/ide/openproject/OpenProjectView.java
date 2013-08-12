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
package com.codenvy.ide.openproject;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.json.JsonArray;

/**
 * The view of {@link OpenProjectPresenter}.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface OpenProjectView extends View<OpenProjectView.ActionDelegate> {
    /** Needs for delegate some function into ChangePerspective view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Open button. */
        void onOpenClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();

        /** Returns selected project. */
        void selectedProject(String projectName);
    }

    /**
     * Sets whether Open button is enabled.
     *
     * @param isEnabled
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setOpenButtonEnabled(boolean isEnabled);

    /**
     * Sets exists projects.
     *
     * @param projects
     */
    void setProjects(JsonArray<String> projects);

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}