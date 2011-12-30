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

import org.exoplatform.ide.codeassistant.jvm.shared.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.JavaType;
import org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AsmTypeInfo extends AsmMember implements TypeInfo
{
   private final ClassNode classNode;

   public AsmTypeInfo(ClassNode classNode)
   {
      super(classNameFromType(classNode.name), classNode.access, classNode);
      this.classNode = classNode;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.TypeInfo#getFields()
    */
   @Override
   public List<FieldInfo> getFields()
   {
      List<FieldInfo> result = new ArrayList<FieldInfo>(classNode.fields.size());
      for (Object node : classNode.fields)
      {
         result.add(new AsmFieldInfo((FieldNode)node, this));
      }
      return result;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.TypeInfo#getInterfaces()
    */
   @Override
   public List<String> getInterfaces()
   {
      List<String> result = new ArrayList<String>(classNode.interfaces.size());
      for (Object type : classNode.interfaces)
      {
         result.add(classNameFromType((String)type));
      }
      return result;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.TypeInfo#getMethods()
    */
   @Override
   public List<MethodInfo> getMethods()
   {
      List<MethodInfo> result = new ArrayList<MethodInfo>(classNode.methods.size());
      for (Object node : classNode.methods)
      {
         result.add(new AsmMethodInfo((MethodNode)node, this));
      }
      return result;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.TypeInfo#getSuperClass()
    */
   @Override
   public String getSuperClass()
   {
      return classNameFromType(classNode.superName);
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.ShortTypeInfo#getType()
    */
   @Override
   public String getType()
   {
      return JavaType.fromClassAttribute(classNode.access).toString();
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.TypeInfo#setFields(java.util.List)
    */
   @Override
   public void setFields(List<FieldInfo> fields)
   {
      throw new RuntimeException("Set not supported");
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.TypeInfo#setInterfaces(java.util.List)
    */
   @Override
   public void setInterfaces(List<String> interfaces)
   {
      throw new RuntimeException("Set not supported");

   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.TypeInfo#setMethods(java.util.List)
    */
   @Override
   public void setMethods(List<MethodInfo> methods)
   {
      throw new RuntimeException("Set not supported");
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.TypeInfo#setSuperClass(java.lang.String)
    */
   @Override
   public void setSuperClass(String superClass)
   {
      throw new RuntimeException("Set not supported");
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.ShortTypeInfo#setType(java.lang.String)
    */
   @Override
   public void setType(String type)
   {
      throw new RuntimeException("Set not supported");
   }

}
