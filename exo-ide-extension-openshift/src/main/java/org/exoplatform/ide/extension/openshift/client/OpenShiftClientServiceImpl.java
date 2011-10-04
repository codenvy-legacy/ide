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
package org.exoplatform.ide.extension.openshift.client;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.extension.openshift.client.marshaller.AppInfoUmarshaller;
import org.exoplatform.ide.extension.openshift.client.marshaller.ApplicationTypesUnmarshaller;
import org.exoplatform.ide.extension.openshift.client.marshaller.CredentialsMarshaller;
import org.exoplatform.ide.extension.openshift.client.marshaller.RHUserInfoUnmarshaller;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;
import org.exoplatform.ide.extension.openshift.shared.RHUserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of {@link OpenShiftClientService}.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 6, 2011 5:50:11 PM anya $
 *
 */
public class OpenShiftClientServiceImpl extends OpenShiftClientService
{
   /**
    * Login method's path.
    */
   private static final String LOGIN = "/ide/openshift/express/login";

   /**
    * Create domain method's path.
    */
   private static final String CREATE_DOMAIN = "/ide/openshift/express/domain/create";

   /**
    * Create application method's path.
    */
   private static final String CREATE_APPLICATION = "/ide/openshift/express/apps/create";

   /**
    * Destroy application method's path.
    */
   private static final String DESTROY_APPLICATION = "/ide/openshift/express/apps/destroy";

   /**
    * User info method's path.
    */
   private static final String USER_INFO = "/ide/openshift/express/user/info";

   /**
    * Get application's info method's path.
    */
   private static final String APPLICATION_INFO = "/ide/openshift/express/apps/info";

   /**
    * Types of the application method's path.
    */
   private static final String APPLICATION_TYPES = "/ide/openshift/express/apps/type";

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
   public OpenShiftClientServiceImpl(HandlerManager eventBus, String restContext, Loader loader)
   {
      this.loader = loader;
      this.eventBus = eventBus;
      this.restServiceContext = restContext;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.OpenShiftClientService#login(java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void login(String login, String password, AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + LOGIN;

      CredentialsMarshaller marshaller = new CredentialsMarshaller(login, password);
      callback.setEventBus(eventBus);

      AsyncRequest.build(RequestBuilder.POST, url, loader).data(marshaller)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.OpenShiftClientService#createDomain(java.lang.String, boolean, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void createDomain(String name, boolean alter, AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + CREATE_DOMAIN;
      callback.setEventBus(eventBus);
      String params = "?namespace=" + name + "&" + "alter=" + alter;
      AsyncRequest.build(RequestBuilder.POST, url + params, loader).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.OpenShiftClientService#createApplication(java.lang.String, java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void createApplication(String name, String vfsId, String projectId, String type,
      AsyncRequestCallback<AppInfo> callback)
   {
      String url = restServiceContext + CREATE_APPLICATION;

      AppInfo appInfo = new AppInfo();
      AppInfoUmarshaller unmarshaller = new AppInfoUmarshaller(appInfo);
      callback.setResult(appInfo);
      callback.setPayload(unmarshaller);
      callback.setEventBus(eventBus);
      String params = "?name=" + name + "&type=" + type + "&vfsid=" + vfsId + "&projectid=" + projectId;
      AsyncRequest.build(RequestBuilder.POST, url + params, loader).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.OpenShiftClientService#destroyApplication(java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void destroyApplication(String name, String vfsId, String projectId, AsyncRequestCallback<String> callback)
   {
      String url = restServiceContext + DESTROY_APPLICATION;
      callback.setEventBus(eventBus);
      String params = "?name=" + name + "&vfsid=" + vfsId + "&projectid=" + projectId;
      AsyncRequest.build(RequestBuilder.POST, url + params, loader).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.OpenShiftClientService#getUserInfo(boolean, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getUserInfo(boolean appsInfo, AsyncRequestCallback<RHUserInfo> callback)
   {
      String url = restServiceContext + USER_INFO;

      RHUserInfo userInfo = new RHUserInfo();
      RHUserInfoUnmarshaller unmarshaller = new RHUserInfoUnmarshaller(userInfo);
      callback.setResult(userInfo);
      callback.setPayload(unmarshaller);
      callback.setEventBus(eventBus);
      String params = "?appsinfo=" + appsInfo;
      AsyncRequest.build(RequestBuilder.GET, url + params, loader).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.OpenShiftClientService#getApplicationInfo(java.lang.String, java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getApplicationInfo(String applicationName, String vfsId, String projectId,
      AsyncRequestCallback<AppInfo> callback)
   {
      String url = restServiceContext + APPLICATION_INFO;

      AppInfo appInfo = new AppInfo();
      AppInfoUmarshaller unmarshaller = new AppInfoUmarshaller(appInfo);
      callback.setResult(appInfo);
      callback.setPayload(unmarshaller);
      callback.setEventBus(eventBus);
      String params = (applicationName != null && !applicationName.isEmpty()) ? "name=" + applicationName + "&" : "";
      params += "vfsid=" + vfsId + "&projectid=" + projectId;
      AsyncRequest.build(RequestBuilder.GET, url + "?" + params, loader).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.OpenShiftClientService#getApplicationTypes(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getApplicationTypes(AsyncRequestCallback<List<String>> callback)
   {
      String url = restServiceContext + APPLICATION_TYPES;

      List<String> types = new ArrayList<String>();
      ApplicationTypesUnmarshaller unmarshaller = new ApplicationTypesUnmarshaller(types);
      callback.setResult(types);
      callback.setPayload(unmarshaller);
      callback.setEventBus(eventBus);
      AsyncRequest.build(RequestBuilder.GET, url, loader).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
         .send(callback);
   }
}
