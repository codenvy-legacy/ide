/*
 * Copyright (C) 2011 eXo Platform SAS.
   
   void addField(int access, String name, String desc)
   {
   }
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
package org.exoplatform.ide.codeassistant.asm;

import org.exoplatform.ide.codeassistant.jvm.RoutineInfo;

import java.util.ArrayList;

/**
 * This class used for building RoutineInfo objects, this class can't be used as
 * is. You can use: ConstructorInfoBuilder and MethodInfoBuilder
 */
public abstract class RoutineInfoBuilder extends MemberInfoBuilder
{

   protected String desc;

   protected String declaredClass;

   protected String[] exceptions;

   public RoutineInfoBuilder(int access, String name, String[] exceptions, String desc, String declaredClass)
   {
      super(access, name);
      this.exceptions = exceptions;
      this.desc = desc;
      this.declaredClass = declaredClass;

   }

   protected void fillRoutineInfo(RoutineInfo routineInfo)
   {
      routineInfo.setModifiers(access);
      routineInfo.setName(name);
      routineInfo.setDeclaringClass(declaredClass);
      if (exceptions != null)
      {
         for (int i = 0; i < exceptions.length; i++)
         {
            exceptions[i] = toDot(exceptions[i]);
         }
      }
      if (exceptions == null)
      {
         routineInfo.setGenericExceptionTypes(new String[0]);
      }
      else
      {
         routineInfo.setGenericExceptionTypes(exceptions);
      }

      String[] variables = splitMethodDesc(desc);
      StringBuilder genericParameterTypesBuilder = new StringBuilder();
      StringBuilder parameterTypesBuilder = new StringBuilder();
      genericParameterTypesBuilder.append("(");
      parameterTypesBuilder.append("(");
      for (int i = 0; i < variables.length; i++)
      {
         if (i > 0)
         {
            genericParameterTypesBuilder.append(", ");
            parameterTypesBuilder.append(", ");
         }
         genericParameterTypesBuilder.append(variables[i]);
         parameterTypesBuilder.append(toShortName(variables[i]));
      }
      genericParameterTypesBuilder.append(")");
      parameterTypesBuilder.append(")");
      routineInfo.setGenericParameterTypes(genericParameterTypesBuilder.toString());
      routineInfo.setParameterTypes(parameterTypesBuilder.toString());

   }

   /**
    * Method get method description and return genericParameterTypes. Return
    * type ignored<br>
    * For example:<br>
    * input: (I,[D,Ljava.lang.String)V output: ["int", "double[]",
    * "java.lang.String"]
    * 
    * @param desc
    * @return
    */
   private String[] splitMethodDesc(String desc)
   {
      desc = desc.substring(0, desc.lastIndexOf(')'));
      ArrayList<String> result = new ArrayList<String>();
      int index = 0;
      int arrayLevel = 0;
      while (index < desc.length())
      {
         char c = desc.charAt(index);
         // skip separator characters
         if (c == '[')
         {
            arrayLevel++;
         }
         else if (!(c == '(' || c == ')' || c == ' '))
         {
            if (c == 'L')
            {
               // object
               int start = index;
               index++;
               while (desc.charAt(index) != ';')
               {
                  index++;
               }
               result.add(transformTypeFormat(desc.substring(start, index + 1), arrayLevel));
            }
            else
            {
               // primitive
               result.add(transformTypeFormat(desc.substring(index, index + 1), arrayLevel));
            }
            arrayLevel = 0;
         }
         index++;
      }
      return result.toArray(new String[0]);
   }

}
