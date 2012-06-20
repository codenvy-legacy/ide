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

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.extension.samples.shared.Repository;

import java.util.List;

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
    * Get the list of available public and private repositories of the authorized user.
    * 
    * @param callback the callback client has to implement
    */
   public abstract void getRepositoriesList(AsyncRequestCallback<List<Repository>> callback) throws RequestException;

   /**
    * Get the list of available public repositories from GitHub user.
    * 
    * @param userName Name of GitHub User
    * @param callback the callback client has to implement
    */
   public abstract void getRepositoriesByUser(String userName, AsyncRequestCallback<List<Repository>> callback)
      throws RequestException;

   /**
    * Log in GitHub account.
    * 
    * @param login user's login
    * @param password user's password
    * @param callback callback the client has to implement
    */
   public abstract void loginGitHub(String login, String password, AsyncRequestCallback<String> callback) throws RequestException;
}
