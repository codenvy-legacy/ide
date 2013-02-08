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

import com.google.gwt.http.client.RequestException;

import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryServices;
import org.exoplatform.ide.extension.cloudfoundry.shared.Framework;
import org.exoplatform.ide.extension.cloudfoundry.shared.ProvisionedService;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemInfo;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.rest.AsyncRequestCallback;
import org.exoplatform.ide.websocket.WebSocketException;

/**
 * Client service for CloudFoundry.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CloudFoundryClientService.java Jul 12, 2011 10:24:53 AM vereshchaka $
 */
public abstract class CloudFoundryClientService
{
   private static CloudFoundryClientService instance;

   public static CloudFoundryClientService getInstance()
   {
      return instance;
   }

   protected CloudFoundryClientService()
   {
      instance = this;
   }

   /**
    * Get the list of available frameworks for CloudFoundry.
    * 
    * @param callback - callback, that client has to implement to receive response
    */
   public abstract void getFrameworks(AsyncRequestCallback<JsonArray<Framework>> callback, String server)
      throws RequestException;

   /**
    * Create application on CloudFoundry.
    * 
    * @param server location of Cloud Foundry instance where application must be created, e.g. http://api.cloudfoundry.com
    * @param name application name. This parameter is mandatory.
    * @param type the type of application (name of framework). Can be <code>null</code> (will try to detect automatically)
    * @param url the url (can be null - than will use default)
    * @param instances the number of instances of application
    * @param memory memory (in MB) allocated for application (optional). If less of equals zero then use default value which is
    *           dependents to framework type
    * @param nostart is start application after creating
    * @param vfsId current virtual file system id
    * @param projectId id of the project with the source code or compiled and packed java web application
    * @param war URL to pre-builded war file. May be present for java (spring, grails, java-web) applications ONLY
    * @param callback callback, that client has to implement to receive response
    */
   public abstract void create(String server, String name, String type, String url, int instances, int memory,
      boolean nostart, String vfsId, String projectId, String war,
      CloudFoundryAsyncRequestCallback<CloudFoundryApplication> callback) throws RequestException;

   /**
    * Create application on CloudFoundry. Sends request over WebSocket.
    * 
    * @param server location of Cloud Foundry instance where application must be created, e.g. http://api.cloudfoundry.com
    * @param name application name. This parameter is mandatory.
    * @param type the type of application (name of framework). Can be <code>null</code> (will try to detect automatically)
    * @param url the url (can be null - than will use default)
    * @param instances the number of instances of application
    * @param memory memory (in MB) allocated for application (optional). If less of equals zero then use default value which is
    *           dependents to framework type
    * @param nostart is start application after creating
    * @param vfsId current virtual file system id
    * @param projectId id of the project with the source code or compiled and packed java web application
    * @param war URL to pre-builded war file. May be present for java (spring, grails, java-web) applications ONLY
    * @param callback callback, that client has to implement to receive response
    */
   public abstract void createWS(String server, String name, String type, String url, int instances, int memory,
      boolean nostart, String vfsId, String projectId, String war,
      CloudFoundryRESTfulRequestCallback<CloudFoundryApplication> callback) throws WebSocketException;

   /**
    * Log in CloudFoundry account.
    * 
    * @param server location of Cloud Foundry instance where to log in
    * @param email user's email (login)
    * @param password user's password
    * @param callback callback, that client has to implement to receive response
    */
   public abstract void login(String server, String email, String password, AsyncRequestCallback<String> callback)
      throws RequestException;

   /**
    * Log out CloudFoundry account.
    * 
    * @param server location of Cloud Foundry instance from which to log out
    * @param callback callback, that client has to implement to receive response
    */
   public abstract void logout(String server, AsyncRequestCallback<String> callback) throws RequestException;

   /**
    * Get the application's information.
    * 
    * @param vfsId current virtual file system id
    * @param projectId id of the project with the source code or compiled and packed java web application
    * @param appId application's id
    * @param server location of Cloud Foundry instance, where application is located
    * @param callback callback, that client has to implement
    */
   public abstract void getApplicationInfo(String vfsId, String projectId, String appId, String server,
      CloudFoundryAsyncRequestCallback<CloudFoundryApplication> callback) throws RequestException;

   /**
    * Delete application from CloudFoundry.
    * 
    * @param vfsId current virtual file system id
    * @param projectId id of the project with the source code or compiled and packed java web application
    * @param appId application's id
    * @param server location of Cloud Foundry instance, where application is located
    * @param deleteServices if <code>true</code> - delete application's services
    * @param callback - callback, that client has to implement
    */
   public abstract void deleteApplication(String vfsId, String projectId, String appId, String server,
      boolean deleteServices, CloudFoundryAsyncRequestCallback<String> callback) throws RequestException;

   /**
    * Start application.
    * 
    * @param vfsId current virtual file system id
    * @param projectId id of the project with the source code or compiled and packed java web application
    * @param name - application name
    * @param server location of Cloud Foundry instance, where application is located
    * @param callback callback, that client has to implement to receive response from server.
    */
   public abstract void startApplication(String vfsId, String projectId, String name, String server,
      CloudFoundryAsyncRequestCallback<CloudFoundryApplication> callback) throws RequestException;

   /**
    * Stop application.
    * 
    * @param vfsId current virtual file system id
    * @param projectId id of the project with the source code or compiled and packed java web application
    * @param name - application name
    * @param server location of Cloud Foundry instance, where application is located
    * @param callback callback, that client has to implement to receive response from server.
    */
   public abstract void stopApplication(String vfsId, String projectId, String name, String server,
      CloudFoundryAsyncRequestCallback<String> callback) throws RequestException;

   /**
    * Restart application.
    * 
    * @param vfsId current virtual file system id
    * @param projectId id of the project with the source code or compiled and packed java web application
    * @param name application's name
    * @param server location of Cloud Foundry instance, where application is located
    * @param callback callback, that client has to implement to receive response from server.
    */
   public abstract void restartApplication(String vfsId, String projectId, String name, String server,
      CloudFoundryAsyncRequestCallback<CloudFoundryApplication> callback) throws RequestException;

   /**
    * Update existing application.
    * 
    * @param vfsId current virtual file system id
    * @param projectId id of the project with the source code or compiled and packed java web application
    * @param name application's name
    * @param server location of Cloud Foundry instance, where application is located
    * @param war location of war file (Java applications only)
    * @param callback callback, that client has to implement to handle response from server
    */
   public abstract void updateApplication(String vfsId, String projectId, String name, String server, String war,
      CloudFoundryAsyncRequestCallback<String> callback) throws RequestException;

   public abstract void renameApplication(String vfsId, String projectId, String name, String server, String newName,
      CloudFoundryAsyncRequestCallback<String> callback) throws RequestException;

   /**
    * Map new URL of the application.
    * 
    * @param vfsId current virtual file system id
    * @param projectId id of the project with the source code or compiled and packed java web application
    * @param name application's name
    * @param server location of Cloud Foundry instance, where application is located
    * @param url URL to map
    * @param callback callback, that client has to implement to handle response from server.
    */
   public abstract void mapUrl(String vfsId, String projectId, String name, String server, String url,
      CloudFoundryAsyncRequestCallback<String> callback) throws RequestException;

   /**
    * Unmap URL from the application.
    * 
    * @param vfsId current virtual file system id
    * @param projectId id of the project with the source code or compiled and packed java web application
    * @param name application's name
    * @param server location of Cloud Foundry instance, where application is located
    * @param url URL to unmap
    * @param callback callback, that client has to implement to handle response from server
    */
   public abstract void unmapUrl(String vfsId, String projectId, String name, String server, String url,
      CloudFoundryAsyncRequestCallback<Object> callback) throws RequestException;

   /**
    * Update the memory size.
    * 
    * @param vfsId current virtual file system id
    * @param projectId id of the project with the source code or compiled and packed java web application
    * @param name application's name
    * @param server location of Cloud Foundry instance, where to update memoryw
    * @param mem mememory size
    * @param callback callback, that client has to implement to handle response from server
    */
   public abstract void updateMemory(String vfsId, String projectId, String name, String server, int mem,
      CloudFoundryAsyncRequestCallback<String> callback) throws RequestException;

   /**
    * Update the number of instances for the application.
    * 
    * @param vfsId current virtual file system id
    * @param projectId id of the project with the source code or compiled and packed java web application
    * @param name application's name
    * @param server location of Cloud Foundry instance, where to update instances
    * @param expression expression for instances updating
    * @param callback callback, that client has to implement to handle response from server
    */
   public abstract void updateInstances(String vfsId, String projectId, String name, String server, String expression,
      CloudFoundryAsyncRequestCallback<String> callback) throws RequestException;

   /**
    * Validate action before building project.
    * 
    * @param action he name of action (create, update)
    * @param server location of Cloud Foundry instance, where to validate action, e.g. http://api.cloudfoundry.com
    * @param appName the name of application (if create - than required, if update - <code>null</code>)
    * @param framework he name of application framework (can be <code>null</code>)
    * @param url application's URL
    * @param vfsId current virtual file system id
    * @param projectId id of the project with the source code or compiled and packed java web application
    * @param instances number of instances
    * @param memory memory size
    * @param nostart
    * @param callback callback, that client has to implement to handle response from server.
    */
   public abstract void validateAction(String action, String server, String appName, String framework, String url,
      String vfsId, String projectId, int instances, int memory, boolean nostart,
      CloudFoundryAsyncRequestCallback<String> callback) throws RequestException;

   /**
    * Get list of deployed applications.
    * 
    * @param server location of Cloud Foundry instance, where applications are located
    * @param callback callback, that client has to implement to handle response from server.
    */
   public abstract void getApplicationList(String server,
      CloudFoundryAsyncRequestCallback<JsonArray<CloudFoundryApplication>> callback) throws RequestException;

   /**
    * Get Cloud Foundry system information.
    * 
    * @param server location of Cloud Foundry instance
    * @param callback callback, that client has to implement to handle response from server
    */
   public abstract void getSystemInfo(String server, AsyncRequestCallback<SystemInfo> callback) throws RequestException;

   /**
    * Get the list of available targets for user.
    * 
    * @param callback callback, that client has to implement to handle response from server
    */
   public abstract void getTargets(AsyncRequestCallback<JsonArray<String>> callback) throws RequestException;

   /**
    * @param callback callback, that client has to implement to handle response from server
    */
   public abstract void getTarget(AsyncRequestCallback<StringBuilder> callback) throws RequestException;

   /**
    * Getting logs for CloudFoundry Application
    * 
    * @param vfsId
    * @param projectId
    * @param callback
    * @throws RequestException
    */
   public abstract void getLogs(String vfsId, String projectId, AsyncRequestCallback<StringBuilder> callback)
      throws RequestException;

   /**
    * Get the list of services available and provisioned.
    * 
    * @param server server's name (may be <code>null</code>)
    * @param callback
    * @throws RequestException
    */
   public abstract void services(String server, AsyncRequestCallback<CloudFoundryServices> callback)
      throws RequestException;

   /**
    * Create new provisioned service.
    * 
    * @param server server's name
    * @param type service's type
    * @param name service's name
    * @param application application's name
    * @param vfsId virtual file system id
    * @param projectId project's id
    * @param callback callback
    * @throws RequestException
    */
   public abstract void createService(String server, String type, String name, String application, String vfsId,
      String projectId, CloudFoundryAsyncRequestCallback<ProvisionedService> callback) throws RequestException;

   /**
    * Delete provisioned service.
    * 
    * @param server server's name
    * @param name service's name
    * @param callback callback
    * @throws RequestException
    */
   public abstract void deleteService(String server, String name, CloudFoundryAsyncRequestCallback<Object> callback)
      throws RequestException;

   /**
    * Bind service to application.
    * 
    * @param server server's name
    * @param name service's name
    * @param application application's name
    * @param vfsId virtual file system id
    * @param projectId project's id
    * @param callback callback
    * @throws RequestException
    */
   public abstract void bindService(String server, String name, String application, String vfsId, String projetcId,
      CloudFoundryAsyncRequestCallback<Object> callback) throws RequestException;

   /**
    * Unbind service from application.
    * 
    * @param server server's name
    * @param name service's name
    * @param application application's name
    * @param vfsId virtual file system id
    * @param projectId project's id
    * @param callback callback
    * @throws RequestException
    */
   public abstract void unbindService(String server, String name, String application, String vfsId, String projetcId,
      CloudFoundryAsyncRequestCallback<Object> callback) throws RequestException;
}