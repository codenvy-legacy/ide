/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.api.resources;

import com.google.gwt.resources.client.ImageResource;

import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;

/**
 * FileType is meta information about file.
 * It's contains
 * <ul>
 * <li> <code>image</code> - image resource associated with file
 * <li> <code>mimeTypes</code> - array of mime types associated with file 
 * <li> <code>extension</code> - extension associated with file
 * <li> <code>namePattern</code> - name pattern
 * </ul>
 * <p>Mime types is array in case when one file type can describe several mime types.(For example JavaScript file can have:
 * "application/javascript", "application/x-javascript", "text/javascript" mime types)
 * <p>
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class FileType
{
   
   private static int ID = 0;
   
   private int id;
   
   private ImageResource image;

   private JsonArray<String> mimeTypes;

   private String extension;

   private String namePattern;
   

   public FileType(ImageResource image, String mimeType, String extension)
   {
      this(image, JsonCollections.createArray(mimeType), extension);
   }

   public FileType(ImageResource image, JsonArray<String> mimeTypes, String extension)
   {
      this(image, mimeTypes, extension, null);
   }

   public FileType(ImageResource image, String namePattern)
   {
      this(image, null, null, namePattern);
   }

   private FileType(ImageResource image, JsonArray<String> mimeTypes, String extension, String namePattern)
   {
      super();
      this.image = image;
      this.mimeTypes = mimeTypes;
      this.extension = extension;
      this.namePattern = namePattern;
      id = ++ID;
   }

   /**
    * @return the mimeTypes
    */
   public JsonArray<String> getMimeTypes()
   {
      return mimeTypes;
   }

   /**
    * @return the extension
    */
   public String getExtension()
   {
      return extension;
   }

   /**
    * @return the namePatterns
    */
   public String getNamePattern()
   {
      return namePattern;
   }

   /**
    * @return the image
    */
   public ImageResource getImage()
   {
      return image;
   }

   public int getId()
   {
      return id;
   }
}
