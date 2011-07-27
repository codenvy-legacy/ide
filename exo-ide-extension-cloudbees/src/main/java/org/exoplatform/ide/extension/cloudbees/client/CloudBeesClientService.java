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

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;

import java.util.List;
import java.util.Map;

/**
 * Client service for CloudBees.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CloudBessService.java Jun 23, 2011 10:11:13 AM vereshchaka $
 *
 */
public abstract class CloudBeesClientService
{
   
   private static CloudBeesClientService instance;
   
   public static CloudBeesClientService getInstance()
   {
      return instance;
   }
   
   protected CloudBeesClientService()
   {
      instance = this;
   }
   
   /**
    * Initialize application.
    * 
    * @param appId
    * @param warFile
    * @param message
    * @param callback
    */
   public abstract void initializeApplication(String appId, String warFile, String message, String workDir,
      CloudBeesAsyncRequestCallback<Map<String, String>> callback);
   
   /**
    * Get the domains.
    * 
    * @param callback - callback that client has to implement
    */
   public abstract void getDomains(CloudBeesAsyncRequestCallback<List<String>> callback);
   
   public abstract void login(String email, String password, AsyncRequestCallback<String> callback);
   
   public abstract void logout(AsyncRequestCallback<String> callback);
   
   /**
    * Get the application info.
    * 
    * @param workDir - the location of work dir on file system
    * @param appId - id of application
    * @param callback - callcack, that client has to implement
    */
   public abstract void getApplicationInfo(String workDir, String appId,
      CloudBeesAsyncRequestCallback<Map<String, String>> callback);
   
   /**
    * Delete application from CloudBees.
    * @param workDir - the location of work dir on file system
    * @param appId - id of application
    * @param callback - callcack, that client has to implement
    */
   public abstract void deleteApplication(String workDir, String appId,
      CloudBeesAsyncRequestCallback<String> callback);

   /**
    * @param appId
    * @param warFile
    * @param message
    * @param callback
    */
   public abstract void deployWar(String appId, String warFile, String message,
      CloudBeesAsyncRequestCallback<Map<String, String>> callback);

}
