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

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.copy.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.copy.MimeType;
import org.exoplatform.ide.extension.heroku.client.create.CreateRequestHandler;
import org.exoplatform.ide.extension.heroku.client.marshaller.Constants;
import org.exoplatform.ide.extension.heroku.client.marshaller.CredentailsMarshaller;

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

   private static final String LOGOUT_PATH = "/ide/heroku/logout";

   private static final String CREATE_APPLICATION = "/ide/heroku/apps/create";

   private static final String DESTROY_APPLICATION = "/ide/heroku/apps/destroy";

   private static final String LOGS = "/ide/heroku/apps/logs";

   private static final String RENAME_APPLICATION = "/ide/heroku/apps/rename";

   private static final String RUN = "/ide/heroku/apps/run";

   private static final String ADD_KEY = "/ide/heroku/keys/add";

   private static final String CLEAR_KEYS = "/ide/heroku/keys/clear";

   private static final String APPLICATION_INFO = "/ide/heroku/apps/info";

   private static final String GET_STACKS = "/ide/heroku/apps/stack";

   private static final String STACK_MIGRATE = "/ide/heroku/apps/stack-migrate";

   /**
    * REST service context.
    */
   private String restServiceContext;

   /**
    * Loader to be displayed.
    */
   private Loader loader;

   /**
    * @param restContext rest context
    * @param loader loader to show on server request
    */
   public HerokuClientServiceImpl(String restContext, Loader loader)
   {
      this.loader = loader;
      this.restServiceContext = restContext;
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.heroku.client.HerokuClientService#login(java.lang.String, java.lang.String)
    */
   @Override
   public void login(String login, String password, AsyncRequestCallback<String> callback) throws RequestException
   {
      String url = restServiceContext + LOGIN_PATH;

      HashMap<String, String> credentials = new HashMap<String, String>();
      credentials.put(Constants.EMAIL, login);
      credentials.put(Constants.PASSWORD, password);
      CredentailsMarshaller marshaller = new CredentailsMarshaller(credentials);
      
      AsyncRequest.build(RequestBuilder.POST, url).loader(loader).data(marshaller.marshal())
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
   }

   @Override
   public void logout(AsyncRequestCallback<String> callback) throws RequestException
   {
      String url = restServiceContext + LOGOUT_PATH;
      AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.heroku.client.HerokuClientService#createApplication(java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void createApplication(String applicationName, String vfsId, String projectid, String remoteName,
      HerokuAsyncRequestCallback callback) throws RequestException
   {
      String url = restServiceContext + CREATE_APPLICATION;
      String params = (applicationName != null && !applicationName.isEmpty()) ? "name=" + applicationName + "&" : "";
      params += (remoteName != null && !remoteName.trim().isEmpty()) ? "remote=" + remoteName + "&" : "";
      params += (vfsId != null && !vfsId.trim().isEmpty()) ? "vfsid=" + vfsId + "&" : "";
      params += (projectid != null && !projectid.trim().isEmpty()) ? "projectid=" + projectid : "";

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, true).loader(loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).requestStatusHandler(new CreateRequestHandler()).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.heroku.client.HerokuClientService#deleteApplication(java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void deleteApplication(String applicationName, String vfsId, String projectid,
      HerokuAsyncRequestCallback callback) throws RequestException
   {
      String url = restServiceContext + DESTROY_APPLICATION;
      String params = (applicationName != null) ? "name=" + applicationName + "&" : "";
      params += (vfsId != null && !vfsId.trim().isEmpty()) ? "vfsid=" + vfsId + "&" : "";
      params += (projectid != null && !projectid.trim().isEmpty()) ? "projectid=" + projectid : "";

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.heroku.client.HerokuClientService#addKey()
    */
   @Override
   public void addKey(HerokuAsyncRequestCallback callback) throws RequestException
   {
      String url = restServiceContext + ADD_KEY;
      AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.heroku.client.HerokuClientService#clearKeys(org.exoplatform.ide.extension.heroku.client.HerokuAsyncRequestCallback)
    */
   @Override
   public void clearKeys(HerokuAsyncRequestCallback callback) throws RequestException
   {
      String url = restServiceContext + CLEAR_KEYS;
      AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.heroku.client.HerokuClientService#getApplicationInfo(java.lang.String, java.lang.String, org.exoplatform.ide.extension.heroku.client.HerokuAsyncRequestCallback)
    */
   @Override
   public void getApplicationInfo(String applicationName, String vfsId, String projectid, boolean isRaw,
      HerokuAsyncRequestCallback callback) throws RequestException
   {
      String url = restServiceContext + APPLICATION_INFO;

      String params = (applicationName != null) ? "name=" + applicationName + "&" : "";
      params += (vfsId != null && !vfsId.trim().isEmpty()) ? "vfsid=" + vfsId + "&" : "";
      params += (projectid != null && !projectid.trim().isEmpty()) ? "projectid=" + projectid : "";

      AsyncRequest.build(RequestBuilder.GET, url + "?" + params).loader(loader)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.heroku.client.HerokuClientService#renameApplication(java.lang.String, java.lang.String, java.lang.String, org.exoplatform.ide.extension.heroku.client.HerokuAsyncRequestCallback)
    */
   @Override
   public void renameApplication(String applicationName, String vfsId, String projectid, String newName,
      HerokuAsyncRequestCallback callback) throws RequestException
   {
      String url = restServiceContext + RENAME_APPLICATION;

      String params = (applicationName != null) ? "name=" + applicationName + "&" : "";
      params = (newName != null) ? "newname=" + newName + "&" : "";
      params += (vfsId != null && !vfsId.trim().isEmpty()) ? "vfsid=" + vfsId + "&" : "";
      params += (projectid != null && !projectid.trim().isEmpty()) ? "projectid=" + projectid : "";

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.heroku.client.HerokuClientService#run(java.lang.String, java.lang.String, java.lang.String, org.exoplatform.ide.extension.heroku.client.HerokuAsyncRequestCallback)
    */
   @Override
   public void run(String applicationName, String vfsId, String projectid, String command,
      RakeCommandAsyncRequestCallback callback) throws RequestException
   {
      String url = restServiceContext + RUN;

      String params = (applicationName != null && !applicationName.isEmpty()) ? "name=" + applicationName + "&" : "";
      params += (vfsId != null && !vfsId.trim().isEmpty()) ? "vfsid=" + vfsId + "&" : "";
      params += (projectid != null && !projectid.trim().isEmpty()) ? "projectid=" + projectid : "";

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).header(HTTPHeader.ACCEPT, MimeType.TEXT_PLAIN)
         .header(HTTPHeader.CONTENT_TYPE, MimeType.TEXT_PLAIN).loader(loader).data(command).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.heroku.client.HerokuClientService#help(java.lang.String, java.lang.String, org.exoplatform.ide.extension.heroku.client.RakeCommandAsyncRequestCallback)
    */
   @Override
   public void help(String applicationName, String vfsId, String projectid, RakeCommandAsyncRequestCallback callback)
      throws RequestException
   {
      String url = restServiceContext + RUN;

      String params = (applicationName != null && !applicationName.isEmpty()) ? "name=" + applicationName + "&" : "";
      params += (vfsId != null && !vfsId.trim().isEmpty()) ? "vfsid=" + vfsId + "&" : "";
      params += (projectid != null && !projectid.trim().isEmpty()) ? "projectid=" + projectid : "";

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).header(HTTPHeader.ACCEPT, MimeType.TEXT_PLAIN)
         .header(HTTPHeader.CONTENT_TYPE, MimeType.TEXT_PLAIN).loader(loader).data("rake -H").send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.heroku.client.HerokuClientService#getStackList(java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getStackList(String applicationName, String vfsId, String projectid,
      StackListAsyncRequestCallback callback) throws RequestException
   {
      String url = restServiceContext + GET_STACKS;

      String params = (applicationName != null && !applicationName.isEmpty()) ? "name=" + applicationName + "&" : "";
      params += (vfsId != null && !vfsId.trim().isEmpty()) ? "vfsid=" + vfsId + "&" : "";
      params += (projectid != null && !projectid.trim().isEmpty()) ? "projectid=" + projectid : "";

      AsyncRequest.build(RequestBuilder.GET, url + "?" + params).loader(loader)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.heroku.client.HerokuClientService#migrateStack(java.lang.String, java.lang.String, java.lang.String, org.exoplatform.ide.extension.heroku.client.StackListAsyncRequestCallback)
    */
   @Override
   public void migrateStack(String applicationName, String vfsId, String projectid, String stack,
      StackMigrationAsyncRequestCallback callback) throws RequestException
   {
      String url = restServiceContext + STACK_MIGRATE;

      String params = (applicationName != null && !applicationName.isEmpty()) ? "name=" + applicationName + "&" : "";
      params = (stack != null && !stack.isEmpty()) ? "stack=" + stack + "&" : "";
      params += (vfsId != null && !vfsId.trim().isEmpty()) ? "vfsid=" + vfsId + "&" : "";
      params += (projectid != null && !projectid.trim().isEmpty()) ? "projectid=" + projectid : "";

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.heroku.client.HerokuClientService#logs(java.lang.String, java.lang.String, org.exoplatform.ide.extension.heroku.client.LogsAsyncRequestCallback)
    */
   @Override
   public void logs(String applicationName, String vfsId, String projectid, int logLines,
      LogsAsyncRequestCallback callback) throws RequestException
   {
      String url = restServiceContext + LOGS;

      String params = (applicationName != null && !applicationName.isEmpty()) ? "name=" + applicationName + "&" : "";
      params += "num=" + logLines + "&";
      params += (vfsId != null && !vfsId.trim().isEmpty()) ? "vfsid=" + vfsId + "&" : "";
      params += (projectid != null && !projectid.trim().isEmpty()) ? "projectid=" + projectid : "";

      AsyncRequest.build(RequestBuilder.GET, url + "?" + params).loader(loader).send(callback);
   }

}
