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
package org.exoplatform.ide.extension.aws.client;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.extension.aws.client.login.Credentials;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ConfigurationOptionInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.CreateApplicationRequest;
import org.exoplatform.ide.extension.aws.shared.beanstalk.SolutionStack;

import java.util.List;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 14, 2012 1:13:05 PM anya $
 * 
 */
public class BeanstalkClientServiceImpl extends BeanstalkClientService
{

   private static final String BASE_URL = "/ide/aws/beanstalk";

   private static final String LOGIN = BASE_URL + "/login";

   private static final String LOGOUT = BASE_URL + "/logout";

   private static final String SOLUTION_STACKS = BASE_URL + "/system/solution-stacks";

   private static final String SOLUTION_STACK_OPTIONS = BASE_URL + "/system/solution-stacks/options";

   private static final String APPLICATION_CREATE = BASE_URL + "/apps/create";

   private static final String APPLICATION_INFO = BASE_URL + "/apps/info";

   private static final String APPLICATION_DELETE = BASE_URL + "/apps/delete";

   private static final String APPLICATIONS = BASE_URL + "/apps";

   /**
    * REST service context.
    */
   private String restServiceContext;

   /**
    * Loader to be displayed.
    */
   private Loader loader;

   public BeanstalkClientServiceImpl(String restContext, Loader loader)
   {
      this.loader = loader;
      this.restServiceContext = restContext;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.BeanstalkClientService#login(java.lang.String, java.lang.String,
    *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void login(String accessKey, String secretKey, AsyncRequestCallback<Object> callback) throws RequestException
   {
      String url = restServiceContext + LOGIN;
      Credentials credentialsBean = AWSExtension.AUTO_BEAN_FACTORY.credentials().as();
      credentialsBean.setAccess_key(accessKey);
      credentialsBean.setSecret_key(secretKey);

      String credentials = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(credentialsBean)).getPayload();

      AsyncRequest.build(RequestBuilder.POST, url).loader(loader).data(credentials)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.BeanstalkClientService#logout(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void logout(AsyncRequestCallback<Object> callback) throws RequestException
   {
      String url = restServiceContext + LOGOUT;

      AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.BeanstalkClientService#getAvailableSolutionStacks(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getAvailableSolutionStacks(AsyncRequestCallback<List<SolutionStack>> callback) throws RequestException
   {
      String url = restServiceContext + SOLUTION_STACKS;

      AsyncRequest.build(RequestBuilder.GET, url).loader(loader).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
         .send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.BeanstalkClientService#getSolutionStackConfigurationOptions(java.lang.String,
    *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getSolutionStackConfigurationOptions(String solutionStack,
      AsyncRequestCallback<List<ConfigurationOptionInfo>> callback) throws RequestException
   {
      StringBuilder url = new StringBuilder(restServiceContext);
      url.append(SOLUTION_STACK_OPTIONS).append("?solution_stack=").append(solutionStack);

      AsyncRequest.build(RequestBuilder.GET, url.toString()).loader(loader)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.BeanstalkClientService#createApplication(java.util.Map,
    *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void createApplication(String vfsId, String projectId, CreateApplicationRequest createApplicationRequest,
      AsyncRequestCallback<ApplicationInfo> callback) throws RequestException
   {
      String url = restServiceContext + APPLICATION_CREATE;
      String data = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(createApplicationRequest)).getPayload();

      AsyncRequest.build(RequestBuilder.POST, url).loader(loader).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
         .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).data(data).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.BeanstalkClientService#getApplicationInfo(java.lang.String, java.lang.String,
    *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getApplicationInfo(String vfsId, String projectId, AsyncRequestCallback<ApplicationInfo> callback)
      throws RequestException
   {
      StringBuilder url = new StringBuilder(restServiceContext);
      url.append(APPLICATION_INFO).append("?vfsid=").append(vfsId).append("&").append("projectid").append(projectId);

      AsyncRequest.build(RequestBuilder.GET, url.toString()).loader(loader)
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.BeanstalkClientService#deleteApplication(java.lang.String, java.lang.String,
    *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void deleteApplication(String vfsId, String projectId, AsyncRequestCallback<Object> callback)
      throws RequestException
   {
      StringBuilder url = new StringBuilder(restServiceContext);
      url.append(APPLICATION_DELETE).append("?vfsid=").append(vfsId).append("&").append("projectid").append(projectId);

      AsyncRequest.build(RequestBuilder.POST, url.toString()).loader(loader).send(callback);

   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.BeanstalkClientService#getApplications(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   @Override
   public void getApplications(AsyncRequestCallback<List<ApplicationInfo>> callback) throws RequestException
   {
      String url = restServiceContext + APPLICATIONS;

      AsyncRequest.build(RequestBuilder.GET, url).loader(loader).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
         .send(callback);
   }
}
