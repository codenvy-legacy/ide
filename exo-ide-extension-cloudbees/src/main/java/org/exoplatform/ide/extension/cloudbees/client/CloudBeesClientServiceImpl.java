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

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.extension.cloudbees.client.info.ApplicationInfo;
import org.exoplatform.ide.extension.cloudbees.client.marshaller.CredentailsMarshaller;

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

   private static final String DEPLOY_WAR = BASE_URL + "/apps/create";

   private static final String APPS_INFO = BASE_URL + "/apps/info";

   private static final String APPS_DELETE = BASE_URL + "/apps/delete";

   private static final String APPS_UPDATE = BASE_URL + "/apps/update";

   private static final String LOGIN = BASE_URL + "/login";

   private static final String LOGOUT = BASE_URL + "/logout";

   private static final String INITIALIZE = BASE_URL + "/apps/create";

   private static final String APP_LIST = BASE_URL + "/apps/all";

   /**
    * REST service context.
    */
   private String restServiceContext;

   /**
    * Loader to be displayed.
    */
   private Loader loader;

   public CloudBeesClientServiceImpl(String restContext, Loader loader)
   {
      this.loader = loader;
      this.restServiceContext = restContext;
   }

   /**
    * @throws RequestException
    * @see org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService#deployWar(java.lang.String, java.lang.String,
    *      java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void deployWar(String appId, String warFile, String message,
      CloudBeesAsyncRequestCallback<Map<String, String>> callback) throws RequestException
   {
      final String url = restServiceContext + DEPLOY_WAR;

      String params = "appid=" + appId + "&";
      params += "war=" + warFile;
      if (message != null && !message.isEmpty())
         params += "&message=" + message;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService#getDomains(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getDomains(CloudBeesAsyncRequestCallback<List<String>> callback) throws RequestException
   {
      final String url = restServiceContext + DOMAINS;

      AsyncRequest.build(RequestBuilder.GET, url).loader(loader).header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService#login(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void login(String email, String password, AsyncRequestCallback<String> callback) throws RequestException
   {
      String url = restServiceContext + LOGIN;

      HashMap<String, String> credentials = new HashMap<String, String>();
      credentials.put("email", email);
      credentials.put("password", password);
      CredentailsMarshaller marshaller = new CredentailsMarshaller(credentials);

      AsyncRequest.build(RequestBuilder.POST, url).loader(loader).data(marshaller.marshal())
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService#logout(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void logout(AsyncRequestCallback<String> callback) throws RequestException
   {
      String url = restServiceContext + LOGOUT;

      AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService#getApplicationInfo(java.lang.String,
    *      java.lang.String, org.exoplatform.ide.extension.cloudbees.client.CloudBeesAsyncRequestCallback)
    */
   @Override
   public void getApplicationInfo(String appId, String vfsId, String projectId,
      CloudBeesAsyncRequestCallback<Map<String, String>> callback) throws RequestException
   {
      final String url = restServiceContext + APPS_INFO;

      String params = (appId != null) ? "appid=" + appId + "&" : "";
      params += "vfsid=" + vfsId;
      params += (projectId != null) ? "&projectid=" + projectId : "";

      AsyncRequest.build(RequestBuilder.GET, url + "?" + params).loader(loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService#deleteApplication(java.lang.String,
    *      java.lang.String, org.exoplatform.ide.extension.cloudbees.client.CloudBeesAsyncRequestCallback)
    */
   @Override
   public void deleteApplication(String appId, String vfsId, String projectId,
      CloudBeesAsyncRequestCallback<String> callback) throws RequestException
   {
      final String url = restServiceContext + APPS_DELETE;

      List<String> paramList = new ArrayList<String>();
      if (appId != null)
         paramList.add("appid=" + appId);
      if (vfsId != null)
         paramList.add("vfsid=" + vfsId);
      if (projectId != null)
         paramList.add("projectid=" + projectId);

      String params;

      if (paramList.size() < 1)
      {
         params = "";
      }
      else
      {
         params = "?" + paramList.get(0);
         if (paramList.size() > 1)
         {
            for (int i = 1; i < paramList.size(); i++)
            {
               params += "&" + paramList.get(i);
            }
         }
      }

      AsyncRequest.build(RequestBuilder.POST, url + params).loader(loader).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService#initializeApplication(java.lang.String,
    *      java.lang.String, java.lang.String, org.exoplatform.ide.extension.cloudbees.client.CloudBeesAsyncRequestCallback)
    */
   @Override
   public void initializeApplication(String appId, String vfsId, String projectId, String warFile, String message,
      CloudBeesAsyncRequestCallback<Map<String, String>> callback) throws RequestException
   {
      final String url = restServiceContext + INITIALIZE;

      String params = "appid=" + appId + "&";
      params += "war=" + warFile;
      params += "&vfsid=" + vfsId;
      params += (projectId != null) ? "&projectid=" + projectId : "";
      if (message != null && !message.isEmpty())
         params += "&message=" + message;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService#applicationList(org.exoplatform.ide.extension.cloudbees.client.CloudBeesAsyncRequestCallback)
    */
   @Override
   public void applicationList(CloudBeesAsyncRequestCallback<List<ApplicationInfo>> callback) throws RequestException
   {
      final String url = restServiceContext + APP_LIST;
      
      AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService#updateApplication(java.lang.String,
    *      java.lang.String, java.lang.String, java.lang.String, java.lang.String,
    *      org.exoplatform.ide.extension.cloudbees.client.CloudBeesAsyncRequestCallback)
    */
   @Override
   public void updateApplication(String appId, String vfsId, String projectId, String warFile, String message,
      CloudBeesAsyncRequestCallback<Map<String, String>> callback) throws RequestException
   {
      final String url = restServiceContext + APPS_UPDATE;

      String params = "appid=" + appId + "&";
      params += "war=" + warFile;
      params += "&vfsid=" + vfsId;
      params += (projectId != null) ? "&projectid=" + projectId : "";
      if (message != null && !message.isEmpty())
         params += "&message=" + message;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

}
