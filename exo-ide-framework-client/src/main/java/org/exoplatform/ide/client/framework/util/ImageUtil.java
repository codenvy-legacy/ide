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
package org.exoplatform.ide.client.framework.util;

import com.google.gwt.resources.client.ImageResource;

import org.exoplatform.ide.client.framework.ui.IconImageBundle;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Project;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class ImageUtil
{

   private static HashMap<String, ImageResource> images = new HashMap<String, ImageResource>();

   static
   {
      images.put(null, IconImageBundle.INSTANCE.defaultFile());
      images.put(Folder.FOLDER_MIME_TYPE, IconImageBundle.INSTANCE.folder());
      images.put(Project.PROJECT_MIME_TYPE, IconImageBundle.INSTANCE.project());
   }

   public static void putIcon(String mimeType, ImageResource imageResource)
   {
      images.put(mimeType, imageResource);
   }

   public static ImageResource getIcon(String mimeType)
   {
      ImageResource icon = images.get(mimeType);
      if (icon == null)
      {
         icon = IconImageBundle.INSTANCE.defaultFile();
      }
      return icon;
   }

   public static Map<String, ImageResource> getIcons()
   {
      return images;
   }

   /**
    * @param newFile
    * @return
    */
   public static ImageResource getIcon(FileModel file)
   {
      return getIcon(file.getMimeType());
   }

}
