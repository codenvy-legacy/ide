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
package org.exoplatform.ide.extension.tasks;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.exoplatform.ide.api.ui.menu.MainMenuAgent;
import org.exoplatform.ide.api.ui.workspace.WorkspaceAgent;
import org.exoplatform.ide.extension.Extension;
import org.exoplatform.ide.extension.tasks.part.TasksPartPresenter;

/**
 * Extension used to demonstrate the IDE 2.0 SDK fetures
 * 
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
@Singleton
@Extension(title = "Tasks extension", id = "ide.ext.tasks", version = "2.0.0")
public class TasksExtension
{

   @Inject
   public TasksExtension(MainMenuAgent menu, final WorkspaceAgent agent, final TasksPartPresenter tasksPartPresenter,
      final OpenTasksViewCommand openTasksViewCommand)
   {
      menu.addMenuItem("Tasks/Show Tasks View", openTasksViewCommand);
   }
}