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
import org.exoplatform.ide.extension.cloudfoundry.client.marshaller.CloudfoundryApplicationUnmarshaller;
import org.exoplatform.ide.extension.cloudfoundry.client.marshaller.CredentailsMarshaller;
import org.exoplatform.ide.extension.cloudfoundry.client.marshaller.FrameworksUnmarshaller;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplication;
import org.exoplatform.ide.extension.cloudfoundry.shared.Framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
   public void create(String name, String type, String url, int instances, int memory, boolean nostart, String workDir,
      String war, CloudFoundryAsyncRequestCallback<CloudfoundryApplication> callback)
   {
      final String requestUrl = restServiceContext + CREATE;
      
      callback.setEventBus(eventBus);
      
      CloudfoundryApplication cloudfoundryApplication = new CloudfoundryApplication();
      
      CloudfoundryApplicationUnmarshaller unmarshaller = new CloudfoundryApplicationUnmarshaller(cloudfoundryApplication);
      callback.setPayload(unmarshaller);
      callback.setResult(cloudfoundryApplication);
      
      String params = "name=" + name;
      params += (type != null) ? "&type=" + type : "";
      params += (url != null) ? "&url=" + type : "";
      params += "&instances=" + instances;
      params += "&mem=" + memory;
      params += "&nostart=" + nostart;
      params += "&workdir=" + workDir;
      params += (war != null) ? "&war=" + war : "";

      AsyncRequest.build(RequestBuilder.POST, requestUrl + "?" + params, loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .send(callback);

   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#login(java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
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
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#logout(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void logout(AsyncRequestCallback<String> callback)
   {
      // TODO Auto-generated method stub
      
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#getApplicationInfo(java.lang.String, java.lang.String, org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback)
    */
   @Override
   public void getApplicationInfo(String workDir, String appId,
      CloudFoundryAsyncRequestCallback<Map<String, String>> callback)
   {
      // TODO Auto-generated method stub
      
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService#deleteApplication(java.lang.String, java.lang.String, org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback)
    */
   @Override
   public void deleteApplication(String workDir, String appId, CloudFoundryAsyncRequestCallback<String> callback)
   {
      // TODO Auto-generated method stub
      
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

      AsyncRequest.build(RequestBuilder.GET, url, loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .send(callback);
   }

}
