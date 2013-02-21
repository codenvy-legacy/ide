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
package org.exoplatform.ide.extension.cloudfoundry.client.project;

import com.google.gwt.event.shared.GwtEvent;

/**
 *  Event occurs, when CloudFoundry application information, properties were changed.
 *  It can be state (after run/stop/restart), memory size and so on.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id:  Dec 8, 2011 4:10:33 PM anya $
 *
 */
public class ApplicationInfoChangedEvent extends GwtEvent<ApplicationInfoChangedHandler>
{
   /**
    * Type used to register event.
    */
   public static final GwtEvent.Type<ApplicationInfoChangedHandler> TYPE =
      new GwtEvent.Type<ApplicationInfoChangedHandler>();

   /**
    * VFS's id.
    */
   private String vfsId;

   /**
    * Project's id.
    */
   private String projectId;

   /**
    * @param vfsId VFS id
    * @param projectId project's id
    */
   public ApplicationInfoChangedEvent(String vfsId, String projectId)
   {
      this.vfsId = vfsId;
      this.projectId = projectId;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ApplicationInfoChangedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(ApplicationInfoChangedHandler handler)
   {
      handler.onApplicationInfoChanged(this);
   }

   /**
    * @return the vfsId VFS id
    */
   public String getVfsId()
   {
      return vfsId;
   }

   /**
    * @return the projectId project's id
    */
   public String getProjectId()
   {
      return projectId;
   }
}
