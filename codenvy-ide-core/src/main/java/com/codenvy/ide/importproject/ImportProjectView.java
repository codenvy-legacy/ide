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

        void onImporterSelected();
    }

    /** Show dialog. */
    void showDialog();

    /** Show warning. */
    void showWarning();

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
     * Sets importer description
     *
     * @param description  description
     */
    void setDescription(@Nonnull String description);

    /**
     * Change the enable state of the import button.
     *
     * @param enabled
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnabledImportButton(boolean enabled);

}
