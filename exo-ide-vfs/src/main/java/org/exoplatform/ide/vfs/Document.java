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
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class Document extends Item
{
   private String versionId;

   private String contentType;

   private long length = -1;

   public Document(String id, String path, long creationDate, long lastModificationDate, String versionId,
      String contentType, long length, boolean locked, List<OutputProperty> properties)
   {
      super(id, Type.DOCUMENT, path, creationDate, lastModificationDate, locked, properties);
      this.versionId = versionId;
      this.contentType = contentType;
      this.length = length;
   }

   public Document()
   {
      super();
   }

   public String getVersionId()
   {
      return versionId;
   }

   public void setVersionId(String versionId)
   {
      this.versionId = versionId;
   }

   public String getContentType()
   {
      return contentType;
   }

   public void setContentType(String contentType)
   {
      this.contentType = contentType;
   }

   public long getLength()
   {
      return length;
   }

   public void setLength(long length)
   {
      this.length = length;
   }
}
