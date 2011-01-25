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
package org.exoplatform.ide.client.module.groovy.service.wadl.event;

import org.exoplatform.gwtframework.commons.exception.ServerExceptionEvent;
import org.exoplatform.gwtframework.commons.wadl.WadlApplication;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class WadlServiceOutputReceivedEvent extends ServerExceptionEvent<WadlServiceOutputReceiveHandler>
{
   public static final GwtEvent.Type<WadlServiceOutputReceiveHandler> TYPE =
      new GwtEvent.Type<WadlServiceOutputReceiveHandler>();

   private Throwable exception;

   private WadlApplication application;

   public WadlServiceOutputReceivedEvent(WadlApplication application)
   {
      this.application = application;
   }

   public WadlApplication getApplication()
   {
      return application;
   }

   @Override
   public void setException(Throwable exception)
   {
      this.exception = exception;
   }

   @Override
   protected void dispatch(WadlServiceOutputReceiveHandler hendler)
   {
      hendler.onWadlServiceOutputReceived(this);
   }

   public Throwable getException()
   {
      return exception;
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<WadlServiceOutputReceiveHandler> getAssociatedType()
   {
      return TYPE;
   }

}
