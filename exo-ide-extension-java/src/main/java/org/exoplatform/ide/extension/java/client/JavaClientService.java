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
package org.exoplatform.ide.extension.java.client;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.extension.java.client.marshaller.MavenResponseUnmarshaller;
import org.exoplatform.ide.extension.java.shared.MavenResponse;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

/**
 * Implementation of {@link JavaClientService} service.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: JavaClientServiceImpl.java Jun 21, 2011 5:02:07 PM vereshchaka $
 *
 */
public class JavaClientService
{
   
   private static final String BASE_URL = "/ide/application/java";
   
   private static final String CREATE_PROJECT = BASE_URL + "/create";
   
   private static final String CLEAN_PROJECT = BASE_URL + "/clean";
   
   private static final String PACKAGE_PROJECT = BASE_URL + "/package";
   
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
   
   private static JavaClientService instance;
   
   /**
    * @return {@link JavaClientService} java client service
    */
   public static JavaClientService getInstance()
   {
      return instance;
   }
   
   
   public JavaClientService(HandlerManager eventBus, String restContext, Loader loader)
   {
      this.loader = loader;
      this.eventBus = eventBus;
      this.restServiceContext = restContext;
      instance = this;
   }
   
   public void createProject(String projectName, String projectType, String groupId, String artifactId, String version, String workDir, MavenResponseCallback callback)
   {
      final String url = restServiceContext + CREATE_PROJECT;
      String params = "projectName=" + projectName;
      params += "&projectType=" + projectType;
      params += "&groupId=" + groupId;
      params += "&artifactId=" + artifactId;
      params += "&version=" + version;
      params += "&parentId=" + workDir;
      params += "&vfsId=" + "dev-monit"; //TODO: need remove hardcode
      
      MavenResponse mavenResponse = new MavenResponse();
      callback.setResult(mavenResponse);
      callback.setEventBus(eventBus);
      
      MavenResponseUnmarshaller unmarshaller = new MavenResponseUnmarshaller(mavenResponse); 
      callback.setPayload(unmarshaller);

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
         .send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.java.client.JavaClientService#packageProject(java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   public void packageProject(String baseDir, MavenResponseCallback callback)
   {
      final String url = restServiceContext + PACKAGE_PROJECT;
      String params = "workdir=" + baseDir;
      
      MavenResponse mavenResponse = new MavenResponse();
      callback.setResult(mavenResponse);
      callback.setEventBus(eventBus);
      
      MavenResponseUnmarshaller unmarshaller = new MavenResponseUnmarshaller(mavenResponse);
      callback.setPayload(unmarshaller);
      
      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader)
      .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
      .send(callback);
   }

   /**
    * @see org.exoplatform.ide.extension.java.client.JavaClientService#cleanProject(java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   public void cleanProject(String baseDir, MavenResponseCallback callback)
   {
      final String url = restServiceContext + CLEAN_PROJECT;
      String params = "workdir=" + baseDir;
      
      MavenResponse mavenResponse = new MavenResponse();
      callback.setResult(mavenResponse);
      callback.setEventBus(eventBus);
      
      MavenResponseUnmarshaller unmarshaller = new MavenResponseUnmarshaller(mavenResponse);
      callback.setPayload(unmarshaller);
      
      AsyncRequest.build(RequestBuilder.POST, url + "?" + params, loader)
      .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
      .send(callback);
   }

}
