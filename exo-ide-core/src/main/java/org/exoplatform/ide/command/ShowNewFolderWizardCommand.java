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
import org.exoplatform.ide.api.resources.ResourceProvider;
import org.exoplatform.ide.core.expressions.Expression;
import org.exoplatform.ide.menu.ExtendedCommand;
import org.exoplatform.ide.wizard.WizardPresenter;
import org.exoplatform.ide.wizard.newfolder.NewFolderPagePresenter;

/**
 * Command for "New/Folder" action.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class ShowNewFolderWizardCommand implements ExtendedCommand
{
   private final Resources resources;

   private final ResourceProvider resourceProvider;

   private final ProjectOpenedExpression expression;

   /**
    * Create command.
    *
    * @param resources
    * @param resourceProvider
    * @param expression
    */
   @Inject
   public ShowNewFolderWizardCommand(Resources resources, ResourceProvider resourceProvider,
      ProjectOpenedExpression expression)
   {
      this.resources = resources;
      this.resourceProvider = resourceProvider;
      this.expression = expression;
   }

   /**
    * {@inheritDoc}
    */
   public void execute()
   {
      NewFolderPagePresenter page = new NewFolderPagePresenter(resources, resourceProvider);
      WizardPresenter wizardDialog = new WizardPresenter(page, "Create folder");
      wizardDialog.showWizard();
   }

   /**
    * {@inheritDoc}
    */
   public Image getIcon()
   {
      // TODO Auto-generated method stub
      return null;
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
}