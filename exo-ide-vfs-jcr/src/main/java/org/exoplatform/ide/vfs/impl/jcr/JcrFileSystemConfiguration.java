/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
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
      {
         throw new IllegalArgumentException(
            "Workspace name or Virtual File System ID or root node path may not be null. ");
      }
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
    * @param id virtual file system id
    * @param workspace JCR workspace name
    */
   public JcrFileSystemConfiguration(String id, String workspace)
   {
      if (id == null || workspace == null)
      {
         throw new IllegalArgumentException("Workspace name or Virtual File System ID may not be null. ");
      }
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
      {
         throw new IllegalArgumentException("Workspace name may not be null. ");
      }
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
