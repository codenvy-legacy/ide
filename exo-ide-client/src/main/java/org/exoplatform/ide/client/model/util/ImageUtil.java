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
package org.exoplatform.ide.client.model.util;

import org.exoplatform.ide.client.Images;

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

   private static HashMap<String, String> images = new HashMap<String, String>();

   static
   {
      images.put(null, Images.FileTypes.DEFAULT);
   }
   
   public static void putIcon(String mimeType, String icon)
   {
      images.put(mimeType, icon);
   }

   public static String getIcon(String mimeType)
   {      
      String icon = images.get(mimeType);
      if (icon == null)
      {
         icon = Images.FileTypes.DEFAULT;
      }
      return icon;
   }
   
   public static Map<String, String> getIcons()
   {
      return images;
   }

}
