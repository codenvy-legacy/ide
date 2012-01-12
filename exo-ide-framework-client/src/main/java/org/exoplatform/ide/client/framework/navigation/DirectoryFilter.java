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

package org.exoplatform.ide.client.framework.navigation;

import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class DirectoryFilter
{

   private static DirectoryFilter instance;

   public static DirectoryFilter get()
   {
      if (instance == null)
      {
         instance = new DirectoryFilter();
      }

      return instance;
   }

   private String[] parts = new String[0];

   public void setPattern(String pattern)
   {
      parts = pattern.split(";");
   }

   public List<Item> filter(List<Item> items)
   {
      List<Item> result = new ArrayList<Item>();

      for (Item item : items)
      {
         if (parts == null || parts.length == 0 || !matchWithPattern(item.getName()))
         {
            result.add(item);
         }
      }

      return result;
   }

   public boolean matchWithPattern(String text)
   {
      try
      {
         for (String p : parts)
         {
            p = p.trim();

            if (p == null || "".equals(p))
            {
               continue;
            }

            /*
             * Matches for *characters
             */
            if (p.startsWith("*"))
            {
               String work = p.substring(1);
               if (text.toLowerCase().endsWith(work.toLowerCase()))
               {
                  return true;
               }
               continue;
            }

            /*
             * Matches for characters*
             */
            if (p.endsWith("*"))
            {
               String work = p.substring(0, p.length() - 1);
               if (text.toLowerCase().startsWith(work.toLowerCase()))
               {
                  return true;
               }
               continue;
            }

            /*
             * Matches for whole name
             */
            if (text.equalsIgnoreCase(p))
            {
               return true;
            }
         }

         return false;
      }

      catch (Exception e)
      {
         return false;
      }
   }

}
