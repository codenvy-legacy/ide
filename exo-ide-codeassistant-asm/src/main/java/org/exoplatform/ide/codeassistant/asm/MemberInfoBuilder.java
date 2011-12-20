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

import java.lang.reflect.Modifier;

/**
 * This class used for building MemberInfo objects
 */
public class MemberInfoBuilder
{

   /**
    * There are constants for <b>modifier</b> flag.<br>
    * There are no (yet) <b>public</b> constants for this flags in {@list
    * Modifier}, because <b>they have different meanings for fields and
    * methods</b>. More details, see in {@link Modifier} source;
    * 
    */
   public static final int MODIFIER_SYNTHETIC = 0x00001000;

   public static final int MODIFIER_ANNOTATION = 0x00002000;

   public static final int MODIFIER_ENUM = 0x00004000;

   protected final int access;

   protected final String name;

   public MemberInfoBuilder(int access, String name)
   {
      this.access = access;
      this.name = toDot(name);
   }

   /**
    * Method transforms variable types<br>
    * from: [Ljava/io/ObjectStreamField;<br>
    * to: java.io.ObjectStreamField[]
    * 
    * @param type
    * @return
    */
   protected static String transformTypeFormat(String type)
   {
      int index = 0;
      while (type.charAt(index) == '[')
      {
         index++;
      }
      return transformTypeFormat(type.substring(index), index);
   }

   /**
    * Method transforms variable types without arrays<br>
    * from: Ljava/io/ObjectStreamField;<br>
    * to: java.io.ObjectStreamField<br>
    * 
    * Mainly, this method will be used only from
    * {@link #transformTypeFormat(String)} method.<br>
    * 
    * @param type
    * @param arrayLevel
    *           level of arrays which will add after type
    * @return
    */
   protected static String transformTypeFormat(String type, int arrayLevel)
   {
      StringBuilder transformedTypeBuilder = new StringBuilder();
      char variableType;
      String ext = null;

      int index = 0;
      variableType = type.charAt(index);

      switch (variableType)
      {
         case 'Z' :
            transformedTypeBuilder.append("boolean");
            break;
         case 'B' :
            transformedTypeBuilder.append("byte");
            break;
         case 'C' :
            transformedTypeBuilder.append("char");
            break;
         case 'S' :
            transformedTypeBuilder.append("short");
            break;
         case 'I' :
            transformedTypeBuilder.append("int");
            break;
         case 'J' :
            transformedTypeBuilder.append("long");
            break;
         case 'F' :
            transformedTypeBuilder.append("float");
            break;
         case 'D' :
            transformedTypeBuilder.append("double");
            break;
         case 'V' :
            transformedTypeBuilder.append("void");
            break;
         case 'L' :
            // type.length() - 1 for removing ';' after class name
            ext = type.substring(index + 1, type.length() - 1);
            transformedTypeBuilder.append(toDot(ext));
            break;
      }

      for (int i = 0; i < arrayLevel; i++)
      {
         transformedTypeBuilder.append("[]");
      }

      return transformedTypeBuilder.toString();
   }

   protected static String toShortName(String name)
   {
      return name.substring(name.lastIndexOf('.') + 1);
   }

   protected static String toDot(String name)
   {
      return name.replace('/', '.');
   }

}
