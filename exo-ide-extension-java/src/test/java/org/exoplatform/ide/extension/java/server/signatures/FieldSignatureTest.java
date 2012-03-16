/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.java.server.signatures;

import static org.fest.assertions.Assertions.assertThat;

import org.exoplatform.ide.codeassistant.jvm.shared.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 5:28:15 PM Mar 13, 2012 evgen $
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class FieldSignatureTest extends SignatureBase
{
   @Test
   public void fieldNonGenericSignature() throws Exception
   {
      StringBuilder b = new StringBuilder("package test;\n");
      b.append("public class TestClass<E>{\n");
      b.append("public String field;\n}");
      TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
      assertThat(typeInfo.getFields()).isNotEmpty().hasSize(1);
      FieldInfo fieldInfo = typeInfo.getFields().get(0);
      assertThat(fieldInfo.getSignature()).isNull();
   }

   @Test
   public void fieldGenericSignature() throws Exception
   {
      StringBuilder b = new StringBuilder("package test;\n");
      b.append("public class TestClass<E>{\n");
      b.append("public E field;\n}");
      TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
      FieldInfo fieldInfo = typeInfo.getFields().get(0);
      assertThat(fieldInfo.getSignature()).isNotNull().isNotEmpty();
   }

   @Test
   public void fieldGenericSignatureTest() throws Exception
   {
      StringBuilder b = new StringBuilder("package test;\n");
      b.append("public class TestClass<E>{\n");
      b.append("public E field;\n}");
      TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
      FieldInfo fieldInfo = typeInfo.getFields().get(0);
      assertThat(fieldInfo.getSignature()).isNotNull().isEqualTo("TE;");
   }

   @Test
   public void fieldTypeGeneric() throws Exception
   {
      StringBuilder b = new StringBuilder("package test;\n");
      b.append("import java.util.List;\n");
      b.append("public class TestClass<E>{\n");
      b.append("public List<String> field;\n}");
      TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
      FieldInfo fieldInfo = typeInfo.getFields().get(0);
      assertThat(fieldInfo.getSignature()).isNotNull().isEqualTo("Ljava/util/List<Ljava/lang/String;>;");
   }
   
   @Test
   public void fieldTypeGeneric2() throws Exception
   {
      StringBuilder b = new StringBuilder("package test;\n");
      b.append("import java.util.List;\n");
      b.append("public class TestClass<E>{\n");
      b.append("public List<E> field;\n}");
      TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
      FieldInfo fieldInfo = typeInfo.getFields().get(0);
      assertThat(fieldInfo.getSignature()).isNotNull().isEqualTo("Ljava/util/List<TE;>;");
   }

   @Test
   public void wildcardsSignature() throws Exception
   {
      StringBuilder b = new StringBuilder("package test;\n");
      b.append("import java.util.List;\n");
      b.append("public class TestClass<E>{\n");
      b.append("public List<?> field;\n}");
      TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
      FieldInfo fieldInfo = typeInfo.getFields().get(0);
      assertThat(fieldInfo.getSignature()).isNotNull().isEqualTo("Ljava/util/List<*>;");
   }
   
   @Test
   public void covarianceSignature() throws Exception
   {
      StringBuilder b = new StringBuilder("package test;\n");
      b.append("import java.util.List;\n");
      b.append("public class TestClass<E>{\n");
      b.append("public List<? extends String> field;\n}");
      TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
      FieldInfo fieldInfo = typeInfo.getFields().get(0);
      assertThat(fieldInfo.getSignature()).isNotNull().isEqualTo("Ljava/util/List<+Ljava/lang/String;>;");
   }
   
   @Test
   public void contravarianceSignature() throws Exception
   {
      StringBuilder b = new StringBuilder("package test;\n");
      b.append("import java.util.List;\n");
      b.append("public class TestClass<E>{\n");
      b.append("public List<? super String> field;\n}");
      TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
      FieldInfo fieldInfo = typeInfo.getFields().get(0);
      assertThat(fieldInfo.getSignature()).isNotNull().isEqualTo("Ljava/util/List<-Ljava/lang/String;>;");
   }
   
   @Test
   public void mixedWildcardSignature() throws Exception
   {
      StringBuilder b = new StringBuilder("package test;\n");
      b.append("import java.util.ArrayList;\n");
      b.append("import java.util.Map;\n");
      b.append("public class TestClass<E>{\n");
      b.append("public ArrayList<? super Map<String, ?>> list;\n}");
      TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
      FieldInfo fieldInfo = typeInfo.getFields().get(0);
      assertThat(fieldInfo.getSignature()).isNotNull().isEqualTo("Ljava/util/ArrayList<-Ljava/util/Map<Ljava/lang/String;*>;>;");
   }
   
   @Test
   public void mixedWildcardSignature2() throws Exception
   {
      StringBuilder b = new StringBuilder("package test;\n");
      b.append("import java.util.ArrayList;\n");
      b.append("import java.util.Map;\n");
      b.append("public class TestClass<E>{\n");
      b.append("public ArrayList<? extends Map<String, ? extends E>> list;\n}");
      TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
      FieldInfo fieldInfo = typeInfo.getFields().get(0);
      assertThat(fieldInfo.getSignature()).isNotNull().isEqualTo("Ljava/util/ArrayList<+Ljava/util/Map<Ljava/lang/String;+TE;>;>;");
   }
   
   @Test
   public void mixedWildcardSignature3() throws Exception
   {
      StringBuilder b = new StringBuilder("package test;\n");
      b.append("import java.util.ArrayList;\n");
      b.append("import java.util.Map;\n");
      b.append("public class TestClass<E>{\n");
      b.append("public ArrayList<? extends Map<String, E>> list;\n}");
      TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
      FieldInfo fieldInfo = typeInfo.getFields().get(0);
      assertThat(fieldInfo.getSignature()).isNotNull().isEqualTo("Ljava/util/ArrayList<+Ljava/util/Map<Ljava/lang/String;TE;>;>;");
   }
   
   @Test
   public void genericArraySignature() throws Exception
   {
      StringBuilder b = new StringBuilder("package test;\n");
      b.append("import java.util.ArrayList;\n");
      b.append("import java.util.Map;\n");
      b.append("public class TestClass<E>{\n");
      b.append("public E[] field;\n}");
      TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
      FieldInfo fieldInfo = typeInfo.getFields().get(0);
      assertThat(fieldInfo.getSignature()).isNotNull().isEqualTo("[TE;");
   }
   
   @Test
   public void typeParameterizedArray() throws Exception
   {
      StringBuilder b = new StringBuilder("package test;\n");
      b.append("import java.util.List;\n");
      b.append("import java.util.Map;\n");
      b.append("public class TestClass<E>{\n");
      b.append("public List<E[]> field;\n}");
      TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
      FieldInfo fieldInfo = typeInfo.getFields().get(0);
      assertThat(fieldInfo.getSignature()).isNotNull().isEqualTo("Ljava/util/List<[TE;>;");
   }
   
   @Test
   public void typeParameterizedArray2() throws Exception
   {
      StringBuilder b = new StringBuilder("package test;\n");
      b.append("import java.util.List;\n");
      b.append("import java.util.Map;\n");
      b.append("public class TestClass<E>{\n");
      b.append("public List<String[]> field;\n}");
      TypeInfo typeInfo = getTypeInfo(b, "test.TestClass");
      FieldInfo fieldInfo = typeInfo.getFields().get(0);
      assertThat(fieldInfo.getSignature()).isNotNull().isEqualTo("Ljava/util/List<[Ljava/lang/String;>;");
   }

}
