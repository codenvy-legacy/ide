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
package org.exoplatform.ide.extension.heroku.client.rename;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.extension.heroku.client.marshaller.Property;

import java.util.List;

/**
 * Event occurs after rename Heroku application operation.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Dec 8, 2011 2:24:25 PM anya $
 * 
 */
public class ApplicationRenamedEvent extends GwtEvent<ApplicationRenamedHandler>
{

   /**
    * Type used to register event.
    */
   public static final GwtEvent.Type<ApplicationRenamedHandler> TYPE = new GwtEvent.Type<ApplicationRenamedHandler>();

   /**
    * Application properties after rename.
    */
   private List<Property> properties;

   /**
    * Project's id.
    */
   private String projectId;

   /**
    * @param projectId project's id
    * @param properties application properties after rename
    */
   public ApplicationRenamedEvent(String projectId, List<Property> properties)
   {
      this.projectId = projectId;
      this.properties = properties;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ApplicationRenamedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(ApplicationRenamedHandler handler)
   {
      handler.onApplicationRenamed(this);
   }

   /**
    * @return the properties application's properties
    */
   public List<Property> getProperties()
   {
      return properties;
   }

   /**
    * @return the projectId project's id
    */
   public String getProjectId()
   {
      return projectId;
   }
}
