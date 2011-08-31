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
package org.exoplatform.ide.editor.api;

import com.google.gwt.resources.client.ImageResource;

import java.util.HashMap;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: EditorProducer Feb 9, 2011 4:37:14 PM evgen $
 *
 */
public abstract class EditorProducer
{
   public final String description;

   private String mimeType;

   private String exstension;

   private boolean isDefault;

   private ImageResource icon;

   protected EditorProducer(String mimeType, String description, String ext, ImageResource icon, boolean isDefault)
   {
      this.description = description;
      this.mimeType = mimeType;
      this.isDefault = isDefault;
      this.exstension = ext;
      this.icon = icon;
   }

   /**
    * @return mime type associated with this editor
    */
   public String getMimeType()
   {
      return mimeType;
   }

   /**
    * @return Editor description
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * @return file extension(i.e. "java", "xml" etc.)
    */
   public String getDefaultFileExtension()
   {
      return exstension;
   }

   /**
    * @return true if editor default for current mime type 
    */
   public boolean isDefault()
   {
      return isDefault;
   }

   /**
    * @return the icon URL to image associated with current mime type
    */
   public ImageResource getIcon()
   {
      return icon;
   }

   /**
    * Create new {@link Editor} widget
    * @param content
    * @param eventBus
    * @param params editor configuration parameters
    * @return instance of {@link Editor} class
    */
   public abstract Editor createEditor(String content, HandlerManager eventBus, HashMap<String, Object> params);

}
