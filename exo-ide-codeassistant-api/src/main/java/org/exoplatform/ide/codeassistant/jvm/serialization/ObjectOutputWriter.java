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

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.UnsupportedEncodingException;

/**
 *
 */
public class ObjectOutputWriter
{
   private final ObjectOutput out;

   public ObjectOutputWriter(ObjectOutput out)
   {
      this.out = out;
   }

   public void writeString(String string) throws UnsupportedEncodingException, IOException
   {
      byte[] bytes = string.getBytes("UTF-8");
      out.writeInt(bytes.length);
      out.write(bytes);
   }

   public <T> void writeObjectArray(T[] array) throws IOException
   {
      out.writeInt(array.length);

      if (array instanceof String[])
      {
         for (T element : array)
         {
            writeString((String)element);
         }
      }
      else
      {
         for (T element : array)
         {
            out.writeObject(element);
         }
      }
   }
}
