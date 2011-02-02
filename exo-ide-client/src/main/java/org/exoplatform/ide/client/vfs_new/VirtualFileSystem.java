/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.vfs_new;

import org.exoplatform.gwtframework.commons.exception.ServerExceptionEvent;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.vfs.client.JSONMarshaller;
import org.exoplatform.ide.vfs.client.JSONUnmarshaller;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Link;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

public class VirtualFileSystem
{
   private static VirtualFileSystem instance;

   private final HandlerManager eventBus;

   private Loader loader;

   public static VirtualFileSystem getInstance()
   {
      return instance;
   }

   public VirtualFileSystem(HandlerManager eventBus,  Loader loader, String restServiceURI, String repository, String workspace)
   {
      instance = this;
      
      this.eventBus = eventBus;
      this.loader = loader;
   }

   /**
    * Get folder content
    * 
    * @param path
    */
   public void getChildren(Folder folder, GwtEvent<?> postEvent, ServerExceptionEvent<?> errorEvent)
   {
      String url = folder.getLinks().get(Link.REL_CHILDREN).getHref();
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, new JSONUnmarshaller(), postEvent, errorEvent);
      AsyncRequest.build(RequestBuilder.POST, url, loader).data(new JSONMarshaller()).send(callback);
   }

   /**
    * Create new folder
    * 
    * @param path
    */
   public void createFolder(Folder parent, String name)
   {
      
//      String path = new StringBuilder() //
//      .append(SERVICE_URI) //
//      .append("folder") //
//      .append(CREATE_TEST_PATH) //
//      .append("?") //
//      .append("name=") //
//      .append(name).toString();
//   ContainerResponse response = launcher.service("POST", path, BASE_URI, null, null, null);      
      
   }

   /**
    * Get content of the file.
    * 
    * @param file
    */
   public void getContent(File file)
   {
   }
   
}
