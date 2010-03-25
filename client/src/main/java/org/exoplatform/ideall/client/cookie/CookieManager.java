/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ideall.client.cookie;

import java.util.HashMap;
import java.util.Iterator;

import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.vfs.api.File;

import com.google.gwt.user.client.Cookies;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CookieManager
{

   private static interface Cookie
   {

      static final String OPENED_FILES = "opened-files";

      static final String ACTIVE_FILE = "active-file";

      static final String INTRY_POINT = "entry-point";

   }

   private static final String OPENED_FILES_DELIMITER = "#";

   private static native String javaScriptDecodeURIComponent(String text) /*-{
                return decodeURIComponent(text);
             }-*/;

   private static native String javaScriptEncodeURIComponent(String text) /*-{
                return encodeURIComponent(text);
             }-*/;

   /**
    * Storing Application context to browser cookies.
    * Stores opened files, active file.
    * 
    * @param context
    */
   public static void storeOpenedFiles(ApplicationContext context)
   {
      storeOpenedFiles(context.getOpenedFiles());
      storeActiveFile(context.getActiveFile());
   }

   /**
    * Store paths of opened files
    * 
    * @param openedFiles
    */
   private static void storeOpenedFiles(HashMap<String, File> openedFiles)
   {
      String files = "";
      Iterator<String> iterator = openedFiles.keySet().iterator();
      while (iterator.hasNext())
      {
         String path = iterator.next();

         File file = openedFiles.get(path);
         if (file.isNewFile())
         {
            continue;
         }

         if (!"".equals(files))
         {
            files += OPENED_FILES_DELIMITER;
         }
         files += javaScriptEncodeURIComponent(path);
      }

      Cookies.setCookie(Cookie.OPENED_FILES, files);
   }

   /**
    * Store active file ( edited file in browser )
    *      
    * @param activeFile
    */
   private static void storeActiveFile(File file)
   {
      //      TODO
      if (file == null)
      {
         Cookies.removeCookie(Cookie.ACTIVE_FILE);
      }
      else
      {
         String fileHref = javaScriptEncodeURIComponent(file.getHref());
         Cookies.setCookie(Cookie.ACTIVE_FILE, fileHref);
      }
   }

   /**
    * Store name of entry point
    * 
    * @param entryPoint
    */
   public static void storeEntryPoint(String entryPoint)
   {
      if (entryPoint == null)
      {
         Cookies.removeCookie(Cookie.INTRY_POINT);
      }
      else
      {
         Cookies.setCookie(Cookie.INTRY_POINT, entryPoint);
      }
   }

   /**
    * Restore Application context from Browser cookies.
    * Restores paths of opened files, active file, selected repository and workspace names.
    */
   public static void getApplicationState(ApplicationContext context)
   {
      String entryPoint = Cookies.getCookie(Cookie.INTRY_POINT);

      context.setEntryPoint(entryPoint);

      restoreOpenedFiles(context);
      restoreActiveFile(context);
   }

   private static void restoreOpenedFiles(ApplicationContext context)
   {
      String openedFilesCookie = Cookies.getCookie(Cookie.OPENED_FILES);
      if (openedFilesCookie == null)
      {
         return;
      }

      String[] files = openedFilesCookie.split(OPENED_FILES_DELIMITER);
      for (String f : files)
      {
         String href = javaScriptDecodeURIComponent(f);
         if ("".equals(href))
         {
            continue;
         }

         File file = new File(href);
         context.getPreloadFiles().put(file.getHref(), file);
      }
   }

   private static void restoreActiveFile(ApplicationContext context)
   {
      String cookie = Cookies.getCookie(Cookie.ACTIVE_FILE);
      if (cookie == null)
      {
         return;
      }

      String activeFilePath = javaScriptDecodeURIComponent(cookie);
      File file = context.getPreloadFiles().get(activeFilePath);
      context.setActiveFile(file);
   }

}
