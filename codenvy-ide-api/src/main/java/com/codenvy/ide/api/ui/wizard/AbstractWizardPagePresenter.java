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

import com.google.gwt.resources.client.ImageResource;

/**
 * AbstractPagePresenter is an abstract base implementation of a WizardPagePresenter.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Deprecated
public abstract class AbstractWizardPagePresenter implements WizardPagePresenter {
    protected WizardUpdateDelegate delegate;

    private WizardPagePresenter previous;

    private final String caption;

    private final ImageResource image;

    /**
     * Create WizardPage with given wizard page caption
     *
     * @param caption
     */
    public AbstractWizardPagePresenter(String caption) {
        this(caption, null);
    }

    /**
     * Create WizardPage with given wizard page caption and wizard page image.
     * Size of image must be less than 48px * 48px.
     *
     * @param caption
     * @param image
     */
    public AbstractWizardPagePresenter(String caption, ImageResource image) {
        this.caption = caption;
        this.image = image;
    }

    /** {@inheritDoc} */
    public String getCaption() {
        return caption;
    }

    /** {@inheritDoc} */
    public ImageResource getImage() {
        return image;
    }

    /** {@inheritDoc} */
    public void doFinish() {

    }

    /** {@inheritDoc} */
    public void doCancel() {

    }

    /** {@inheritDoc} */
    public void setUpdateDelegate(WizardUpdateDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    public WizardPagePresenter flipToPrevious() {
        return previous;
    }

    /** {@inheritDoc} */
    public void setPrevious(WizardPagePresenter previous) {
        this.previous = previous;
    }

    /** {@inheritDoc} */
    public boolean hasPrevious() {
        return previous != null;
    }
}