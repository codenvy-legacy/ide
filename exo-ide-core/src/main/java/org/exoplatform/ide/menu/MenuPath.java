/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
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
package org.exoplatform.ide.menu;

import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.util.StringUtils;

/**
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public class MenuPath
{
   public static final String PATH_SEPARATOR = "/";

   JsonArray<String> pathElemetns;

   /**
    * Constructs MenuPath from String 
    */
   public MenuPath(String path)
   {
      // trim "/" at the beginning and at the end
      if (path.startsWith(PATH_SEPARATOR))
      {
         path = path.substring(1);
      }
      if (path.endsWith(PATH_SEPARATOR))
      {
         path = path.substring(0, path.length() - 1);
      }
      pathElemetns = StringUtils.split(path, PATH_SEPARATOR);
   }

   /**
    * @return number of Path Elements
    */
   public int getSize()
   {
      return pathElemetns.size();
   }

   /**
    * The String representation of Parent's path. Having a Path "a/b/c"
    * and calling getParentPath(2) will produce "a/b" and calling with 
    * argument (1) will return "a"
    * 
    * @param depth
    * @return
    */
   public String getParentPath(int depth)
   {
      if (depth > pathElemetns.size())
      {
         depth = pathElemetns.size();
      }
      return pathElemetns.slice(0, depth).join(PATH_SEPARATOR);
   }
   
   /**
    * Retrieves path Element at given level. I.e. for a/b/c
    * calling getPathElementAt(0) returns "a"<br>
    * calling getPathElementAt(1) returns "b"<br>
    * calling getPathElementAt(2) returns "c"<br>
    * 
    * @param level
    * @return
    */
   public String getPathElementAt(int level)
   {
      if (level >= pathElemetns.size())
      {
         level = pathElemetns.size()-1;
      }
      return pathElemetns.get(level);
   }

}
