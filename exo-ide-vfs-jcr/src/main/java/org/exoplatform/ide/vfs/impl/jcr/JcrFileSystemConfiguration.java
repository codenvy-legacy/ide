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
package org.exoplatform.ide.vfs.impl.jcr;

/**
 * Extended configuration of JCR Virtual File System. It should be used as 'object-param' in configuration of
 * JcrFileSystemInitializer if need to use other than the root node of the workspace as entry point.
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JcrFileSystemConfiguration
{
   private String id;
   private String workspace;
   private String path = "/";

   public JcrFileSystemConfiguration(String id, String workspace, String path)
   {
      if (id == null || workspace == null || path == null)
         throw new IllegalArgumentException(
            "Workspace name or Virtual File System ID or root node path may not be null. ");
      this.id = id;
      this.workspace = workspace;
      this.path = path;
   }

   /**
    * Create configuration:
    * <ul>
    * <li><code>id</code> - <i>id</i></li>
    * <li>workspace</code> - <i>workspace</i></li>
    * <li>path</code> - <i>/</i></li>
    * </ul>
    * 
    * @param workspace JCR workspace name
    */
   public JcrFileSystemConfiguration(String id, String workspace)
   {
      if (id == null || workspace == null)
         throw new IllegalArgumentException("Workspace name or Virtual File System ID may not be null. ");
      this.id = id;
      this.workspace = workspace;
   }

   /**
    * Create configuration:
    * <ul>
    * <li><code>id</code> - <i>workspace</i></li>
    * <li><code>workspace</code> - <i>workspace</i></li>
    * <li><code>path</code> - <i>/</i></li>
    * </ul>
    * 
    * @param workspace JCR workspace name
    */
   public JcrFileSystemConfiguration(String workspace)
   {
      if (workspace == null)
         throw new IllegalArgumentException("Workspace name may not be null. ");
      this.id = workspace;
      this.workspace = workspace;
   }

   // For using in components configuration. 
   public JcrFileSystemConfiguration()
   {
   }

   public String getId()
   {
      return id;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public String getWorkspace()
   {
      return workspace;
   }

   public void setWorkspace(String workspace)
   {
      this.workspace = workspace;
   }

   public String getPath()
   {
      return path;
   }

   public void setPath(String path)
   {
      this.path = path;
   }
}
