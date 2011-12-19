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
package org.exoplatform.ide.extension.cloudfoundry.client.delete;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when CloudFoundry application is deleted.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id:  Dec 8, 2011 2:39:55 PM anya $
 *
 */
public class ApplicationDeletedEvent extends GwtEvent<ApplicationDeletedHandler>
{

   /**
    * Type used to register event.
    */
   public static final GwtEvent.Type<ApplicationDeletedHandler> TYPE = new GwtEvent.Type<ApplicationDeletedHandler>();

   private String applicationName;
   
   /**
    * @param vfsId VFS id
    * @param projectId project's id
    */
   public  ApplicationDeletedEvent(String applicationName)
   {
      this.applicationName = applicationName;
   }
   
   
   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ApplicationDeletedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(ApplicationDeletedHandler handler)
   {
      handler.onApplicationDeleted(this);
   }


   /**
    * @return {@link String} deleted application name
    */
   public String getApplicationName()
   {
      return applicationName;
   }
}
