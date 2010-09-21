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
package org.exoplatform.ide.client.module.groovy.service.groovy.event;

import org.exoplatform.gwtframework.commons.exception.ServerExceptionEvent;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class GroovyValidateResultReceivedEvent extends ServerExceptionEvent<GroovyValidateResultReceivedHandler>
{

   public static final GwtEvent.Type<GroovyValidateResultReceivedHandler> TYPE =
      new GwtEvent.Type<GroovyValidateResultReceivedHandler>();

   private Throwable exception;

   private String fileName;
   
   private String fileHref;
   
   public GroovyValidateResultReceivedEvent(String fileName, String fileHref)
   {
      this.fileName = fileName;
      this.fileHref = fileHref;
   }

   @Override
   protected void dispatch(GroovyValidateResultReceivedHandler handler)
   {
      handler.onGroovyValidateResultReceived(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<GroovyValidateResultReceivedHandler> getAssociatedType()
   {
      return TYPE;
   }

   @Override
   public void setException(Throwable exception)
   {
      this.exception = exception;
   }

   public String getFileName()
   {
      return fileName;
   }
   
   public String getFileHref()
   {
      return fileHref;
   }
   
   public Throwable getException()
   {
      return exception;
   }

}
