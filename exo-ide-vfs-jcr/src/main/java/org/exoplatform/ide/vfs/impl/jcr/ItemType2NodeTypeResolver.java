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
package org.exoplatform.ide.vfs.impl.jcr;

import org.exoplatform.ide.vfs.shared.Project;

import javax.ws.rs.core.MediaType;

/**
 * Provide JCR node type name dependent to specified media or item type.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class ItemType2NodeTypeResolver
{
   public ItemType2NodeTypeResolver()
   {
   }

   /**
    * By default return 'nt:file' always.
    */
   public String getFileNodeType(String mediaType)
   {
      return "nt:file";
   }

   public final String getFileNodeType(MediaType mediaType)
   {
      if (mediaType == null)
         return getFileNodeType((String)null);
      return getFileNodeType((mediaType.getType() + "/" + mediaType.getSubtype()));
   }

   /**
    * By default return 'nt:resource' always.
    */
   public String getFileContentNodeType(String mediaType)
   {
      return "nt:resource";
   }

   public final String getFileContentNodeType(MediaType mediaType)
   {
      if (mediaType == null)
         return getFileContentNodeType((String)null);
      return getFileContentNodeType((mediaType.getType() + "/" + mediaType.getSubtype()));
   }

   /**
    * Get mixin for adding to JCR file node.
    * 
    * @param type type of file
    * @return mixins set of mixin node types
    */
   public String[] getFileMixins(String mediaType)
   {
      return null;
   }

   public final String[] getFileMixins(MediaType mediaType)
   {
      if (mediaType == null)
         return getFileMixins((String)null);
      return getFileMixins((mediaType.getType() + "/" + mediaType.getSubtype()));
   }

   /**
    * By default return 'nt:folder' always.
    */
   public String getFolderNodeType(String mediaType)
   {
      return "nt:folder";
   }

   public final String getFolderNodeType(MediaType mediaType)
   {
      if (mediaType == null)
         return getFolderNodeType((String)null);
      return getFolderNodeType((mediaType.getType() + "/" + mediaType.getSubtype()));
   }

   /**
    * Get mixin for adding to JCR folder node.
    * 
    * @param type type of folder
    * @return mixins set of mixin node types
    */
   public String[] getFolderMixins(String mediaType)
   {
      /*if (mediaType == null)
         return null;
      if (mediaType.equalsIgnoreCase(Project.PROJECT_MIME_TYPE))
         return new String[]{"vfs:project"};
      return new String[]{"vfs:folder"};*/
      if (mediaType == null)
         return null;
      if (mediaType.equalsIgnoreCase(Project.PROJECT_MIME_TYPE))
         return new String[]{"vfs:mixunstructured"};
      return null;
   }

   public final String[] getFolderMixins(MediaType mediaType)
   {
      if (mediaType == null)
         return getFolderMixins((String)null);
      return getFolderMixins((mediaType.getType() + "/" + mediaType.getSubtype()));
   }
}
