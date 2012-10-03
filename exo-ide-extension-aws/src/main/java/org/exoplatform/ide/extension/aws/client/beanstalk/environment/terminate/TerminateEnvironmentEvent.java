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
package org.exoplatform.ide.extension.aws.client.beanstalk.environment.terminate;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentInfo;

/**
 * Event occurs, when user tries to terminate application's environment.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 20, 2012 4:52:19 PM anya $
 * 
 */
public class TerminateEnvironmentEvent extends GwtEvent<TerminateEnvironmentHandler>
{

   /**
    * Type, used to register event.
    */
   public static final GwtEvent.Type<TerminateEnvironmentHandler> TYPE =
      new GwtEvent.Type<TerminateEnvironmentHandler>();

   private EnvironmentInfo environmentInfo;

   private TerminateEnvironmentStartedHandler terminateEnvironmentStartedHandler;

   public TerminateEnvironmentEvent(EnvironmentInfo environmentInfo,
      TerminateEnvironmentStartedHandler terminateEnvironmentStartedHandler)
   {
      this.environmentInfo = environmentInfo;
      this.terminateEnvironmentStartedHandler = terminateEnvironmentStartedHandler;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<TerminateEnvironmentHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(TerminateEnvironmentHandler handler)
   {
      handler.onTerminateEnvironment(this);
   }

   public EnvironmentInfo getEnvironmentInfo()
   {
      return environmentInfo;
   }

   /**
    * @return the terminateEnvironmentStartedHandler
    */
   public TerminateEnvironmentStartedHandler getTerminateEnvironmentStartedHandler()
   {
      return terminateEnvironmentStartedHandler;
   }

}
