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
package com.codenvy.ide.api.paas;

import com.codenvy.ide.api.ui.wizard.AbstractWizardPagePresenter;
import com.codenvy.ide.api.wizard.newproject.CreateProjectHandler;
import com.codenvy.ide.api.wizard.newproject.HasCreateProjectHandler;
import com.google.gwt.resources.client.ImageResource;

/**
 * AbstractPaasWizardPagePresenter is an abstract base implementation of PaaS's wizard page.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public abstract class AbstractPaasWizardPagePresenter extends AbstractWizardPagePresenter implements HasCreateProjectHandler {
    private CreateProjectHandler createProjectHandler;

    /**
     * Create WizardPage with given wizard page caption
     *
     * @param caption
     */
    public AbstractPaasWizardPagePresenter(String caption) {
        super(caption);
    }

    /**
     * Create WizardPage with given wizard page caption and wizard page image.
     * Size of image must be less than 48px * 48px.
     *
     * @param caption
     * @param image
     */
    public AbstractPaasWizardPagePresenter(String caption, ImageResource image) {
        super(caption, image);
    }

    /** {@inheritDoc} */
    @Override
    public CreateProjectHandler getCreateProjectHandler() {
        return createProjectHandler;
    }

    /** {@inheritDoc} */
    @Override
    public void setCreateProjectHandler(CreateProjectHandler handler) {
        createProjectHandler = handler;
    }
}