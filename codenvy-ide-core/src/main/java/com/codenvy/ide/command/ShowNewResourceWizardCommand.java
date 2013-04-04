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
import com.codenvy.ide.api.expressions.ProjectOpenedExpression;
import com.codenvy.ide.api.ui.menu.ExtendedCommand;
import com.codenvy.ide.wizard.WizardAgentImpl;
import com.codenvy.ide.wizard.WizardPresenter;
import com.codenvy.ide.wizard.newresource.NewResourcePagePresenter;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * Command for "New/File" action.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class ShowNewResourceWizardCommand implements ExtendedCommand {
    private final Resources resources;

    private final WizardAgentImpl wizardAgent;

    private final ProjectOpenedExpression expression;

    /**
     * Create command.
     *
     * @param resources
     * @param wizardAgent
     * @param expression
     */
    @Inject
    public ShowNewResourceWizardCommand(Resources resources, WizardAgentImpl wizardAgent,
                                        ProjectOpenedExpression expression) {
        this.resources = resources;
        this.wizardAgent = wizardAgent;
        this.expression = expression;
    }

    /** {@inheritDoc} */
    public void execute() {
        NewResourcePagePresenter page = new NewResourcePagePresenter(resources, wizardAgent);
        WizardPresenter wizardDialog = new WizardPresenter(page, "Create resource");
        wizardDialog.showWizard();
    }

    /** {@inheritDoc} */
    public ImageResource getIcon() {
        return resources.file();
    }

    /** {@inheritDoc} */
    public Expression inContext() {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    public Expression canExecute() {
        return expression;
    }

    /** {@inheritDoc} */
    @Override
    public String getToolTip() {
        return "Create new resource";
    }
}