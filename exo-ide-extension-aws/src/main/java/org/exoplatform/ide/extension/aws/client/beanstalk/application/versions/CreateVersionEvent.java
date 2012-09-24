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
package org.exoplatform.ide.extension.aws.client.beanstalk.application.versions;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Event occurs, when user tries to create new version.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 21, 2012 11:44:58 AM anya $
 * 
 */
public class CreateVersionEvent extends GwtEvent<CreateVersionHandler>
{
   public static final GwtEvent.Type<CreateVersionHandler> TYPE = new GwtEvent.Type<CreateVersionHandler>();

   private String vfsId;

   private ProjectModel project;

   private String applicationName;

   private VersionCreatedHandler versionCreatedHandler;

   public CreateVersionEvent(String vfsId, ProjectModel project, String applicationName,
      VersionCreatedHandler versionCreatedHandler)
   {
      this.vfsId = vfsId;
      this.project = project;
      this.applicationName = applicationName;
      this.versionCreatedHandler = versionCreatedHandler;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<CreateVersionHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(CreateVersionHandler handler)
   {
      handler.onCreateVersion(this);
   }

   /**
    * @return the vfsId
    */
   public String getVfsId()
   {
      return vfsId;
   }

   /**
    * @return the project
    */
   public ProjectModel getProject()
   {
      return project;
   }

   /**
    * @return the applicationName
    */
   public String getApplicationName()
   {
      return applicationName;
   }

   /**
    * @return the versionCreatedHandler
    */
   public VersionCreatedHandler getVersionCreatedHandler()
   {
      return versionCreatedHandler;
   }
}
