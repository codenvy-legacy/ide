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
package com.codenvy.ide.api.ui.wizard.paas;

import com.codenvy.ide.api.paas.PaaS;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPage;
import com.google.gwt.resources.client.ImageResource;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PAAS;

/**
 * The abstract implementation of page that the PaaS provides.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public abstract class AbstractPaasPage extends AbstractWizardPage {
    private String paasID;

    /**
     * Create PaaS page.
     *
     * @param caption
     * @param image
     * @param paasID
     */
    public AbstractPaasPage(@Nullable String caption, @Nullable ImageResource image, @NotNull String paasID) {
        super(caption, image);
        this.paasID = paasID;
    }

    /** {@inheritDoc} */
    @Override
    public String getNotice() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void focusComponent() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void removeOptions() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public boolean inContext() {
        PaaS paas = wizardContext.getData(PAAS);
        return paas != null && paas.getId().equals(paasID);
    }
}