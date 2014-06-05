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
package com.codenvy.ide.wizard.newproject.pages.start;

import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.collections.Array;

/**
 * Interface of new project page view.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface NewProjectPageView extends View<NewProjectPageView.ActionDelegate> {
    /** Needs for delegate some function into NewProjectPage view. */
    public interface ActionDelegate {
        /**
         * Performs any actions appropriate in response to the user having selected project type.
         *
         * @param id
         *         button's id
         */
        void onProjectTypeSelected(int id);

        /** Checks whether project's name is complete or not and updates navigation buttons. */
        void checkProjectName();

        /**
         * Performs any actions appropriate in response to the user having pressed the Technology icon.
         *
         * @param x
         *         the mouse x-position within the browser window's client area.
         * @param y
         *         the mouse y-position within the browser window's client area.
         */
        void onTechnologyIconClicked(int x, int y);
    }

    /**
     * Returns project's name.
     *
     * @return project's name
     */
    String getProjectName();

    /**
     * Set project types on place on view.
     *
     * @param projectTypes
     *         project types those need to be set
     */
    void setProjectTypes(Array<ProjectTypeDescriptor> projectTypes);

    /**
     * Select project type on view.
     *
     * @param id
     *         id of project type that need to be selected
     */
    void selectProjectType(int id);

    /** Focus project name field on view. */
    void focusProjectName();

    /**
     * Show popup with some message.
     *
     * @param message
     *         message what need to show
     * @param left
     *         x-position element
     * @param top
     *         y-position element
     */
    void showPopup(String message, int left, int top);
}