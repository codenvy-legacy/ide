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

import com.codenvy.ide.api.mvp.Presenter;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import javax.annotation.Nonnull;

/**
 * @author Roman Nikitenko
 */
public class ImporterBasePagePresenter implements Presenter, ImporterBasePageView.ActionDelegate {

    private ImporterBasePageView     view;
    private ImporterBasePageListener listener;

    @Inject
    public ImporterBasePagePresenter(ImporterBasePageView view) {
        this.view = view;
        this.view.setDelegate(this);
    }

    /**
     * Set a listener for detecting the moment when property for {@link ImporterBasePageView} was changed.
     *
     * @param listener
     *         listener that need to be added
     */
    public void setListener(@Nonnull ImporterBasePageListener listener) {
        this.listener = listener;
    }

    /** {@inheritDoc} */
    @Override
    public void projectNameChanged(@Nonnull String name) {
        listener.projectNameChanged(name);
    }

    /** {@inheritDoc} */
    @Override
    public void projectUrlChanged(@Nonnull String url) {
        listener.projectUrlChanged(url);
    }

    /** {@inheritDoc} */
    @Override
    public void projectDescriptionChanged(@Nonnull String projectDescriptionValue) {
        listener.projectDescriptionChanged(projectDescriptionValue);
    }

    /** {@inheritDoc} */
    @Override
    public void projectVisibilityChanged(boolean aPublic) {
        listener.projectVisibilityChanged(aPublic);
    }

    /**
     * Set the project's URL.
     *
     * @param url
     *         the project's URL to set
     */
    public void setProjectUrl(@Nonnull String url) {
        view.setProjectUrl(url);
    }

    /** Reset the page. */
    public void reset() {
        view.reset();
    }

    /** Show the name error. */
    public void showNameError() {
        view.showNameError();
    }

    /** Hide the name error. */
    public void hideNameError() {
        view.hideNameError();
    }

    /** Show URL error. */
    public void showUrlError(@Nonnull String message) {
        view.showUrlError(message);
    }

    /** Hide URL error. */
    public void hideUrlError() {
        view.hideUrlError();
    }

    /**
     * Display importer's description.
     *
     * @param text
     *         description
     */
    public void setImporterDescription(@Nonnull String text) {
        view.setImporterDescription(text);
    }

    /**
     * Get the project's name value.
     *
     * @return {@link String} project's name
     */
    @Nonnull
    public String getProjectName() {
        return view.getProjectName();
    }

    /**
     * Set the project's name value.
     *
     * @param projectName
     *         project's name to set
     */
    public void setProjectName(@Nonnull String projectName) {
        view.setProjectName(projectName);
    }

    /** Give focus to project's URL input. */
    public void focusInUrlInput() {
        view.focusInUrlInput();
    }

    /**
     * Set the enable state of the inputs.
     *
     * @param isEnabled
     *         <code>true</code> if enabled, <code>false</code> if disabled
     */
    public void setInputsEnableState(boolean isEnabled) {
        view.setInputsEnableState(isEnabled);
        if (isEnabled) {
            focusInUrlInput();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void go(@Nonnull AcceptsOneWidget container) {
        container.setWidget(view);
    }
}
