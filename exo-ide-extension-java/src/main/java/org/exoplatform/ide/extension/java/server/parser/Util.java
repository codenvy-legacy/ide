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
package org.exoplatform.ide.extension.java.server.parser;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.Type;

import org.exoplatform.ide.codeassistant.jvm.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.JavaType;
import org.exoplatform.ide.codeassistant.jvm.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.RoutineInfo;
import org.exoplatform.ide.codeassistant.jvm.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Nov 29, 2011 9:54:16 AM evgen $
 *
 */
public class Util
{

   public enum Modifier {
      STATIC(0x00000008), FINAL(0x00000010), PRIVATE(0x00000002), PUBLIC(0x00000001), PROTECTED(0x00000004), ABSTRACT(
         0x00000400), STRICTFP(0x00000800), SYNCHRONIZED(0x00000020), THREADSAFE(0), TRANSIENT(0x00000080), VOLATILE(
         0x00000040), NATIVE(0x00000100);
      private final int mod;

      Modifier(int i)
      {
         this.mod = i;
      }

      public int value()
      {
         return mod;
      }
   }

   public static TypeInfo convert(JavaClass clazz)
   {
      TypeInfo type = new TypeInfo();
      type.setName(clazz.getName());
      type.setQualifiedName(clazz.getFullyQualifiedName());
      type.setType(getType(clazz).name());
      if (clazz.getSuperJavaClass() != null)
         type.setSuperClass(clazz.getSuperJavaClass().getFullyQualifiedName());
      else
         type.setSuperClass("java.lang.Object");

      type.setModifiers(modifiersToInteger(clazz.getModifiers()));

      type.setInterfaces(toArray(clazz.getImplements()));
      type.setFields(toFieldInfo(clazz.getFields()));
      JavaMethod[] methods = clazz.getMethods(true);
      type.setConstructors(toConstructors(methods));
      // clazz.getMethods(true) return all methods, without private
      type.setMethods(toMethods(clazz.getMethods()));
      return type;
   }

   public static JavaType getType(JavaClass clazz)
   {
      if (clazz.isInterface())
      {
         return JavaType.INTERFACE;
      }
      if (clazz.isEnum())
         return JavaType.ENUM;

      return JavaType.CLASS;
   }

   /**
    * @param methods
    * @return
    */
   private static MethodInfo[] toMethods(JavaMethod[] methods)
   {
      List<MethodInfo> con = new ArrayList<MethodInfo>();
      for (JavaMethod m : methods)
      {
         if (!m.isConstructor())
         {
            MethodInfo i = new MethodInfo();
            i.setGenericExceptionTypes(toArray(m.getExceptions()));
            i.setGeneric(m.getDeclarationSignature(true));
            i.setModifiers(modifiersToInteger(m.getModifiers()));
            i.setParameterTypes(toParameters(m.getParameterTypes(true)));
            i.setName(m.getName());
            i.setDeclaringClass(m.getParentClass().getFullyQualifiedName());
            String returnType = m.getReturnType().getFullyQualifiedName();
            i.setGenericReturnType(returnType);
            i.setReturnType(returnType.substring(returnType.lastIndexOf('.') + 1));
            con.add(i);
         }
      }

      MethodInfo[] info = new MethodInfo[con.size()];
      return con.toArray(info);
   }

   /**
    * @param methods
    * @return
    */
   private static RoutineInfo[] toConstructors(JavaMethod[] methods)
   {
      List<RoutineInfo> con = new ArrayList<RoutineInfo>();
      for (JavaMethod m : methods)
      {
         if (m.isConstructor())
         {
            RoutineInfo i = new RoutineInfo();
            i.setGenericExceptionTypes(toArray(m.getExceptions()));
            i.setGeneric(m.getDeclarationSignature(true));
            i.setModifiers(modifiersToInteger(m.getModifiers()));
            i.setParameterTypes(toParameters(m.getParameterTypes(true)));
            i.setName(m.getName());
            i.setDeclaringClass(m.getParentClass().getFullyQualifiedName());
            con.add(i);
         }
      }

      RoutineInfo[] info = new RoutineInfo[con.size()];
      return con.toArray(info);
   }

   /**
    * @param parameterTypes
    * @return
    */
   private static String toParameters(Type[] parameterTypes)
   {
      int iMax = parameterTypes.length - 1;
      if (iMax == -1)
         return "()";

      StringBuilder b = new StringBuilder();
      b.append('(');
      for (int i = 0;; i++)
      {
         String fqn = parameterTypes[i].getJavaClass().getName();
         b.append(fqn.substring(fqn.lastIndexOf('.') + 1));
         if (i == iMax)
            return b.append(')').toString();
         b.append(", ");
      }
   }

   /**
    * @param parameterTypes
    * @return
    */
   private static String toGenericParameters(Type[] parameterTypes)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @param fields
    * @return
    */
   private static FieldInfo[] toFieldInfo(JavaField[] fields)
   {
      FieldInfo[] fi = new FieldInfo[fields.length];
      for (int i = 0; i < fields.length; i++)
      {
         FieldInfo info = new FieldInfo();
         JavaField f = fields[i];
         info.setDeclaringClass(f.getParentClass().getFullyQualifiedName());
         info.setType(f.getType().getValue());
         info.setName(f.getName());
         info.setModifiers(modifiersToInteger(f.getModifiers()));
         fi[i] = info;
      }

      return fi;
   }

   /**
    * @param modifiers
    * @return
    */
   private static Integer modifiersToInteger(String[] modifiers)
   {
      int i = 0;

      for (String s : modifiers)
      {
         i = i | Modifier.valueOf(s.toUpperCase()).value();
      }

      return i;
   }

   /**
    * Convert array type to String array of FQNs
    * @param types
    * @return
    */
   private static String[] toArray(Type[] types)
   {
      String[] arr = new String[types.length];
      for (int i = 0; i < types.length; i++)
      {
         arr[i] = types[i].getFullyQualifiedName();
      }
      return arr;
   }

   /**
    * @param clazz
    * @return
    */
   public static ShortTypeInfo toShortTypeInfo(JavaClass clazz)
   {
      ShortTypeInfo info = new ShortTypeInfo();
      info.setModifiers(modifiersToInteger(clazz.getModifiers()));
      info.setName(clazz.getName());
      info.setQualifiedName(clazz.getFullyQualifiedName());
      info.setType(getType(clazz).name());
      return info;
   }
}
