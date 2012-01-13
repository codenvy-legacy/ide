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
package org.exoplatform.ide.vfs.client;

import com.google.gwt.http.client.RequestException;

import com.google.gwt.http.client.RequestBuilder;

import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.List;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 26, 2011 5:00:39 PM anya $
 * 
 */
public class VirtualFileSystemFactory
{
   /**
    * VFS factory instance.
    */
   private static VirtualFileSystemFactory instance;

   private final String VFS_URL = "/ide/vfs";

   private String restContext;

   public static VirtualFileSystemFactory getInstance()
   {
      return instance;
   }

   /**
    * @param workspaceURL
    */
   public VirtualFileSystemFactory(String restContext)
   {
      instance = this;
      this.restContext = restContext;
   }

   public void getAvailableFileSystems(AsyncRequestCallback<List<VirtualFileSystemInfo>> callback)
      throws RequestException
   {
      AsyncRequest.build(RequestBuilder.GET, restContext + VFS_URL).send(callback);
   }
}
