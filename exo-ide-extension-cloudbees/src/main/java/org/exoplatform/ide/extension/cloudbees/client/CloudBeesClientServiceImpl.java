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
package org.exoplatform.ide.extension.cloudbees.client;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.extension.cloudbees.client.marshaller.CredentailsMarshaller;
import org.exoplatform.ide.extension.cloudbees.client.marshaller.DeployWarUnmarshaller;
import org.exoplatform.ide.extension.cloudbees.client.marshaller.DomainsUnmarshaller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CloudBeesServiceImpl.java Jun 23, 2011 10:11:33 AM vereshchaka $
 *
 */
public class CloudBeesClientServiceImpl extends CloudBeesClientService
{
   
   private static final String BASE_URL = "/ide/cloudbees";
   
   private static final String DOMAINS = BASE_URL + "/domains";
   
   private static final String DEPLOY_WAR = BASE_URL + "/apps/war-deploy";
   
   private static final String APPS_INFO = BASE_URL + "/apps/info";
   
   private static final String APPS_DELETE = BASE_URL + "/apps/delete";
   
   private static final String LOGIN = BASE_URL + "/login";
   
   private static final String LOGOUT = BASE_URL + "/logout";
   
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
   
   public CloudBeesClientServiceImpl(HandlerManager eventBus, String restContext, Loader loader)
   {
      this.loader = loader;
      this.eventBus = eventBus;
      this.restServiceContext = restContext;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService#deployWar(java.lang.String, java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void deployWar(String appId, String warFile, String message,
      CloudBeesAsyncRequestCallback<Map<String, String>> callback)
   {
      final String url = restServiceContext + DEPLOY_WAR;
      
      String params = "appid=" + appId + "&";
      params += "war=" + warFile;
      if (message != null && !message.isEmpty())
         params += "&message=" + message;

      Map<String, String> responseMap = new HashMap<String, String>();
      callback.setResult(responseMap);
      callback.setEventBus(eventBus);
      
      DeployWarUnmarshaller unmarshaller = new DeployWarUnmarshaller(responseMap);
      callback.setPayload(unmarshaller);

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService#getDomains(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getDomains(CloudBeesAsyncRequestCallback<List<String>> callback)
   {
      final String url = restServiceContext + DOMAINS;
      
      List<String> domains = new ArrayList<String>();
      callback.setResult(domains);
      callback.setEventBus(eventBus);
      
      DomainsUnmarshaller unmarshaller = new DomainsUnmarshaller(domains);
      callback.setPayload(unmarshaller);

      AsyncRequest.build(RequestBuilder.GET, url, loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService#login(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void login(String email, String password, AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + LOGIN;

      HashMap<String, String> credentials = new HashMap<String, String>();
      credentials.put("email", email);
      credentials.put("password", password);
      CredentailsMarshaller marshaller = new CredentailsMarshaller(credentials);

      callback.setEventBus(eventBus);

      AsyncRequest.build(RequestBuilder.POST, url, loader).data(marshaller)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService#logout(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void logout(AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + LOGOUT;
      callback.setEventBus(eventBus);
      
      AsyncRequest.build(RequestBuilder.POST, url, loader).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService#getApplicationInfo(java.lang.String, java.lang.String, org.exoplatform.ide.extension.cloudbees.client.CloudBeesAsyncRequestCallback)
    */
   @Override
   public void getApplicationInfo(String workDir, String appId,
      CloudBeesAsyncRequestCallback<Map<String, String>> callback)
   {
      final String url = restServiceContext + APPS_INFO;
      
      String params = (appId != null) ? "appid=" + appId + "&" : "";
      params += "workdir=" + workDir;

      Map<String, String> responseMap = new HashMap<String, String>();
      callback.setResult(responseMap);
      callback.setEventBus(eventBus);
      
      DeployWarUnmarshaller unmarshaller = new DeployWarUnmarshaller(responseMap);
      callback.setPayload(unmarshaller);

      AsyncRequest.build(RequestBuilder.GET, url + "?" + params, loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService#deleteApplication(java.lang.String, java.lang.String, org.exoplatform.ide.extension.cloudbees.client.CloudBeesAsyncRequestCallback)
    */
   @Override
   public void deleteApplication(String workDir, String appId,
      CloudBeesAsyncRequestCallback<String> callback)
   {
      final String url = restServiceContext + APPS_DELETE;
      
      String params = (appId != null) ? "appid=" + appId + "&" : "";
      params += "workdir=" + workDir;

      callback.setResult(null);
      callback.setEventBus(eventBus);
      
      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader)
         .send(callback);
   }

}
