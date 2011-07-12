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
package org.exoplatform.ide.git.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class GitHelper
{
   public static void addToGitIgnore(File dir, String... rules) throws IOException
   {
      if (rules == null || rules.length == 0)
         return;

      Set<String> toAdd = new LinkedHashSet<String>(Arrays.asList(rules));

      File f = new File(dir, ".gitignore");
      FileWriter w = null;
      try
      {
         if (f.exists() && f.length() > 0)
         {
            BufferedReader r = null;
            try
            {
               r = new BufferedReader(new FileReader(f));
               for (String l = r.readLine(); l != null; l = r.readLine())
                  toAdd.remove(l.trim());
            }
            finally
            {
               if (r != null)
                  r.close();
            }

            w = new FileWriter(f, true);
            w.write('\n');
         }
         else
         {
            w = new FileWriter(f);
         }

         writeIgnore(w, toAdd);
      }
      finally
      {
         if (w != null)
         {
            w.flush();
            w.close();
         }
      }
   }

   private static void writeIgnore(FileWriter w, Set<String> rules) throws IOException
   {
      for (String l : rules)
      {
         w.write(l);
         w.write('\n');
      }
   }
}
