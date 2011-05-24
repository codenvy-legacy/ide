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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Heroku
{
   static interface Setter
   {
      void setValue(Object inst, Field f, String value);
   }

   static abstract class FieldSetter implements Setter
   {
      @Override
      public void setValue(Object inst, Field f, String value)
      {
         if (!Modifier.isPublic(f.getModifiers()))
            f.setAccessible(true);
         try
         {
            f.set(inst, convertValue(value));
         }
         catch (IllegalAccessException e)
         {
            // Access checked before and set to true.
            throw new RuntimeException(e.getMessage());
         }
      }
      
      protected abstract Object convertValue(String source);
   }
   
   static class StringFieldSetter extends FieldSetter
   {
      @Override
      protected Object convertValue(String source)
      {
         return source;
      }
   }
   
   static class BooleanFieldSetter extends FieldSetter
   {
      @Override
      protected Object convertValue(String source)
      {
         return Boolean.parseBoolean(source);
      }
   }

   private static Map<Class<?>, Heroku.Setter> setters = new HashMap<Class<?>, Heroku.Setter>();

   static
   {
      setters.put(String.class, new StringFieldSetter());
      setters.put(Boolean.class, new BooleanFieldSetter());
      setters.put(boolean.class, new BooleanFieldSetter());
   }

   private static class HerokuHolder
   {
      private static final Heroku INSTANCE = new Heroku();
   }

   public static Heroku getInstance()
   {
      return HerokuHolder.INSTANCE;
   }

   public static final String HEROKU_API = "https://api.heroku.com";

   private final Map<String, Class<? extends HerokuCommand>> commands;

   private Heroku()
   {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      commands = new HashMap<String, Class<? extends HerokuCommand>>();
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
                  commands.put(name(c), c);
               }
               catch (ClassNotFoundException ignored)
               {
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
      /*for (Map.Entry<String, Class<? extends HerokuCommand>> e : commands.entrySet())
         System.out.println("\t" + e.getKey() + "\t: " + e.getValue());*/
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

   public Object execute(String name, Map<String, String> opts, List<String> args, File workDir)
      throws HerokuException, CommandException
   {
      Class<? extends HerokuCommand> c = commands.get(name);
      if (c == null)
         throw new IllegalArgumentException("Unsupported command " + name);
      HerokuCommand command = init(c, opts, args, workDir);
      Object result = command.execute();
      return result;
   }

   private static HerokuCommand init(Class<? extends HerokuCommand> c, Map<String, String> opts, List<String> args,
      File workDir)
   {
      HerokuCommand command;
      try
      {
         Constructor<? extends HerokuCommand> constr = c.getDeclaredConstructor(File.class);
         command = constr.newInstance(workDir);
      }
      catch (NoSuchMethodException e)
      {
         throw new RuntimeException(e.getMessage(), e);
      }
      catch (InstantiationException e)
      {
         throw new RuntimeException(e.getMessage(), e);
      }
      catch (IllegalAccessException e)
      {
         throw new RuntimeException(e.getMessage(), e);
      }
      catch (InvocationTargetException e)
      {
         throw new RuntimeException(e.getMessage(), e);
      }
      for (@SuppressWarnings("rawtypes")
      Class k = c; k != Object.class; k = k.getSuperclass())
      {
         Field[] fields = k.getDeclaredFields();
         for (int i = 0; i < fields.length; i++)
         {
            Field field = fields[i];
            if (field.isAnnotationPresent(Option.class))
            {
               Option option = field.getAnnotation(Option.class);
               String value = opts != null ? opts.get(option.name()) : null;
               if (value == null)
               {
                  Default def = field.getAnnotation(Default.class);
                  if (def != null)
                     value = def.value();
               }
               if (value == null && option.required())
                  throw new RuntimeException("Required option " + option.name() + " is not initialized. ");
               initField(command, field, value);
            }
            else if (field.isAnnotationPresent(Arg.class))
            {
               Arg arg = field.getAnnotation(Arg.class);
               int index = arg.index();
               String value = null;
               if (args != null && index < args.size())
                  value = args.get(arg.index());
               if (value == null)
               {
                  Default def = field.getAnnotation(Default.class);
                  if (def != null)
                     value = def.value();
               }
               initField(command, field, value);
            }
         }
      }
      return command;
   }

   private static void initField(Object inst, Field f, String value)
   {
      setters.get(f.getType()).setValue(inst, f, value);
   }
}
