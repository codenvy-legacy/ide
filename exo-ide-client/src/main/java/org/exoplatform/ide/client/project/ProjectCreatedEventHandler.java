/*
 * Copyright (C) 2011 eXo Platform SAS.
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

package org.exoplatform.ide.client.project;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.CloseProjectEvent;
import org.exoplatform.ide.client.framework.project.OpenProjectEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectCreatedEvent;
import org.exoplatform.ide.client.framework.project.ProjectCreatedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ProjectCreatedEventHandler implements ProjectCreatedHandler, ProjectOpenedHandler, ProjectClosedHandler
{
   
   private ProjectModel openedProject;
   
   private ProjectModel projectToBeOpened;
   
   public ProjectCreatedEventHandler() {
      IDE.addHandler(ProjectCreatedEvent.TYPE, this);
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(ProjectClosedEvent.TYPE, this);
   }

   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      openedProject = event.getProject();
   }
   
   @Override
   public void onProjectCreated(final ProjectCreatedEvent event)
   {
      projectToBeOpened = null;
      
      if (openedProject == null) {
         openProject(event.getProject());
         return;
      }

      Dialogs.getInstance().ask("IDE", "Open project " + event.getProject().getName() + " ?",
         new BooleanValueReceivedHandler()
         {
            @Override
            public void booleanValueReceived(Boolean value)
            {
               if (true == value)
               {
                  projectToBeOpened = event.getProject();
                  IDE.fireEvent(new CloseProjectEvent());
               }
            }
         });
   }
   
   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      openedProject = null;
      
      if (projectToBeOpened != null) {
         openProject(projectToBeOpened);
         projectToBeOpened = null;
      }
   }
   
   private void openProject(final ProjectModel project) {
      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            IDE.fireEvent(new OpenProjectEvent(project));
         }
      });      
   }
   
}
