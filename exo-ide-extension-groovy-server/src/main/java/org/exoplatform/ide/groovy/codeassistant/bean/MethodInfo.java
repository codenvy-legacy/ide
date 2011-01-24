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
package org.exoplatform.ide.groovy.codeassistant.bean;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class MethodInfo extends RoutineInfo
{

   private String genericReturnType;
   
   private String returnType;

   public MethodInfo()
   {
   }

   public MethodInfo(Integer modifiers, String name, String[] genericExceptionTypes,
      String genericParameterTypes,  String parameterTypes, String generic, String declaringClass, String genericReturnType, String returnType)
   {
      super(modifiers, name, genericExceptionTypes,genericParameterTypes,parameterTypes, generic, declaringClass);
      this.genericReturnType = genericReturnType;
      this.returnType = returnType;
   }

   public String getGenericReturnType()
   {
      return genericReturnType;
   }

   public void setGenericReturnType(String genericReturnType)
   {
      this.genericReturnType = genericReturnType;
   }
   
   public void setReturnType(String returnType)
   {
      this.returnType = returnType;
   }
   
   public String getReturnType()
   {
      return returnType;
   }
}
