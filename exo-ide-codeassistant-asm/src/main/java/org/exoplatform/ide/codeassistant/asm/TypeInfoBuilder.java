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

import org.exoplatform.ide.codeassistant.jvm.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class TypeInfoBuilder
{

   public static String classNameFromType(String type)
   {
      return Type.getObjectType(type).getClassName();
   }

   public static List<String> classNamesFromTypes(List<String> types)
   {
      List<String> result = new ArrayList<String>(types.size());
      for (String type : types)
      {
         result.add(classNameFromType(type));
      }
      return result;
   }

   public static String[] argumentTypesFromMethodDescriptor(String methodDescriptor)
   {
      Type[] types = Type.getArgumentTypes(methodDescriptor);
      String[] result = new String[types.length];
      for (int i = 0; i < types.length; i++)
      {
         result[i] = types[i].getClassName();
      }
      return result;
   }

   public static MethodInfo fromMethodNode(MethodNode node)
   {
      //      return new MethodInfo(classNameFromType(node.name), node.access, classNamesFromTypes(node.exceptions),
      //         argumentTypesFromMethodDescriptor(node.desc), false, node.);
      return null;
   }

   public static TypeInfo fromClassNode(ClassNode node)
   {
      return new TypeInfo(classNameFromType(node.name), node.access, null, null, classNameFromType(node.superName),
         classNamesFromTypes(node.interfaces), "removeme");
   }
}
