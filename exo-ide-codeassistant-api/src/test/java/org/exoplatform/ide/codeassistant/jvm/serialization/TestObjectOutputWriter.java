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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.exoplatform.ide.codeassistant.jvm.BaseTest;
import org.exoplatform.ide.codeassistant.jvm.serialization.ObjectOutputWriter;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;

/**
 *
 */
public class TestObjectOutputWriter extends BaseTest
{

   @Test
   public void shouldSerializeCyrillicString() throws IOException
   {
      shouldSerializeString("Кириллическая строка");
   }

   @Test
   public void shouldDeserializeLatinString() throws IOException
   {
      shouldSerializeString("Latin String");
   }

   @Test
   public void shouldSerializeObjectArray() throws IOException, ClassNotFoundException
   {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(out);
      Integer[] serializedArray = new Integer[]{1, 2, 3};

      ObjectOutputWriter objectOutputWtiter = new ObjectOutputWriter(oos);
      objectOutputWtiter.writeObjectArray(serializedArray);
      oos.flush();

      ObjectInputStream in = createObjectInputStream(out.toByteArray());
      int arrayLength = in.readInt();
      Integer[] deserializedArray = new Integer[arrayLength];
      for (int i = 0; i < arrayLength; i++)
      {
         deserializedArray[i] = (Integer)in.readObject();
      }

      assertArrayEquals(serializedArray, deserializedArray);
   }

   @Test
   public void shouldSerializeStringArray() throws IOException, ClassNotFoundException
   {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(out);
      String[] strings = new String[]{"one", "two", "tree"};

      ObjectOutputWriter objectOutputWtiter = new ObjectOutputWriter(oos);
      objectOutputWtiter.writeObjectArray(strings);
      oos.flush();

      ObjectInputStream in = createObjectInputStream(out.toByteArray());
      int arrayLength = in.readInt();
      String[] deserializedArray = new String[arrayLength];
      for (int i = 0; i < arrayLength; i++)
      {
         deserializedArray[i] = readString(in);
      }

      assertArrayEquals(strings, deserializedArray);
   }

   @Test
   public void shouldNotInvokeWriteObjectOnStringWritting() throws IOException
   {
      ObjectOutput out = mock(ObjectOutput.class);

      ObjectOutputWriter objectOutputWriter = new ObjectOutputWriter(out);
      objectOutputWriter.writeString("String");

      verify(out, never()).writeObject(anyString());
   }

   @Test
   public void shouldNotInvokeWriteObjectOnStringArrayWritting() throws IOException
   {
      ObjectOutput out = mock(ObjectOutput.class);

      ObjectOutputWriter objectOutputWriter = new ObjectOutputWriter(out);
      objectOutputWriter.writeObjectArray(new String[]{"one", "two", "three"});

      verify(out, never()).writeObject(anyString());
   }

   private void shouldSerializeString(String string) throws IOException
   {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(out);

      ObjectOutputWriter objectOutputWtiter = new ObjectOutputWriter(oos);
      objectOutputWtiter.writeString(string);
      oos.flush();

      ObjectInputStream in = createObjectInputStream(out.toByteArray());
      String deserializedString = readString(in);

      assertEquals(string, deserializedString);
   }

   private String readString(ObjectInputStream in) throws IOException, UnsupportedEncodingException
   {
      int stringLength = in.readInt();
      byte[] stringBytes = new byte[stringLength];
      in.read(stringBytes);
      String deserializedString = new String(stringBytes, "UTF-8");
      return deserializedString;
   }

}
