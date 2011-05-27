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
package org.exoplatform.ide.extension.heroku.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Heroku
{
   private static class HerokuHolder
   {
      private static final Heroku INSTANCE = new Heroku();
   }

   public static Heroku getInstance()
   {
      return HerokuHolder.INSTANCE;
   }

   /** Base URL of heroku REST API. */
   public static final String HEROKU_API = "https://api.heroku.com";

   private final Map<String, HerokuCommand> commands;

   private Heroku()
   {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      commands = new HashMap<String, HerokuCommand>();
      String catalog = "META-INF/services/" + HerokuCommand.class.getName();
      InputStream input = cl.getResourceAsStream(catalog);
      BufferedReader reader = null;
      try
      {
         reader = new BufferedReader(new InputStreamReader(input, "utf-8"));
         String line = null;
         while ((line = reader.readLine()) != null)
         {
            if (line.length() > 0 && line.charAt(0) != '#')
            {
               try
               {
                  Class<? extends HerokuCommand> c = Class.forName(line, true, cl).asSubclass(HerokuCommand.class);
                  commands.put(name(c), c.newInstance());
               }
               catch (ClassNotFoundException ignored)
               {
                  // Ignore at this stage as result command will be not available.
               }
               catch (InstantiationException ignored)
               {
                  // Ignore at this stage as result command will be not available.
               }
               catch (IllegalAccessException ignored)
               {
                  // Ignore at this stage as result command will be not available.
               }
            }
         }
      }
      catch (IOException ioe)
      {
         throw new RuntimeException("Failed to read commands catalog. " + ioe.getMessage());
      }
      finally
      {
         try
         {
            if (reader != null)
               reader.close();
         }
         catch (IOException ignored)
         {
         }
         try
         {
            input.close();
         }
         catch (IOException ignored)
         {
         }
      }
      /*for (Map.Entry<String, HerokuCommand> e : commands.entrySet())
         System.out.println("\t" + e.getKey() + "\t: " + e.getValue().getClass().getName());*/
   }

   private static String name(Class<? extends HerokuCommand> c)
   {
      char[] clname = c.getSimpleName().toCharArray();
      StringBuilder name = new StringBuilder();
      for (int i = 0; i < clname.length; i++)
      {
         if (Character.isUpperCase(clname[i]))
         {
            if (name.length() > 0)
               name.append(':');
            name.append(Character.toLowerCase(clname[i]));
         }
         else
         {
            name.append(clname[i]);
         }
      }
      return name.toString();
   }

   public HerokuCommand getCommand(String name)
   {
      HerokuCommand c = commands.get(name);
      if (c == null)
         throw new IllegalArgumentException("Unsupported command " + name);
      return c;
   }
}
