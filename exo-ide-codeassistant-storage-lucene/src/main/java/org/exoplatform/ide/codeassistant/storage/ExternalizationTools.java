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
package org.exoplatform.ide.codeassistant.storage;

import org.exoplatform.ide.codeassistant.jvm.bean.FieldInfoBean;
import org.exoplatform.ide.codeassistant.jvm.bean.MethodInfoBean;
import org.exoplatform.ide.codeassistant.jvm.bean.TypeInfoBean;
import org.exoplatform.ide.codeassistant.jvm.shared.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.Member;
import org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Contains set of operations for more efficient object Externalization. String
 * objects will be saved as array of bytes in UTF-8 format. For serialization
 * and deserialization of objects where it's possible will be used they own
 * Externalization mechanism.
 */
public class ExternalizationTools
{

   private static final String DEFAULT_ENCODING = "UTF-8";

   /**
    * Creates ObjectInputStream from array of bytes. Used for initialization of
    * deserialization.
    * 
    * @param data
    *           - serialized object
    * @return - ObjectInputStream
    * @throws IOException
    */
   public static ObjectInputStream createObjectInputStream(byte[] data) throws IOException
   {
      ByteArrayInputStream io = new ByteArrayInputStream(data);
      return new ObjectInputStream(io);
   }

   /**
    * 
    * Read list of serialized FieldInfo from ObjectInput. First int in the
    * stream expected as a length of the List.
    * 
    * @see ExternalizationTools#readStringUTF(ObjectInput)
    * @param in
    *           - input
    * @return - List of FieldInfo
    * @throws IOException
    */
   public static List<FieldInfo> readFields(ObjectInput in) throws IOException
   {
      int size = in.readInt();
      List<FieldInfo> result = null;
      if (size == 0)
      {
         result = Collections.emptyList();
      }
      else
      {
         result = new ArrayList<FieldInfo>(size);
         for (int i = 0; i < size; i++)
         {
            FieldInfo field = new FieldInfoBean();
            // Member
            field.setModifiers(in.readInt());
            field.setName(readStringUTF(in));
            //Field
            field.setType(readStringUTF(in));
            field.setDeclaringClass(readStringUTF(in));
            result.add(field);
         }
      }
      return result;
   }

   /**
    * Read list of serialized MethodInfo from ObjectInput. First int in the
    * stream expected as a length of the List.
    * 
    * @see ExternalizationTools#readStringUTF(ObjectInput)
    * @param in
    *           - input
    * @return - list of MethodInfo.
    * @throws IOException
    */
   public static List<MethodInfo> readMethods(ObjectInput in) throws IOException
   {
      int size = in.readInt();
      List<MethodInfo> result = null;
      if (size == 0)
      {
         result = Collections.emptyList();
      }
      else
      {
         result = new ArrayList<MethodInfo>(size);
         for (int i = 0; i < size; i++)
         {
            MethodInfo method = new MethodInfoBean();
            // Member
            method.setModifiers(in.readInt());
            method.setName(readStringUTF(in));

            //Field
            method.setDeclaringClass(readStringUTF(in));
            method.setExceptionTypes(readStringUTFList(in));
            method.setParameterTypes(readStringUTFList(in));
            method.setParameterNames(readStringUTFList(in));
            method.setReturnType(readStringUTF(in));
            method.setConstructor(in.readBoolean());
            result.add(method);

         }
      }
      return result;
   }

   /**
    * Read UTF-8 string from ObjectInput. Reading will be made in the same order
    * as writing. At the first time - length of the string, then array of bytes
    * (content of the string). The most important to keep the same encoding
    * during serialization and deserialization.
    * 
    * 
    * @param in
    *           - input
    * @return - deserealized string.
    * @throws IOException
    */
   public static String readStringUTF(ObjectInput in) throws IOException
   {
      int length = in.readInt();
      String result = null;
      if (length == 0)
      {
         result = "";
      }
      else
      {
         byte[] bytes = new byte[length];
         in.readFully(bytes);

         result = new String(bytes, DEFAULT_ENCODING);
      }
      return result;
   }

   /**
    * Read list of strings in UTF-8 encoding from ObjectInput
    * 
    * First int in the stream expected as a length of the array then string will
    * be read one by one with help if readStringUTF method
    * 
    * @see ExternalizationTools#readStringUTF(ObjectInput)
    * @param in
    *           - input
    * @return - List of strings
    * @throws IOException
    * 
    */
   public static List<String> readStringUTFList(ObjectInput in) throws IOException
   {
      int size = in.readInt();
      List<String> result = null;
      if (size == 0)
      {
         result = Collections.emptyList();
      }
      else
      {
         result = new ArrayList<String>(size);

         for (int i = 0; i < size; i++)
         {
            result.add(readStringUTF(in));
         }

      }
      return result;
   }

   /**
    * Write list of Member to the ObjectOutput.
    * 
    * At the first time the length of the list will be written to the output.
    * 
    * @see ExternalizationTools#writeStringUTF(String, ObjectOutput)
    * @param list
    *           - list of Member objects
    * @param out
    *           - serealized output.
    * @throws IOException
    */
   public static void writeObjectList(List<? extends Member> list, ObjectOutput out) throws IOException
   {
      if (list == null)
      {
         out.writeInt(0);
      }
      else
      {
         out.writeInt(list.size());

         for (Member element : list)
         {

            // Member
            out.writeInt(element.getModifiers());
            writeStringUTF(element.getName(), out);

            if (element instanceof FieldInfo)
            {
               //FieldInfo
               writeStringUTF(((FieldInfo)element).getType(), out);
               writeStringUTF(((FieldInfo)element).getDeclaringClass(), out);
            }
            else if (element instanceof MethodInfo)
            {
               //MethodInfe
               writeStringUTF(((MethodInfo)element).getDeclaringClass(), out);
               writeStringUTFList(((MethodInfo)element).getExceptionTypes(), out);
               writeStringUTFList(((MethodInfo)element).getParameterTypes(), out);
               writeStringUTFList(((MethodInfo)element).getParameterNames(), out);
               writeStringUTF(((MethodInfo)element).getReturnType(), out);
               out.writeBoolean(((MethodInfo)element).isConstructor());
            }

         }
      }
   }

   /**
    * Write UTF-8 string to ObjectOutput. At the first time - length of the
    * string, then array of bytes (content of the string) will be writtento
    * output. The most important to keep the same encoding during serialization
    * and deserialization.
    * 
    * 
    * @param in
    *           - input
    * @return - deserealized string.
    * @throws IOException
    */
   public static void writeStringUTF(String string, ObjectOutput out) throws UnsupportedEncodingException, IOException
   {
      if (string == null)
      {
         out.writeInt(0);
      }
      else
      {
         byte[] bytes = string.getBytes(DEFAULT_ENCODING);
         out.writeInt(bytes.length);
         out.write(bytes);
      }

   }

   /**
    * Write list of UTF-8 strings to object output.
    * 
    * @param list
    *           - list of strings
    * @param out
    *           - serialized output
    * @throws IOException
    */
   public static void writeStringUTFList(List<String> list, ObjectOutput out) throws IOException
   {

      if (list == null)
      {
         out.writeInt(0);
      }
      else
      {
         out.writeInt(list.size());

         for (String element : list)
         {
            writeStringUTF(element, out);
         }

      }
   }

   /**
    * Read TypeInfo from serialized state.
    * 
    * @param content
    *           - Serialized TypeInfo
    * @return - deserialized TypeInfo object.
    * @throws IOException
    */
   public static TypeInfo readExternal(InputStream content) throws IOException
   {
      TypeInfoBean result = new TypeInfoBean();
      ObjectInputStream in = new ObjectInputStream(content);
      // Member
      result.setModifiers(in.readInt());
      result.setName(readStringUTF(in));
      // ShortType
      result.setType(readStringUTF(in));
      // TypeInfo
      result.setSuperClass(readStringUTF(in));
      result.setInterfaces(readStringUTFList(in));
      result.setFields(readFields(in));
      result.setMethods(readMethods(in));
      return result;
   }

   /**
    * Serialize TypeInfo to the array of bytes.
    * 
    * @param typeInfo
    *           - initial object
    * @return - serialized object
    * @throws IOException
    */
   public static byte[] externalize(TypeInfo typeInfo) throws IOException
   {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(bos);
      // Member
      out.writeInt(typeInfo.getModifiers());
      writeStringUTF(typeInfo.getName(), out);
      // ShortType
      writeStringUTF(typeInfo.getType(), out);
      // TypeInfo
      writeStringUTF(typeInfo.getSuperClass(), out);
      writeStringUTFList(typeInfo.getInterfaces(), out);
      writeObjectList(typeInfo.getFields(), out);
      writeObjectList(typeInfo.getMethods(), out);
      out.close();
      return bos.toByteArray();
   }

   /**
    * Protect utill class from creation.
    */
   private ExternalizationTools()
   {
   }
}
