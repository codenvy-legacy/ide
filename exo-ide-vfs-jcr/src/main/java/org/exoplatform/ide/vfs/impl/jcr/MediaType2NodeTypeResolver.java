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

import org.exoplatform.ide.vfs.shared.Project;

import javax.ws.rs.core.MediaType;

/**
 * Provide JCR node types name dependent to specified media type.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: MediaType2NodeTypeResolver.java 79579 2012-02-17 13:27:25Z andrew00x $
 */
public class MediaType2NodeTypeResolver
{
   /**
    * Get file node type for specified media type. By default return 'nt:file' always.
    *
    * @param mediaType media type
    * @return file node type
    */
   public String getFileNodeType(String mediaType)
   {
      return "nt:file";
   }

   public final String getFileNodeType(MediaType mediaType)
   {
      if (mediaType == null)
      {
         return getFileNodeType((String)null);
      }
      return getFileNodeType((mediaType.getType() + "/" + mediaType.getSubtype()));
   }

   /**
    * Get file content node type for specified media type. By default return 'nt:resource' always.
    *
    * @param mediaType media type
    * @return file content node type
    */
   public String getFileContentNodeType(String mediaType)
   {
      return "nt:resource";
   }

   public final String getFileContentNodeType(MediaType mediaType)
   {
      if (mediaType == null)
      {
         return getFileContentNodeType((String)null);
      }
      return getFileContentNodeType((mediaType.getType() + "/" + mediaType.getSubtype()));
   }

   /**
    * Get mixin for adding to JCR file node.
    *
    * @param mediaType media type
    * @return set of mixin node types
    */
   public String[] getFileMixins(String mediaType)
   {
      return null;
   }

   public final String[] getFileMixins(MediaType mediaType)
   {
      if (mediaType == null)
      {
         return getFileMixins((String)null);
      }
      return getFileMixins((mediaType.getType() + "/" + mediaType.getSubtype()));
   }

   //

   /**
    * Get folder node type for specified media type. By default return 'nt:folder' always.
    *
    * @param mediaType type of folder
    * @return folder node type
    */
   public String getFolderNodeType(String mediaType)
   {
      return "nt:folder";
   }

   public final String getFolderNodeType(MediaType mediaType)
   {
      if (mediaType == null)
      {
         return getFolderNodeType((String)null);
      }
      return getFolderNodeType((mediaType.getType() + "/" + mediaType.getSubtype()));
   }

   /**
    * Get mixin for adding to JCR folder node.
    *
    * @param mediaType type of folder
    * @return set of mixin node types
    */
   public String[] getFolderMixins(String mediaType)
   {
      if (mediaType == null)
      {
         return null;
      }
      if (mediaType.equalsIgnoreCase(Project.PROJECT_MIME_TYPE))
      {
         return new String[]{"vfs:project"};
      }
      return new String[]{"vfs:folder"};
   }

   public final String[] getFolderMixins(MediaType mediaType)
   {
      if (mediaType == null)
      {
         return getFolderMixins((String)null);
      }
      return getFolderMixins((mediaType.getType() + "/" + mediaType.getSubtype()));
   }
}
