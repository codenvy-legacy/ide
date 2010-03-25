/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.model.util;

import java.util.HashMap;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ideall.client.Images;

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
      images.put(MimeType.TEXT_HTML, Images.FileTypes.HTML);
      images.put(MimeType.TEXT_XML, Images.FileTypes.XML);
      images.put(MimeType.TEXT_PLAIN, Images.FileTypes.TXT);
      images.put(MimeType.SCRIPT_GROOVY, Images.FileTypes.GROOVY);
      images.put(MimeType.GOOGLE_GADGET, Images.FileTypes.GADGET);
      images.put(MimeType.APPLICATION_JAVASCRIPT, Images.FileTypes.JAVASCRIPT);
      images.put(MimeType.TEXT_JAVASCRIPT, Images.FileTypes.JAVASCRIPT);
      images.put(MimeType.APPLICATION_X_JAVASCRIPT, Images.FileTypes.JAVASCRIPT);
      images.put(MimeType.TEXT_CSS, Images.FileTypes.CSS);
   }

   public static String getIcon(String mimeType)
   {
      System.out.println("tru get icon for: " + mimeType);
      
      String icon = images.get(mimeType);
      if (icon == null)
      {
         icon = Images.FileTypes.DEFAULT;
      }
      return icon;
   }

}
