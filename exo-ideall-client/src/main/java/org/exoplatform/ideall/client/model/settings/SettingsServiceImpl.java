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
package org.exoplatform.ideall.client.model.settings;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.initializer.RegistryConstants;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.configuration.Configuration;
import org.exoplatform.ideall.client.model.settings.event.ApplicationContextReceivedEvent;
import org.exoplatform.ideall.client.model.settings.event.ApplicationContextSavedEvent;
import org.exoplatform.ideall.client.model.settings.marshal.ApplicationContextMarshaller;
import org.exoplatform.ideall.client.model.settings.marshal.ApplicationContextUnmarshaller;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.Random;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SettingsServiceImpl extends SettingsService
{

   private HandlerManager eventBus;

   private Loader loader;

   private String registryServiceURL;

   public SettingsServiceImpl(HandlerManager eventBus, Loader loader, String registryServiceURL)
   {
      this.eventBus = eventBus;
      this.loader = loader;
      this.registryServiceURL = registryServiceURL;
   }

   private String getURL(ApplicationContext context)
   {
      String url =
         registryServiceURL + "/" + RegistryConstants.EXO_USERS + "/" + context.getUserInfo().getName() + "/"
            + Configuration.APPLICATION_NAME;
      return url;
   }

   @Override
   public void getSettings(ApplicationContext context)
   {
      String url = getURL(context) + "/?nocache=" + Random.nextInt();
      getSettings(context, url);
   }

   @Override
   public void saveSetting(ApplicationContext context)
   {
      String url = getURL(context) + "/?createIfNotExist=true";
      saveSettings(context, url);
   }

   @Override
   protected void getSettings(ApplicationContext context, String url)
   {
      ApplicationContextReceivedEvent event = new ApplicationContextReceivedEvent(context);
      ApplicationContextUnmarshaller unmarshaller = new ApplicationContextUnmarshaller(eventBus, context);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, event);
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }

   @Override
   protected void saveSettings(ApplicationContext context, String url)
   {
      ApplicationContextMarshaller marshaller = new ApplicationContextMarshaller(context);
      ApplicationContextSavedEvent event = new ApplicationContextSavedEvent(context);

      String errorMessage = "Registry service is not deployed.";
      ExceptionThrownEvent errorEvent = new ExceptionThrownEvent(errorMessage);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event, errorEvent);
      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, "PUT").header(
         HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_XML).data(marshaller).send(callback);
   }

}
