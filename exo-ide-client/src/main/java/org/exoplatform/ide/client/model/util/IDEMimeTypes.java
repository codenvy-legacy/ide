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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.exoplatform.gwtframework.commons.rest.MimeType;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class IDEMimeTypes
{

   private static HashMap<String, List<String>> mimeTypes = new HashMap<String, List<String>>();

   private static HashMap<String, String> extensions = new HashMap<String, String>();

   private static void add(String extension, String mimeType)
   {
      if (mimeTypes.containsKey(extension))
         mimeTypes.get(extension).add(mimeType);
      else
      {
         List<String> list = new ArrayList<String>();
         list.add(mimeType);
         mimeTypes.put(extension, list);
      }
      if (!extensions.containsKey(mimeType))
      {
         extensions.put(mimeType, extension);
      }
   }

   static
   {
      add("html", MimeType.TEXT_HTML);
      add("htm", MimeType.TEXT_HTML);
      add("css", MimeType.TEXT_CSS);
      add("txt", MimeType.TEXT_PLAIN);
      add("js", MimeType.TEXT_JAVASCRIPT);
      add("js", MimeType.APPLICATION_JAVASCRIPT);
      add("js", MimeType.APPLICATION_X_JAVASCRIPT);
      add("xml", MimeType.TEXT_XML);
      add("grs", MimeType.GROOVY_SERVICE);
      add("groovy", MimeType.APPLICATION_GROOVY);
      add("groovyclasspath", MimeType.APPLICATION_JSON);
      add("xml", MimeType.GOOGLE_GADGET);
      add("html", MimeType.UWA_WIDGET);
      add("gtmpl", MimeType.GROOVY_TEMPLATE);      
      add("groovy", MimeType.CHROMATTIC_DATA_OBJECT);
      add("json", MimeType.APPLICATION_JSON);      
   }

   public static boolean isMimeTypeSupported(String mimeType)
   {
      return !(mimeTypes.get(mimeType) == null);
   }

   public static Set<String> getExtensions()
   {
      return mimeTypes.keySet();
   }

   public static List<String> getMimeTypes(String fileName)
   {
      String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);

      List<String> mimeType = mimeTypes.get(fileExtension);
      return mimeType;
   }

   public static HashMap<String, String> getExtensionsMap()
   {
      return extensions;
   }
   
   public static List<String> getSupportedMimeTypes()
   {
      List<String> supportedMimeTypes = new ArrayList<String>();
      
      for (List<String> mimeTypeList : mimeTypes.values())
      {
         for (String mimeType : mimeTypeList)
         {
            if (!supportedMimeTypes.contains(mimeType))
            {
               supportedMimeTypes.add(mimeType);
            }
         }
      }
      
      return supportedMimeTypes;
   }

}
