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
import org.exoplatform.ide.extension.heroku.client.marshaller.LogsResponse;
import org.exoplatform.ide.extension.heroku.client.marshaller.LogsUnmarshaller;
import org.exoplatform.ide.extension.heroku.client.marshaller.Property;
import org.exoplatform.ide.extension.heroku.client.marshaller.RakeResultUnmarshaller;
import org.exoplatform.ide.extension.heroku.client.marshaller.StackListUnmarshaller;
import org.exoplatform.ide.extension.heroku.client.marshaller.StackMigrationResponse;
import org.exoplatform.ide.extension.heroku.client.marshaller.StackMigrationUnmarshaller;
import org.exoplatform.ide.extension.heroku.client.rake.RakeCommandResult;
import org.exoplatform.ide.extension.heroku.shared.Stack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

   @Override
   public void logout(AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + LOGOUT_PATH;
      callback.setEventBus(eventBus);

      AsyncRequest.build(RequestBuilder.POST, url, loader).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.HerokuClientService#createApplication(java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void createApplication(String applicationName, String vfsId, String path, String remoteName,
      HerokuAsyncRequestCallback callback)
   {
      String url = restServiceContext + CREATE_APPLICATION;
      String params = (applicationName != null && !applicationName.isEmpty()) ? "name=" + applicationName + "&" : "";
      params += (remoteName != null && !remoteName.trim().isEmpty()) ? "remote=" + remoteName + "&" : "";
      params += (vfsId != null && !vfsId.trim().isEmpty()) ? "vfsId=" + vfsId + "&": "";
      params += (path != null && !path.trim().isEmpty()) ? "path=" + path : "";

      List<Property> properties = new ArrayList<Property>();
      ApplicationInfoUnmarshaller unmarshaller = new ApplicationInfoUnmarshaller(properties);
      callback.setResult(properties);
      callback.setPayload(unmarshaller);

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.HerokuClientService#deleteApplication(java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void deleteApplication(String applicationName, String vfsId, String path, HerokuAsyncRequestCallback callback)
   {
      String url = restServiceContext + DESTROY_APPLICATION;
      String params = (applicationName != null) ? "name=" + applicationName + "&" : "";
      params += (vfsId != null && !vfsId.trim().isEmpty()) ? "vfsId=" + vfsId + "&": "";
      params += (path != null && !path.trim().isEmpty()) ? "path=" + path : "";
      
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
   public void getApplicationInfo(String applicationName, String vfsId, String path, boolean isRaw,
      HerokuAsyncRequestCallback callback)
   {
      String url = restServiceContext + APPLICATION_INFO;

      String params = (applicationName != null) ? "name=" + applicationName + "&" : "";
      params += (vfsId != null && !vfsId.trim().isEmpty()) ? "vfsId=" + vfsId + "&": "";
      params += (path != null && !path.trim().isEmpty()) ? "path=" + path : "";
      
      List<Property> properties = new ArrayList<Property>();
      ApplicationInfoUnmarshaller unmarshaller = new ApplicationInfoUnmarshaller(properties);
      callback.setResult(properties);
      callback.setPayload(unmarshaller);

      AsyncRequest.build(RequestBuilder.GET, url + "?" + params, loader)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.HerokuClientService#renameApplication(java.lang.String, java.lang.String, java.lang.String, org.exoplatform.ide.extension.heroku.client.HerokuAsyncRequestCallback)
    */
   @Override
   public void renameApplication(String applicationName, String vfsId, String path, String newName,
      HerokuAsyncRequestCallback callback)
   {
      String url = restServiceContext + RENAME_APPLICATION;

      String params = (applicationName != null) ? "name=" + applicationName + "&" : "";
      params = (newName != null) ? "newname=" + newName + "&" : "";
      params += (vfsId != null && !vfsId.trim().isEmpty()) ? "vfsId=" + vfsId + "&": "";
      params += (path != null && !path.trim().isEmpty()) ? "path=" + path : "";
      
      List<Property> properties = new ArrayList<Property>();
      ApplicationInfoUnmarshaller unmarshaller = new ApplicationInfoUnmarshaller(properties);
      callback.setResult(properties);
      callback.setPayload(unmarshaller);

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.HerokuClientService#run(java.lang.String, java.lang.String, java.lang.String, org.exoplatform.ide.extension.heroku.client.HerokuAsyncRequestCallback)
    */
   @Override
   public void run(String applicationName, String vfsId, String path, String command, RakeCommandAsyncRequestCallback callback)
   {
      String url = restServiceContext + RUN;

      String params = (applicationName != null && !applicationName.isEmpty()) ? "name=" + applicationName + "&" : "";
      params += (vfsId != null && !vfsId.trim().isEmpty()) ? "vfsId=" + vfsId + "&": "";
      params += (path != null && !path.trim().isEmpty()) ? "path=" + path : "";
      
      RakeCommandResult rakeCommandResult = new RakeCommandResult();
      RakeResultUnmarshaller unmarshaller = new RakeResultUnmarshaller(rakeCommandResult);

      callback.setResult(rakeCommandResult);
      callback.setPayload(unmarshaller);

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader)
         .header(HTTPHeader.ACCEPT, MimeType.TEXT_PLAIN).header(HTTPHeader.CONTENT_TYPE, MimeType.TEXT_PLAIN)
         .data(command).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.HerokuClientService#help(java.lang.String, java.lang.String, org.exoplatform.ide.extension.heroku.client.RakeCommandAsyncRequestCallback)
    */
   @Override
   public void help(String applicationName, String vfsId, String path, RakeCommandAsyncRequestCallback callback)
   {
      String url = restServiceContext + RUN;

      String params = (applicationName != null && !applicationName.isEmpty()) ? "name=" + applicationName + "&" : "";
      params += (vfsId != null && !vfsId.trim().isEmpty()) ? "vfsId=" + vfsId + "&": "";
      params += (path != null && !path.trim().isEmpty()) ? "path=" + path : "";
      
      RakeCommandResult rakeCommandResult = new RakeCommandResult();
      RakeResultUnmarshaller unmarshaller = new RakeResultUnmarshaller(rakeCommandResult);

      callback.setResult(rakeCommandResult);
      callback.setPayload(unmarshaller);

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader)
         .header(HTTPHeader.ACCEPT, MimeType.TEXT_PLAIN).header(HTTPHeader.CONTENT_TYPE, MimeType.TEXT_PLAIN)
         .data("rake -H").send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.HerokuClientService#getStackList(java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getStackList(String applicationName, String vfsId, String path, StackListAsyncRequestCallback callback)
   {
      String url = restServiceContext + GET_STACKS;

      String params = (applicationName != null && !applicationName.isEmpty()) ? "name=" + applicationName + "&" : "";
      params += (vfsId != null && !vfsId.trim().isEmpty()) ? "vfsId=" + vfsId + "&": "";
      params += (path != null && !path.trim().isEmpty()) ? "path=" + path : "";
      
      List<Stack> stackList = new ArrayList<Stack>();
      StackListUnmarshaller unmarshaller = new StackListUnmarshaller(stackList);

      callback.setEventBus(eventBus);
      callback.setResult(stackList);
      callback.setPayload(unmarshaller);

      AsyncRequest.build(RequestBuilder.GET, url + "?" + params, loader)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.HerokuClientService#migrateStack(java.lang.String, java.lang.String, java.lang.String, org.exoplatform.ide.extension.heroku.client.StackListAsyncRequestCallback)
    */
   @Override
   public void migrateStack(String applicationName, String vfsId, String path, String stack,
      StackMigrationAsyncRequestCallback callback)
   {
      String url = restServiceContext + STACK_MIGRATE;

      String params = (applicationName != null && !applicationName.isEmpty()) ? "name=" + applicationName + "&" : "";
      params = (stack != null && !stack.isEmpty()) ? "stack=" + stack + "&" : "";
      params += (vfsId != null && !vfsId.trim().isEmpty()) ? "vfsId=" + vfsId + "&": "";
      params += (path != null && !path.trim().isEmpty()) ? "path=" + path : "";
      
      StackMigrationResponse stackMigrationResponse = new StackMigrationResponse();
      StackMigrationUnmarshaller unmarshaller = new StackMigrationUnmarshaller(stackMigrationResponse);

      callback.setEventBus(eventBus);
      callback.setResult(stackMigrationResponse);
      callback.setPayload(unmarshaller);

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.HerokuClientService#logs(java.lang.String, java.lang.String, org.exoplatform.ide.extension.heroku.client.LogsAsyncRequestCallback)
    */
   @Override
   public void logs(String applicationName, String vfsId, String path, int logLines, LogsAsyncRequestCallback callback)
   {
      String url = restServiceContext + LOGS;

      String params = (applicationName != null && !applicationName.isEmpty()) ? "name=" + applicationName + "&" : "";
      params += "num=" + logLines + "&";
      params += (vfsId != null && !vfsId.trim().isEmpty()) ? "vfsId=" + vfsId + "&": "";
      params += (path != null && !path.trim().isEmpty()) ? "path=" + path : "";
      
      LogsResponse logsResponse = new LogsResponse();
      LogsUnmarshaller unmarshaller = new LogsUnmarshaller(logsResponse);

      callback.setEventBus(eventBus);
      callback.setResult(logsResponse);
      callback.setPayload(unmarshaller);

      AsyncRequest.build(RequestBuilder.GET, url + "?" + params, loader).send(callback);
   }

}
