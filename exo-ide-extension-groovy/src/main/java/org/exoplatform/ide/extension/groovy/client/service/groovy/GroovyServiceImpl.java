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

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestBuilder.Method;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.HTTPMethod;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.copy.HTTPHeader;
import org.exoplatform.ide.extension.groovy.client.service.RestServiceOutput;
import org.exoplatform.ide.extension.groovy.client.service.SimpleParameterEntry;
import org.exoplatform.ide.extension.groovy.client.service.groovy.marshal.ClassPath;
import org.exoplatform.ide.extension.groovy.shared.Jar;
import org.exoplatform.ide.vfs.client.model.FileModel;

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

   public static final String JARS_LOCATION = "/jars";

   private String restServiceContext;

   private Loader loader;

   public GroovyServiceImpl(String restServiceContext, Loader loader)
   {
      this.restServiceContext = restServiceContext;
      this.loader = loader;
   }

   /**
    * {@inheritDoc}
    * 
    * @throws RequestException
    */
   @Override
   public void deploy(String itemId, String vfsId, String projectId, AsyncRequestCallback<String> callback)
      throws RequestException
   {
      String deployUrl = restServiceContext + SERVICE_PATH + DEPLOY;
      deploy(itemId, deployUrl, vfsId, projectId, callback);
   }

   @Override
   public void deploySandbox(String itemId, String vfsId, String projectId, AsyncRequestCallback<String> callback)
      throws RequestException
   {
      final String deployUrl = restServiceContext + SERVICE_PATH + DEPLOY_SANDBOX;
      deploy(itemId, deployUrl, vfsId, projectId, callback);
   }

   /**
    * @param href - href of source to deploy
    * @param deployUrl - url to deploy (production or sandbox)
    * @param callback - the callback code which the user has to implement
    * @throws RequestException
    */
   private void deploy(String itemId, String deployUrl, String vfsid, String projectid,
      AsyncRequestCallback<String> callback) throws RequestException
   {
      deployUrl += "?id=" + itemId + "&vfsid=" + vfsid + "&projectid=" + projectid;
      AsyncRequest.build(RequestBuilder.POST, deployUrl).loader(loader)
         .header(HTTPHeader.CONTENTTYPE, "application/x-www-form-urlencoded").send(callback);
   }

   /**
    * {@inheritDoc}
    * 
    * @throws RequestException
    */
   @Override
   public void undeploy(String itemId, String vfsId, String projectId, AsyncRequestCallback<String> callback)
      throws RequestException
   {
      final String udeployUrl = restServiceContext + SERVICE_PATH + UNDEPLOY;
      undeploy(itemId, udeployUrl, vfsId, projectId, callback);
   }

   /**
    * @throws RequestException
    * @see org.exoplatform.ide.client.module.groovy.service.groovy.GroovyService#undeploySandbox(java.lang.String,
    *      org.exoplatform.ide.client.module.groovy.service.groovy.GroovyDeployUndeployCallback)
    */
   @Override
   public void undeploySandbox(String itemId, String vfsId, String projectId, AsyncRequestCallback<String> callback)
      throws RequestException
   {
      final String udeployUrl = restServiceContext + SERVICE_PATH + UNDEPLOY_SANDBOX;
      undeploy(itemId, udeployUrl, vfsId, projectId, callback);
   }

   /**
    * Undeploy rest service.
    * 
    * @param href - href of source.
    * @param undeployUrl - undeploy url
    * @param groovyCallback - the callback code which the user has to implement
    * @throws RequestException
    */
   private void undeploy(String itemId, String undeployUrl, String vfsid, String projectid,
      AsyncRequestCallback<String> callback) throws RequestException
   {
      undeployUrl += "?id=" + itemId + "&vfsid=" + vfsid + "&projectid=" + projectid;
      AsyncRequest.build(RequestBuilder.POST, undeployUrl).loader(loader)
         .header(HTTPHeader.CONTENTTYPE, "application/x-www-form-urlencoded").send(callback);
   }

   /**
    * @throws RequestException
    * @see org.exoplatform.ide.client.module.groovy.service.groovy.GroovyService#validate(java.lang.String, java.lang.String,
    *      java.lang.String, org.exoplatform.ide.client.module.groovy.service.groovy.GroovyValidateCallback)
    */
   @Override
   public void validate(FileModel file, String vfsid, AsyncRequestCallback<Object> callback) throws RequestException
   {
      String url = restServiceContext + SERVICE_PATH + VALIDATE + "?vfsid=" + vfsid + "&name=" + file.getName();// TODO:file name
                                                                                                                // need for
                                                                                                                // unsaved file
      if (file.getProject() != null)
         url += "&projectid=" + file.getProject().getId();
      AsyncRequest.build(RequestBuilder.POST, url).loader(loader).header(HTTPHeader.CONTENT_TYPE, "script/groovy")
         .data(file.getContent()).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.client.module.groovy.service.groovy.GroovyService#getOutput(java.lang.String, java.lang.String,
    *      java.util.List, java.util.List, java.lang.String,
    *      org.exoplatform.ide.client.module.groovy.service.groovy.GroovyOutputCallback)
    */
   @Override
   public void getOutput(String url, String method, List<SimpleParameterEntry> headers,
      List<SimpleParameterEntry> params, String body, AsyncRequestCallback<RestServiceOutput> callback) throws RequestException
   {
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
         headers.add(new SimpleParameterEntry(HTTPHeader.X_HTTP_METHOD_OVERRIDE, method)); // add X_HTTP_METHOD_OVERRIDE header
                                                                                           // for the methods like OPTION, PUT,
                                                                                           // DELETE and others.
      }

      AsyncRequest request = AsyncRequest.build(httpMethod, url).loader(loader);
      if (headers != null)
      {
         for (SimpleParameterEntry header : headers)
         {
            if (header.getName() != null && header.getName().length() != 0)
               request.header(header.getName(), header.getValue());
         }
      }

      // Add this code for fix bug http://jira.exoplatform.org/browse/IDE-229
      body = body.intern();
      if (body != null && body.length() > 0)
      {
         request.data(body);
      }
      request.send(callback);
   }

   /**
    * @throws RequestException
    * @see org.exoplatform.ide.client.module.groovy.service.groovy.GroovyService#getClassPathLocation(java.lang.String,
    *      org.exoplatform.ide.client.module.groovy.service.groovy.ClasspathCallback)
    */
   public void getClassPathLocation(String href, AsyncRequestCallback<ClassPath> callback) throws RequestException
   {
      String url = restServiceContext + SERVICE_PATH + CLASSPATH_LOCATION;
      String location = URL.decodePathSegment(href);

      AsyncRequest.build(RequestBuilder.GET, url).loader(loader).header(HTTPHeader.LOCATION, location).send(callback);
   }

   @Override
   public void getAvailableJarLibraries(AsyncRequestCallback<List<Jar>> callback) throws RequestException
   {
      String url = restServiceContext + SERVICE_PATH + JARS_LOCATION;
      AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
   }

}
