/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.framework.project.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.gwtframework.commons.exception.ServerExceptionEvent;
import org.exoplatform.ide.client.framework.project.Project;

/**
 * Event occurs, when created project is saved to registry with its classpath location.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 22, 2010 $
 *
 */
public class ProjectSavedEvent extends ServerExceptionEvent<ProjectSavedHandler>
{
   /**
    * Used to register this event.
    */
   public static final  GwtEvent.Type<ProjectSavedHandler> TYPE = new GwtEvent.Type<ProjectSavedHandler>();
      
   /**
    * Project.
    */
   private Project project;
   
   /**
    * Exception while saving created project.
    */
   private Throwable exception;

   /**
    * @param project
    */
   public ProjectSavedEvent(Project project)
   {
      this.project = project;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ProjectSavedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(ProjectSavedHandler handler)
   {
      handler.onProjectSaved(this);
   }

   /**
    * @see org.exoplatform.gwtframework.commons.exception.ServerExceptionEvent#setException(java.lang.Throwable)
    */
   @Override
   public void setException(Throwable exception)
   {
      this.exception = exception;
   }

   /**
    * @return the project
    */
   public Project getProject()
   {
      return project;
   }

   /**
    * @return the exception
    */
   public Throwable getException()
   {
      return exception;
   }
}
