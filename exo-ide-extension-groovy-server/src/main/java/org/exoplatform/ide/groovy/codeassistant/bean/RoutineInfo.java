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
public class RoutineInfo extends Member
{
   private String[] genericExceptionTypes;

   private String genericParameterTypes;
   
   private String parameterTypes;

   private String generic;

   private String declaringClass;

   public RoutineInfo()
   {
   }

   public RoutineInfo(Integer modifiers, String name, String[] genericExceptionTypes, String genericParameterTypes,
String parameterTypes, String generic, String declaringClass)
   {
      super(modifiers, name);
      this.genericExceptionTypes = genericExceptionTypes;
      this.genericParameterTypes = genericParameterTypes;
      this.parameterTypes = parameterTypes;
      this.generic = generic;
      this.declaringClass = declaringClass;
   }


   public String getGenericParameterTypes()
   {
      return genericParameterTypes;
   }

   public void setGenericParameterTypes(String genericParameterTypes)
   {
      this.genericParameterTypes = genericParameterTypes;
   }

   public void setGeneric(String generic)
   {
      this.generic = generic;
   }

   public String getGeneric()
   {
      return generic;
   }

   public String getDeclaringClass()
   {
      return declaringClass;
   }

   public void setDeclaringClass(String declaringClass)
   {
      this.declaringClass = declaringClass;
   }

   public String[] getGenericExceptionTypes()
   {
      return genericExceptionTypes;
   }

   public void setGenericExceptionTypes(String[] genericExceptionTypes)
   {
      this.genericExceptionTypes = genericExceptionTypes;
   }
   
   public String getParameterTypes()
   {
      return parameterTypes;
   }
   
   public void setParameterTypes(String parameterTypes)
   {
      this.parameterTypes = parameterTypes;
   }

}
