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
package org.exoplatform.ide.client.module.netvibes.service.deploy.event;

import org.exoplatform.gwtframework.commons.exception.ServerExceptionEvent;
import org.exoplatform.ide.client.module.netvibes.model.DeployResult;
import org.exoplatform.ide.client.module.netvibes.model.DeployWidget;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event is fired when the result of deploying UWA widget is received.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 1, 2010 $
 *
 */
public class WidgetDeployResultReceivedEvent extends ServerExceptionEvent<WidgetDeployResultReceivedHandler>
{

   /**
    * Type used to register events with the HandlerManager. 
    */
   public static final GwtEvent.Type<WidgetDeployResultReceivedHandler> TYPE =
      new GwtEvent.Type<WidgetDeployResultReceivedHandler>();

   /**
    * Data for deploying widget.
    */
   private DeployWidget deployWidget;
   
   /**
    * The result of the deploy.
    */
   private DeployResult deployResult;
   
   /**
    * Exception, occured while deploy operation.
    */
   private Throwable exception;

   /**
    * @param deployWidget data for deploying widget
    * @param deployResult deploy result
    */
   public WidgetDeployResultReceivedEvent(DeployWidget deployWidget, DeployResult deployResult)
   {
      this.deployWidget = deployWidget;
      this.deployResult = deployResult;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<WidgetDeployResultReceivedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(WidgetDeployResultReceivedHandler handler)
   {
      handler.onWidgetDeployResultReceived(this);
   }

   /**
    * @see org.exoplatform.gwtframework.commons.exception.ServerExceptionEvent#setException(java.lang.Throwable)
    */
   @Override
   public void setException(Throwable exception)
   {
      this.exception = exception;
   }

   /**
    * @return the exception
    */
   public Throwable getException()
   {
      return exception;
   }

   /**
    * @return the deployWidget
    */
   public DeployWidget getDeployWidget()
   {
      return deployWidget;
   }

   /**
    * @return the deployResult
    */
   public DeployResult getDeployResult()
   {
      return deployResult;
   }
   
}
