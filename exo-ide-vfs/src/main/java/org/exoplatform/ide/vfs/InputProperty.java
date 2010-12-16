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
package org.exoplatform.ide.vfs;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;

/**
 * Input property. It will be restored from JSON source.To simplify JSON
 * structure we accept all values as array of String. It is implementation
 * specific how to transform it to required types. Values may be transformed to
 * required type via method {@link #valueAs(Class)}.
 * <p>
 * Here is example of JSON source for input property:
 * 
 * <pre>
 * {"name":"mediaType", "value":["text/plain;charset=utf8"]}"
 * </pre>
 * 
 * @see #valueAs(Class)
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class InputProperty
{
   /** Name of property. */
   private String name;

   /** Value of property. */
   private String[] value;

   /**
    * @return name of property
    */
   public String getName()
   {
      return name;
   }

   /**
    * @param name the name of property
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return value of property
    */
   public String[] getValue()
   {
      return value;
   }

   /**
    * @param value the value of property
    */
   public void setValue(String[] value)
   {
      this.value = value;
   }

   /**
    * Get value as array of specified type. Components of new array must be as
    * one of followed:
    * <ul>
    * <li>has String type</li>
    * <li>has constructor that accepts String as argument</li>
    * <li>has static method 'valueOf' that accepts String as argument</li>
    * </ul>
    * <p>
    * Example:
    * </p>
    * 
    * <pre>
    * InputProperty in = new InputProperty();
    * in.setValue(new String[]{&quot;123&quot;, &quot;456&quot;});
    * Integer[] res = in.valueAs(Integer[].class);
    * System.out.println(java.util.Arrays.toString(res));
    * </pre>
    * 
    * As result should be: [123, 456]
    * 
    * @param toType new array type
    * @return new array of specified type
    * @throws IllegalArgumentException if specified new array type is not as
    *            described above
    */
   @SuppressWarnings("unchecked")
   public <O> O[] valueAs(Class<? extends O[]> toType)
   {
      if (value == null)
         return null;
      final Class<?> componentType = toType.getComponentType();
      // If String then just copy array.
      if (componentType == String.class)
      {
         String[] a = new String[value.length];
         System.arraycopy(value, 0, a, 0, a.length);
         return (O[])a;
      }

      // Look up Constructor with String parameter. 
      Constructor<?> stringConstr = null;
      try
      {
         stringConstr = AccessController.doPrivileged(new PrivilegedExceptionAction<Constructor<?>>() {
            public Constructor<?> run() throws Exception
            {
               return componentType.getConstructor(String.class);
            }
         });
      }
      catch (PrivilegedActionException e)
      {
         Throwable cause = e.getCause();
         if (cause instanceof NoSuchMethodException)
            ; // ignored
         else if (cause instanceof RuntimeException)
            throw (RuntimeException)cause;
         else
            throw new RuntimeException(cause);
      }
      if (stringConstr != null)
      {
         Object a = (O[])Array.newInstance(componentType, value.length);
         for (int i = 0; i < value.length; i++)
         {
            try
            {
               Array.set(a, i, stringConstr.newInstance(value[i]));
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
               throw new RuntimeException(e);
            }
            catch (IllegalArgumentException e)
            {
               throw new RuntimeException(e);
            }
            catch (InstantiationException e)
            {
               throw new RuntimeException(e);
            }
            catch (IllegalAccessException e)
            {
               throw new RuntimeException(e);
            }
            catch (InvocationTargetException e)
            {
               throw new RuntimeException(e);
            }
         }
         return (O[])a;
      }

      // Look up static Method 'valueOf' with String parameter. 
      Method valueOf = null;
      try
      {
         Method temp = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception
            {
               return componentType.getDeclaredMethod("valueOf", String.class);
            }
         });
         if (Modifier.isStatic(temp.getModifiers()))
            valueOf = temp;
      }
      catch (PrivilegedActionException e)
      {
         Throwable cause = e.getCause();
         if (cause instanceof NoSuchMethodException)
            ; // ignored
         else if (cause instanceof RuntimeException)
            throw (RuntimeException)cause;
         else
            throw new RuntimeException(cause);
      }
      if (valueOf != null)
      {
         Object a = (O[])Array.newInstance(componentType, value.length);
         for (int i = 0; i < value.length; i++)
         {
            try
            {
               Array.set(a, i, valueOf.invoke(null, value[i]));
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
               throw new RuntimeException(e);
            }
            catch (IllegalArgumentException e)
            {
               throw new RuntimeException(e);
            }
            catch (IllegalAccessException e)
            {
               throw new RuntimeException(e);
            }
            catch (InvocationTargetException e)
            {
               throw new RuntimeException(e);
            }
         }
         return (O[])a;
      }
      throw new IllegalArgumentException("Unsupported type " + componentType.getName()
         + ". Must have Constructor with one String argument or static method 'valueOf' with String argument. ");
   }

   /**
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      return "<" + name + ": " + Arrays.toString(value) + ">";
   }
}
