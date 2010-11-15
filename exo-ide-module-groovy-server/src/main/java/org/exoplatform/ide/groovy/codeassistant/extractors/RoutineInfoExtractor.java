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

import org.exoplatform.ide.groovy.codeassistant.bean.MethodInfo;
import org.exoplatform.ide.groovy.codeassistant.bean.RoutineInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * 
 * Extracting meta information from given routine (methods & constructors) to the 
 * bean object that can be transform to JSON
 * {@link MethodInfo}
 * {@link RoutineInfo}  
 * 
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class RoutineInfoExtractor
{
   
   public static RoutineInfo extractConstructorInfo(Constructor<?> constructor)
   {
      Type[] types = constructor.getGenericExceptionTypes();
      String[] genExceptionTypes = new String[types.length];
      for (int j = 0; j < types.length; j++)
      {
         genExceptionTypes[j] = genericType2String(types[j]);
      }

      types = constructor.getGenericParameterTypes();
      String[] genericParameterTypes = new String[types.length];
      for (int j = 0; j < types.length; j++)
      {
         genericParameterTypes[j] = genericType2String(types[j]);
      }
      return new RoutineInfo(constructor.getModifiers(), constructor.getName(), genExceptionTypes,
            genericParameterTypes, constructor.toGenericString(), constructor.getDeclaringClass()
               .getCanonicalName());
   }
   
   public static MethodInfo extractMethodInfo(Method method)
   {
      Type[] types = method.getGenericExceptionTypes();
      String[] genericExceptionTypes = new String[types.length];
      for (int j = 0; j < types.length; j++)
      {
         genericExceptionTypes[j] = genericType2String(types[j]);
      }

      types = method.getGenericParameterTypes();
      String[] genericParameterTypes = new String[types.length];
      for (int j = 0; j < types.length; j++)
      {
         genericParameterTypes[j] = genericType2String(types[j]);
      }
      
      return  new MethodInfo(method.getModifiers(), method.getName(), genericExceptionTypes, genericParameterTypes,
            method.toGenericString(), method.getDeclaringClass().getCanonicalName(), genericType2String(method.getGenericReturnType()));
   }
   
   private static String genericType2String(Type type)
   {

      return ((type instanceof Class) ? getTypeName((Class)type) : type.toString());
   }

   private static String getTypeName(Class type)
   {
      if (type.isArray())
      {
         try
         {
            Class cl = type;
            int dimensions = 0;
            while (cl.isArray())
            {
               dimensions++;
               cl = cl.getComponentType();
            }
            StringBuffer sb = new StringBuffer();
            sb.append(cl.getName());
            for (int i = 0; i < dimensions; i++)
            {
               sb.append("[]");
            }
            return sb.toString();
         }
         catch (Throwable e)
         { /*FALLTHRU*/
         }
      }
      return type.getName();
   }

}
