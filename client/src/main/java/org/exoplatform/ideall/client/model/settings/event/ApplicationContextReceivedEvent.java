/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ideall.client.model.settings.event;

import org.exoplatform.gwt.commons.exceptions.ServerExceptionEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ApplicationContextReceivedEvent extends ServerExceptionEvent<ApplicationContextReceivedHandler>
{

   public static final GwtEvent.Type<ApplicationContextReceivedHandler> TYPE =
      new GwtEvent.Type<ApplicationContextReceivedHandler>();

   private ApplicationContext context;

   private Throwable exception;

   public ApplicationContextReceivedEvent(ApplicationContext context)
   {
      this.context = context;
   }

   @Override
   protected void dispatch(ApplicationContextReceivedHandler handler)
   {
      handler.onApplicationContextReceived(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ApplicationContextReceivedHandler> getAssociatedType()
   {
      return TYPE;
   }

   public ApplicationContext getContext()
   {
      return context;
   }

   @Override
   public void setException(Throwable exception)
   {
      this.exception = exception;
   }

   public Throwable getException()
   {
      return exception;
   }

}
