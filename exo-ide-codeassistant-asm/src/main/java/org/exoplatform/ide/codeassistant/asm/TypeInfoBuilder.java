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
import org.exoplatform.ide.codeassistant.jvm.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.RoutineInfo;
import org.exoplatform.ide.codeassistant.jvm.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * This class used for building TypeInfo object
 */
public class TypeInfoBuilder extends MemberInfoBuilder
{

   private final List<FieldInfo> fields;

   private final List<RoutineInfo> constructors;

   private final List<MethodInfo> methods;

   private final String qualifiedName;

   private final String superName;

   private final String[] interfaces;

   private final String type;

   public TypeInfoBuilder(int access, String name, String superName, String[] interfaces)
   {
      super(access, toShortName(toDot(name)));
      this.qualifiedName = toDot(name);
      if (superName != null)
      {
         this.superName = toDot(superName);
      }
      else
      {
         this.superName = null;
      }
      this.interfaces = interfaces;
      for (int i = 0; i < interfaces.length; i++)
      {
         interfaces[i] = toDot(interfaces[i]);
      }

      if ((access & MODIFIER_ENUM) != 0)
      {
         type = "ENUM";
      }
      else if ((access & MODIFIER_ANNOTATION) != 0)
      {
         type = "ANNOTATION";
      }
      else if ((access & Modifier.INTERFACE) != 0)
      {
         type = "INTERFACE";
      }
      else
      {
         type = "CLASS";
      }

      this.fields = new ArrayList<FieldInfo>();
      this.constructors = new ArrayList<RoutineInfo>();
      this.methods = new ArrayList<MethodInfo>();
   }

   public String getQualifiedName()
   {
      return qualifiedName;
   }

   public ShortTypeInfo buildShortTypeInfo()
   {
      return new ShortTypeInfo(access, name, qualifiedName, type);
   }

   public TypeInfo buildTypeInfo()
   {
      TypeInfo typeInfo = new TypeInfo();
      typeInfo.setModifiers(access);
      typeInfo.setName(name);
      typeInfo.setQualifiedName(qualifiedName);
      typeInfo.setDeclaredFields(fields.toArray(new FieldInfo[fields.size()]));
      typeInfo.setDeclaredMethods(methods.toArray(new MethodInfo[methods.size()]));
      typeInfo.setDeclaredConstructors(constructors.toArray(new RoutineInfo[constructors.size()]));

      List<FieldInfo> publicFields = new ArrayList<FieldInfo>();
      for (FieldInfo field : fields)
      {
         if ((field.getModifiers() & Modifier.PUBLIC) > 0)
         {
            publicFields.add(field);
         }
      }
      typeInfo.setFields(publicFields.toArray(new FieldInfo[publicFields.size()]));
      List<RoutineInfo> publicConstructors = new ArrayList<RoutineInfo>();
      for (RoutineInfo constructor : constructors)
      {
         if ((constructor.getModifiers() & Modifier.PUBLIC) > 0)
         {
            publicConstructors.add(constructor);
         }
      }
      typeInfo.setConstructors(publicConstructors.toArray(new RoutineInfo[publicConstructors.size()]));
      List<MethodInfo> publicMethods = new ArrayList<MethodInfo>();
      for (MethodInfo method : methods)
      {
         if ((method.getModifiers() & Modifier.PUBLIC) > 0)
         {
            publicMethods.add(method);
         }
      }
      typeInfo.setMethods(publicMethods.toArray(new MethodInfo[publicMethods.size()]));

      typeInfo.setInterfaces(interfaces);
      typeInfo.setSuperClass(superName);
      typeInfo.setType(type);
      return typeInfo;
   }

   public void addField(FieldInfo field)
   {
      if ((field.getModifiers() & MODIFIER_SYNTHETIC) == 0)
      {
         this.fields.add(field);
      }
   }

   public void addConstructor(RoutineInfo constructor)
   {
      if ((constructor.getModifiers() & MODIFIER_SYNTHETIC) == 0)
      {
         this.constructors.add(constructor);
      }
   }

   public void addMethod(MethodInfo method)
   {
      if ((method.getModifiers() & MODIFIER_SYNTHETIC) == 0)
      {
         this.methods.add(method);
      }
   }

}
