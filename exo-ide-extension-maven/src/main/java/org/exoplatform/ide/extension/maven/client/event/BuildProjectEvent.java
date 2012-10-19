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
package org.exoplatform.ide.extension.maven.client.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Event occurs, when user tries to build project by maven builder.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuildProjectEvent.java Feb 17, 2012 4:04:56 PM azatsarynnyy $
 *
 */
public class BuildProjectEvent extends GwtEvent<BuildProjectHandler>
{
   /**
    * Project for build.
    */
   private ProjectModel project;

   private final boolean publish;
   
   public BuildProjectEvent()
   {
      this(false);
   }

   /**
    * If <code>publish</code> artifact will be in public repository after build.
    * By default set to false
    * @param publish
    */
   public BuildProjectEvent(boolean publish)
   {
      this(null, publish);
   }

   /**
    * @param project
    */
   public BuildProjectEvent(ProjectModel project)
   {
      this(project, false);
   }

   /**
    * If <code>publish</code> artifact will be in public repository after build.
    * By default set to false
    * @param project
    * @param publish
    */
   public BuildProjectEvent(ProjectModel project, boolean publish)
   {
      this.project = project;
      this.publish = publish;
   }

   /**
    * Type used to register this event.
    */
   public static final GwtEvent.Type<BuildProjectHandler> TYPE = new GwtEvent.Type<BuildProjectHandler>();

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<BuildProjectHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(BuildProjectHandler handler)
   {
      handler.onBuildProject(this);
   }

   /**
    * Get the project for build.
    * 
    * @return the project
    */
   public ProjectModel getProject()
   {
      return project;
   }

   public boolean isPublish()
   {
      return publish;
   }
}
