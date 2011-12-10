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
package org.exoplatform.ide.codeassistant.storage.extension;

import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage;
import org.exoplatform.ide.codeassistant.jvm.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.RoutineInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class DeclaredInfo
{

   public static final int ALL_ALLOWED = 0;

   public static final int EXCEPT_PRIVATE = Modifier.PRIVATE;

   private List<FieldInfo> declaredFields;

   private Set<String> declaredFieldNames;

   private List<RoutineInfo> declaredConstructors;

   private Set<String> declaredConstructorNames;

   private List<MethodInfo> declaredMethods;

   private Set<String> declaredMethodNames;

   public DeclaredInfo()
   {
      this.declaredFields = new ArrayList<FieldInfo>();
      this.declaredConstructors = new ArrayList<RoutineInfo>();
      this.declaredMethods = new ArrayList<MethodInfo>();

      this.declaredFieldNames = new HashSet<String>();
      this.declaredConstructorNames = new HashSet<String>();
      this.declaredMethodNames = new HashSet<String>();
   }

   public List<FieldInfo> getDeclaredFields()
   {
      return declaredFields;
   }

   public List<RoutineInfo> getDeclaredConstructors()
   {
      return declaredConstructors;
   }

   public List<MethodInfo> getDeclaredMethods()
   {
      return declaredMethods;
   }

   void addDeclaredField(FieldInfo field)
   {
      this.declaredFields.add(field);
      this.declaredFieldNames.add(field.getName());
   }

   boolean addDeclaredFieldIfNotExists(FieldInfo field)
   {
      if (!containsDeclaredField(field))
      {
         addDeclaredField(field);
         return true;
      }
      else
      {
         return false;
      }
   }

   void addDeclaredFields(FieldInfo[] fields, int access)
   {
      for (FieldInfo field : fields)
      {
         if ((field.getModifiers() & access) == 0)
         {
            addDeclaredField(field);
         }
      }
   }

   void addDeclaredFields(List<FieldInfo> fields, int access)
   {
      for (FieldInfo field : fields)
      {
         if ((field.getModifiers() & access) == 0)
         {
            addDeclaredField(field);
         }
      }
   }

   void addDeclaredFieldsIfNotExists(FieldInfo[] fields, int access)
   {
      for (FieldInfo field : fields)
      {
         if ((field.getModifiers() & access) == 0)
         {
            addDeclaredFieldIfNotExists(field);
         }
      }
   }

   void addDeclaredFieldsIfNotExists(List<FieldInfo> fields, int access)
   {
      for (FieldInfo field : fields)
      {
         if ((field.getModifiers() & access) == 0)
         {
            addDeclaredFieldIfNotExists(field);
         }
      }
   }

   void addDeclaredConstructor(RoutineInfo constructor)
   {
      this.declaredConstructors.add(constructor);
      this.declaredConstructorNames.add(constructor.getName());
   }

   boolean addDeclaredConstructorIfNotExists(RoutineInfo constructor)
   {
      if (!containsDeclaredConstructor(constructor))
      {
         addDeclaredConstructor(constructor);
         return true;
      }
      else
      {
         return false;
      }
   }

   void addDeclaredConstructors(RoutineInfo[] constructors, int access)
   {
      for (RoutineInfo constructor : constructors)
      {
         if ((constructor.getModifiers() & access) == 0)
         {
            addDeclaredConstructor(constructor);
         }
      }
   }

   void addDeclaredConstructors(List<RoutineInfo> constructors, int access)
   {
      for (RoutineInfo constructor : constructors)
      {
         if ((constructor.getModifiers() & access) == 0)
         {
            addDeclaredConstructor(constructor);
         }
      }
   }

   void addDeclaredConstructorsIfNotExists(RoutineInfo[] constructors, int access)
   {
      for (RoutineInfo constructor : constructors)
      {
         if ((constructor.getModifiers() & access) == 0)
         {
            addDeclaredConstructorIfNotExists(constructor);
         }
      }
   }

   void addDeclaredConstructorsIfNotExists(List<RoutineInfo> constructors, int access)
   {
      for (RoutineInfo constructor : constructors)
      {
         if ((constructor.getModifiers() & access) == 0)
         {
            addDeclaredConstructorIfNotExists(constructor);
         }
      }
   }

   void addDeclaredMethod(MethodInfo method)
   {
      this.declaredMethods.add(method);
      this.declaredMethodNames.add(method.getName());
   }

   boolean addDeclaredMethodIfNotExists(MethodInfo method)
   {
      if (!containsDeclaredMethod(method))
      {
         addDeclaredMethod(method);
         return true;
      }
      else
      {
         return false;
      }
   }

   void addDeclaredMethods(MethodInfo[] methods, int access)
   {
      for (MethodInfo method : methods)
      {
         if ((method.getModifiers() & access) == 0)
         {
            addDeclaredMethod(method);
         }
      }
   }

   void addDeclaredMethods(List<MethodInfo> methods, int access)
   {
      for (MethodInfo method : methods)
      {
         if ((method.getModifiers() & access) == 0)
         {
            addDeclaredMethod(method);
         }
      }
   }

   void addDeclaredMethodsIfNotExists(MethodInfo[] methods, int access)
   {
      for (MethodInfo method : methods)
      {
         if ((method.getModifiers() & access) == 0)
         {
            addDeclaredMethodIfNotExists(method);
         }
      }
   }

   void addDeclaredMethodsIfNotExists(List<MethodInfo> methods, int access)
   {
      for (MethodInfo method : methods)
      {
         if ((method.getModifiers() & access) == 0)
         {
            addDeclaredMethodIfNotExists(method);
         }
      }
   }

   public boolean containsDeclaredField(FieldInfo field)
   {
      return declaredFieldNames.contains(field.getName());
   }

   public boolean containsDeclaredConstructor(RoutineInfo constructor)
   {
      return declaredConstructorNames.contains(constructor.getName());
   }

   public boolean containsDeclaredMethod(MethodInfo method)
   {
      return declaredMethodNames.contains(method.getName());
   }

}

public class DeclaredTypeInfoResolver
{

   private final CodeAssistantStorage storage;

   public DeclaredTypeInfoResolver(CodeAssistantStorage storage)
   {
      this.storage = storage;
   }

   public TypeInfo resolveTypeInfo(TypeInfo typeInfo) throws CodeAssistantException
   {
      DeclaredInfo declaredInfo = new DeclaredInfo();
      declaredInfo.addDeclaredFields(typeInfo.getFields(), DeclaredInfo.ALL_ALLOWED);
      declaredInfo.addDeclaredConstructors(typeInfo.getConstructors(), DeclaredInfo.ALL_ALLOWED);
      declaredInfo.addDeclaredMethods(typeInfo.getMethods(), DeclaredInfo.ALL_ALLOWED);
      if (!typeInfo.getSuperClass().isEmpty())
      {
         DeclaredInfo superInfo = getDeclaredInfoByTypeInfo(storage.getTypeByFqn(typeInfo.getSuperClass()));
         declaredInfo.addDeclaredFields(superInfo.getDeclaredFields(), DeclaredInfo.EXCEPT_PRIVATE);
         declaredInfo.addDeclaredFields(superInfo.getDeclaredFields(), DeclaredInfo.EXCEPT_PRIVATE);
         declaredInfo.addDeclaredFields(superInfo.getDeclaredFields(), DeclaredInfo.EXCEPT_PRIVATE);
      }

      for (String current : typeInfo.getInterfaces())
      {
         DeclaredInfo currentInfo = getDeclaredInfoByTypeInfo(storage.getTypeByFqn(current));
         declaredInfo.addDeclaredFieldsIfNotExists(currentInfo.getDeclaredFields(), DeclaredInfo.EXCEPT_PRIVATE);
         declaredInfo.addDeclaredMethodsIfNotExists(currentInfo.getDeclaredMethods(), DeclaredInfo.EXCEPT_PRIVATE);
      }

      typeInfo.setDeclaredFields(declaredInfo.getDeclaredFields().toArray(
         new FieldInfo[declaredInfo.getDeclaredFields().size()]));
      typeInfo.setDeclaredConstructors(declaredInfo.getDeclaredConstructors().toArray(
         new RoutineInfo[declaredInfo.getDeclaredConstructors().size()]));
      typeInfo.setDeclaredMethods(declaredInfo.getDeclaredMethods().toArray(
         new MethodInfo[declaredInfo.getDeclaredMethods().size()]));

      return typeInfo;
   }

   private DeclaredInfo getDeclaredInfoByTypeInfo(TypeInfo typeInfo) throws CodeAssistantException
   {
      DeclaredInfo declaredInfo = new DeclaredInfo();

      for (FieldInfo field : typeInfo.getFields())
      {
         if ((field.getModifiers() & Modifier.PRIVATE) == 0)
         {
            declaredInfo.addDeclaredField(field);
         }
      }
      for (RoutineInfo constructor : typeInfo.getConstructors())
      {
         if ((constructor.getModifiers() & Modifier.PRIVATE) == 0)
         {
            declaredInfo.addDeclaredConstructor(constructor);
         }
      }
      for (MethodInfo method : typeInfo.getMethods())
      {
         if ((method.getModifiers() & Modifier.PRIVATE) == 0)
         {
            declaredInfo.addDeclaredMethod(method);
         }
      }

      if (!typeInfo.getSuperClass().isEmpty())
      {
         DeclaredInfo superInfo = getDeclaredInfoByTypeInfo(storage.getTypeByFqn(typeInfo.getSuperClass()));

         declaredInfo.addDeclaredFieldsIfNotExists(superInfo.getDeclaredFields(), DeclaredInfo.EXCEPT_PRIVATE);
         // declaredInfo.addDeclaredConstructorsIfNotExists(superInfo.getDeclaredConstructors(), DeclaredInfo.EXCEPT_PRIVATE);
         declaredInfo.addDeclaredMethodsIfNotExists(superInfo.getDeclaredMethods(), DeclaredInfo.EXCEPT_PRIVATE);
      }

      for (String current : typeInfo.getInterfaces())
      {
         DeclaredInfo interfaceInfo = getDeclaredInfoByTypeInfo(storage.getTypeByFqn(current));

         declaredInfo.addDeclaredFieldsIfNotExists(interfaceInfo.getDeclaredFields(), DeclaredInfo.EXCEPT_PRIVATE);
         declaredInfo.addDeclaredMethodsIfNotExists(interfaceInfo.getDeclaredMethods(), DeclaredInfo.EXCEPT_PRIVATE);
      }

      return declaredInfo;
   }

}
