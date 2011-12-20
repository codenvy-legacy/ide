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

import org.exoplatform.ide.codeassistant.jvm.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.MethodInfo;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ???
 */
public class MemberInfo
{

   public static final int ALL_ALLOWED = 0;

   public static final int EXCEPT_PRIVATE = Modifier.PRIVATE;

   private final List<FieldInfo> fields;

   private final Set<String> fieldNames;

   private final List<MethodInfo> methods;

   private final Set<String> methodNames;

   public MemberInfo()
   {
      this.fields = new ArrayList<FieldInfo>();
      this.methods = new ArrayList<MethodInfo>();

      this.fieldNames = new HashSet<String>();
      this.methodNames = new HashSet<String>();
   }

   public List<FieldInfo> getFields()
   {
      return fields;
   }

   public List<MethodInfo> getMethods()
   {
      return methods;
   }

   void addField(FieldInfo field)
   {
      this.fields.add(field);
      this.fieldNames.add(field.getName());
   }

   boolean addFieldIfNotExists(FieldInfo field)
   {
      if (!containsField(field))
      {
         addField(field);
         return true;
      }
      else
      {
         return false;
      }
   }

   void addFields(FieldInfo[] fields, int access)
   {
      for (FieldInfo field : fields)
      {
         if ((field.getModifiers() & access) == 0)
         {
            addField(field);
         }
      }
   }

   void addFields(List<FieldInfo> fields, int access)
   {
      for (FieldInfo field : fields)
      {
         if ((field.getModifiers() & access) == 0)
         {
            addField(field);
         }
      }
   }

   void addFieldsIfNotExists(FieldInfo[] fields, int access)
   {
      for (FieldInfo field : fields)
      {
         if ((field.getModifiers() & access) == 0)
         {
            addFieldIfNotExists(field);
         }
      }
   }

   void addFieldsIfNotExists(List<FieldInfo> fields, int access)
   {
      for (FieldInfo field : fields)
      {
         if ((field.getModifiers() & access) == 0)
         {
            addFieldIfNotExists(field);
         }
      }
   }

   void addMethod(MethodInfo method)
   {
      this.methods.add(method);
      this.methodNames.add(method.getName());
   }

   boolean addMethodIfNotExists(MethodInfo method)
   {
      if (!containsMethod(method))
      {
         addMethod(method);
         return true;
      }
      else
      {
         return false;
      }
   }

   void addMethods(MethodInfo[] methods, int access)
   {
      for (MethodInfo method : methods)
      {
         if ((method.getModifiers() & access) == 0)
         {
            addMethod(method);
         }
      }
   }

   void addMethods(List<MethodInfo> methods, int access)
   {
      for (MethodInfo method : methods)
      {
         if ((method.getModifiers() & access) == 0)
         {
            addMethod(method);
         }
      }
   }

   void addMethodsIfNotExists(MethodInfo[] methods, int access)
   {
      for (MethodInfo method : methods)
      {
         if ((method.getModifiers() & access) == 0)
         {
            addMethodIfNotExists(method);
         }
      }
   }

   void addMethodsIfNotExists(List<MethodInfo> methods, int access)
   {
      for (MethodInfo method : methods)
      {
         if ((method.getModifiers() & access) == 0)
         {
            addMethodIfNotExists(method);
         }
      }
   }

   public boolean containsField(FieldInfo field)
   {
      return fieldNames.contains(field.getName());
   }

   public boolean containsMethod(MethodInfo method)
   {
      return methodNames.contains(method.getName());
   }

}