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
package org.exoplatform.ide.codeassistant.jvm.serialization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains set of operations for more efficient object Externalization. String
 * objects will be saved as array of bytes in UTF-8 format. For serialization
 * and deserialization of objects where it's possible will be used they own
 * Externalization mechanism.
 */
public class ExternalizationTools
{
   private static final Logger LOG = LoggerFactory.getLogger(ExternalizationTools.class);

   private static final String UTF_8 = "UTF-8";

   private ExternalizationTools()
   {
   }

   public static void writeStringUTF(String string, ObjectOutput out) throws UnsupportedEncodingException, IOException
   {
      byte[] bytes = string.getBytes(UTF_8);
      out.writeInt(bytes.length);
      out.write(bytes);
   }

   public static void writeStringUTFList(List<String> list, ObjectOutput out) throws IOException
   {
      out.writeInt(list.size());

      for (String element : list)
      {
         writeStringUTF(element, out);
      }
   }

   public static <T> void writeObjectList(Class<T> type, List<T> list, ObjectOutput out) throws IOException
   {
      out.writeInt(list.size());

      for (T element : list)
      {
         if (Externalizable.class.isAssignableFrom(type))
         {
            ((Externalizable)element).writeExternal(out);
         }
         else
         {
            out.writeObject(element);
         }
      }
   }

   public static String readStringUTF(ObjectInput in) throws IOException
   {
      int length = in.readInt();
      byte[] bytes = new byte[length];
      in.read(bytes);

      return new String(bytes, UTF_8);
   }

   public static List<String> readStringUTFList(ObjectInput in) throws IOException
   {
      int size = in.readInt();
      List<String> result = new ArrayList<String>(size);

      for (int i = 0; i < size; i++)
      {
         result.add(readStringUTF(in));
      }

      return result;
   }

   public static <T> List<T> readObjectList(Class<T> type, ObjectInput in) throws IOException, ClassNotFoundException
   {
      int size = in.readInt();
      List<T> elements = new ArrayList<T>(size);
      for (int i = 0; i < size; i++)
      {
         if (Externalizable.class.isAssignableFrom(type))
         {
            try
            {
               T element = type.newInstance();
               ((Externalizable)element).readExternal(in);
               elements.add(element);
            }
            catch (InstantiationException e)
            {
               LOG.warn("Can't instantiate component of type " + type.getCanonicalName(), e);
            }
            catch (IllegalAccessException e)
            {
               LOG.warn("Can't instantiate component of type " + type.getCanonicalName(), e);
            }
         }
         else
         {
            elements.add((T)in.readObject());
         }
      }
      return elements;
   }
}
