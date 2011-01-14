/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.framework.discovery.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class DefaultEntryPointReceivedEvent extends GwtEvent<DefaultEntryPointReceivedHandler>
{

   public static final GwtEvent.Type<DefaultEntryPointReceivedHandler> TYPE = new GwtEvent.Type<DefaultEntryPointReceivedHandler>();

   private String defaultEntryPoint;
   
   /**
    * @return the defaultEntryPoint
    */
   public String getDefaultEntryPoint()
   {
      return defaultEntryPoint;
   }
   
   /**
    * @param defaultEntryPoint the defaultEntryPoint to set
    */
   public void setDefaultEntryPoint(String defaultEntryPoint)
   {
      this.defaultEntryPoint = defaultEntryPoint;
   }

   @Override
   protected void dispatch(DefaultEntryPointReceivedHandler handler)
   {
      handler.onDefaultEntryPointReceived(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<DefaultEntryPointReceivedHandler> getAssociatedType()
   {
      return TYPE;
   }

}
