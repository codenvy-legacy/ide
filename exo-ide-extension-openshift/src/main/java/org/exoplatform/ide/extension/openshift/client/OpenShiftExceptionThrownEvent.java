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
package org.exoplatform.ide.extension.openshift.client;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs on exception that concerns actions with OpenShift. Implement {@link OpenShiftExceptionThrownHandler} to handle
 * event.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 10, 2011 5:26:52 PM anya $
 * 
 */
public class OpenShiftExceptionThrownEvent extends GwtEvent<OpenShiftExceptionThrownHandler>
{
   /**
    * Type used to register event,
    */
   public static final GwtEvent.Type<OpenShiftExceptionThrownHandler> TYPE =
      new GwtEvent.Type<OpenShiftExceptionThrownHandler>();

   /**
    * Exception, that occurred.
    */
   private Throwable exception;

   /**
    * Error message.
    */
   private String errorMessage;

   /**
    * @param exception occurred exception
    * @param errorMessage error message
    */
   public OpenShiftExceptionThrownEvent(Throwable exception, String errorMessage)
   {
      this.exception = exception;
      this.errorMessage = errorMessage;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<OpenShiftExceptionThrownHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(OpenShiftExceptionThrownHandler handler)
   {
      handler.onOpenShiftExceptionThrown(this);
   }

   /**
    * @return the exception
    */
   public Throwable getException()
   {
      return exception;
   }

   /**
    * @return the errorMessage
    */
   public String getErrorMessage()
   {
      return errorMessage;
   }
}
