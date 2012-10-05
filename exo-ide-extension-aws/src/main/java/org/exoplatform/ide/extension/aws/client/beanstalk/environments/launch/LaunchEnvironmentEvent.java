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
package org.exoplatform.ide.extension.aws.client.beanstalk.environments.launch;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 21, 2012 2:57:38 PM anya $
 * 
 */
public class LaunchEnvironmentEvent extends GwtEvent<LaunchEnvironmentHandler>
{
   /**
    * Type used to register the event.
    */
   public static final GwtEvent.Type<LaunchEnvironmentHandler> TYPE = new GwtEvent.Type<LaunchEnvironmentHandler>();

   private String applicationName;

   private String versionLabel;

   private String vfsId;

   private String projectId;

   private LaunchEnvironmentStartedHandler environmentLaunchedHandler;

   public LaunchEnvironmentEvent(String vfsId, String projectId, String applicationName, String versionLabel,
      LaunchEnvironmentStartedHandler environmentLaunchedHandler)
   {
      this.vfsId = vfsId;
      this.projectId = projectId;
      this.applicationName = applicationName;
      this.versionLabel = versionLabel;
      this.environmentLaunchedHandler = environmentLaunchedHandler;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(LaunchEnvironmentHandler handler)
   {
      handler.onLaunchEnvironment(this);
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<LaunchEnvironmentHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @return the applicationName
    */
   public String getApplicationName()
   {
      return applicationName;
   }

   /**
    * @return the versionLabel
    */
   public String getVersionLabel()
   {
      return versionLabel;
   }

   /**
    * @return the vfsId
    */
   public String getVfsId()
   {
      return vfsId;
   }

   /**
    * @return the projectId
    */
   public String getProjectId()
   {
      return projectId;
   }

   /**
    * @return the environmentLaunchedHandler
    */
   public LaunchEnvironmentStartedHandler getEnvironmentCreatedHandler()
   {
      return environmentLaunchedHandler;
   }
}
