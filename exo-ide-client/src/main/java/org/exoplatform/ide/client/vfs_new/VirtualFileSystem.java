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

import org.exoplatform.gwtframework.commons.loader.Loader;

import com.google.gwt.event.shared.HandlerManager;

public class VirtualFileSystem
{
   private static VirtualFileSystem instance;

   private final HandlerManager eventBus;
   
   private String workspace;

   private Loader loader;

   public static VirtualFileSystem getInstance()
   {
      return instance;
   }

   public VirtualFileSystem(HandlerManager eventBus,  Loader loader, String workspace)
   {
      instance = this;
      
      this.eventBus = eventBus;
      this.loader = loader;
      this.workspace = workspace;
   }
   
   public void getVFSInfo() {
      
   }
   
   /**
    * Get folder content
    * 
    * @param path
    */
   public void getChildren(String id)
   {
   }

   /**
    * Create new folder
    * 
    * @param path
    */
   public void createFolder(String parentId, String name)
   {
   }

   /**
    * Get content of the file.
    * 
    * @param file
    */
   public void getFileContent(String id)
   {
   }
   
   public void saveFileContent(String id, String mediaType, String content, String lockToken) {
   }
   
   public void copy(String source, String destination) {
   }
   
   public void move(String id, String parentId, String lockToken) {
      
   }
   
   public void delete(String id, String lockToken) {
      
   }
   
   public void rename(String id, String mediaType, String newname, String lockToken) {
      
   }
   
   public void lock(String id) {
   }
   
   public void unlock(String id, String lockToken) {
      
   }
   
}
