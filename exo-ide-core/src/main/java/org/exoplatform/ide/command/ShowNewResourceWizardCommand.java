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
package org.exoplatform.ide.command;

import com.google.gwt.user.client.ui.Image;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.exoplatform.ide.Resources;
import org.exoplatform.ide.core.expressions.Expression;
import org.exoplatform.ide.menu.ExtendedCommand;
import org.exoplatform.ide.wizard.WizardAgentImpl;
import org.exoplatform.ide.wizard.WizardPresenter;
import org.exoplatform.ide.wizard.newresource.NewResourcePagePresenter;

/**
 * Command for "New/File" action.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class ShowNewResourceWizardCommand implements ExtendedCommand
{
   private final Resources resources;

   private final WizardAgentImpl wizardAgent;

   private final ProjectOpenedExpression expression;

   private final Image icon;

   /**
    * Create command.
    *
    * @param resources
    * @param wizardAgent
    * @param expression
    */
   @Inject
   public ShowNewResourceWizardCommand(Resources resources, WizardAgentImpl wizardAgent,
      ProjectOpenedExpression expression)
   {
      this.resources = resources;
      this.wizardAgent = wizardAgent;
      this.expression = expression;
      this.icon = new Image(resources.file());
   }

   /**
    * {@inheritDoc}
    */
   public void execute()
   {
      NewResourcePagePresenter page = new NewResourcePagePresenter(resources, wizardAgent);
      WizardPresenter wizardDialog = new WizardPresenter(page, "Create resource");
      wizardDialog.showWizard();
   }

   /**
    * {@inheritDoc}
    */
   public Image getIcon()
   {
      return icon;
   }

   /**
    * {@inheritDoc}
    */
   public Expression inContext()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Expression canExecute()
   {
      return expression;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getToolTip()
   {
      return "Create new resource";
   }
}