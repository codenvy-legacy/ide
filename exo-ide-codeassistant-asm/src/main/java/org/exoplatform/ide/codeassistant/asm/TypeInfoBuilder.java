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

import org.exoplatform.ide.codeassistant.jvm.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.JavaType;
import org.exoplatform.ide.codeassistant.jvm.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.exoplatform.ide.codeassistant.jvm.bean.FieldInfoBean;
import org.exoplatform.ide.codeassistant.jvm.bean.MethodInfoBean;
import org.exoplatform.ide.codeassistant.jvm.bean.TypeInfoBean;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class TypeInfoBuilder
{
   public final static String CONSTRUCTOR_METHOD_NAME = "<init>";

   public static String classNameFromType(String type)
   {
      // can be null for Object super class.
      return type == null ? "" : Type.getObjectType(type).getClassName();
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

   public static List<String> argumentTypesFromMethodDescriptor(String methodDescriptor)
   {
      Type[] types = Type.getArgumentTypes(methodDescriptor);
      List<String> result = new ArrayList<String>(types.length);
      for (Type type : types)
      {
         result.add(type.getClassName());
      }
      return result;
   }

   public static FieldInfo fromFieldNode(String declaredClass, FieldNode node)
   {
      return new FieldInfoBean(node.name, node.access, Type.getType(node.desc).getClassName(), declaredClass);
   }

   public static List<FieldInfo> fromFieldNodes(String declaredClass, List fields)
   {
      List<FieldInfo> result = new ArrayList<FieldInfo>(fields.size());
      for (Object field : fields)
      {
         result.add(fromFieldNode(declaredClass, (FieldNode)field));
      }
      return result;
   }

   public static MethodInfo fromMethodNode(String declaredClass, MethodNode node)
   {
      boolean isConstructor = CONSTRUCTOR_METHOD_NAME.equals(node.name);
      return new MethodInfoBean(isConstructor ? declaredClass : node.name, node.access,
         classNamesFromTypes(node.exceptions), argumentTypesFromMethodDescriptor(node.desc), new ArrayList<String>(),
         isConstructor, Type.getReturnType(node.desc).getClassName(), declaredClass);
   }

   public static List<MethodInfo> fromMethodNodes(String declaredClass, List methods)
   {
      List<MethodInfo> result = new ArrayList<MethodInfo>(methods.size());
      for (Object node : methods)
      {
         result.add(fromMethodNode(declaredClass, (MethodNode)node));
      }
      return result;
   }

   public static TypeInfo fromClassNode(ClassNode node)
   {
      String declaredClass = classNameFromType(node.name);
      return new TypeInfoBean(declaredClass, node.access, fromMethodNodes(declaredClass, node.methods), fromFieldNodes(
         declaredClass, node.fields), classNameFromType(node.superName), classNamesFromTypes(node.interfaces), JavaType
         .fromClassAttribute(node.access).toString());
   }
}
