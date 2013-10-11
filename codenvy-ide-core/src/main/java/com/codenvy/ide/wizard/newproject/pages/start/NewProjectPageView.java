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
package com.codenvy.ide.wizard.newproject.pages.start;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.paas.PaaS;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.wizard.newproject.ProjectTypeData;

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

        /**
         * Performs any actions appropriate in response to the user having selected paas.
         *
         * @param id
         *         button's id
         */
        void onPaaSSelected(int id);

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

        /**
         * Performs any actions appropriate in response to the user having pressed the PaaS icon.
         *
         * @param x
         *         the mouse x-position within the browser window's client area.
         * @param y
         *         the mouse y-position within the browser window's client area.
         */
        void onPaaSIconClicked(int x, int y);
    }

    /**
     * Returns project's name.
     *
     * @return project's name
     */
    String getProjectName();

    void setProjectTypes(JsonArray<ProjectTypeData> projectTypes);

    void setPaases(JsonArray<PaaS> paases);

    void selectProjectType(int id);

    void selectPaas(int id);

    void focusProjectName();

    void setEnablePaas(int id, boolean isEnabled);

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