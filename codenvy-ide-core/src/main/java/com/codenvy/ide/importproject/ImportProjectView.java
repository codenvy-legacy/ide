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

package com.codenvy.ide.importproject;

import com.google.gwt.user.client.ui.IsWidget;

import javax.annotation.Nonnull;
import java.util.List;


/**
 * The view of {@link com.codenvy.ide.importproject.ImportProjectPresenter}.
 *
 * @author Roman Nikitenko
 */
public interface ImportProjectView extends IsWidget {

    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();

        /** Performs any actions appropriate in response to the user having pressed the Import button. */
        void onImportClicked();

        /** Performs any actions appropriate in response to the user having changed something. */
        void onValueChanged();
    }

    /** Show dialog. */
    void showDialog();

    /** Close dialog */
    void close();

    /** Sets the delegate to receive events from this view. */
    void setDelegate(ActionDelegate delegate);

    /** @return project name */
    @Nonnull
    String getProjectName();

    /**
     * Set project name into field on the view.
     *
     * @param projectName
     *         text what will be shown on view
     */
    void setProjectName(@Nonnull String projectName);

    /**
     * Set uri into field on the view.
     *
     * @param uri
     *         text what will be shown on view
     */
    void setUri(@Nonnull String uri);

    /** @return uri */
    @Nonnull
    String getUri();

    /**
     * Returns selected importer.
     *
     * @return importer.
     */
    @Nonnull
    String getImporter();

    /**
     * Sets available importers.
     *
     * @param importers
     *         available importers
     */
    void setImporters(@Nonnull List<String> importers);

    /**
     * Change the enable state of the import button.
     *
     * @param enabled
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnabledImportButton(boolean enabled);

}
