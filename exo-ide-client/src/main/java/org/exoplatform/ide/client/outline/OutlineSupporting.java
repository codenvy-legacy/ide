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
package org.exoplatform.ide.client.outline;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.rest.MimeType;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class OutlineSupporting
{

   private static List<String> mimeTypes = new ArrayList<String>();

   static
   {
      mimeTypes.add(MimeType.APPLICATION_JAVASCRIPT);
      mimeTypes.add(MimeType.APPLICATION_X_JAVASCRIPT);
      mimeTypes.add(MimeType.GOOGLE_GADGET);
      mimeTypes.add(MimeType.TEXT_JAVASCRIPT);
      mimeTypes.add(MimeType.APPLICATION_XML);
      mimeTypes.add(MimeType.TEXT_XML);
      mimeTypes.add(MimeType.TEXT_HTML);
      mimeTypes.add(MimeType.GROOVY_SERVICE);
      mimeTypes.add(MimeType.APPLICATION_GROOVY);
      mimeTypes.add(MimeType.GROOVY_TEMPLATE);
      mimeTypes.add(MimeType.UWA_WIDGET);
      mimeTypes.add(MimeType.CHROMATTIC_DATA_OBJECT);
   }

   public static boolean isOutlineSupported(String mimeType)
   {
      return mimeTypes.contains(mimeType);
   }

}
