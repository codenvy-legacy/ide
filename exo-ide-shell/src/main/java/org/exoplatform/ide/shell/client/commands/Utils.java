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
package org.exoplatform.ide.shell.client.commands;

import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.shell.client.EnvironmentVariables;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Aug 11, 2011 evgen $
 *
 */
public class Utils
{

   /**
    * Create new path with current folder and user entered path.
    * Respect relative paths i.e "../../a/b/../b1"
    * @param currentFolder
    * @param path to new location
    * @return new absolute path
    */
   public static String getPath(String currentFolder, String path)
   {
      if (path.startsWith("./"))
      {
         path = path.substring(2);
      }
      if (!path.endsWith("/"))
      {
         path += "/";
      }
      if (path.startsWith("/"))
      {
         return VirtualFileSystem.getInstance().getEnvironmentVariable(EnvironmentVariables.ENTRY_POINT)
            + path.substring(1);
      }
      if (!path.startsWith(".."))
      {
         if (path.startsWith("/"))
         {
            path = path.substring(1);
         }
         return currentFolder + path;
      }
      else
      {
         String[] parent = path.split("/");
         String currentPath = currentFolder;
         if (currentPath.endsWith("/"))
         {
            currentPath = currentPath.substring(0, currentPath.length() - 1);
         }
         for (String s : parent)
         {
            if (s.equals(".."))
            {
               currentPath = currentPath.substring(0, currentPath.lastIndexOf("/"));
            }
            else
            {
               currentPath += "/" + s;
            }
         }
         if (!currentPath.endsWith("/"))
            currentPath += "/";
         return currentPath;
      }
   }

   /**
    * HTML-encode a string. This simple method only replaces the five characters &, <, >, ", and '.
    * 
    * @param input the String to convert
    * @return a new String with HTML encoded characters
    */
   public static String htmlEncode(String input)
   {
      String output = input.replaceAll("&", "&amp;");
      output = output.replaceAll("<", "&lt;");
      output = output.replaceAll(">", "&gt;");
      output = output.replaceAll("\"", "&quot;");
      output = output.replaceAll("'", "&#039;");
      return output;
   }

}
