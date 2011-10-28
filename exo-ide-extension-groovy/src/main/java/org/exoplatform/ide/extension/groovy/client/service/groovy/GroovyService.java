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

import java.util.List;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.extension.groovy.client.service.RestServiceOutput;
import org.exoplatform.ide.extension.groovy.client.service.SimpleParameterEntry;
import org.exoplatform.ide.extension.groovy.client.service.groovy.marshal.ClassPath;
import org.exoplatform.ide.extension.groovy.shared.Jar;
import org.exoplatform.ide.vfs.client.model.FileModel;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public abstract class GroovyService
{

   private static GroovyService instance;

   public static GroovyService getInstance()
   {
      return instance;
   }

   protected GroovyService()
   {
      instance = this;
   }

   /**
    * Validate Groovy script.
    * 
    * @param file - the file to validate
    * @param vfsid 
    * @param fileContent - file content
    * @param groovyCallback - callback to handler response from server
    */
   public abstract void validate(FileModel file, String vfsid, AsyncRequestCallback<FileModel> callback);
   
   /**
    * Deploy Groovy script.
    * 
    * @param itemId - id of source file to deploy
    * @param callback - the callback code which the user has to implement
    */
   public abstract void deploy(String itemId, String vfsId, String projectId, AsyncRequestCallback<String> callback);
   
   /**
    * Deploy Groovy script.
    * 
    * @param href - href of source to deploy (encoded)
    * @param callback - the callback code which the user has to implement
    */
   public abstract void deploySandbox(String itemId, String vfsId, String projectId, AsyncRequestCallback<String> callback);
   
   /**
    * Undeploy deployed Groovy script.
    * 
    * @param href - href of source to undeploy (encoded)
    * @param callback - the callback code which the user has to implement
    */
   public abstract void undeploySandbox(String itemId, String vfsId, String projectId, AsyncRequestCallback<String> callback);
   
   /**
    * Undeploy deployed Groovy script.
    * 
    * @param href - href of source to undeploy (encoded)
    * @param callback - the callback code which the user has to implement
    */
   public abstract void undeploy(String itemId, String vfsId, String projectId, AsyncRequestCallback<String> callback);
   

   /**
    * Get location of the groovy classpath file if exists.
    * Return status:
    * 200 - groovy classpath location in the response body
    * 404 - groovy classpath location is not found.
    * 500 - internal server error, message of the error is in the response body.
    * 
    * @param href location of the item, 
    * with respect to which the classpath location must be found (file or folder)  (encoded)
    * @param callback - handle the results when they are returned from the server
    */
   @Deprecated
   public abstract void getClassPathLocation(String href, AsyncRequestCallback<ClassPath> callback);
   
   /**
    * Get Groovy script output.
    * 
    * @param url - the url of request
    * @param method - the method of request
    * @param headers - the headers of request
    * @param params - the params of request
    * @param body - the body of request
    * @param callback - handle the results when they are returned from the server
    */
   public abstract void getOutput(String url, String method, List<SimpleParameterEntry> headers,
      List<SimpleParameterEntry> params, String body, AsyncRequestCallback<RestServiceOutput> callback);

   /**
    * Get list of available JAR libraries.
    * 
    * @param callback - handle the results from the server
    */
   public abstract void getAvailableJarLibraries(AsyncRequestCallback<List<Jar>> callback);
   
}
