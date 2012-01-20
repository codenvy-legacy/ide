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

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.ide.extension.java.shared.ast.AstItem;
import org.exoplatform.ide.extension.java.shared.ast.JavaProject;
import org.exoplatform.ide.extension.java.shared.ast.Package;
import org.exoplatform.ide.extension.java.shared.ast.RootPackage;

import java.util.List;

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

   private static final String PROJECTS = BASE_URL + "/projects";

   private static final String ROOT_PACKAGES = BASE_URL + "/project/packages/root";

   private static final String PACKAGES = BASE_URL + "/project/packages/list";

   private static final String PACKAGE = BASE_URL + "/project/package";

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

   public JavaClientService(String restContext, Loader loader)
   {
      this.loader = loader;
      this.restServiceContext = restContext;
      instance = this;
   }

   public void createProject(String projectName, String projectType, String groupId, String artifactId, String version,
      String workDir, MavenResponseCallback callback) throws RequestException
   {
      final String url = restServiceContext + CREATE_PROJECT;
      String params = "projectName=" + projectName;
      params += "&projectType=" + projectType;
      params += "&groupId=" + groupId;
      params += "&artifactId=" + artifactId;
      params += "&version=" + version;
      params += "&parentId=" + workDir;
      params += "&vfsId=" + "dev-monit"; // TODO: need remove hardcode

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.java.client.JavaClientService#packageProject(java.lang.String,
    *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   public void packageProject(String baseDir, MavenResponseCallback callback) throws RequestException
   {
      final String url = restServiceContext + PACKAGE_PROJECT;
      String params = "workdir=" + baseDir;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * @throws RequestException 
    * @see org.exoplatform.ide.extension.java.client.JavaClientService#cleanProject(java.lang.String,
    *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
    */
   public void cleanProject(String baseDir, MavenResponseCallback callback) throws RequestException
   {
      final String url = restServiceContext + CLEAN_PROJECT;
      String params = "workdir=" + baseDir;

      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
         .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
   }

   /**
    * Get list of projects.
    * 
    * @param vfsId
    * @param callback
    * @throws RequestException 
    */
   public void getProjects(String vfsId, AsyncRequestCallback<List<JavaProject>> callback) throws RequestException
   {
      final String url = restServiceContext + PROJECTS + "?vfsId=" + vfsId;

      AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
   }

   /**
    * Get root packages.
    * 
    * @param vfsId
    * @param projectId
    * @param callback
    * @throws RequestException 
    */
   public void getRootPackages(String vfsId, String projectId, AsyncRequestCallback<List<RootPackage>> callback) throws RequestException
   {
      final String url = restServiceContext + ROOT_PACKAGES + "?vfsId=" + vfsId + "&projectId=" + projectId;

      AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
   }

   /**
    * Get packages.
    * 
    * @param vfsId
    * @param projectId
    * @param source
    * @param callback
    * @throws RequestException 
    */
   public void getPackages(String vfsId, String projectId, String source, AsyncRequestCallback<List<Package>> callback) throws RequestException
   {
      String url = restServiceContext + PACKAGES + "?vfsId=" + vfsId + "&projectId=" + projectId + "&source=" + source;

      AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
   }

   public void getPackageEntries(String vfsId, String projectId, String packageSource, String packageName,
      AsyncRequestCallback<List<AstItem>> callback) throws RequestException
   {
      String url =
         restServiceContext + PACKAGE + "?vfsId=" + vfsId + "&projectId=" + projectId + "&packageName=" + packageName
            + "&packageSource=" + packageSource;

      AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
   }

}
