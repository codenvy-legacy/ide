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
package com.codenvy.ide.api.wizard;

import com.google.gwt.resources.client.ImageResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.codenvy.ide.api.wizard.Wizard.UpdateDelegate;

/**
 * This is an abstract base implementation of a {@link WizardPage}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public abstract class AbstractWizardPage implements WizardPage {
    private final String         caption;
    private final ImageResource  image;
    protected     UpdateDelegate delegate;
    protected     WizardContext  wizardContext;

    /**
     * Create wizard page with given caption and image.
     *
     * @param caption
     * @param image
     */
    public AbstractWizardPage(@Nullable String caption, @Nullable ImageResource image) {
        this.caption = caption;
        this.image = image;
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public String getCaption() {
        return caption;
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public ImageResource getImage() {
        return image;
    }

    /** {@inheritDoc} */
    @Override
    public void setUpdateDelegate(@Nonnull UpdateDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void setContext(@Nonnull WizardContext wizardContext) {
        this.wizardContext = wizardContext;
    }

    /** {@inheritDoc} */
    @Override
    public void storeOptions() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public boolean canSkip() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean inContext() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void commit(@Nonnull CommitCallback callback) {
        callback.onSuccess();
    }
}