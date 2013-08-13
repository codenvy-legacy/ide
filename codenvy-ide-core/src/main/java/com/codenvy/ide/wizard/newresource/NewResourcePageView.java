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
package com.codenvy.ide.wizard.newresource;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.json.JsonArray;

/**
 * Interface of new resource view.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface NewResourcePageView extends View<NewResourcePageView.ActionDelegate> {
    /** Needs for delegate some function into NewResource view. */
    public interface ActionDelegate {
        /**
         * Returns wizard for selected resource type.
         *
         * @param newFileWizard
         */
        void selectedFileType(NewResourceWizardData newFileWizard);
    }

    /**
     * Sets available resource wizards.
     *
     * @param resourceWizards
     */
    void setResourceWizard(JsonArray<NewResourceWizardData> resourceWizards);
}