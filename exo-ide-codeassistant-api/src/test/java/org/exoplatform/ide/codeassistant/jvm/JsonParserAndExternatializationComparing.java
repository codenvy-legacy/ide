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
package org.exoplatform.ide.codeassistant.jvm;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonGenerator;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.exoplatform.ide.codeassistant.jvm.bean.FieldInfoBean;
import org.exoplatform.ide.codeassistant.jvm.bean.MethodInfoBean;
import org.exoplatform.ide.codeassistant.jvm.bean.TypeInfoBean;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * This is a perf test
 */
@Ignore
public class JsonParserAndExternatializationComparing
{
   private final TypeInfoBean[] typeInfos = generateTypeInfos();

   private final TypeInfoBean typeInfo = generateTypeInfo();

   private static final int OBJECTS_COUNT = 100000;

   @Test
   public void jsonSerialization() throws JsonException, IOException
   {
      long startTime = System.currentTimeMillis();
      for (TypeInfo typeInfo : typeInfos)
      {
         JsonGenerator.createJsonObject(typeInfo);
      }
      long endTime = System.currentTimeMillis();
      System.out.println("Serialization Json time " + (endTime - startTime));
   }

   @Test
   public void extSerialization() throws JsonException, IOException
   {
      long startTime = System.currentTimeMillis();
      for (TypeInfoBean typeInfo : typeInfos)
      {
         ObjectOutputStream out = new ObjectOutputStream(new ByteArrayOutputStream());
         typeInfo.writeExternal(out);
         out.flush();
         out.close();
      }
      long endTime = System.currentTimeMillis();
      System.out.println("Serialization Externalizable time " + (endTime - startTime) + "\n");
   }

   @Test
   public void jsonDeserialization() throws JsonException, IOException, ClassNotFoundException
   {
      JsonValue jsonValue = JsonGenerator.createJsonObject(generateTypeInfo());
      byte[] jsonBytes = jsonValue.toString().getBytes();

      System.out.println("json size " + jsonBytes.length);

      int i = 0;
      long startTime = System.currentTimeMillis();
      while (i < OBJECTS_COUNT)
      {
         JsonParser jsonParser = new JsonParser();
         jsonParser.parse(new ByteArrayInputStream(jsonBytes));
         JsonValue jsonValue2 = jsonParser.getJsonObject();
         ObjectBuilder.createObject(TypeInfoBean.class, jsonValue2);

         i++;
      }
      long endTime = System.currentTimeMillis();
      System.out.println("Deserialization Json time " + (endTime - startTime));
   }

   @Test
   public void extDeserialization() throws JsonException, IOException, ClassNotFoundException
   {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(out);
      typeInfo.writeExternal(oos);
      oos.flush();
      oos.close();

      byte[] extBytes = out.toByteArray();

      System.out.println("Externalizable size " + extBytes.length);

      int i = 0;
      long startTime = System.currentTimeMillis();
      while (i < OBJECTS_COUNT)
      {
         ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(extBytes));
         TypeInfoBean typeInfo2 = new TypeInfoBean();
         typeInfo2.readExternal(ois);
         ois.close();

         i++;
      }
      long endTime = System.currentTimeMillis();
      System.out.println("Deserialization Externalizable time " + (endTime - startTime));
   }

   private TypeInfoBean[] generateTypeInfos()
   {
      TypeInfoBean[] typeInfos = new TypeInfoBean[OBJECTS_COUNT];
      TypeInfoBean typeInfo = generateTypeInfo();

      for (int i = 0; i < OBJECTS_COUNT; i++)
      {
         typeInfos[i] = typeInfo;
      }

      return typeInfos;
   }

   private TypeInfoBean generateTypeInfo()
   {
      TypeInfoBean typeInfo = new TypeInfoBean();

      typeInfo.setModifiers(Modifier.PUBLIC);
      typeInfo.setName("test.TestClass");
      typeInfo.setSuperClass("java.lang.Object");
      typeInfo.setType("CLASS");

      String[] interfaces = new String[]{"java.io.Serializable"};
      typeInfo.setInterfaces(Arrays.asList(interfaces));

      MethodInfoBean publicConstructor =
         new MethodInfoBean("test.TestClass", Modifier.PUBLIC, Arrays.asList(new String[]{"java.io.IOException",
            "java.lang.IllegalStateException"}), Arrays.asList(new String[]{"java.lang.Object", "Object"}),
            Arrays.asList(new String[]{"param1", "param2"}), true, "", "test.TestClass");
      MethodInfoBean protectedConstructor =
         new MethodInfoBean("test.TestClass", Modifier.PROTECTED, Arrays.asList(new String[]{"java.io.IOException"}),
            Arrays.asList(new String[]{"java.lang.String", "String"}), Arrays.asList(new String[]{"param1", "param2"}),
            true, "", "test.TestClass");

      MethodInfoBean publicMethod =
         new MethodInfoBean("method1", Modifier.PUBLIC, Arrays.asList(new String[]{"java.io.IOException"}),
            Arrays.asList(new String[]{"java.lang.Object", "Object"}), Arrays.asList(new String[]{"param1", "param2"}),
            false, "test.TestClass", "java.lang.Integer");
      MethodInfoBean privateMethod =
         new MethodInfoBean("method2", Modifier.PRIVATE, Arrays.asList(new String[]{"java.io.IOException"}),
            Arrays.asList(new String[]{"java.lang.String", "String"}), Arrays.asList(new String[]{"param1", "param2"}),
            false, "test.TestClass", "java.lang.Integer");
      typeInfo.setMethods(Arrays.asList(new MethodInfo[]{publicConstructor, protectedConstructor, publicMethod,
         privateMethod}));

      FieldInfoBean publicField = new FieldInfoBean("field1", Modifier.PUBLIC, "test.TestClass", "String");
      FieldInfoBean privateField = new FieldInfoBean("field2", Modifier.PRIVATE, "test.TestClass", "Integer");
      typeInfo.setFields(Arrays.asList(new FieldInfo[]{publicField, privateField}));
      return typeInfo;
   }
}
