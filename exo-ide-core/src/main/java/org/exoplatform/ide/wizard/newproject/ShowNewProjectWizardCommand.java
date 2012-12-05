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
package org.exoplatform.ide.wizard.newproject;

import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.exoplatform.ide.Resources;
import org.exoplatform.ide.wizard.WizardPresenter;

/**
 * Open New project wizard dialog.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class ShowNewProjectWizardCommand implements Command
{
   private final NewProjectWizardAgentImpl wizardAgent;

   private final Resources resources;

   @Inject
   public ShowNewProjectWizardCommand(NewProjectWizardAgentImpl wizardAgent, Resources resources)
   {
      this.wizardAgent = wizardAgent;
      this.resources = resources;
   }

   public void execute()
   {
      NewProjectPagePresenter firstPage = new NewProjectPagePresenter(wizardAgent, resources);
      WizardPresenter wizardDialog = new WizardPresenter(firstPage, resources, "Create project");
      wizardDialog.showWizard();
   }
}