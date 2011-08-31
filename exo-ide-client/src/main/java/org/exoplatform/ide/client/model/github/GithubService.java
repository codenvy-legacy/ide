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
package org.exoplatform.ide.client.model.github;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;

import java.util.List;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: GithubService.java Aug 29, 2011 4:49:01 PM vereshchaka $
 *
 */
public abstract class GithubService
{
   
   private static GithubService instance;
   
   public static GithubService getInstance()
   {
      return instance;
   }
   
   protected GithubService()
   {
      instance = this;
   }
   
   /**
    * Get the list of repositories.
    * 
    * @param repos
    */
   public abstract void getRepositoriesList(AsyncRequestCallback<List<Repository>> callback);

}
