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
package org.exoplatform.ide.codeassistant.storage.lucene.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.MapFieldSelector;
import org.apache.lucene.index.IndexReader;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.jvm.shared.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.externalization.ExternalizationTools;
import org.exoplatform.ide.codeassistant.storage.lucene.DataIndexFields;
import org.exoplatform.ide.codeassistant.storage.lucene.LuceneCodeAssistantStorage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Create TypeInfo from lucene document.
 */
public class TypeInfoExtractor implements ContentExtractor<TypeInfo>
{

   private final LuceneCodeAssistantStorage luceneCodeAssistantStorage;

   private final static String OBJECT_NAME = "java.lang.Object";

   /**
    * This constant used for caching TypeInfo of java.lang.Object class. It will
    * be initialized in first query
    */
   private static TypeInfo OBJECT_TYPE = null;

   /**
    * @param luceneCodeAssistantStorage
    */
   public TypeInfoExtractor(LuceneCodeAssistantStorage luceneCodeAssistantStorage)
   {
      this.luceneCodeAssistantStorage = luceneCodeAssistantStorage;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.storage.lucene.search.ContentExtractor#getValue(int)
    */
   @Override
   public TypeInfo getValue(IndexReader reader, int doc) throws IOException
   {

      Document document = reader.document(doc, new MapFieldSelector(new String[]{DataIndexFields.TYPE_INFO}));
      byte[] contentField = document.getBinaryValue(DataIndexFields.TYPE_INFO);
      TypeInfo result = ExternalizationTools.readExternal(new ByteArrayInputStream(contentField));
      if (result.getSuperClass().isEmpty() && result.getInterfaces().isEmpty())
      {
         return result;
      }
      else
      {
         try
         {
            if (!result.getSuperClass().isEmpty())
            {
               if (OBJECT_NAME.equals(result.getSuperClass()))
               {
                  if (OBJECT_TYPE == null)
                  {
                     OBJECT_TYPE = luceneCodeAssistantStorage.getTypeByFqn(result.getSuperClass());
                  }
                  mergeType(result, OBJECT_TYPE);
               }
               else
               {
                  mergeType(result, luceneCodeAssistantStorage.getTypeByFqn(result.getSuperClass()));
               }
            }
            for (String interfaceName : result.getInterfaces())
            {
               mergeType(result, luceneCodeAssistantStorage.getTypeByFqn(interfaceName));
            }
         }
         catch (CodeAssistantException e)
         {
            throw new IOException(e.getLocalizedMessage(), e);
         }
      }
      return result;
   }

   public void mergeType(TypeInfo recipient, TypeInfo ancestor)
   {
      if (ancestor != null)
      {
         ArrayList<FieldInfo> fields = new ArrayList<FieldInfo>();
         Set<String> existedFields = new HashSet<String>();
         for (FieldInfo field : recipient.getFields())
         {
            if (Modifier.isPublic(field.getModifiers()))
            {
               existedFields.add(field.getName());
               fields.add(field);
            }
         }
         for (FieldInfo field : ancestor.getFields())
         {
            if (Modifier.isPublic(field.getModifiers()) && !existedFields.contains(field.getName()))
            {
               existedFields.add(field.getName());
               fields.add(field);
            }
         }
         recipient.setFields(fields);

         ArrayList<MethodInfo> methods = new ArrayList<MethodInfo>();
         Set<String> existedMethods = new HashSet<String>();
         for (MethodInfo method : recipient.getMethods())
         {
            if (Modifier.isPublic(method.getModifiers()))
            {
               existedMethods.add(getMethodDeclaration(method));
               methods.add(method);
            }
         }
         for (MethodInfo method : ancestor.getMethods())
         {
            if (Modifier.isPublic(method.getModifiers()) && !existedMethods.contains(getMethodDeclaration(method))
               && !method.isConstructor())
            {
               existedMethods.add(getMethodDeclaration(method));
               methods.add(method);
            }
         }
         recipient.setMethods(methods);
      }
   }

   private String getMethodDeclaration(MethodInfo method)
   {
      StringBuilder builder = new StringBuilder();
      builder.append(method.getName());
      builder.append("(");
      boolean isFirst = true;
      for (String parameter : method.getParameterTypes())
      {
         if (!isFirst)
         {
            builder.append(", ");
         }
         builder.append(parameter);
         isFirst = false;
      }
      builder.append(")");
      return builder.toString();
   }

}