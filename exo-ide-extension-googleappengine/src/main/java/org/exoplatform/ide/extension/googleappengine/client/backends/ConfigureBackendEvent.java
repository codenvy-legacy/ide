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
package org.exoplatform.ide.extension.googleappengine.client.backends;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when user tries to configure backend.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 30, 2012 5:24:39 PM anya $
 * 
 */
public class ConfigureBackendEvent extends GwtEvent<ConfigureBackendHandler>
{
   /**
    * Type, used to register the event.
    */
   public static final GwtEvent.Type<ConfigureBackendHandler> TYPE = new GwtEvent.Type<ConfigureBackendHandler>();

   private String backendName;

   public ConfigureBackendEvent(String backendName)
   {
      this.backendName = backendName;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ConfigureBackendHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(ConfigureBackendHandler handler)
   {
      handler.onConfigureBackend(this);
   }

   /**
    * @return the backendName
    */
   public String getBackendName()
   {
      return backendName;
   }
}
