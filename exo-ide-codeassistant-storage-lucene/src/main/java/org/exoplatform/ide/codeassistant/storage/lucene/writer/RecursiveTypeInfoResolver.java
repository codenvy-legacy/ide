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
import org.exoplatform.ide.codeassistant.jvm.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;

import java.lang.reflect.Modifier;

/**
 * ???
 */
public class RecursiveTypeInfoResolver
{

   private final CodeAssistantStorage storage;

   public RecursiveTypeInfoResolver(CodeAssistantStorage storage)
   {
      this.storage = storage;
   }

   public TypeInfo resolveTypeInfo(TypeInfo typeInfo) throws CodeAssistantException
   {
      MemberInfo Info = new MemberInfo();
      Info.addFields(typeInfo.getFields(), MemberInfo.ALL_ALLOWED);
      Info.addMethods(typeInfo.getMethods(), MemberInfo.ALL_ALLOWED);
      if (!typeInfo.getSuperClass().isEmpty())
      {
         MemberInfo superInfo = getMemberInfoByTypeInfo(storage.getTypeByFqn(typeInfo.getSuperClass()));
         Info.addFields(superInfo.getFields(), MemberInfo.EXCEPT_PRIVATE);
         Info.addMethods(superInfo.getMethods(), MemberInfo.EXCEPT_PRIVATE);
      }

      for (String current : typeInfo.getInterfaces())
      {
         MemberInfo currentInfo = getMemberInfoByTypeInfo(storage.getTypeByFqn(current));
         Info.addFieldsIfNotExists(currentInfo.getFields(), MemberInfo.EXCEPT_PRIVATE);
         Info.addMethodsIfNotExists(currentInfo.getMethods(), MemberInfo.EXCEPT_PRIVATE);
      }

      typeInfo.setFields(Info.getFields().toArray(new FieldInfo[Info.getFields().size()]));
      typeInfo.setMethods(Info.getMethods().toArray(new MethodInfo[Info.getMethods().size()]));

      return typeInfo;
   }

   private MemberInfo getMemberInfoByTypeInfo(TypeInfo typeInfo) throws CodeAssistantException
   {
      MemberInfo Info = new MemberInfo();

      for (FieldInfo field : typeInfo.getFields())
      {
         if ((field.getModifiers() & Modifier.PRIVATE) == 0)
         {
            Info.addField(field);
         }
      }
      for (MethodInfo method : typeInfo.getMethods())
      {
         if ((method.getModifiers() & Modifier.PRIVATE) == 0)
         {
            Info.addMethod(method);
         }
      }

      if (!typeInfo.getSuperClass().isEmpty())
      {
         MemberInfo superInfo = getMemberInfoByTypeInfo(storage.getTypeByFqn(typeInfo.getSuperClass()));

         Info.addFieldsIfNotExists(superInfo.getFields(), MemberInfo.EXCEPT_PRIVATE);
         Info.addMethodsIfNotExists(superInfo.getMethods(), MemberInfo.EXCEPT_PRIVATE);
      }

      for (String current : typeInfo.getInterfaces())
      {
         MemberInfo interfaceInfo = getMemberInfoByTypeInfo(storage.getTypeByFqn(current));

         Info.addFieldsIfNotExists(interfaceInfo.getFields(), MemberInfo.EXCEPT_PRIVATE);
         Info.addMethodsIfNotExists(interfaceInfo.getMethods(), MemberInfo.EXCEPT_PRIVATE);
      }

      return Info;
   }

}
