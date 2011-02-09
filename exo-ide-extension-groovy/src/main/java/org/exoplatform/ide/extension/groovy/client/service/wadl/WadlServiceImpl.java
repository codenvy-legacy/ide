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
package org.exoplatform.ide.extension.groovy.client.service.wadl;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPMethod;
import org.exoplatform.gwtframework.commons.wadl.WadlApplication;
import org.exoplatform.ide.extension.groovy.client.service.wadl.event.WadlServiceOutputReceivedEvent;
import org.exoplatform.ide.extension.groovy.client.service.wadl.marshal.WadlServiceOutputUnmarshaller;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class WadlServiceImpl extends WadlService
{
   private HandlerManager eventBus;

   private Loader loader;

   public WadlServiceImpl(HandlerManager eventBus, Loader loader)
   {
      this.eventBus = eventBus;
      this.loader = loader;
   }

   @Override
   public void getWadl(String url)
   {
      WadlApplication application = new WadlApplication();
      WadlServiceOutputUnmarshaller unmarshaller = new WadlServiceOutputUnmarshaller(eventBus, application);
      WadlServiceOutputReceivedEvent event = new WadlServiceOutputReceivedEvent(application);
      
      String errorMessage = "Service is not deployed.";
      ExceptionThrownEvent errorEvent = new ExceptionThrownEvent(errorMessage);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, errorEvent);

      AsyncRequest request = AsyncRequest.build(RequestBuilder.POST, url, loader);

      request.header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.OPTIONS);
      request.send(callback);
   }

}
