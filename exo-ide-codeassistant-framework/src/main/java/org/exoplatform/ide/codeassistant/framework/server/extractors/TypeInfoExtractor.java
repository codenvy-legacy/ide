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

import org.exoplatform.ide.codeassistant.api.CodeAssistantStorage.JavaType;
import org.exoplatform.ide.codeassistant.api.FieldInfo;
import org.exoplatform.ide.codeassistant.api.MethodInfo;
import org.exoplatform.ide.codeassistant.api.RoutineInfo;
import org.exoplatform.ide.codeassistant.api.TypeInfo;

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
   /**
    * @param clazz
    * @return
    * @throws IncompatibleClassChangeError
    */
   public static TypeInfo extract(Class<?> clazz) throws IncompatibleClassChangeError
   {
      TypeInfo classDescription = new TypeInfo();
      Constructor<?>[] constructors = clazz.getConstructors();
      RoutineInfo[] cds = new RoutineInfo[constructors.length];
      for (int i = 0; i < constructors.length; i++)
      {
         cds[i] = RoutineInfoExtractor.extractConstructorInfo(constructors[i]);
      }
      constructors = clazz.getDeclaredConstructors();
      RoutineInfo[] decCds = new RoutineInfo[constructors.length];
      for (int i = 0; i < constructors.length; i++)
      {
         decCds[i] = RoutineInfoExtractor.extractConstructorInfo(constructors[i]);
      }
      Method[] methods = clazz.getMethods();
      MethodInfo[] mds = new MethodInfo[methods.length];
      for (int i = 0; i < methods.length; i++)
      {
         mds[i] = RoutineInfoExtractor.extractMethodInfo(methods[i]);
      }
      methods = clazz.getDeclaredMethods();
      MethodInfo[] decMds = new MethodInfo[methods.length];
      for (int i = 0; i < methods.length; i++)
      {
         decMds[i] = RoutineInfoExtractor.extractMethodInfo(methods[i]);
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
         fds[i] =
            new FieldInfo(fields[i].getType().getSimpleName(), fields[i].getModifiers(), fields[i].getName(), fields[i]
               .getDeclaringClass().getCanonicalName());
      }
      fields = clazz.getDeclaredFields();
      FieldInfo[] decFds = new FieldInfo[fields.length];
      for (int i = 0; i < fields.length; i++)
      {
         decFds[i] =
            new FieldInfo(fields[i].getType().getSimpleName(), fields[i].getModifiers(), fields[i].getName(), fields[i]
               .getDeclaringClass().getCanonicalName());
      }

      classDescription.setModifiers(clazz.getModifiers());
      classDescription.setInterfaces(iFaces);

      if (clazz.getSuperclass() != null)
      {
         classDescription.setSuperClass(clazz.getSuperclass().getCanonicalName());
      }

      classDescription.setConstructors(cds);
      classDescription.setDeclaredConstructors(decCds);

      classDescription.setFields(fds);
      classDescription.setDeclaredFields(decFds);

      classDescription.setMethods(mds);
      classDescription.setDeclaredMethods(decMds);

      classDescription.setQualifiedName(clazz.getCanonicalName());
      classDescription.setName(clazz.getSimpleName());

      if (clazz.isAnnotation())
      {
         classDescription.setType(JavaType.ANNOTATION.name());
      }
      else if (clazz.isInterface())
      {
         classDescription.setType(JavaType.INTERFACE.name());
      }
      else if(clazz.isEnum())
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
