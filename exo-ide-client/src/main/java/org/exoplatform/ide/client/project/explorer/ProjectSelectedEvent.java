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
package org.exoplatform.ide.client.project.explorer;

import org.exoplatform.ide.vfs.client.model.ProjectModel;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when project is selected in project's list (Project Explorer view).
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Aug 20, 2012 3:14:17 PM anya $
 * 
 */
public class ProjectSelectedEvent extends GwtEvent<ProjectSelectedHandler>
{
   /**
    * Type used to register the event.
    */
   public static final GwtEvent.Type<ProjectSelectedHandler> TYPE = new GwtEvent.Type<ProjectSelectedHandler>();

   /**
    * Selected project.
    */
   private ProjectModel project;

   /**
    * @param project selected project.
    */
   public ProjectSelectedEvent(ProjectModel project)
   {
      this.project = project;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ProjectSelectedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(ProjectSelectedHandler handler)
   {
      handler.onProjectSelected(this);
   }

   /**
    * @return {@link ProjectModel} selected project
    */
   public ProjectModel getProject()
   {
      return project;
   }
}
