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
package com.codenvy.ide.extension.tasks;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.Constraints;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.action.IdeActions;
import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * Extension used to demonstrate the IDE 3.0 SDK fetures
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@Singleton
@Extension(title = "Tasks extension", version = "3.0.0")
public class TasksExtension {

    @Inject
    public TasksExtension(ActionManager actionManager,
                          final OpenTasksAction openTasksAction) {

        DefaultActionGroup main = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_MAIN_MENU);
        DefaultActionGroup tasks = new DefaultActionGroup("Tasks", true, actionManager);
        actionManager.registerAction("tasksGroup", tasks);
        actionManager.registerAction("openTasks", openTasksAction);

        main.add(tasks, Constraints.LAST);
        tasks.add(openTasksAction);
    }
}