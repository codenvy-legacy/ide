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

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplication;
import org.exoplatform.ide.extension.cloudfoundry.shared.Framework;

import java.util.List;

/**
 * Client service for CloudFoundry.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CloudFoundryClientService.java Jul 12, 2011 10:24:53 AM vereshchaka $
 *
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
   public abstract void getFrameworks(AsyncRequestCallback<List<Framework>> callback);
   
   /**
    * Create application on CloudFoundry.
    * 
    * @param name - application name. This parameter is mandatory.
    * @param type - the type of application (name of framework). Can be <code>null</code> (will try to detect automatically)
    * @param url - the url (can be null - than will use default)
    * @param instances - number of instanses of application
    * @param memory - memory (in MB) allocated for application (optional). If less of equals zero then use default value
    *           which is dependents to framework type
    * @param nostart - is start application after creationg
    * @param workDir - directory that contains source code (Ruby) or compiled and packed java web application
    * @param war - URL to pre-builded war file. May be present for java (spring, grails, java-web) applications ONLY
    * @param callback - callback, that client has to implement to receive response
    */
   public abstract void create(String name, String type, String url, int instances, int memory, boolean nostart,
      String workDir, String war, CloudFoundryAsyncRequestCallback<CloudfoundryApplication> callback);
   
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
      CloudFoundryAsyncRequestCallback<CloudfoundryApplication> callback);
   
   /**
    * Delete application from CloudFoundry.
    * @param workDir - the location of work dir on file system
    * @param appId - id of application
    * @param callback - callcack, that client has to implement
    */
   public abstract void deleteApplication(String workDir, String appId, boolean deleteServices,
      CloudFoundryAsyncRequestCallback<String> callback);
   
   /**
    * Start application.
    * 
    * @param workDir - the location of application
    * @param name - application name
    * @param callback callback, that client has to implement to receive response from server.
    */
   public abstract void startApplication(String workDir, String name,
      CloudFoundryAsyncRequestCallback<CloudfoundryApplication> callback);
   
   /**
    * Stop application.
    * 
    * @param workDir - the location of application
    * @param name - application name
    * @param callback callback, that client has to implement to receive response from server.
    */
   public abstract void stopApplication(String workDir, String name,
      CloudFoundryAsyncRequestCallback<String> callback);
   
   /**
    * Restart application.
    * 
    * @param workDir - the location of application
    * @param name - application name
    * @param callback callback, that client has to implement to receive response from server.
    */
   public abstract void restartApplication(String workDir, String name,
      CloudFoundryAsyncRequestCallback<CloudfoundryApplication> callback);
   
   /**
    * Update existing application.
    * 
    * @param workDir the location of application
    * @param name application name
    * @param war location of war file (Java applications only)
    * @param callback callback, that client has to implement to handle response from server.
    */
   public abstract void updateApplication(String workDir, String name, String war,
      CloudFoundryAsyncRequestCallback<String> callback);
   
   public abstract void renameApplication(String workDir, String name, String newName,
      CloudFoundryAsyncRequestCallback<String> callback);
   
   public abstract void mapUrl(String workDir, String name, String url,
      CloudFoundryAsyncRequestCallback<String> callback);
   
   public abstract void unmapUrl(String workDir, String name, String url,
      CloudFoundryAsyncRequestCallback<String> callback);
   
   public abstract void updateMemory(String workDir, String name, int mem,
      CloudFoundryAsyncRequestCallback<String> callback);
   
   public abstract void updateInstances(String workDir, String name, String expression,
      CloudFoundryAsyncRequestCallback<String> callback);
   
   /**
    * Validates action before building project.
    * @param action the name of action (create, update)
    * @param appName the name of application (if create - than required, if update - <code>null</code>)
    * @param framework the name of application framework (can be <code>null</code>)
    * @param url application URL
    * @param workDir the work dir of application
    * @param callback callback, that client has to implement to handle response from server.
    */
   public abstract void validateAction(String action, String appName, String framework, String url, String workDir,
      CloudFoundryAsyncRequestCallback<String> callback);

}
