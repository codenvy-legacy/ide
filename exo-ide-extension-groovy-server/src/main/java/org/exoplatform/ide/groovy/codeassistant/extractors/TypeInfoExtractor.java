/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.groovy.codeassistant.extractors;

import org.exoplatform.ide.groovy.codeassistant.bean.FieldInfo;
import org.exoplatform.ide.groovy.codeassistant.bean.MethodInfo;
import org.exoplatform.ide.groovy.codeassistant.bean.RoutineInfo;
import org.exoplatform.ide.groovy.codeassistant.bean.TypeInfo;

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
    * 
    */
   public static final String ANNOTATION = "ANNOTATION";

   /**
    * 
    */
   public static final String INTERFACE = "INTERFACE";

   /**
    * 
    */
   public static final String CLASS = "CLASS";

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
         classDescription.setType(ANNOTATION);
      }
      else if (clazz.isInterface())
      {
         classDescription.setType(INTERFACE);
      }
      else
      {
         classDescription.setType(CLASS);
      }

      return classDescription;
   }

}
