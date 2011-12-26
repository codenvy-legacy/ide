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
package org.exoplatform.ide.codeassitant.framework;

import junit.framework.TestCase;

import org.exoplatform.ide.codeassistant.framework.server.extractors.TypeInfoExtractor;
import org.exoplatform.ide.codeassistant.jvm.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.JavaType;
import org.exoplatform.ide.codeassistant.jvm.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class ClassInfoExtractorTest extends TestCase
{

   public void testExctractClass() throws ClassFormatError, ClassNotFoundException
   {
      TypeInfo cd = TypeInfoExtractor.extract(A.class);
      assertEquals(A.class.getDeclaredConstructors().length, cd.getDeclaredConstructors().length);
      assertEquals(A.class.getConstructors().length, cd.getConstructors().length);
      assertEquals(A.class.getDeclaredMethods().length, cd.getDeclaredMethods().length);
      assertEquals(A.class.getMethods().length, cd.getMethods().length);
      assertEquals(A.class.getFields().length, cd.getFields().length);
      assertEquals(A.class.getDeclaredFields().length, cd.getDeclaredFields().length);
      assertEquals(A.class.getCanonicalName(), cd.getQualifiedName());
      assertEquals(A.class.getSimpleName(), cd.getName());

      Method[] c = Collections.class.getMethods();
      for (Method element : c)
      {
         System.out.print(element.getName() + "(");
         Type[] types = element.getParameterTypes();
         for (Type type : types)
         {
            System.out.print(type + ",");
         }
         System.out.print(")");
         System.out.println();
      }
   }

   public void testExctractField()
   {
      TypeInfo cd = TypeInfoExtractor.extract(A.class);
      FieldInfo[] fds = cd.getDeclaredFields();
      Field[] fields = A.class.getDeclaredFields();
      for (Field field : fields)
      {
         FieldInfo fd = getFieldInfo(fds, field.getName());
         if (fd == null)
         {
            fail();
         }
         assertEquals(field.getModifiers(), fd.getModifiers());
         assertEquals(field.getType().getSimpleName(), fd.getType());
      }
   }

   private FieldInfo getFieldInfo(FieldInfo[] fds, String fieldName)
   {
      for (FieldInfo fd : fds)
      {
         if (fd.getName().equals(fieldName))
         {
            return fd;
         }
      }
      return null;
   }

   public void testExctractMethod()
   {
      TypeInfo cd = TypeInfoExtractor.extract(B.class);
      MethodInfo[] mds = cd.getDeclaredMethods();
      Method[] methods = B.class.getDeclaredMethods();
      for (Method method : methods)
      {
         MethodInfo md = getMethodInfo(mds, method.toGenericString());
         if (md == null)
         {
            fail();
         }
         assertEquals(method.getModifiers(), md.getModifiers());
      }
   }

   public void testEnumExtract()
   {
      TypeInfo en = TypeInfoExtractor.extract(E.class);
      assertEquals(JavaType.ENUM.name(), en.getType());
      assertEquals("ONE", en.getFields()[0].getName());
   }

   private MethodInfo getMethodInfo(MethodInfo[] mds, String generic)
   {
      for (MethodInfo md : mds)
      {
         if (md.getGeneric().equals(generic))
         {
            return md;
         }
      }
      return null;
   }
}
