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
package org.exoplatform.ide.groovy.codeassistant.bean;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class MethodInfo extends RoutineInfo
{

   private String genericReturnType;

   public MethodInfo()
   {
   }

   public MethodInfo(Integer modifiers, String name, String[] genericExceptionTypes,
      String[] genericParameterTypes, String generic, String declaringClass, String genericReturnType)
   {
      super(modifiers, name, genericExceptionTypes, genericParameterTypes, generic, declaringClass);
      this.genericReturnType = genericReturnType;
   }

   public String getGenericReturnType()
   {
      return genericReturnType;
   }

   public void setGenericReturnType(String genericReturnType)
   {
      this.genericReturnType = genericReturnType;
   }
}
