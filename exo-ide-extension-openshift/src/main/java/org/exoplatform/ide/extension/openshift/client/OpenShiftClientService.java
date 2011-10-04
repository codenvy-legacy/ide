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
package org.exoplatform.ide.extension.openshift.client;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;
import org.exoplatform.ide.extension.openshift.shared.RHUserInfo;

import java.util.List;

/**
 * OpenShift client service.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 6, 2011 5:49:43 PM anya $
 *
 */
public abstract class OpenShiftClientService
{
   /**
    * OpenShift client service.
    */
   private static OpenShiftClientService instance;

   /**
    * @return {@link OpenShiftClientService} OpenShiftClientService client service
    */
   public static OpenShiftClientService getInstance()
   {
      return instance;
   }

   protected OpenShiftClientService()
   {
      instance = this;
   }

   /**
    * Performs log in OpenShift.
    * 
    * @param login user's email
    * @param password user's password
    * @param callback callback
    */
   public abstract void login(String login, String password, AsyncRequestCallback<String> callback);

   /**
    * Creates new domain name. A domain name is a requirement for 
    * each new application that you create in the cloud 
    * and is part of the application name.
    * 
    * @param name domain's name
    * @param alter alter namespace (will change urls) and/or ssh key
    * @param callback callback
    */
   public abstract void createDomain(String name, boolean alter, AsyncRequestCallback<String> callback);

   /**
    * Creates OpenShift application.
    * 
    * @param name application's name
    * @param vfsId virtual file system's id
    * @param projectId project's id
    * @param type application's type
    * @param workdir application's working directory
    * @param callback callback
    */
   public abstract void createApplication(String name, String vfsId, String projectId, String type,
      AsyncRequestCallback<AppInfo> callback);

   /**
    * Destroys application with pointed name from OpenShift. 
    * 
    * @param name application's name
    * @param vfsId virtual file system's id
    * @param projectId project's id
    * @param callback callback
    */
   public abstract void destroyApplication(String name, String vfsId, String projectId, AsyncRequestCallback<String> callback);

   /**
    * Get user's information. 
    * You can view the Framework Type, Creation Date, GitURL and PublicURL details for each application.
    * 
    * @param appsInfo if <code>true</code>, then list applications
    * @param callback callback
    */
   public abstract void getUserInfo(boolean appsInfo, AsyncRequestCallback<RHUserInfo> callback);

   /**
    * Get application's information.
    * 
    * @param applicationName application name
    * @param vfsId virtual file system's id
    * @param projectId project's id
    * @param callback callback
    */
   public abstract void getApplicationInfo(String applicationName, String vfsId, String projectId, AsyncRequestCallback<AppInfo> callback);

   /**
    * Get types of allowed applications.
    * 
    * @param callback callback
    */
   public abstract void getApplicationTypes(AsyncRequestCallback<List<String>> callback);
}
