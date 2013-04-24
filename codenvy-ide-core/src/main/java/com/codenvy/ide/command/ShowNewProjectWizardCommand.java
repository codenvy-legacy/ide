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
package com.codenvy.ide.command;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.expressions.Expression;
import com.codenvy.ide.api.ui.menu.ExtendedCommand;
import com.codenvy.ide.paas.PaaSAgentImpl;
import com.codenvy.ide.wizard.WizardAgentImpl;
import com.codenvy.ide.wizard.WizardPresenter;
import com.codenvy.ide.wizard.newproject.NewProjectPagePresenter;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;


/**
 * Open New project wizard dialog.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class ShowNewProjectWizardCommand implements ExtendedCommand {
    private final WizardAgentImpl wizardAgent;

    private final Resources resources;

    private final PaaSAgentImpl paasAgent;

    private final Provider<NewProjectPagePresenter> firstPage;

    /**
     * Create command
     *
     * @param wizardAgent
     * @param resources
     */
    @Inject
    public ShowNewProjectWizardCommand(WizardAgentImpl wizardAgent, Resources resources, PaaSAgentImpl paasAgent,
                                       Provider<NewProjectPagePresenter> firstPage) {
        this.wizardAgent = wizardAgent;
        this.resources = resources;
        this.paasAgent = paasAgent;
        this.firstPage = firstPage;
    }

    /** {@inheritDoc} */
    public void execute() {
        WizardPresenter wizardDialog = new WizardPresenter(firstPage.get(), "Create project");
        wizardDialog.showWizard();
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getIcon() {
        // TODO need correct image
        return resources.project();
    }

    /** {@inheritDoc} */
    @Override
    public String getToolTip() {
        return "Create new project";
    }

    /** {@inheritDoc} */
    @Override
    public Expression inContext() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Expression canExecute() {
        return null;
    }
}