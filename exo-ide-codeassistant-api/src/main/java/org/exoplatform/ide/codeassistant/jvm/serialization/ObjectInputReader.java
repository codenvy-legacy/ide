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
import java.io.ObjectInput;

/**
 *
 */
public class ObjectInputReader
{
   private final ObjectInput in;

   public ObjectInputReader(ObjectInput in)
   {
      this.in = in;
   }

   public String readString() throws IOException
   {
      int length = in.readInt();
      byte[] bytes = new byte[length];
      in.read(bytes);

      return new String(bytes, "UTF-8");
   }

   public String[] readStringArray() throws IOException
   {
      int length = in.readInt();
      String[] array = new String[length];

      for (int i = 0; i < array.length; i++)
      {
         array[i] = readString();
      }

      return array;
   }

   private <T> T[] readArrayFromObjectInput(T[] array) throws IOException, ClassNotFoundException
   {
      for (int i = 0; i < array.length; i++)
      {
         array[i] = (T)in.readObject();
      }
      return array;
   }

}
