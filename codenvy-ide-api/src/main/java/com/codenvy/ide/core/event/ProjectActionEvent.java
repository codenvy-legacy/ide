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
package com.codenvy.ide.core.event;

import com.codenvy.ide.resources.model.Project;

import com.google.gwt.event.shared.GwtEvent;


/**
 * Event that describes the fact that Project Action has be performed
 * 
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class ProjectActionEvent extends GwtEvent<ProjectActionHandler>
{

   public static Type<ProjectActionHandler> TYPE = new Type<ProjectActionHandler>();

   /**
    * Set of possible Project Actions
    */
   public static enum ProjectAction {
      OPENED, CLOSED, DESCRIPTION_CHANGED, RESOURCE_CHANGED;
   }

   private final Project project;

   private final ProjectAction projectAction;

   /**
    * Creates a Project Opened Event
    * 
    * @param project - an instance of affected project
    * @return
    */
   public static ProjectActionEvent createProjectOpenedEvent(Project project)
   {
      return new ProjectActionEvent(project, ProjectAction.OPENED);
   }

   /**
    * Creates a Project Closed Event
    * 
    * @param project - an instance of affected project
    * @return
    */
   public static ProjectActionEvent createProjectClosedEvent(Project project)
   {
      return new ProjectActionEvent(project, ProjectAction.CLOSED);
   }

   /**
    * Creates a Project's Description Changed Event
    * 
    * @param project - an instance of affected project
    * @return
    */
   public static ProjectActionEvent createProjectDescriptionChangedEvent(Project project)
   {
      return new ProjectActionEvent(project, ProjectAction.DESCRIPTION_CHANGED);
   }

   /**
    * @param project
    * @param projectAction
    */
   protected ProjectActionEvent(Project project, ProjectAction projectAction)
   {
      this.project = project;
      this.projectAction = projectAction;
   }

   @Override
   public Type<ProjectActionHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @return the instance of affected project
    */
   public Project getProject()
   {
      return project;
   }

   /**
    * @return the type of action
    */
   public ProjectAction getProjectAction()
   {
      return projectAction;
   }

   @Override
   protected void dispatch(ProjectActionHandler handler)
   {
      switch (projectAction)
      {
         case OPENED :
            handler.onProjectOpened(this);
            break;
         case CLOSED :
            handler.onProjectClosed(this);
            break;
         case DESCRIPTION_CHANGED :
            handler.onProjectDescriptionChanged(this);
            break;
         default :
            break;
      }
   }
}
