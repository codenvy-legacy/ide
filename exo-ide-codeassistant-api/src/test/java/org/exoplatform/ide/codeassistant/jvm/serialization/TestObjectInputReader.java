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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.exoplatform.ide.codeassistant.jvm.BaseTest;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 */
public class TestObjectInputReader extends BaseTest
{

   @Test
   public void shouldDeserializeCyrillicString() throws IOException
   {
      shouldDeserializeString("Кириллическая строка");
   }

   @Test
   public void shouldDeserializeLatinString() throws IOException
   {
      shouldDeserializeString("Latin string");
   }

   @Test
   public void shouldDeserializeArrayString() throws IOException
   {
      String[] serializedArray = new String[]{"one", "two", "three"};

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(out);

      oos.writeInt(serializedArray.length);

      for (String element : serializedArray)
      {
         oos.writeInt(element.length());
         oos.write(element.getBytes("UTF-8"));
      }
      oos.flush();

      ObjectInputStream io = createObjectInputStream(out.toByteArray());
      ObjectInputReader objectInputReader = new ObjectInputReader(io);
      String[] deserializedArray = objectInputReader.readStringArray();

      assertArrayEquals(serializedArray, deserializedArray);
   }

   @Test
   public void shouldNotInvokeReadObjectOnStringReading() throws IOException, ClassNotFoundException
   {
      ObjectInput in = mock(ObjectInput.class);

      ObjectInputReader objectInputReader = new ObjectInputReader(in);
      objectInputReader.readString();

      verify(in, never()).readObject();
   }

   @Test
   public void shouldNotInvokeWriteObjectOnStringArrayWritting() throws IOException, ClassNotFoundException
   {
      ObjectInput in = mock(ObjectInput.class);

      ObjectInputReader objectInputReader = new ObjectInputReader(in);
      objectInputReader.readStringArray();

      verify(in, never()).readObject();
   }

   private void shouldDeserializeString(String serializedString) throws IOException
   {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(out);

      byte[] bytes = serializedString.getBytes("UTF-8");
      oos.writeInt(bytes.length);
      oos.write(bytes);
      oos.flush();

      ObjectInputStream io = createObjectInputStream(out.toByteArray());
      ObjectInputReader objectInputReader = new ObjectInputReader(io);

      String deserializedString = objectInputReader.readString();

      assertEquals(serializedString, deserializedString);
   }

}
