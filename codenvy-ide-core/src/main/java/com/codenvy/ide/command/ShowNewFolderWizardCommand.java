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

import com.codenvy.ide.core.expressions.ProjectOpenedExpression;

import com.codenvy.ide.api.ui.menu.ExtendedCommand;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.core.expressions.Expression;
import com.codenvy.ide.wizard.WizardPresenter;
import com.codenvy.ide.wizard.newfolder.NewFolderPagePresenter;

import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;
import com.google.inject.Singleton;


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

   private final SelectionAgent selectionAgent;

   /**
    * Create command.
    *
    * @param resources
    * @param resourceProvider
    * @param expression
    */
   @Inject
   public ShowNewFolderWizardCommand(Resources resources, ResourceProvider resourceProvider,
      ProjectOpenedExpression expression, SelectionAgent selectionAgent)
   {
      this.resources = resources;
      this.resourceProvider = resourceProvider;
      this.expression = expression;
      this.selectionAgent = selectionAgent;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void execute()
   {
      NewFolderPagePresenter page = new NewFolderPagePresenter(resources, resourceProvider, selectionAgent);
      WizardPresenter wizardDialog = new WizardPresenter(page, "Create folder");
      wizardDialog.showWizard();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ImageResource getIcon()
   {
      return resources.folder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Expression inContext()
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
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
      return "Create new folder";
   }
}