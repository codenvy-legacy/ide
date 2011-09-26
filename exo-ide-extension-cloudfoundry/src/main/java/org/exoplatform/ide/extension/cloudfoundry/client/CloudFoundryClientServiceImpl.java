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
package org.exoplatform.ide.extension.cloudfoundry.client;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.extension.cloudfoundry.client.marshaller.ApplicationListUnmarshaller;
import org.exoplatform.ide.extension.cloudfoundry.client.marshaller.CloudfoundryApplicationUnmarshaller;
import org.exoplatform.ide.extension.cloudfoundry.client.marshaller.CredentailsMarshaller;
import org.exoplatform.ide.extension.cloudfoundry.client.marshaller.FrameworksUnmarshaller;
import org.exoplatform.ide.extension.cloudfoundry.client.marshaller.SystemInfoUnmarshaller;
import org.exoplatform.ide.extension.cloudfoundry.client.marshaller.TargetUnmarshaller;
import org.exoplatform.ide.extension.cloudfoundry.client.marshaller.TargetsUnmarshaller;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplication;
import org.exoplatform.ide.extension.cloudfoundry.shared.Framework;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Implementation for {@link CloudFoundryClientService}.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CloudFoundryClientServiceImpl.java Jul 12, 2011 10:25:10 AM vereshchaka $
 *
 */
public class CloudFoundryClientServiceImpl extends CloudFoundryClientService
{

   private static final String BASE_URL = "/ide/cloudfoundry";

   private static final String CREATE = BASE_URL + "/apps/create";

   private static final String FRAMEWORKS = BASE_URL + "/info/frameworks";

   private static final String START = BASE_URL + "/apps/start";

   private static final String RESTART = BASE_URL + "/apps/restart";

   private static final String DELETE = BASE_URL + "/apps/delete";

   private static final String STOP = BASE_URL + "/apps/stop";

   private static final String LOGIN = BASE_URL + "/login";

   private static final String LOGOUT = BASE_URL + "/logout";

   private static final String APPS_INFO = BASE_URL + "/apps/info";

   private static final String UPDATE = BASE_URL + "/apps/update";

   private static final String RENAME = BASE_URL + "/apps/rename";

   private static final String MAP_URL = BASE_URL + "/apps/map";

   private static final String SYSTEM_INFO_URL = BASE_URL + "/info/system";

   private static final String UNMAP_URL = BASE_URL + "/apps/unmap";

   private static final String UPDATE_MEMORY = BASE_URL + "/apps/mem";

   private static final String UPDATE_INSTANCES = BASE_URL + "/apps/instances";

   private static final String VALIDATE_ACTION = BASE_URL + "/apps/validate-action";

   private static final String APPS = BASE_URL + "/apps";
   
   private static final String TARGETS = BASE_URL + "/target/all";
   
   private static final String TARGET = BASE_URL + "/target";
   
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

   public static final String SUPPORT = "support";

   public CloudFoundryClientServiceImpl(HandlerManager eventBus, String restContext, Loader loader)
   {
      this.loader = loader;
      this.eventBus = eventBus;
      this.restServiceContext = restContext;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#create(java.lang.String, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void create(String server, String name, String type, String url, int instances, int memory, boolean nostart,
      String workDir, String war, CloudFoundryAsyncRequestCallback<CloudfoundryApplication> callback)
   {
      final String requestUrl = restServiceContext + CREATE;

      callback.setEventBus(eventBus);

      CloudfoundryApplication cloudfoundryApplication = new CloudfoundryApplication();

      CloudfoundryApplicationUnmarshaller unmarshaller =
         new CloudfoundryApplicationUnmarshaller(cloudfoundryApplication);
      callback.setPayload(unmarshaller);
      callback.setResult(cloudfoundryApplication);

      String params = "name=" + name;
      params += (server == null) ? "" : "&server=" + server;
      params += (type != null) ? "&type=" + type : "";
      params += (url != null) ? "&url=" + url : "";
      params += "&instances=" + instances;
      params += "&mem=" + memory;
      params += "&nostart=" + nostart;
      params += "&workdir=" + workDir;
      params += (war != null) ? "&war=" + war : "";

      AsyncRequest.build(RequestBuilder.POST, requestUrl + "?" + params, loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);

   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#login(java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void login(String server, String email, String password, AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + LOGIN;

      HashMap<String, String> credentials = new HashMap<String, String>();
      credentials.put("server", server);
      credentials.put("email", email);
      credentials.put("password", password);
      CredentailsMarshaller marshaller = new CredentailsMarshaller(credentials);

      callback.setEventBus(eventBus);

      AsyncRequest.build(RequestBuilder.POST, url, loader).data(marshaller)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#logout(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void logout(AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + LOGOUT;

      callback.setEventBus(eventBus);

      AsyncRequest.build(RequestBuilder.POST, url, loader).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#getApplicationInfo(java.lang.String, java.lang.String, org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback)
    */
   @Override
   public void getApplicationInfo(String workDir, String appId,
      CloudFoundryAsyncRequestCallback<CloudfoundryApplication> callback)
   {
      final String url = restServiceContext + APPS_INFO;

      String appIdParam = (appId != null) ? "name=" + appId : null;
      String workDirParam = (workDir != null) ? "workdir=" + workDir : null;
      String params = "";
      if (appIdParam != null && workDirParam != null)
      {
         params = appIdParam + "&" + workDirParam;
      }
      else
      {
         params = (appIdParam != null) ? appIdParam : workDirParam;
      }

      callback.setEventBus(eventBus);

      CloudfoundryApplication application = new CloudfoundryApplication();
      callback.setResult(application);
      CloudfoundryApplicationUnmarshaller unmarshaller = new CloudfoundryApplicationUnmarshaller(application);
      callback.setPayload(unmarshaller);

      AsyncRequest.build(RequestBuilder.GET, url + "?" + params, loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#deleteApplication(java.lang.String, java.lang.String, org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback)
    */
   @Override
   public void deleteApplication(String workDir, String appId, boolean deleteServices,
      CloudFoundryAsyncRequestCallback<String> callback)
   {
      final String url = restServiceContext + DELETE;

      String params = (appId != null) ? "name=" + appId + "&" : "";
      params += (workDir != null) ? "workdir=" + workDir + "&" : "";
      params += "delete-services=" + String.valueOf(deleteServices);

      callback.setEventBus(eventBus);

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#getFrameworks()
    */
   @Override
   public void getFrameworks(AsyncRequestCallback<List<Framework>> callback)
   {
      final String url = restServiceContext + FRAMEWORKS;

      List<Framework> frameworks = new ArrayList<Framework>();
      callback.setResult(frameworks);
      callback.setEventBus(eventBus);

      FrameworksUnmarshaller unmarshaller = new FrameworksUnmarshaller(frameworks);
      callback.setPayload(unmarshaller);

      AsyncRequest.build(RequestBuilder.GET, url, loader).header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#startApplication(java.lang.String, java.lang.String)
    */
   @Override
   public void startApplication(String workDir, String name,
      CloudFoundryAsyncRequestCallback<CloudfoundryApplication> callback)
   {
      final String url = restServiceContext + START;

      callback.setEventBus(eventBus);

      CloudfoundryApplication cloudfoundryApplication = new CloudfoundryApplication();

      CloudfoundryApplicationUnmarshaller unmarshaller =
         new CloudfoundryApplicationUnmarshaller(cloudfoundryApplication);
      callback.setPayload(unmarshaller);
      callback.setResult(cloudfoundryApplication);

      String params = (name != null) ? "name=" + name : "";
      String workDirParam = (params.isEmpty()) ? "" : "&";
      workDirParam += "workdir=" + workDir;
      params += (workDir != null) ? workDirParam : "";

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#stopApplication(java.lang.String, java.lang.String, org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback)
    */
   @Override
   public void stopApplication(String workDir, String name, CloudFoundryAsyncRequestCallback<String> callback)
   {
      final String url = restServiceContext + STOP;

      callback.setEventBus(eventBus);

      String params = (name != null) ? "name=" + name : "";
      String workDirParam = (params.isEmpty()) ? "" : "&";
      workDirParam += "workdir=" + workDir;
      params += (workDir != null) ? workDirParam : "";

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#restartApplication(java.lang.String, java.lang.String, org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback)
    */
   @Override
   public void restartApplication(String workDir, String name,
      CloudFoundryAsyncRequestCallback<CloudfoundryApplication> callback)
   {
      final String url = restServiceContext + RESTART;

      callback.setEventBus(eventBus);

      CloudfoundryApplication cloudfoundryApplication = new CloudfoundryApplication();

      CloudfoundryApplicationUnmarshaller unmarshaller =
         new CloudfoundryApplicationUnmarshaller(cloudfoundryApplication);
      callback.setPayload(unmarshaller);
      callback.setResult(cloudfoundryApplication);

      String params = (name != null) ? "name=" + name : "";
      String workDirParam = (params.isEmpty()) ? "" : "&";
      workDirParam += "workdir=" + workDir;
      params += (workDir != null) ? workDirParam : "";

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#updateApplication(java.lang.String, java.lang.String, java.lang.String, org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback)
    */
   @Override
   public void updateApplication(String workDir, String name, String war,
      CloudFoundryAsyncRequestCallback<String> callback)
   {
      final String url = restServiceContext + UPDATE;
      callback.setEventBus(eventBus);

      String result = (workDir != null) ? workDir : name;
      callback.setResult(result);

      String params = (name != null) ? "name=" + name : "";
      String workDirParam = (params.isEmpty()) ? "" : "&";
      workDirParam += "workdir=" + workDir;
      params += (workDir != null) ? workDirParam : "";
      params += (war != null) ? "&war=" + war : "";

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#renameApplication(java.lang.String, java.lang.String, java.lang.String, org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback)
    */
   @Override
   public void renameApplication(String workDir, String name, String newName,
      CloudFoundryAsyncRequestCallback<String> callback)
   {
      final String url = restServiceContext + RENAME;

      String params = (name != null) ? "name=" + name + "&" : "";
      params += (workDir != null) ? "workdir=" + workDir + "&" : "";
      params += "newname=" + newName;

      callback.setEventBus(eventBus);

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#mapUrl(java.lang.String, java.lang.String, java.lang.String, org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback)
    */
   @Override
   public void mapUrl(String workDir, String name, String url, CloudFoundryAsyncRequestCallback<String> callback)
   {
      final String requestUrl = restServiceContext + MAP_URL;

      String params = (name != null) ? "name=" + name + "&" : "";
      params += (workDir != null) ? "workdir=" + workDir + "&" : "";
      params += "url=" + url;

      callback.setEventBus(eventBus);

      AsyncRequest.build(RequestBuilder.POST, requestUrl + "?" + params, loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#unmapUrl(java.lang.String, java.lang.String, java.lang.String, org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback)
    */
   @Override
   public void unmapUrl(String workDir, String name, String url, CloudFoundryAsyncRequestCallback<String> callback)
   {
      final String requestUrl = restServiceContext + UNMAP_URL;

      String params = (name != null) ? "name=" + name + "&" : "";
      params += (workDir != null) ? "workdir=" + workDir + "&" : "";
      params += "url=" + url;

      callback.setEventBus(eventBus);
      
      callback.setResult(url);

      AsyncRequest.build(RequestBuilder.POST, requestUrl + "?" + params, loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#updateMemory(java.lang.String, java.lang.String, int, org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback)
    */
   @Override
   public void updateMemory(String workDir, String name, int mem, CloudFoundryAsyncRequestCallback<String> callback)
   {
      final String url = restServiceContext + UPDATE_MEMORY;

      String params = (name != null) ? "name=" + name + "&" : "";
      params += (workDir != null) ? "workdir=" + workDir + "&" : "";
      params += "mem=" + mem;

      callback.setEventBus(eventBus);

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#updateInstances(java.lang.String, java.lang.String, java.lang.String, org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback)
    */
   @Override
   public void updateInstances(String workDir, String name, String expression,
      CloudFoundryAsyncRequestCallback<String> callback)
   {
      final String url = restServiceContext + UPDATE_INSTANCES;

      String params = (name != null) ? "name=" + name + "&" : "";
      params += (workDir != null) ? "workdir=" + workDir + "&" : "";
      params += "expr=" + expression;

      callback.setEventBus(eventBus);

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#validateAction(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback)
    */
   @Override
   public void validateAction(String action, String server, String appName, String framework, String url,
      String workDir, int instances, int memory, boolean nostart, CloudFoundryAsyncRequestCallback<String> callback)
   {
      final String postUrl = restServiceContext + VALIDATE_ACTION;

      String params = "action=" + action;
      params += (server == null) ? "" : "&server=" + server;
      params += (appName == null) ? "" : "&name=" + appName;
      params += (framework == null) ? "" : "&type=" + framework;
      params += (url == null) ? "" : "&url=" + url;
      params += "&instances=" + instances;
      params += "&mem=" + memory;
      params += "&nostart=" + nostart;
      params += (workDir == null) ? "" : "&workdir=" + workDir;

      callback.setEventBus(eventBus);

      AsyncRequest.build(RequestBuilder.POST, postUrl + "?" + params, loader).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#checkFileExists(java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void checkFileExists(String location, AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + "/ide/discovery/find/file?location=" + location;

      callback.setEventBus(eventBus);

      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#getSystemInfo(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getSystemInfo(String server, AsyncRequestCallback<SystemInfo> callback)
   {
      final String url = restServiceContext + SYSTEM_INFO_URL;

      String params = (server == null) ? "" : "?server=" + server;
      
      SystemInfo systemInfo = new SystemInfo();
      SystemInfoUnmarshaller unmarshaller = new SystemInfoUnmarshaller(systemInfo);
      
      callback.setResult(systemInfo);
      callback.setPayload(unmarshaller);
      callback.setEventBus(eventBus);
      
      AsyncRequest.build(RequestBuilder.GET, url + params, loader).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
         .send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#getApplicationList(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getApplicationList(String server, CloudFoundryAsyncRequestCallback<List<CloudfoundryApplication>> callback)
   {
      String url = restServiceContext + APPS;
      
      if (server != null && !server.isEmpty())
      {
         url += "?server=" + server;
      }
      
      callback.setEventBus(eventBus);
      List<CloudfoundryApplication> apps = new ArrayList<CloudfoundryApplication>();
      callback.setPayload(new ApplicationListUnmarshaller(apps));
      callback.setResult(apps);
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#getTargets(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getTargets(AsyncRequestCallback<List<String>> callback)
   {
      String url = restServiceContext + TARGETS;
      callback.setEventBus(eventBus);
      List<String> targes = new ArrayList<String>();
      callback.setResult(targes);
      TargetsUnmarshaller unmarshaller = new TargetsUnmarshaller(targes);
      callback.setPayload(unmarshaller);
      
      AsyncRequest.build(RequestBuilder.GET, url, loader).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
      .send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#getTarget(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getTarget(AsyncRequestCallback<StringBuilder> callback)
   {
      String url = restServiceContext + TARGET;
      
      callback.setEventBus(eventBus);
      StringBuilder target = new StringBuilder();
      callback.setResult(target);
      TargetUnmarshaller unmarshaller = new TargetUnmarshaller(target);
      callback.setPayload(unmarshaller);
      
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }

}
