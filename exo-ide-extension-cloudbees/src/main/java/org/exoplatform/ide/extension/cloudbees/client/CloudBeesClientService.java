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
import org.exoplatform.ide.extension.cloudbees.client.info.ApplicationInfo;

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
    * @param appId application's id
    * @param vfsId virtual file system's id
    * @param projectId project's id
    * @param warFile location of the build war with application
    * @param message initialization message
    * @param callback callback
    */
   public abstract void initializeApplication(String appId, String vfsId, String projectId, String warFile,
      String message, CloudBeesAsyncRequestCallback<Map<String, String>> callback);

   /**
    * Get the available domains.
    * 
    * @param callback - callback that client has to implement
    */
   public abstract void getDomains(CloudBeesAsyncRequestCallback<List<String>> callback);

   /**
    * Login CloudBees.
    * 
    * @param email user's email (login)
    * @param password user's password
    * @param callback callback
    */
   public abstract void login(String email, String password, AsyncRequestCallback<String> callback);

   /**
    * Logout CloudBees.
    * 
    * @param callback callback
    */
   public abstract void logout(AsyncRequestCallback<String> callback);

   /**
    * Get the application info.
    * 
    * @param appId application's id
    * @param vfsId virtual file system's id
    * @param projectId project's id
    * @param callback callback
    */
   public abstract void getApplicationInfo(String appId, String vfsId, String projectId,
      CloudBeesAsyncRequestCallback<Map<String, String>> callback);

   /**
    * Delete application from CloudBees.
    * 
    * @param appId application's id
    * @param vfsId virtual file system's id
    * @param projectId project's id
    * @param callback callback
    */
   public abstract void deleteApplication(String appId, String vfsId, String projectId,
      CloudBeesAsyncRequestCallback<String> callback);

   /**
    * Update application on CloudBees.
    * 
    * @param appId application's id
    * @param vfsId virtual file system's id
    * @param projectId project's id
    * @param warFile location of the build war with application
    * @param message initialization message
    * @param callback callback
    */
   public abstract void updateApplication(String appId, String vfsId, String projectId, String warFile, String message,
      CloudBeesAsyncRequestCallback<Map<String, String>> callback);

   /**
    * Deploy war with the application.
    * 
    * @param appId application's id
    * @param warFile deploy built war with the application
    * @param message message for deploying war
    * @param callback callback
    */
   public abstract void deployWar(String appId, String warFile, String message,
      CloudBeesAsyncRequestCallback<Map<String, String>> callback);

   /**
    * Receive all CB applications for this account.
    */
   public abstract void applicationList(CloudBeesAsyncRequestCallback<List<ApplicationInfo>> callback);

}
