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
import org.exoplatform.ideall.client.model.configuration.Configuration;
import org.exoplatform.ideall.client.model.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ideall.client.model.settings.event.ApplicationSettingsSavedEvent;
import org.exoplatform.ideall.client.model.settings.marshal.ApplicationSettingsMarshaller;
import org.exoplatform.ideall.client.model.settings.marshal.ApplicationSettingsUnmarshaller;

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
   
   private String userName;

   public SettingsServiceImpl(HandlerManager eventBus, Loader loader, String registryServiceURL, String userName)
   {
      this.eventBus = eventBus;
      this.loader = loader;
      this.registryServiceURL = registryServiceURL;
      this.userName = userName;
   }

   private String getURL()
   {
      String url = registryServiceURL + "/" + RegistryConstants.EXO_USERS + "/" + userName + "/" + Configuration.APPLICATION_NAME;
      return url;
   }

   @Override
   public void getSettings(ApplicationSettings applicationSettings)
   {
      String url = getURL() + "/?nocache=" + Random.nextInt();

      ApplicationSettingsReceivedEvent event = new ApplicationSettingsReceivedEvent(applicationSettings);
      ApplicationSettingsUnmarshaller unmarshaller = new ApplicationSettingsUnmarshaller(applicationSettings);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, event);
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }

   @Override
   public void saveSetting(ApplicationSettings applicationSettings)
   {
      String url = getURL() + "/?createIfNotExist=true";
      
      ApplicationSettingsMarshaller marshaller = new ApplicationSettingsMarshaller(applicationSettings);
      ApplicationSettingsSavedEvent event = new ApplicationSettingsSavedEvent(applicationSettings);

      String errorMessage = "Registry service is not deployed.";
      ExceptionThrownEvent errorEvent = new ExceptionThrownEvent(errorMessage);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event, errorEvent);
      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, "PUT").header(
         HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_XML).data(marshaller).send(callback);
   }

}
