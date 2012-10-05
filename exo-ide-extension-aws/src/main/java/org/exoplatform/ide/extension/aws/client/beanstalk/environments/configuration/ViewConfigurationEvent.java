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
package org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: ViewConfigurationEvent.java Oct 5, 2012 1:17:17 PM azatsarynnyy $
 *
 */
public class ViewConfigurationEvent extends GwtEvent<ViewConfigurationHandler>
{
   /**
    * Type used to register the event.
    */
   public static final GwtEvent.Type<ViewConfigurationHandler> TYPE = new GwtEvent.Type<ViewConfigurationHandler>();

   private String environmentId;

   public ViewConfigurationEvent(String environmentId)
   {
      this.environmentId = environmentId;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(ViewConfigurationHandler handler)
   {
      handler.onViewConfiguration(this);
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ViewConfigurationHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * Returns the environment identifier.
    * 
    * @return the environment identifier
    */
   public String getEnvironmentId()
   {
      return environmentId;
   }

}
