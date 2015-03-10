/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.projectimport.zip;

import org.eclipse.che.ide.api.mvp.View;
import com.google.inject.ImplementedBy;

import javax.annotation.Nonnull;

/**
 * @author Roman Nikitenko
 */
@ImplementedBy(ZipImporterPageViewImpl.class)
public interface ZipImporterPageView extends View<ZipImporterPageView.ActionDelegate> {
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having changed the project's name. */
        void projectNameChanged(@Nonnull String name);

        /** Performs any actions appropriate in response to the user having changed the project's URL. */
        void projectUrlChanged(@Nonnull String url);

        /** Performs any actions appropriate in response to the user having changed the project's description. */
        void projectDescriptionChanged(@Nonnull String projectDescriptionValue);

        /** Performs any actions appropriate in response to the user having changed the project's visibility. */
        void projectVisibilityChanged(boolean visible);

        /** Performs any actions appropriate in response to the user having selected a skip first level. */
        void skipFirstLevelChanged(boolean isSkipFirstLevel);
    }

    /** Show the name error. */
    void showNameError();

    /** Hide the name error. */
    void hideNameError();

    /** Show URL error. */
    void showUrlError(@Nonnull String message);

    /** Hide URL error. */
    void hideUrlError();

    /**
     * Set the project's URL.
     *
     * @param url
     *         the project's URL to set
     */
    void setProjectUrl(@Nonnull String url);

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

    void setProjectDescription(@Nonnull String projectDescription);

    /** Give focus to project's URL input. */
    void focusInUrlInput();

    /**
     * Set the enable state of the inputs.
     *
     * @param isEnabled
     *         <code>true</code> if enabled, <code>false</code> if disabled
     */
    void setInputsEnableState(boolean isEnabled);

    /** Performs when user select skip first level. */
    boolean isSkipFirstLevelSelected();

    void setSkipFirstLevel(boolean skip);

    void setVisibility(boolean visible);
}
