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
package org.exoplatform.ide.git.client.service;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 23, 2011 11:48:14 AM anya $
 *
 */
public abstract class GitClientService
{
   /**
    * Instance of {@link GitClientService}
    */
   private static GitClientService instance;

   /**
    * @return {@link GitClientService}
    */
   public static GitClientService getInstance()
   {
      return instance;
   }

   protected GitClientService()
   {
      instance = this;
   }
   
   public abstract void init(String workDir, boolean bare, AsyncRequestCallback<String> callback);
   
   public abstract void cloneRepository(String workDir, String remoteUri, String remoteName, AsyncRequestCallback<String> callback);
}
