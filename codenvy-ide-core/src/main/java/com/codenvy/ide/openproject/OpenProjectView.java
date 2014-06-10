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
package com.codenvy.ide.openproject;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.collections.Array;

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
    void setProjects(Array<String> projects);

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}