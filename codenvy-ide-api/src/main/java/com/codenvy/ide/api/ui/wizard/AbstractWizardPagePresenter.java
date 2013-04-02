/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.api.ui.wizard;

import com.google.gwt.resources.client.ImageResource;

/**
 * AbstractPagePresenter is an abstract base implementation of a WizardPagePresenter.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
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