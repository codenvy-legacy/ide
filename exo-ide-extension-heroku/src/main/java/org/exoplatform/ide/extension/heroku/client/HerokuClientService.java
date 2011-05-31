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
package org.exoplatform.ide.extension.heroku.client;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;


/**
 * Heroku client service.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  May 25, 2011 12:19:59 PM anya $
 *
 */
public abstract class HerokuClientService
{
   /**
    * Heroku client service.
    */
   private static HerokuClientService instance;

   /**
    * @return {@link HerokuClientService} Heroku client service
    */
   public static HerokuClientService getInstance()
   {
      return instance;
   }

   protected HerokuClientService()
   {
      instance = this;
   }
   
   /**
    * Logins user on Heroku.
    * 
    * @param login user's email
    * @param password user's password
    * @param callback callback
    */
   public abstract void login(String login, String password, AsyncRequestCallback<String> callback);
   
   /**
    * Creates new application on Heroku.
    * 
    * @param applicationName application's name
    * @param gitWorkDir Git working directory href
    * @param remoteName name of Git remote repository
    * @param callback callback
    */
   public abstract void createApplication(String applicationName, String gitWorkDir, String remoteName, HerokuAsyncRequestCallback callback);

   /**
    * Deletes application pointed by it's name or Git location.
    * 
    * @param gitWorkDir href of Git working directory
    * @param applicationName application's name
    * @param callback callback
    */
   public abstract void deleteApplication(String gitWorkDir, String applicationName, HerokuAsyncRequestCallback callback);

   public abstract void addKey(HerokuAsyncRequestCallback callback);
   
   public abstract void clearKeys(HerokuAsyncRequestCallback callback);
}
