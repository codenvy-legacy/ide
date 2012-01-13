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
package org.exoplatform.ide.extension.samples.client;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.extension.samples.client.paas.cloudbees.CloudBeesAsyncRequestCallback;
import org.exoplatform.ide.extension.samples.client.paas.cloudfoundry.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.samples.client.paas.cloudfoundry.CloudfoundryApplication;
import org.exoplatform.ide.extension.samples.client.paas.heroku.HerokuAsyncRequestCallback;
import org.exoplatform.ide.extension.samples.shared.Repository;

import java.util.List;
import java.util.Map;

/**
 * Client service for Samples.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: SamplesClientService.java Sep 2, 2011 12:34:16 PM vereshchaka $
 * 
 */
public abstract class SamplesClientService
{
   public enum Paas {
      CLOUDBEES, CLOUDFOUNDRY, HEROKU, OPENSHIFT;
   }

   private static SamplesClientService instance;

   public static SamplesClientService getInstance()
   {
      return instance;
   }

   protected SamplesClientService()
   {
      instance = this;
   }

   /**
    * Get the list of available public repositories from GitHub with sample applications.
    * 
    * @param callback the callback client has to implement
    */
   public abstract void getRepositoriesList(AsyncRequestCallback<List<Repository>> callback);

   /**
    * Get the list of available public repositories from GitHub user.
    * 
    * @param userName Name of GitHub User
    * @param callback the callback client has to implement
    */
   public abstract void getRepositoriesList(String userName, AsyncRequestCallback<List<Repository>> callback);

   /************ CloudBees operations ************/

   /**
    * Initialize application.
    * 
    * @param appId
    * @param warFile
    * @param message
    * @param callback
    */
   public abstract void createCloudBeesApplication(String appId, String vfsId, String projectId, String warFile,
      String message, CloudBeesAsyncRequestCallback<Map<String, String>> callback);

   /**
    * Get the domains.
    * 
    * @param callback - callback that client has to implement
    */
   public abstract void getDomains(CloudBeesAsyncRequestCallback<List<String>> callback);

   public abstract void loginToCloudBees(String email, String password, AsyncRequestCallback<String> callback);

   /**
    * Login to paas.
    * 
    * @param paas <code>cloudbees</code> or <code>cloudfoundry</code> - where to login
    * @param email email to login
    * @param password password
    * @param callback callback that client has to implement
    */
   public abstract void login(Paas paas, String email, String password, AsyncRequestCallback<String> callback);

   /**
    * Validates <code>create</code> CloudFoundry action before building project.
    * 
    * @param server server
    * @param appName the name of application (if create - than required, if update - <code>null</code>)
    * @param workDir the work dir of application
    * @param callback callback, that client has to implement to handle response from server.
    */
   public abstract void validateCloudfoundryAction(String server, String appName, String workDir,
      CloudFoundryAsyncRequestCallback<String> callback);

   /**
    * Create application on CloudFoundry.
    * 
    * @param server server
    * @param name - application name. This parameter is mandatory.
    * @param url - application URL.
    * @param workDir - directory that contains source code of java web application
    * @param war - URL to pre-builded war file. May be present for java (spring, grails, java-web) applications ONLY
    * @param callback - callback, that client has to implement to receive response
    */
   public abstract void createCloudFoundryApplication(String vfsId, String server, String name, String url,
      String workDir, String projectId, String war, CloudFoundryAsyncRequestCallback<CloudfoundryApplication> callback);

   /**
    * Get the list of CloudFoundry targets.
    * 
    * @param callback
    */
   public abstract void getCloudFoundryTargets(AsyncRequestCallback<List<String>> callback);

   /**
    * Get the types of application on OpenShift.
    * 
    * @param callback
    */
   public abstract void getOpenShiftTypes(AsyncRequestCallback<List<String>> callback);

   public abstract void createOpenShitfApplication(String name, String vfsId, String projectId, String type,
      AsyncRequestCallback<String> callback);

   public abstract void createHerokuApplication(String applicationName, String vfsId, String projectid,
      String remoteName, HerokuAsyncRequestCallback<String> callback);

}
