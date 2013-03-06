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
package com.codenvy.ide.extension.maven.client.event;

import com.codenvy.ide.extension.maven.shared.BuildStatus;

import com.google.gwt.event.shared.GwtEvent;

import com.codenvy.ide.extension.maven.client.event.ProjectBuiltHandler;

/**
 * Event occurs, when project has built by maven builder.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ProjectBuiltEvent.java Apr 3, 2012 12:23:44 PM azatsarynnyy $
 *
 */
public class ProjectBuiltEvent extends GwtEvent<ProjectBuiltHandler>
{

   /**
    * Status of build.
    */
   private BuildStatus status;

   /**
    * Type used to register this event.
    */
   public static final GwtEvent.Type<ProjectBuiltHandler> TYPE = new Type<ProjectBuiltHandler>();

   /**
    * @param status status of build
    */
   public ProjectBuiltEvent(BuildStatus status)
   {
      this.status = status;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ProjectBuiltHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(ProjectBuiltHandler handler)
   {
      handler.onProjectBuilt(this);
   }

   /**
    * Returns the status of build project.
    * 
    * @return the build status
    */
   public BuildStatus getBuildStatus()
   {
      return status;
   }

}
