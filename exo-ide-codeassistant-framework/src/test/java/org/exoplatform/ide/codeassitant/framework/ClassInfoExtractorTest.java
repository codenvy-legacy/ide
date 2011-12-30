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

import org.exoplatform.ide.codeassistant.framework.server.extractors.TypeInfoExtractor;
import org.exoplatform.ide.codeassistant.jvm.shared.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.JavaType;
import org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import junit.framework.TestCase;

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
      assertEquals(A.class.getMethods().length, cd.getMethods().size());
      assertEquals(A.class.getFields().length, cd.getFields().size());
      assertEquals(A.class.getCanonicalName(), cd.getName());
   }

   public void testExctractField()
   {
      TypeInfo cd = TypeInfoExtractor.extract(A.class);
      Field[] fields = A.class.getFields();
      List<FieldInfo> fds = cd.getFields();
      for (Field field : fields)
      {
         FieldInfo fd = getFieldInfo(fds, field);
         if (fd == null)
         {
            fail();
         }
         assertEquals(field.getModifiers(), fd.getModifiers());
         assertEquals(field.getType().getSimpleName(), fd.getType());
      }
   }

   private FieldInfo getFieldInfo(List<FieldInfo> fds, Field field)
   {

      for (FieldInfo fd : fds)
      {
         System.err.println(fd.toString());
         if (fd.getName().equals(field.getName()))
         {
            return fd;
         }
      }
      return null;
   }

   public void testExctractMethod()
   {
      TypeInfo cd = TypeInfoExtractor.extract(B.class);
      List<MethodInfo> mds = cd.getMethods();
      Method[] methods = B.class.getDeclaredMethods();
      for (Method method : methods)
      {
         MethodInfo md = getMethodInfo(mds, method.getName());
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
      assertEquals("ONE", en.getFields().get(0).getName());
   }

   private MethodInfo getMethodInfo(List<MethodInfo> mds, String name)
   {
      for (MethodInfo md : mds)
      {
         if (md.getName().equals(name))
         {
            return md;
         }
      }
      return null;
   }
}
