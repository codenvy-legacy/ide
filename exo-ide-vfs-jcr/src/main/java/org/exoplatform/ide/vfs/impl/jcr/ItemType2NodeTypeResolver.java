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

import javax.ws.rs.core.MediaType;

/**
 * Provide JCR node type name dependent to specified media or item type.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class ItemType2NodeTypeResolver
{
   /**
    * By default return 'nt:file' always.
    */
   public String getFileNodeType(MediaType mediaType)
   {
      return "nt:file";
   }

   /**
    * By default return 'nt:resource' always.
    */
   public String getFileContentNodeType(MediaType mediaType)
   {
      return "nt:resource";
   }

   /**
    * Get mixin for adding to JCR file node.
    * 
    * @param type type of project
    * @return mixins set of mixin node types
    */
   public String[] getFileMixins(MediaType mediaType)
   {
      // TODO
      return null;
   }

   /**
    * By default return 'nt:folder' always.
    */
   public String getFolderNodeType(String type)
   {
      return "nt:folder";
   }

   /**
    * Get mixin for adding to JCR folder node.
    * 
    * @param type type of folder
    * @return mixins set of mixin node types
    */
   public String[] getFolderMixins(String type)
   {
      if ("project".equalsIgnoreCase(type))
         return new String[]{"vfs:project"};
      // TODO
      return null;
   }
}
