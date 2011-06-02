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
package org.exoplatform.ide.extension.heroku.client;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.extension.heroku.client.marshaller.ApplicationInfoUnmarshaller;
import org.exoplatform.ide.extension.heroku.client.marshaller.Constants;
import org.exoplatform.ide.extension.heroku.client.marshaller.CredentailsMarshaller;
import org.exoplatform.ide.git.client.GitClientUtil;

import java.util.HashMap;

/**
 * Implementation of {@link HerokuClientService} service.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  May 25, 2011 12:23:29 PM anya $
 *
 */
public class HerokuClientServiceImpl extends HerokuClientService
{
   private static final String LOGIN_PATH = "/ide/heroku/login";

   private static final String CREATE_APPLICATION = "/ide/heroku/apps/create";

   private static final String DESTROY_APPLICATION = "/ide/heroku/apps/destroy";
   
   private static final String RENAME_APPLICATION = "/ide/heroku/apps/rename";

   private static final String ADD_KEY = "/ide/heroku/keys/add";

   private static final String CLEAR_KEYS = "/ide/heroku/keys/clear";

   private static final String APPLICATION_INFO = "/ide/heroku/apps/info";

   /**
    * Events handler.
    */
   private HandlerManager eventBus;

   /**
    * REST service context.
    */
   private String restServiceContext;

   /**
    * Loader to be displayed.
    */
   private Loader loader;

   /**
    * @param eventBus eventBus
    * @param restContext rest context
    * @param loader loader to show on server request
    */
   public HerokuClientServiceImpl(HandlerManager eventBus, String restContext, Loader loader)
   {
      this.loader = loader;
      this.eventBus = eventBus;
      this.restServiceContext = restContext;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.HerokuClientService#login(java.lang.String, java.lang.String)
    */
   @Override
   public void login(String login, String password, AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + LOGIN_PATH;

      HashMap<String, String> credentials = new HashMap<String, String>();
      credentials.put(Constants.EMAIL, login);
      credentials.put(Constants.PASSWORD, password);
      CredentailsMarshaller marshaller = new CredentailsMarshaller(credentials);

      callback.setEventBus(eventBus);

      AsyncRequest.build(RequestBuilder.POST, url, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.HerokuClientService#createApplication(java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void createApplication(String applicationName, String gitWorkDir, String remoteName,
      HerokuAsyncRequestCallback callback)
   {
      String workDir = GitClientUtil.getWorkingDirFromHref(gitWorkDir, restServiceContext);
      String url = restServiceContext + CREATE_APPLICATION;
      String params = (applicationName != null && !applicationName.isEmpty()) ? "name=" + applicationName + "&" : "";
      params += (remoteName != null && !remoteName.trim().isEmpty()) ? "remote=" + remoteName + "&" : "";
      params += "&workDir=" + workDir;

      HashMap<String, String> applicationInfo = new HashMap<String, String>();
      ApplicationInfoUnmarshaller unmarshaller = new ApplicationInfoUnmarshaller(applicationInfo);
      callback.setResult(applicationInfo);
      callback.setPayload(unmarshaller);

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.HerokuClientService#deleteApplication(java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void deleteApplication(String gitWorkDir, String applicationName, HerokuAsyncRequestCallback callback)
   {
      if (gitWorkDir != null)
      {
         gitWorkDir = GitClientUtil.getWorkingDirFromHref(gitWorkDir, restServiceContext);
      }

      String url = restServiceContext + DESTROY_APPLICATION;
      String params = (applicationName != null) ? "name=" + applicationName + "&" : "";
      params += (gitWorkDir != null) ? "workDir=" + gitWorkDir : "";

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.HerokuClientService#addKey()
    */
   @Override
   public void addKey(HerokuAsyncRequestCallback callback)
   {
      String url = restServiceContext + ADD_KEY;
      AsyncRequest.build(RequestBuilder.POST, url, loader).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.HerokuClientService#clearKeys(org.exoplatform.ide.extension.heroku.client.HerokuAsyncRequestCallback)
    */
   @Override
   public void clearKeys(HerokuAsyncRequestCallback callback)
   {
      String url = restServiceContext + CLEAR_KEYS;
      AsyncRequest.build(RequestBuilder.POST, url, loader).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.HerokuClientService#getApplicationInfo(java.lang.String, java.lang.String, org.exoplatform.ide.extension.heroku.client.HerokuAsyncRequestCallback)
    */
   @Override
   public void getApplicationInfo(String gitWorkDir, String applicationName, boolean isRaw,
      HerokuAsyncRequestCallback callback)
   {
      if (gitWorkDir != null)
      {
         gitWorkDir = GitClientUtil.getWorkingDirFromHref(gitWorkDir, restServiceContext);
      }

      String url = restServiceContext + APPLICATION_INFO;

      String params = (applicationName != null) ? "name=" + applicationName + "&" : "";
      params += (gitWorkDir != null) ? "workDir=" + gitWorkDir : "";

      HashMap<String, String> applicationInfo = new HashMap<String, String>();
      ApplicationInfoUnmarshaller unmarshaller = new ApplicationInfoUnmarshaller(applicationInfo);
      callback.setResult(applicationInfo);
      callback.setPayload(unmarshaller);

      AsyncRequest.build(RequestBuilder.GET, url + "?" + params, loader)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.HerokuClientService#renameApplication(java.lang.String, java.lang.String, java.lang.String, org.exoplatform.ide.extension.heroku.client.HerokuAsyncRequestCallback)
    */
   @Override
   public void renameApplication(String gitWorkDir, String applicationName, String newName,
      HerokuAsyncRequestCallback callback)
   {
      if (gitWorkDir != null)
      {
         gitWorkDir = GitClientUtil.getWorkingDirFromHref(gitWorkDir, restServiceContext);
      }

      String url = restServiceContext + RENAME_APPLICATION;

      String params = (applicationName != null) ? "name=" + applicationName + "&" : "";
      params = (newName != null) ? "newname=" + newName + "&" : "";
      params += (gitWorkDir != null) ? "workDir=" + gitWorkDir : "";

      HashMap<String, String> applicationInfo = new HashMap<String, String>();
      ApplicationInfoUnmarshaller unmarshaller = new ApplicationInfoUnmarshaller(applicationInfo);
      callback.setResult(applicationInfo);
      callback.setPayload(unmarshaller);

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
   }
}
