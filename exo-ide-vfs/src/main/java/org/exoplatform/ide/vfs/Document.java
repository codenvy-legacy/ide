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
package org.exoplatform.ide.vfs;

import java.util.List;

/**
 * Representation of Document object used to interaction with client via JSON.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class Document extends Item
{
   /** Identifier of version of document. */
   private String versionId;

   /** Content type. */
   private String contentType;

   /** Content length. */
   private long length = -1;

   /**
    * Instance of Document with specified attributes.
    * 
    * @param id identifier of object
    * @param path path of object
    * @param creationDate creation date in long format
    * @param lastModificationDate date of last modification in long format
    * @param versionId identifier of versions of document
    * @param contentType content type
    * @param length content length
    * @param locked is document locked or not
    * @param properties other properties of document
    */
   public Document(String id, String path, long creationDate, long lastModificationDate, String versionId,
      String contentType, long length, boolean locked, List<OutputProperty> properties)
   {
      super(id, Type.DOCUMENT, path, creationDate, lastModificationDate, locked, properties);
      this.versionId = versionId;
      this.contentType = contentType;
      this.length = length;
   }

   /**
    * Empty instance of Document.
    */
   public Document()
   {
      super();
   }

   /**
    * @return version identifier
    */
   public String getVersionId()
   {
      return versionId;
   }

   /**
    * @param versionId the version identifier
    */
   public void setVersionId(String versionId)
   {
      this.versionId = versionId;
   }

   /**
    * @return content type
    */
   public String getContentType()
   {
      return contentType;
   }

   /**
    * @param contentType the content type
    */
   public void setContentType(String contentType)
   {
      this.contentType = contentType;
   }

   /**
    * @return content length
    */
   public long getLength()
   {
      return length;
   }

   /**
    * @param length the content length
    */
   public void setLength(long length)
   {
      this.length = length;
   }
}
