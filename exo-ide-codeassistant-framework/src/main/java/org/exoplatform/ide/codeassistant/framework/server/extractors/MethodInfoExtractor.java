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

import org.exoplatform.ide.codeassistant.jvm.bean.MethodInfoBean;
import org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * Extracting meta information from given routine (methods & constructors) to the bean object that can be transform to JSON
 * {@link MethodInfo} {@link RoutineInfo}
 * 
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class MethodInfoExtractor
{

   /**
    * Get meta info from java.lang.reflect.Constructor
    * 
    * @param constructor the constructor
    * @return RoutineInfo bean that describe getting constructor and can be transform to JSON
    */
   public static MethodInfo extractConstructorInfo(Constructor<?> constructor)
   {
      Type[] types = constructor.getGenericExceptionTypes();
      List<String> genExceptionTypes = new ArrayList<String>();
      for (int j = 0; j < types.length; j++)
      {
         genExceptionTypes.add(genericType2String(types[j]));
      }

      types = constructor.getGenericParameterTypes();
      List<String> genericParameterTypes = new ArrayList<String>();
      for (int j = 0; j < types.length; j++)
      {
         genericParameterTypes.add(genericType2String(types[j]));
      }
      return new MethodInfoBean(constructor.getName(),//
         constructor.getModifiers(),//
         genExceptionTypes,//
         genericParameterTypes,//
         Collections.<String> emptyList(),//
         true,//
         null,//
         constructor.getDeclaringClass().getCanonicalName());
   }

   /**
    * Get meta info from java.lang.reflect.Method
    * 
    * @param method the Method for getting meta info
    * @return RoutineInfo bean that describe getting Method and can be transform to JSON
    */
   public static MethodInfo extractMethodInfo(Method method)
   {
      Type[] types = method.getGenericExceptionTypes();

      List<String> genericExceptionTypes = new ArrayList<String>();
      for (int j = 0; j < types.length; j++)
      {
         genericExceptionTypes.add(genericType2String(types[j]));
      }

      types = method.getParameterTypes();
      List<String> genericParameterTypes = new ArrayList<String>();
      for (int j = 0; j < types.length; j++)
      {
         genericParameterTypes.add(genericType2String(types[j]));
      }
      return new MethodInfoBean(method.getName(),//
         method.getModifiers(),//
         genericExceptionTypes,//
         genericParameterTypes,//
         Collections.<String> emptyList(), //
         false,//
         genericType2String(method.getGenericReturnType()),//
         method.getDeclaringClass().getCanonicalName());
   }

   /**
    * Convert type to human readable String
    * 
    * @param type
    * @return
    */
   private static String genericType2String(Type type)
   {

      return ((type instanceof Class) ? getTypeName((Class)type) : type.toString());
   }

   /**
    * @param type
    * @return
    */
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
         {
         }
      }
      return type.getName();
   }

}
