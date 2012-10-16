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
package org.exoplatform.ide.extension.aws.client.ec2.stop;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when user tries to stop an EC2 instance.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: RestartAppServerEvent.java Sep 28, 2012 3:49:51 PM azatsarynnyy $
 *
 */
public class StopInstanceEvent extends GwtEvent<StopInstanceHandler>
{

   /**
    * Type, used to register event.
    */
   public static final GwtEvent.Type<StopInstanceHandler> TYPE = new GwtEvent.Type<StopInstanceHandler>();

   private String instanceId;

   public StopInstanceEvent(String instanceId)
   {
      this.instanceId = instanceId;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<StopInstanceHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(StopInstanceHandler handler)
   {
      handler.onStopInstance(this);
   }

   /**
    * 
    * @return
    */
   public String getInstanceId()
   {
      return instanceId;
   }

}
