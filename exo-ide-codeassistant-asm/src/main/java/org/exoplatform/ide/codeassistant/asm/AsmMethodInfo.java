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
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.TraceSignatureVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class AsmMethodInfo extends AsmMember implements MethodInfo
{

   public final static String CONSTRUCTOR_METHOD_NAME = "<init>";

   private final MethodNode methodNode;

   private final AsmTypeInfo declaredClass;

   public AsmMethodInfo(MethodNode methodNode, AsmTypeInfo declaredClass)
   {
      super(methodNode.name, methodNode.access, methodNode);
      this.methodNode = methodNode;
      this.declaredClass = declaredClass;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.asm.AsmMember#getName()
    */
   @Override
   public String getName()
   {
      return isConstructor() ? getDeclaringClass() : super.getName();
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.MethodInfo#getDeclaringClass()
    */
   @Override
   public String getDeclaringClass()
   {
      return declaredClass.getName();
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.MethodInfo#getExceptionTypes()
    */
   @Override
   public List<String> getExceptionTypes()
   {
      List<String> result = new ArrayList<String>(methodNode.exceptions.size());
      for (Object type : methodNode.exceptions)
      {
         result.add(classNameFromType((String)type));
      }
      return result;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.MethodInfo#getParameterNames()
    */
   @Override
   public List<String> getParameterNames()
   {
      if (methodNode.localVariables == null || methodNode.localVariables.size() < 1)
      {
         return Collections.emptyList();
      }
      List<String> result = new ArrayList<String>(methodNode.localVariables.size() - 1);
      for (int i = 1; i < methodNode.localVariables.size(); i++)
      {
         LocalVariableNode var = (LocalVariableNode)methodNode.localVariables.get(i);
         result.add(var.name);
      }
      return result;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.MethodInfo#getParameterTypes()
    */
   @Override
   public List<String> getParameterTypes()
   {
      if (methodNode.signature != null)
      {
         SignatureReader reader = new SignatureReader(methodNode.signature);
         TraceSignatureVisitor v = new TraceSignatureVisitor(methodNode.access);
         reader.accept(v);
         String declaration = v.getDeclaration();
         if (declaration.length() > 2)
         {
            //(java.lang.String, java.lang.Integer, java.util.List<java.lang.String>)
            // cut braces, and split on ', '
            return Arrays.asList(declaration.substring(1, declaration.length() - 1).split(", "));
         }
         else
         {
            return Collections.emptyList();
         }
      }

      Type[] types = Type.getArgumentTypes(methodNode.desc);
      List<String> result = new ArrayList<String>(types.length);
      for (Type type : types)
      {
         result.add(type.getClassName());
      }
      return result;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.MethodInfo#getReturnType()
    */
   @Override
   public String getReturnType()
   {
      return Type.getReturnType(methodNode.desc).getClassName();
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.MethodInfo#isConstructor()
    */
   @Override
   public boolean isConstructor()
   {
      return CONSTRUCTOR_METHOD_NAME.equals(methodNode.name);
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.MethodInfo#setConstructor(boolean)
    */
   @Override
   public void setConstructor(boolean isConstructor)
   {
      throw new RuntimeException("Set not supported");
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.MethodInfo#setDeclaringClass(java.lang.String)
    */
   @Override
   public void setDeclaringClass(String declaringClass)
   {
      throw new RuntimeException("Set not supported");
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.MethodInfo#setExceptionTypes(java.util.List)
    */
   @Override
   public void setExceptionTypes(List<String> exceptionTypes)
   {
      throw new RuntimeException("Set not supported");
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.MethodInfo#setParameterNames(java.util.List)
    */
   @Override
   public void setParameterNames(List<String> parameterNames)
   {
      throw new RuntimeException("Set not supported");
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.MethodInfo#setParameterTypes(java.util.List)
    */
   @Override
   public void setParameterTypes(List<String> parameterTypes)
   {
      throw new RuntimeException("Set not supported");
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.MethodInfo#setReturnType(java.lang.String)
    */
   @Override
   public void setReturnType(String returnType)
   {
      throw new RuntimeException("Set not supported");
   }

}
