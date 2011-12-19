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
package org.exoplatform.ide.codeassistant.storage.lucene.writer;

import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage;
import org.exoplatform.ide.codeassistant.jvm.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.Member;
import org.exoplatform.ide.codeassistant.jvm.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class CachedTypeInfoResolver
{

   protected final CodeAssistantStorage storage;

   protected final Set<String> resolvedTypes;

   public CachedTypeInfoResolver(CodeAssistantStorage storage)
   {
      this.storage = storage;
      this.resolvedTypes = new HashSet<String>();
   }

   abstract protected boolean saveTypeInfo(List<TypeInfo> typeInfos);

   public TypeInfo resolveTypeInfo(String fqn) throws CodeAssistantException
   {
      TypeInfo typeInfo = storage.getTypeByFqn(fqn);
      if (typeInfo != null)
      {
         return resolveTypeInfo(typeInfo);
      }
      else
      {
         return null;
      }
   }

   public TypeInfo resolveTypeInfo(final TypeInfo typeInfo) throws CodeAssistantException
   {
      if (resolvedTypes.contains(typeInfo.getQualifiedName()))
      {
         return typeInfo;
      }

      List<FieldInfo> fields = new ArrayList<FieldInfo>();
      Set<String> hasFields = new HashSet<String>();
      List<MethodInfo> methods = new ArrayList<MethodInfo>();
      Set<String> hasMethods = new HashSet<String>();

      for (FieldInfo field : getPublicMembers(typeInfo.getFields()))
      {
         fields.add(field);
         hasFields.add(field.getName());
      }
      for (MethodInfo method : getPublicMembers(typeInfo.getMethods()))
      {
         methods.add(method);
         hasMethods.add(method.getName() + method.getGenericParameterTypes());
      }

      if (typeInfo.getSuperClass() != null)
      {
         TypeInfo parent = resolveTypeInfo(typeInfo.getSuperClass());
         if (parent != null)
         {
            for (FieldInfo field : getPublicMembers(parent.getFields()))
            {
               if (!hasFields.contains(field.getName()))
               {
                  fields.add(field);
                  hasFields.add(field.getName());
               }
            }
            for (MethodInfo method : getPublicMembers(parent.getMethods()))
            {
               if (!hasMethods.contains(method.getName() + method.getGenericParameterTypes()))
               {
                  methods.add(method);
                  hasMethods.add(method.getName() + method.getGenericParameterTypes());
               }
            }
         }
      }
      if (typeInfo.getInterfaces() != null)
      {
         for (String current : typeInfo.getInterfaces())
         {
            TypeInfo interfaceType = resolveTypeInfo(current);
            for (MethodInfo method : interfaceType.getMethods())
            {
               if (!hasMethods.contains(method.getName() + method.getGenericParameterTypes()))
               {
                  methods.add(method);
                  hasMethods.add(method.getName() + method.getGenericParameterTypes());
               }
            }
         }
      }

      typeInfo.setFields(fields.toArray(new FieldInfo[fields.size()]));
      typeInfo.setMethods(methods.toArray(new MethodInfo[methods.size()]));

      /*
       * If saving was failed, then CachedTypeInfoResolver will not cache typeInfo,
       * so next time, when CachedTypeInfoResolver receive to resolve its class or class which extends it,
       * it resolve TypeInfo recursively again.
       */
      List<TypeInfo> saveList = new ArrayList<TypeInfo>();
      saveList.add(typeInfo);
      if (saveTypeInfo(saveList))
      {
         resolvedTypes.add(typeInfo.getQualifiedName());
      }

      return typeInfo;
   }

   private <T extends Member> List<T> getPublicMembers(T[] members)
   {
      List<T> result = new ArrayList<T>();
      for (T member : members)
      {
         int modifiers = member.getModifiers();
         if ((modifiers & Modifier.PUBLIC) != 0)
         {
            result.add(member);
         }
      }
      return result;
   }

   private <T extends Member> List<T> getNotPrivateMembers(T[] members)
   {
      List<T> result = new ArrayList<T>();
      for (T member : members)
      {
         int modifiers = member.getModifiers();
         if ((modifiers & Modifier.PRIVATE) == 0)
         {
            result.add(member);
         }
      }
      return result;
   }

}
