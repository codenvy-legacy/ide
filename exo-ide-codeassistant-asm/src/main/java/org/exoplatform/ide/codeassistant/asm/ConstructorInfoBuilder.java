/*
 * Copyright (C) 2011 eXo Platform SAS.
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

/**
 * This class used for building RoutineInfo objects as constructors
 */
public class ConstructorInfoBuilder extends RoutineInfoBuilder
{

   public ConstructorInfoBuilder(int access, String[] exceptions, String desc, String declaredClass)
   {
      super(access, toShortName(declaredClass), exceptions, desc, declaredClass);
   }

   public RoutineInfo buildConstructorInfo()
   {
      RoutineInfo constructorInfo = new RoutineInfo();

      fillRoutineInfo(constructorInfo);

      StringBuilder genericBuilder = new StringBuilder();
      String stringModifier = constructorInfo.modifierToString();
      if (!stringModifier.isEmpty())
      {
         genericBuilder.append(stringModifier);
         genericBuilder.append(" ");
      }
      genericBuilder.append(constructorInfo.getDeclaringClass());
      genericBuilder.append(constructorInfo.getGenericParameterTypes());
      if (exceptions != null && exceptions.length > 0)
      {
         genericBuilder.append(" throws ");
         for (int i = 0; i < exceptions.length; i++)
         {
            if (i != 0)
            {
               genericBuilder.append(", ");
            }
            genericBuilder.append(exceptions[i]);
         }
      }
      constructorInfo.setGeneric(genericBuilder.toString());

      return constructorInfo;
   }

}
