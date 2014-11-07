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
package com.codenvy.ide.api.projectimporter.basepage;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.ImplementedBy;

import javax.annotation.Nonnull;

/**
 * @author Roman Nikitenko
 */
@ImplementedBy(ImporterBasePageViewImpl.class)
public interface ImporterBasePageView extends IsWidget {
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having changed the project's name. */
        void projectNameChanged(@Nonnull String name);

        /** Performs any actions appropriate in response to the user having changed the project's URL. */
        void projectUrlChanged(@Nonnull String url);

        /** Performs any actions appropriate in response to the user having changed the project's description. */
        void projectDescriptionChanged(@Nonnull String projectDescriptionValue);

        /** Performs any actions appropriate in response to the user having changed the project's visibility. */
        void projectVisibilityChanged(boolean aPublic);
    }

    /**
     * Set the project's URL.
     *
     * @param url
     *         the project's URL to set
     */
    void setProjectUrl(@Nonnull String url);

    /** Reset the page. */
    void reset();

    /** Show the name error. */
    void showNameError();

    /** Hide the name error. */
    void hideNameError();

    /** Show URL error. */
    void showUrlError(@Nonnull String message);

    /** Hide URL error. */
    void hideUrlError();

    /**
     * Display importer's description.
     *
     * @param text
     *         description
     */
    void setImporterDescription(@Nonnull String text);

    /**
     * Get the project's name value.
     *
     * @return {@link String} project's name
     */
    @Nonnull
    String getProjectName();

    /**
     * Set the project's name value.
     *
     * @param projectName
     *         project's name to set
     */
    void setProjectName(@Nonnull String projectName);

    /** Give focus to project's URL input. */
    void focusInUrlInput();

    /**
     * Set the enable state of the inputs.
     *
     * @param isEnabled
     *         <code>true</code> if enabled, <code>false</code> if disabled
     */
    void setInputsEnableState(boolean isEnabled);

    /** Sets the delegate to receive events from this view. */
    void setDelegate(@Nonnull ActionDelegate delegate);

}
