/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.extension.demo;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import org.exoplatform.ide.api.resources.ResourceProvider;
import org.exoplatform.ide.api.ui.menu.MainMenuAgent;
import org.exoplatform.ide.api.ui.workspace.WorkspaceAgent;
import org.exoplatform.ide.command.EditorActiveExpression;
import org.exoplatform.ide.command.ProjectOpenedExpression;
import org.exoplatform.ide.core.editor.EditorAgent;
import org.exoplatform.ide.core.expressions.Expression;
import org.exoplatform.ide.core.expressions.ExpressionManager;
import org.exoplatform.ide.extension.Extension;
import org.exoplatform.ide.extension.demo.perspective.ExtendedPerspectivePresenter;
import org.exoplatform.ide.menu.ExtendedCommand;
import org.exoplatform.ide.menu.MainMenuPresenter;
import org.exoplatform.ide.outline.OutlinePartPrenter;
import org.exoplatform.ide.perspective.WorkspacePresenter;

/**
 * Extension used to demonstrate the IDE 2.0 SDK fetures
 * 
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
@Singleton
@Extension(title = "Demo extension", id = "ide.ext.demo", version = "2.0.0")
public class DemoExtension
{

   /**
    * 
    */
   private static final String EXTENDED_PERSPECTIVE = "Extended Perspective";

   @Inject
   public DemoExtension(MainMenuAgent menuAgent, final WorkspaceAgent workspace,
      Provider<ExtendedPerspectivePresenter> extendedPerspectivePresenter, MainMenuPresenter menu,
      EditorAgent editorAgent, final ResourceProvider resourceProvider, final ExpressionManager expressionManager,
      OutlinePartPrenter outlinePresenter, EditorActiveExpression editorActiveExpression,
      final ProjectOpenedExpression projectOpenedExpression, 
      CreateDemoCommand createDemoCommand)
   {
      workspace.registerPerspective(EXTENDED_PERSPECTIVE, null, extendedPerspectivePresenter);

      menuAgent.addMenuItem("Window/Open Extended Perspective Demo", new Command()
      {
         @Override
         public void execute()
         {
            workspace.openPerspective(EXTENDED_PERSPECTIVE);
         }

      });

      // CREATE DYNAMIC MENU CONTENT
      menu.addMenuItem("File/Create Demo Content", createDemoCommand);

      menu.addMenuItem("Edit", null, null, editorActiveExpression, null);
      menu.addMenuItem("Edit/Some Editor Operation", null, null, editorActiveExpression, null);

      menu.addMenuItem("Project", null, null, projectOpenedExpression, null);

      menu.addMenuItem("Window/Open generic perspective(demo)", new ExtendedCommand()
      {

         @Override
         public Expression inContext()
         {
            return null;
         }

         @Override
         public Image getIcon()
         {
            return null;
         }

         @Override
         public void execute()
         {
            workspace.openPerspective(WorkspacePresenter.GENERAL_PERSPECTIVE);
         }

         @Override
         public Expression canExecute()
         {
            return null;
         }
      });

      menu.addMenuItem("Project/Some Project Operation", new ExtendedCommand()
      {

         @Override
         public Expression inContext()
         {
            return projectOpenedExpression;
         }

         @Override
         public Image getIcon()
         {
            return null;
         }

         @Override
         public void execute()
         {

         }

         @Override
         public Expression canExecute()
         {
            return null;
         }
      });

   }
}
