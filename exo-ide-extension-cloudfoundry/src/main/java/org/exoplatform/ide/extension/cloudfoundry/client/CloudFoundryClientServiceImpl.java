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

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.marshaller.CredentailsMarshaller;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplication;
import org.exoplatform.ide.extension.cloudfoundry.shared.Framework;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemInfo;

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
    * REST service context.
    */
   private String restServiceContext;

   /**
    * Loader to be displayed.
    */
   private Loader loader;

   public static final String SUPPORT = "support";

   public CloudFoundryClientServiceImpl(String restContext, Loader loader)
   {
      this.loader = loader;
      this.restServiceContext = restContext;
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#create(java.lang.String, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void create(String server, String name, String type, String url, int instances, int memory, boolean nostart,
      String vfsId, String projectId, String war, CloudFoundryAsyncRequestCallback<CloudfoundryApplication> callback) throws RequestException
   {
      final String requestUrl = restServiceContext + CREATE;

      String params = "name=" + name;
      params += (server == null) ? "" : "&server=" + server;
      params += (type != null) ? "&type=" + type : "";
      params += (url != null) ? "&url=" + url : "";
      params += "&instances=" + instances;
      params += "&mem=" + memory;
      params += "&nostart=" + nostart;
      params += (vfsId != null) ? "&vfsid=" + vfsId : "";
      params += (projectId != null) ? "&projectid=" + projectId : "";
      params += (war != null) ? "&war=" + war : "";

      AsyncRequest.build(RequestBuilder.POST, requestUrl + "?" + params).loader(loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);

   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#login(java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void login(String server, String email, String password, AsyncRequestCallback<String> callback) throws RequestException
   {
      String url = restServiceContext + LOGIN;

      HashMap<String, String> credentials = new HashMap<String, String>();
      credentials.put("server", server);
      credentials.put("email", email);
      credentials.put("password", password);
      CredentailsMarshaller marshaller = new CredentailsMarshaller(credentials);


      AsyncRequest.build(RequestBuilder.POST, url).loader(loader).data(marshaller.marshal())
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#logout(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void logout(String server, AsyncRequestCallback<String> callback) throws RequestException
   {
      String url = restServiceContext + LOGOUT;

      String params = (server != null) ? "?server=" + server : "";

      AsyncRequest.build(RequestBuilder.POST, url + params).loader(loader).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#getApplicationInfo(java.lang.String, java.lang.String, org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback)
    */
   @Override
   public void getApplicationInfo(String vfsId, String projectId, String appId, String server,
      CloudFoundryAsyncRequestCallback<CloudfoundryApplication> callback) throws RequestException
   {
      final String url = restServiceContext + APPS_INFO;

      String params = (appId != null) ? "name=" + appId : "";
      params += (vfsId != null) ? "&vfsid=" + vfsId : "";
      params += (projectId != null) ? "&projectid=" + projectId : "";
      params += (server != null) ? "&server=" + server : "";
      params = (params.startsWith("&")) ? params.substring(1) : params;

      AsyncRequest.build(RequestBuilder.GET, url + "?" + params).loader(loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#deleteApplication(java.lang.String, java.lang.String, org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback)
    */
   @Override
   public void deleteApplication(String vfsId, String projectId, String appId, String server, boolean deleteServices,
      CloudFoundryAsyncRequestCallback<String> callback) throws RequestException
   {
      final String url = restServiceContext + DELETE;

      String params = (appId != null) ? "name=" + appId + "&" : "";
      params += (vfsId != null) ? "vfsid=" + vfsId + "&" : "";
      params += (projectId != null) ? "projectid=" + projectId + "&" : "";
      params += (server != null) ? "server=" + server + "&" : "";
      params += "delete-services=" + String.valueOf(deleteServices);

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#getFrameworks()
    */
   @Override
   public void getFrameworks(AsyncRequestCallback<List<Framework>> callback) throws RequestException
   {
      final String url = restServiceContext + FRAMEWORKS;

      AsyncRequest.build(RequestBuilder.GET, url).loader(loader).header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#startApplication(java.lang.String, java.lang.String)
    */
   @Override
   public void startApplication(String vfsId, String projectId, String name, String server,
      CloudFoundryAsyncRequestCallback<CloudfoundryApplication> callback) throws RequestException
   {
      final String url = restServiceContext + START;

      String params = (name != null) ? "name=" + name : "";
      params += (vfsId != null) ? "&vfsid=" + vfsId : "";
      params += (projectId != null) ? "&projectid=" + projectId : "";
      params += (server != null) ? "&server=" + server : "";
      params = (params.startsWith("&")) ? params.substring(1) : params;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#stopApplication(java.lang.String, java.lang.String, org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback)
    */
   @Override
   public void stopApplication(String vfsId, String projectId, String name, String server,
      CloudFoundryAsyncRequestCallback<String> callback) throws RequestException
   {
      final String url = restServiceContext + STOP;

      String params = (name != null) ? "name=" + name : "";
      params += (vfsId != null) ? "&vfsid=" + vfsId : "";
      params += (projectId != null) ? "&projectid=" + projectId : "";
      params += (server != null) ? "&server=" + server : "";
      params = (params.startsWith("&")) ? params.substring(1) : params;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#restartApplication(java.lang.String, java.lang.String, org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback)
    */
   @Override
   public void restartApplication(String vfsId, String projectId, String name, String server,
      CloudFoundryAsyncRequestCallback<CloudfoundryApplication> callback) throws RequestException
   {
      final String url = restServiceContext + RESTART;

      String params = (name != null) ? "name=" + name : "";
      params += (vfsId != null) ? "&vfsid=" + vfsId : "";
      params += (projectId != null) ? "&projectid=" + projectId : "";
      params += (server != null) ? "&server=" + server : "";
      params = (params.startsWith("&")) ? params.substring(1) : params;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#updateApplication(java.lang.String, java.lang.String, java.lang.String, org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback)
    */
   @Override
   public void updateApplication(String vfsId, String projectId, String name, String server, String war,
      CloudFoundryAsyncRequestCallback<String> callback) throws RequestException
   {
      final String url = restServiceContext + UPDATE;

      String params = (name != null) ? "name=" + name : "";
      params += (vfsId != null) ? "&vfsid=" + vfsId : "";
      params += (projectId != null) ? "&projectid=" + projectId : "";
      params += (server != null) ? "&server=" + server : "";
      params += (war != null) ? "&war=" + war : "";
      params = (params.startsWith("&")) ? params.substring(1) : params;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#renameApplication(java.lang.String, java.lang.String, java.lang.String, org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback)
    */
   @Override
   public void renameApplication(String vfsId, String projectId, String name, String server, String newName,
      CloudFoundryAsyncRequestCallback<String> callback) throws RequestException
   {
      final String url = restServiceContext + RENAME;

      String params = (name != null) ? "name=" + name + "&" : "";
      params += (vfsId != null) ? "vfsid=" + vfsId + "&" : "";
      params += (projectId != null) ? "projectid=" + projectId + "&" : "";
      params += (server != null) ? "server=" + server + "&" : "";
      params += "newname=" + newName;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#mapUrl(java.lang.String, java.lang.String, java.lang.String, org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback)
    */
   @Override
   public void mapUrl(String vfsId, String projectId, String name, String server, String url,
      CloudFoundryAsyncRequestCallback<String> callback) throws RequestException
   {
      final String requestUrl = restServiceContext + MAP_URL;

      String params = (name != null) ? "name=" + name + "&" : "";
      params += (vfsId != null) ? "vfsid=" + vfsId + "&" : "";
      params += (projectId != null) ? "projectid=" + projectId + "&" : "";
      params += (server != null) ? "server=" + server + "&" : "";
      params += "url=" + url;

      AsyncRequest.build(RequestBuilder.POST, requestUrl + "?" + params).loader(loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#unmapUrl(java.lang.String, java.lang.String, java.lang.String, org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback)
    */
   @Override
   public void unmapUrl(String vfsId, String projectId, String name, String server, String url,
      CloudFoundryAsyncRequestCallback<String> callback) throws RequestException
   {
      final String requestUrl = restServiceContext + UNMAP_URL;

      String params = (name != null) ? "name=" + name + "&" : "";
      params += (vfsId != null) ? "vfsid=" + vfsId + "&" : "";
      params += (projectId != null) ? "projectid=" + projectId + "&" : "";
      params += (server != null) ? "server=" + server + "&" : "";
      params += "url=" + url;

      AsyncRequest.build(RequestBuilder.POST, requestUrl + "?" + params).loader(loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#updateMemory(java.lang.String, java.lang.String, int, org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback)
    */
   @Override
   public void updateMemory(String vfsId, String projectId, String name, String server, int mem,
      CloudFoundryAsyncRequestCallback<String> callback) throws RequestException
   {
      final String url = restServiceContext + UPDATE_MEMORY;

      String params = (name != null) ? "name=" + name + "&" : "";
      params += (vfsId != null) ? "vfsid=" + vfsId + "&" : "";
      params += (projectId != null) ? "projectid=" + projectId + "&" : "";
      params += (server != null) ? "server=" + server + "&" : "";
      params += "mem=" + mem;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#updateInstances(java.lang.String, java.lang.String, java.lang.String, org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback)
    */
   @Override
   public void updateInstances(String vfsId, String projectId, String name, String server, String expression,
      CloudFoundryAsyncRequestCallback<String> callback) throws RequestException
   {
      final String url = restServiceContext + UPDATE_INSTANCES;

      String params = (name != null) ? "name=" + name + "&" : "";
      params += (vfsId != null) ? "vfsid=" + vfsId + "&" : "";
      params += (projectId != null) ? "projectid=" + projectId + "&" : "";
      params += (server != null) ? "server=" + server + "&" : "";
      params += "expr=" + expression;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#validateAction(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback)
    */
   @Override
   public void validateAction(String action, String server, String appName, String framework, String url, String vfsId,
      String projectId, int instances, int memory, boolean nostart, CloudFoundryAsyncRequestCallback<String> callback) throws RequestException
   {
      final String postUrl = restServiceContext + VALIDATE_ACTION;

      String params = "action=" + action;
      params += (appName == null) ? "" : "&name=" + appName;
      params += (framework == null) ? "" : "&type=" + framework;
      params += (url == null) ? "" : "&url=" + url;
      params += "&instances=" + instances;
      params += "&mem=" + memory;
      params += "&nostart=" + nostart;
      params += (vfsId != null) ? "&vfsid=" + vfsId : "";
      params += (projectId != null) ? "&projectid=" + projectId : "";
      params += (server != null) ? "&server=" + server : "";

      AsyncRequest.build(RequestBuilder.POST, postUrl + "?" + params).loader(loader).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#getSystemInfo(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getSystemInfo(String server, AsyncRequestCallback<SystemInfo> callback) throws RequestException
   {
      final String url = restServiceContext + SYSTEM_INFO_URL;

      String params = (server == null) ? "" : "?server=" + server;

      AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
         .send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#getApplicationList(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getApplicationList(String server,
      CloudFoundryAsyncRequestCallback<List<CloudfoundryApplication>> callback) throws RequestException
   {
      String url = restServiceContext + APPS;

      if (server != null && !server.isEmpty())
      {
         url += "?server=" + server;
      }

      AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#getTargets(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getTargets(AsyncRequestCallback<List<String>> callback) throws RequestException
   {
      String url = restServiceContext + TARGETS;

      AsyncRequest.build(RequestBuilder.GET, url).loader(loader).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
         .send(callback);
   }

   /**
    * @throws RequestException
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#getTarget(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getTarget(AsyncRequestCallback<StringBuilder> callback) throws RequestException
   {
      String url = restServiceContext + TARGET;

      AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
   }

}
