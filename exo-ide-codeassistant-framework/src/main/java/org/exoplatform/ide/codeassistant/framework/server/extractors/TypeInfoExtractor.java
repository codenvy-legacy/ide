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
package org.exoplatform.ide.codeassistant.framework.server.extractors;

import org.exoplatform.ide.codeassistant.jvm.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.JavaType;
import org.exoplatform.ide.codeassistant.jvm.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Extracting meta information from given classes to the bean object that can be transform to JSON
 * org.exoplatform.ide.groovy.codeassistant.bean.ClassInfo  
 * 
 * 
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class TypeInfoExtractor
{

   public static class V
   {

   }

   /**
    * @param clazz
    * @return
    * @throws IncompatibleClassChangeError
    */
   public static TypeInfo extract(Class<?> clazz) throws IncompatibleClassChangeError
   {
      TypeInfo classDescription = new TypeInfo();
      Constructor<?>[] constructors = clazz.getConstructors();
      MethodInfo[] cds = new MethodInfo[constructors.length];
      for (int i = 0; i < constructors.length; i++)
      {
         cds[i] = MethodInfoExtractor.extractConstructorInfo(constructors[i]);
      }
      constructors = clazz.getDeclaredConstructors();
      MethodInfo[] decCds = new MethodInfo[constructors.length];
      for (int i = 0; i < constructors.length; i++)
      {
         decCds[i] = MethodInfoExtractor.extractConstructorInfo(constructors[i]);
      }
      Method[] methods = clazz.getMethods();
      MethodInfo[] mds = new MethodInfo[methods.length];
      for (int i = 0; i < methods.length; i++)
      {
         mds[i] = MethodInfoExtractor.extractMethodInfo(methods[i]);
      }
      methods = clazz.getDeclaredMethods();
      MethodInfo[] decMds = new MethodInfo[methods.length];
      for (int i = 0; i < methods.length; i++)
      {
         decMds[i] = MethodInfoExtractor.extractMethodInfo(methods[i]);
      }
      Class<?>[] interfaces = clazz.getInterfaces();
      String[] iFaces = new String[interfaces.length];
      for (int i = 0; i < interfaces.length; i++)
      {
         iFaces[i] = interfaces[i].getCanonicalName();
      }

      Field[] fields = clazz.getFields();
      FieldInfo[] fds = new FieldInfo[fields.length];
      for (int i = 0; i < fields.length; i++)
      {
         fds[i] = new FieldInfo(fields[i].getName(),//
            fields[i].getModifiers(),//
            fields[i].getType().getCanonicalName(), //
            fields[i].getDeclaringClass().getCanonicalName());
      }
      //      fields = clazz.getDeclaredFields();
      //      TODO: need check declared fields
      //      FieldInfo[] decFds = new FieldInfo[fields.length];
      //      for (int i = 0; i < fields.length; i++)
      //      {
      //         decFds[i] =
      //            new FieldInfo(fields[i].getType().getCanonicalName(), fields[i].getModifiers(), fields[i].getName(), fields[i]
      //               .getDeclaringClass().getCanonicalName());
      //      }

      classDescription.setModifiers(clazz.getModifiers());
      classDescription.setInterfaces(iFaces);

      if (clazz.getSuperclass() != null)
      {
         classDescription.setSuperClass(clazz.getSuperclass().getCanonicalName());
      }

      classDescription.setFields(fds);

      classDescription.setMethods(mds);

      classDescription.setName(clazz.getCanonicalName());

      if (clazz.isAnnotation())
      {
         classDescription.setType(JavaType.ANNOTATION.name());
      }
      else if (clazz.isInterface())
      {
         classDescription.setType(JavaType.INTERFACE.name());
      }
      else if (clazz.isEnum())
      {
         classDescription.setType(JavaType.ENUM.name());
      }
      else
      {
         classDescription.setType(JavaType.CLASS.name());
      }

      return classDescription;
   }

}
