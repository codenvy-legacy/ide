/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.model.groovy.event;

import org.exoplatform.gwt.commons.exceptions.ServerExceptionEvent;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class GroovyUndeployResultReceivedEvent extends ServerExceptionEvent<GroovyUndeployResultReceivedHandler>
{

   public static final GwtEvent.Type<GroovyUndeployResultReceivedHandler> TYPE =
      new GwtEvent.Type<GroovyUndeployResultReceivedHandler>();

   private String path;

   private Throwable exception;

   public GroovyUndeployResultReceivedEvent(String path)
   {
      this.path = path;
   }

   @Override
   protected void dispatch(GroovyUndeployResultReceivedHandler handler)
   {
      handler.onGroovyUndeployResultReceived(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<GroovyUndeployResultReceivedHandler> getAssociatedType()
   {
      return TYPE;
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

   public String getPath()
   {
      return path;
   }

}
