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
package org.exoplatform.ide.client.module.groovy.service.wadl.marshal;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.gwtframework.commons.wadl.IllegalWADLException;
import org.exoplatform.gwtframework.commons.wadl.WadlApplication;
import org.exoplatform.gwtframework.commons.wadl.WadlProcessor;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.Response;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class WadlServiceOutputUnmarshaller implements Unmarshallable
{

   private WadlApplication application;

   private HandlerManager eventBus;

   public WadlServiceOutputUnmarshaller(HandlerManager eventBus, WadlApplication application)
   {
      this.eventBus = eventBus;
      this.application = application;
   }

   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         WadlProcessor.unmarshal(application, response.getText());
      }
      catch (IllegalWADLException e)
      {
         eventBus.fireEvent(new ExceptionThrownEvent(e));
      }
      catch (Exception exc)
      {
         String message = "Can't parse output wadl service output!";
         throw new UnmarshallerException(message);
      }
   }

}
