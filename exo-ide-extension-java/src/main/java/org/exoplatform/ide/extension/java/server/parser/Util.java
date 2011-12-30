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

import org.exoplatform.ide.codeassistant.jvm.bean.FieldInfoBean;
import org.exoplatform.ide.codeassistant.jvm.bean.MethodInfoBean;
import org.exoplatform.ide.codeassistant.jvm.bean.ShortTypeInfoBean;
import org.exoplatform.ide.codeassistant.jvm.bean.TypeInfoBean;
import org.exoplatform.ide.codeassistant.jvm.shared.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.JavaType;
import org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.Type;

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
      TypeInfo type = new TypeInfoBean();
      type.setName(clazz.getFullyQualifiedName());
      type.setType(getType(clazz).name());
      if (clazz.getSuperJavaClass() != null)
         type.setSuperClass(clazz.getSuperJavaClass().getFullyQualifiedName());
      else
         type.setSuperClass("java.lang.Object");

      type.setModifiers(modifiersToInteger(clazz.getModifiers()));

      type.setInterfaces(toListFqn(clazz.getImplements()));
      type.setFields(toFieldInfo(clazz.getFields()));
      JavaMethod[] methods = clazz.getMethods(true);
      type.setMethods(toMethods(methods));
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
   private static List<MethodInfo> toMethods(JavaMethod[] methods)
   {
      List<MethodInfo> con = new ArrayList<MethodInfo>();
      for (JavaMethod m : methods)
      {
         MethodInfo i = new MethodInfoBean();
         i.setExceptionTypes(toListFqn(m.getExceptions()));
         i.setModifiers(modifiersToInteger(m.getModifiers()));
         Type[] parameterTypes = m.getParameterTypes(true);
         i.setParameterTypes(toParameters(parameterTypes));
         i.setName(m.getName());
         i.setDeclaringClass(m.getParentClass().getFullyQualifiedName());

         if (!m.isConstructor())
         {
            String returnType = m.getReturnType().getFullyQualifiedName();
            i.setReturnType(returnType);
            i.setConstructor(false);
         }
         else
         {
            i.setConstructor(true);
         }
         con.add(i);
      }
      return con;
   }

   /**
    * @param parameterTypes
    * @return
    */
   public static List<String> toParameters(Type[] parameterTypes)
   {
      List<String> params = new ArrayList<String>();
      for (Type type : parameterTypes)
      {
         params.add(type.getFullyQualifiedName());
      }
      return params;
   }

   /**
    * @param fields
    * @return
    */
   private static List<FieldInfo> toFieldInfo(JavaField[] fields)
   {
      List<FieldInfo> fi = new ArrayList<FieldInfo>();
      for (int i = 0; i < fields.length; i++)
      {
         FieldInfo info = new FieldInfoBean();
         JavaField f = fields[i];
         info.setDeclaringClass(f.getParentClass().getFullyQualifiedName());
         info.setType(f.getType().getValue());
         info.setName(f.getName());
         info.setModifiers(modifiersToInteger(f.getModifiers()));
         fi.add(info);
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
   private static List<String> toListFqn(Type[] types)
   {
      List<String> arr = new ArrayList<String>();
      for (int i = 0; i < types.length; i++)
      {
         arr.add(types[i].getFullyQualifiedName());
      }
      return arr;
   }

   /**
    * @param clazz
    * @return
    */
   public static ShortTypeInfo toShortTypeInfo(JavaClass clazz)
   {
      ShortTypeInfo info = new ShortTypeInfoBean();
      info.setModifiers(modifiersToInteger(clazz.getModifiers()));
      info.setName(clazz.getFullyQualifiedName());
      info.setType(getType(clazz).name());
      return info;
   }

   /**
    * @param tags
    * @return
    */
   public static String tagsToString(DocletTag[] tags)
   {
      if (tags == null)
         return "";
      StringBuilder b = new StringBuilder();
      for (DocletTag t : tags)
      {
         b.append("<p>").append("<b>").append(t.getName()).append("</b>").append("<br/>").append(t.getValue())
            .append("</p>");
      }
      return b.toString();
   }
}
