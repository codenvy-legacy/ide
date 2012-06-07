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
package org.exoplatform.ide.codeassistant.asm;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.fest.assertions.Assertions.*;

import org.exoplatform.ide.codeassistant.asm.test.Bar;
import org.exoplatform.ide.codeassistant.jvm.shared.Annotation;
import org.exoplatform.ide.codeassistant.jvm.shared.AnnotationParameter;
import org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.junit.Test;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class AnnotationExtractorTest
{
   @Test
   public void shouldExtractDefailtValues() throws Exception
   {
      TypeInfo cd = ClassParser.parse(Bar.class);
      assertNotNull(cd.getMethods().get(0).getAnnotationDefault());
   }

   @Test
   public void shouldExtractPrimitiveIntDefailtValue() throws Exception
   {
      TypeInfo cd = ClassParser.parse(Bar.class);
      AnnotationValue defaultValue = cd.getMethods().get(0).getAnnotationDefault();
      String[] primitiveType = defaultValue.getPrimitiveType();
      assertNotNull(primitiveType);
      assertEquals(2, primitiveType.length);
      assertEquals("Integer", primitiveType[0]);
      assertEquals("42", primitiveType[1]);
   }

   @Test
   public void shouldExtractPrimitiveStringDefailtValue() throws Exception
   {
      TypeInfo cd = ClassParser.parse(Bar.class);
      AnnotationValue defaultValue = cd.getMethods().get(1).getAnnotationDefault();
      String[] primitiveType = defaultValue.getPrimitiveType();
      assertNotNull(primitiveType);
      assertEquals(2, primitiveType.length);
      assertEquals("String", primitiveType[0]);
      assertEquals("", primitiveType[1]);
   }

   @Test
   public void shouldExtractEnumDefailtValue() throws Exception
   {
      TypeInfo cd = ClassParser.parse(Bar.class);
      AnnotationValue defaultValue = cd.getMethods().get(2).getAnnotationDefault();
      String[] primitiveType = defaultValue.getPrimitiveType();
      assertNull(primitiveType);
      String[] constant = defaultValue.getEnumConstant();
      assertEquals(2, constant.length);
      assertEquals("Lorg/exoplatform/ide/codeassistant/asm/test/E;", constant[0]);
      assertEquals("ONE", constant[1]);
   }

   @Test
   public void shouldExtractClassDefailtValue() throws Exception
   {
      TypeInfo cd = ClassParser.parse(Bar.class);
      AnnotationValue defaultValue = cd.getMethods().get(3).getAnnotationDefault();
      assertEquals("Ljava/lang/String;", defaultValue.getClassSignature());
   }

   @Test
   public void shouldExtractStringArrayDefailtValue() throws Exception
   {
      TypeInfo cd = ClassParser.parse(Bar.class);
      AnnotationValue defaultValue = cd.getMethods().get(4).getAnnotationDefault();
      String[] arrayType = defaultValue.getArrayType();
      assertThat(arrayType).isNotEmpty().containsOnly("String", "str");
   }

   @Test
   public void shouldExtractDoubleArrayDefailtValue() throws Exception
   {
      TypeInfo cd = ClassParser.parse(Bar.class);
      AnnotationValue defaultValue = cd.getMethods().get(5).getAnnotationDefault();
      String[] arrayType = defaultValue.getArrayType();
      assertThat(arrayType).isNotEmpty().containsOnly("Double", "1.4", "2.04", "5.0007");
   }

   @Test
   public void shouldExtractClassArrayDefailtValue() throws Exception
   {
      TypeInfo cd = ClassParser.parse(Bar.class);
      AnnotationValue defaultValue = cd.getMethods().get(6).getAnnotationDefault();
      String[] arrayType = defaultValue.getArrayType();
      assertThat(arrayType).isNotEmpty().containsOnly("Type", "Ljava/lang/Integer;", "Ljava/util/List;",
         "Ljava/lang/Math;");
   }

   @Test
   public void shouldExtractAnnotationDefailtValue() throws Exception
   {
      TypeInfo cd = ClassParser.parse(Bar.class);
      AnnotationValue defaultValue = cd.getMethods().get(7).getAnnotationDefault();
      Annotation annotation = defaultValue.getAnnotation();
      assertThat(annotation).isNotNull();
      assertThat(annotation.getTypeName()).isEqualTo("Lorg/exoplatform/ide/codeassistant/asm/test/Foo;");
      assertThat(annotation.getAnnotationParameters()).hasSize(2);
   }

   @Test
   public void shouldExtractAnnotationPrimitiveParameter() throws Exception
   {
      TypeInfo cd = ClassParser.parse(Bar.class);
      AnnotationValue defaultValue = cd.getMethods().get(7).getAnnotationDefault();
      Annotation annotation = defaultValue.getAnnotation();
      AnnotationParameter parameter = annotation.getAnnotationParameters()[0];
      assertThat(parameter.getName()).isEqualTo("foo");
      assertThat(parameter.getValue().getPrimitiveType()).containsOnly("Integer", "5");
   }

   @Test
   public void shouldExtractAnnotationArrayParameter() throws Exception
   {
      TypeInfo cd = ClassParser.parse(Bar.class);
      AnnotationValue defaultValue = cd.getMethods().get(7).getAnnotationDefault();
      Annotation annotation = defaultValue.getAnnotation();
      AnnotationParameter parameter = annotation.getAnnotationParameters()[1];
      assertThat(parameter.getName()).isEqualTo("bar");
      assertThat(parameter.getValue().getArrayType()).containsOnly("String", "aaa", "bbb");
   }

}
