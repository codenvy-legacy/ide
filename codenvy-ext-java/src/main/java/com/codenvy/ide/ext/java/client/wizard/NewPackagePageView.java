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
package com.codenvy.ide.ext.java.client.wizard;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.json.JsonArray;


/**
 * View for new Java package wizard.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface NewPackagePageView extends View<NewPackagePageView.ActionDelegate> {
    /** Action delegate for new Java package wizard. */
    public interface ActionDelegate {
        /**
         * Package parent changed.
         *
         * @param index
         *         the parent index.
         */
        void parentChanged(int index);

        /** New package name changed, validate it. */
        void checkPackageName();
    }

    /**
     * Set all packages or source folders in project
     *
     * @param parents
     *         the packages or source folder names
     */
    void setParents(JsonArray<String> parents);

    /**
     * Select parent by index
     *
     * @param index
     *         of the parent in the list
     */
    void selectParent(int indexOf);

    /**
     * Get new package name.
     *
     * @return the new package name
     */
    String getPackageName();

    /** Disable all ui components. */
    void disableAllUi();

}
