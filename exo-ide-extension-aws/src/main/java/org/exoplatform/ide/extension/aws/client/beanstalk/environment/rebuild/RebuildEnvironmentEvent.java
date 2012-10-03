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
package org.exoplatform.ide.extension.aws.client.beanstalk.environment.rebuild;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentInfo;

/**
 * Event occurs, when user tries to rebuild application's environment.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: RebuildEnvironmentEvent.java Sep 28, 2012 3:49:51 PM azatsarynnyy $
 *
 */
public class RebuildEnvironmentEvent extends GwtEvent<RebuildEnvironmentHandler>
{

   /**
    * Type, used to register event.
    */
   public static final GwtEvent.Type<RebuildEnvironmentHandler> TYPE = new GwtEvent.Type<RebuildEnvironmentHandler>();

   private EnvironmentInfo environmentInfo;

   private RebuildEnvironmentStartedHandler rebuildEnvironmentStartedHandler;

   public RebuildEnvironmentEvent(EnvironmentInfo environmentInfo,
      RebuildEnvironmentStartedHandler rebuildEnvironmentStartedHandler)
   {
      this.environmentInfo = environmentInfo;
      this.rebuildEnvironmentStartedHandler = rebuildEnvironmentStartedHandler;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<RebuildEnvironmentHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(RebuildEnvironmentHandler handler)
   {
      handler.onRebuildEnvironment(this);
   }

   /**
    * 
    * @return
    */
   public EnvironmentInfo getEnvironmentInfo()
   {
      return environmentInfo;
   }

   /**
    * @return the rebuildEnvironmentStartedHandler
    */
   public RebuildEnvironmentStartedHandler getRebuildEnvironmentStartedHandler()
   {
      return rebuildEnvironmentStartedHandler;
   }

}
