/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.gwtframework.ui.client.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TreeNode
{

   /**
    * List of children 
    */
   private List<TreeNode> children = new ArrayList<TreeNode>();

   /**
    * URL to Icon
    * If icon is not set then default icons are used from TreeRecord.Images ( for collections and simple items ) 
    */
   private String icon;

   /**
    * Name of a tree node
    */
   private String name;

   /**
    * Is Folder status
    */
   private boolean isFolder = true;

   /**
    * Entry used for storing any object which is binded with this tree node
    */
   private Object entry;

   public TreeNode(String name)
   {
      this.name = name;
   }

   public TreeNode(String name, String icon)
   {
      this.name = name;
      this.icon = icon;
   }

   public TreeNode(String name, Object entry)
   {
      this.name = name;
      this.entry = entry;
   }

   public TreeNode(String name, Object entry, boolean isFolder)
   {
      this.name = name;
      this.entry = entry;
      this.isFolder = isFolder;
   }

   public TreeNode(String name, String icon, Object entry)
   {
      this.name = name;
      this.icon = icon;
      this.entry = entry;
   }

   /**
    * @return list of children
    */
   public List<TreeNode> getChildren()
   {
      return children;
   }

   /**
    * @return url to icon
    */
   public String getIcon()
   {
      return icon;
   }

   /**
    * @return name of tree node
    */
   public String getName()
   {
      return name;
   }

   /**
    * @return is folder status
    */
   public boolean isFolder()
   {
      return isFolder;
   }

   /**
    * Set Is Folder status
    * 
    * @param isFolder
    */
   public void setIsFolder(boolean isFolder)
   {
      this.isFolder = isFolder;
   }

   /**
    * @return entry
    */
   public Object getEntry()
   {
      return entry;
   }

   /**
    * Set entry
    * 
    * @param entry
    */
   public void setEntry(Object entry)
   {
      this.entry = entry;
   }

}
