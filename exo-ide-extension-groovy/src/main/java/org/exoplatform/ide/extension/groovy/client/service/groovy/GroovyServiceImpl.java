/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.extension.groovy.client.service.groovy;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestBuilder.Method;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPMethod;
import org.exoplatform.ide.extension.groovy.client.service.RestServiceOutput;
import org.exoplatform.ide.extension.groovy.client.service.SimpleParameterEntry;
import org.exoplatform.ide.extension.groovy.client.service.groovy.marshal.ClassPath;
import org.exoplatform.ide.extension.groovy.client.service.groovy.marshal.ClassPathUnmarshaller;
import org.exoplatform.ide.extension.groovy.client.service.groovy.marshal.RestServiceOutputUnmarshaller;

import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class GroovyServiceImpl extends GroovyService
{

   public static final String SERVICE_PATH = "/ide/groovy";

   public static final String DEPLOY = "/deploy";
   
   public static final String DEPLOY_SANDBOX = "/deploy-sandbox";

   public static final String UNDEPLOY = "/undeploy";

   public static final String UNDEPLOY_SANDBOX = "/undeploy-sandbox";

   public static final String VALIDATE = "/validate-script";
   
   public static final String CLASSPATH_LOCATION = "/classpath-location";

   private HandlerManager eventBus;
   
   private String restServiceContext;

   private Loader loader;

   public GroovyServiceImpl(HandlerManager eventBus, String restServiceContext, Loader loader)
   {
      this.eventBus = eventBus;
      this.restServiceContext = restServiceContext;
      this.loader = loader;
   }

  
   /**
    * {@inheritDoc}
    */
   @Override
   public void deploy(String href, GroovyDeployUndeployCallback groovyCallback)
   {
      String deployUrl = restServiceContext + SERVICE_PATH + DEPLOY;
      deploy(href, deployUrl, groovyCallback);
   }
   
   @Override
   public void deploySandbox(String href, GroovyDeployUndeployCallback groovyCallback)
   {
      final String deployUrl = restServiceContext + SERVICE_PATH + DEPLOY_SANDBOX;
      deploy(href, deployUrl, groovyCallback);
   }
   
 
   /**
    * @param href - href of source to deploy
    * @param deployUrl - url to deploy (production or sandbox)
    * @param groovyCallback - the callback code which the user has to implement
    */
   private void deploy(String href, String deployUrl, GroovyDeployUndeployCallback groovyCallback)
   {
      groovyCallback.setHref(href);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, groovyCallback);
      AsyncRequest.build(RequestBuilder.POST, deployUrl, loader).header(HTTPHeader.LOCATION, href).send(callback);
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public void undeploy(String href, GroovyDeployUndeployCallback groovyCallback)
   {
      final String udeployUrl = restServiceContext + SERVICE_PATH + UNDEPLOY;
      undeploy(href, udeployUrl, groovyCallback);
   }
   
   /**
    * @see org.exoplatform.ide.client.module.groovy.service.groovy.GroovyService#undeploySandbox(java.lang.String, org.exoplatform.ide.client.module.groovy.service.groovy.GroovyDeployUndeployCallback)
    */
   @Override
   public void undeploySandbox(String href, GroovyDeployUndeployCallback groovyCallback)
   {
      final String udeployUrl = restServiceContext + SERVICE_PATH + UNDEPLOY_SANDBOX;
      undeploy(href, udeployUrl, groovyCallback);
   }
   
   /**
    * Undeploy rest service.
    * 
    * @param href - href of source.
    * @param undeployUrl - undeploy url 
    * @param groovyCallback - the callback code which the user has to implement
    */
   private void undeploy(String href, String undeployUrl, GroovyDeployUndeployCallback groovyCallback)
   {
      groovyCallback.setHref(href);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, groovyCallback);
      AsyncRequest.build(RequestBuilder.POST, undeployUrl, loader).header(HTTPHeader.LOCATION, href).send(callback);
   }
 
   /**
    * @see org.exoplatform.ide.client.module.groovy.service.groovy.GroovyService#validate(java.lang.String, java.lang.String, java.lang.String, org.exoplatform.ide.client.module.groovy.service.groovy.GroovyValidateCallback)
    */
   @Override
   public void validate(String fileName, String fileHref, String fileContent, GroovyValidateCallback groovyCallback)
   {
      String url = restServiceContext + SERVICE_PATH + VALIDATE;
      groovyCallback.setFileName(fileName);
      groovyCallback.setFileHref(fileHref);
      
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, groovyCallback);

      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.CONTENT_TYPE, "script/groovy").header(
         HTTPHeader.LOCATION, fileHref).data(fileContent).send(callback);
   }
   

   /**
    * @see org.exoplatform.ide.client.module.groovy.service.groovy.GroovyService#getOutput(java.lang.String, java.lang.String, java.util.List, java.util.List, java.lang.String, org.exoplatform.ide.client.module.groovy.service.groovy.GroovyOutputCallback)
    */
   @Override
   public void getOutput(String url, String method, List<SimpleParameterEntry> headers,
      List<SimpleParameterEntry> params, String body, GroovyOutputCallback groovyCallback)
   {
      RestServiceOutput output = new RestServiceOutput(url, method);

      if (params != null && params.size() > 0)
      {
         if (!url.contains("?"))
         {
            url += "?";
         }
         for (SimpleParameterEntry param : params)
         {
            if (param.getName() != null && param.getName().length() != 0)
               url += param.getName() + "=" + param.getValue() + "&";
         }
         url = url.substring(0, url.lastIndexOf("&"));
      }

      Method httpMethod;
      if (method.equals(HTTPMethod.GET))
      {
         httpMethod = RequestBuilder.GET;
      }
      else if (method.equals(HTTPMethod.POST))
      {
         httpMethod = RequestBuilder.POST;
      }
      else
      {
         httpMethod = RequestBuilder.POST;
         headers.add(new SimpleParameterEntry(HTTPHeader.X_HTTP_METHOD_OVERRIDE, method));  // add X_HTTP_METHOD_OVERRIDE header for the methods like OPTION, PUT, DELETE and others. 
      }
      
      groovyCallback.setOutput(output);
      RestServiceOutputUnmarshaller unmarshaller = new RestServiceOutputUnmarshaller(output);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, groovyCallback);
      AsyncRequest request = AsyncRequest.build(httpMethod, url, loader);
      if (headers != null)
      {
         for (SimpleParameterEntry header : headers)
         {
            if (header.getName() != null && header.getName().length() != 0)
               request.header(header.getName(), header.getValue());
         }
      }
      
      //Add this code for fix bug http://jira.exoplatform.org/browse/IDE-229
      body = body.intern();
      if (body != null && body.length() > 0)
      {
         request.data(body);
      }
      request.send(callback);
   }


   /**
    * @see org.exoplatform.ide.client.module.groovy.service.groovy.GroovyService#getClassPathLocation(java.lang.String, org.exoplatform.ide.client.module.groovy.service.groovy.ClasspathCallback)
    */
   public void getClassPathLocation(String href, ClasspathCallback classpathCallback)
   {
      String url = restServiceContext + SERVICE_PATH + CLASSPATH_LOCATION;
      ClassPath classPath = new ClassPath();
      classpathCallback.setClassPath(classPath);
      ClassPathUnmarshaller unmarshaller = new ClassPathUnmarshaller(classPath);
      
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, classpathCallback);

      AsyncRequest.build(RequestBuilder.GET, url, loader).header(
         HTTPHeader.LOCATION, href).send(callback);
   }
}
