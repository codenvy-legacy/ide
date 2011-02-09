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
package org.exoplatform.ide.extension.groovy.client.service.groovy.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.gwtframework.commons.exception.ServerExceptionEvent;
import org.exoplatform.ide.extension.groovy.client.service.groovy.marshal.ClassPath;

/**
 * Event is fired, when on groovy classpath file location is received from server.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jan 6, 2011 $
 *
 */
public class ClassPathLocationReceivedEvent extends ServerExceptionEvent<ClassPathLocationReceivedHandler>
{
   /**
    * Type used to register this event.
    */
   public static final GwtEvent.Type<ClassPathLocationReceivedHandler> TYPE = new GwtEvent.Type<ClassPathLocationReceivedHandler>();
   
   /**
    * Error, that occurred while getting classpath location.
    */
   private Throwable exception;
   
   /**
    * Classpath info.
    */
   private ClassPath classPath;
   
   /**
    * @param classPath
    */
   public ClassPathLocationReceivedEvent(ClassPath classPath)
   {
      this.classPath = classPath;
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
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ClassPathLocationReceivedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(ClassPathLocationReceivedHandler handler)
   {
      handler.onClassPathLocationReceived(this);
   }

   /**
    * @return the exception
    */
   public Throwable getException()
   {
      return exception;
   }

   /**
    * @return the classPath
    */
   public ClassPath getClassPath()
   {
      return classPath;
   }
}
