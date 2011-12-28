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
import java.util.ArrayList;
import java.util.List;

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
      List<MethodInfo> cds = new ArrayList<MethodInfo>(); 
      for (int i = 0; i < constructors.length; i++)
      {
         cds.add(MethodInfoExtractor.extractConstructorInfo(constructors[i]));
      }
      Method[] methods = clazz.getMethods();
      List<MethodInfo> mds = new ArrayList<MethodInfo>(); 
      for (int i = 0; i < methods.length; i++)
      {
         mds.add(MethodInfoExtractor.extractMethodInfo(methods[i]));
      }
      methods = clazz.getDeclaredMethods();
      Class<?>[] interfaces = clazz.getInterfaces();
      List<String> iFaces = new ArrayList<String>();
      for (int i = 0; i < interfaces.length; i++)
      {
         iFaces.add(interfaces[i].getCanonicalName());
      }

      Field[] fields = clazz.getFields();
      List<FieldInfo> fds = new ArrayList<FieldInfo>();
      for (int i = 0; i < fields.length; i++)
      {
         fds.add(new FieldInfo(fields[i].getName(),//
            fields[i].getModifiers(),//
            fields[i].getType().getCanonicalName(), //
            fields[i].getDeclaringClass().getCanonicalName()));
      }
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
