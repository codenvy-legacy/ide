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

import elemental.html.Element;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import org.exoplatform.ide.api.ui.workspace.WorkspaceAgent;
import org.exoplatform.ide.command.ProjectOpenedExpression;
import org.exoplatform.ide.core.editor.EditorAgent;
import org.exoplatform.ide.core.expressions.Expression;
import org.exoplatform.ide.extension.Extension;
import org.exoplatform.ide.extension.demo.perspective.ExtendedPerspectivePresenter;
import org.exoplatform.ide.menu.ExtendedCommand;
import org.exoplatform.ide.menu.MainMenuPresenter;

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
   public DemoExtension(final WorkspaceAgent workspace,
      Provider<ExtendedPerspectivePresenter> extendedPerspectivePresenter, MainMenuPresenter menu,
      EditorAgent editorAgent, final ProjectOpenedExpression projectOpenedExpression,
      CreateDemoCommand createDemoCommand)
   {
      workspace.registerPerspective(EXTENDED_PERSPECTIVE, null, extendedPerspectivePresenter);

      // CREATE DYNAMIC MENU CONTENT
      menu.addMenuItem("File/Create Demo Content", createDemoCommand);
      menu.addMenuItem("Project/Some Project Operation", new ExtendedCommand()
      {
         @Override
         public Expression inContext()
         {
            return projectOpenedExpression;
         }

         @Override
         public ImageResource getIcon()
         {
            return null;
         }

         @Override
         public void execute()
         {
            Window
               .alert("This is test item. The item changes enable/disable state when something happend(project was opened).");
         }

         @Override
         public Expression canExecute()
         {
            return null;
         }

         @Override
         public String getToolTip()
         {
            return null;
         }
      });
   }
}