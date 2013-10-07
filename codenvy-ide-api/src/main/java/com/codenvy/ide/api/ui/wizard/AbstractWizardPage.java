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
package com.codenvy.ide.api.ui.wizard;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.google.gwt.resources.client.ImageResource;

import static com.codenvy.ide.api.ui.wizard.WizardModel.UpdateDelegate;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
public abstract class AbstractWizardPage implements WizardPage {
    private final String         caption;
    private final ImageResource  image;
    protected     UpdateDelegate delegate;
    protected     WizardContext  wizardContext;

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
    public void setUpdateDelegate(@NotNull UpdateDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void setContext(@NotNull WizardContext wizardContext) {
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
    public void commit(@NotNull CommitCallback callback) {
        callback.onSuccess();
    }
}