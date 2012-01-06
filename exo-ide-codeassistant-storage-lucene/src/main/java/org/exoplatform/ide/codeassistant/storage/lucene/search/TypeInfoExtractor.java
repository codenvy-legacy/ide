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
import org.exoplatform.ide.codeassistant.asm.ClassParser;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.jvm.shared.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.externalization.ExternalizationTools;
import org.exoplatform.ide.codeassistant.storage.lucene.DataIndexFields;
import org.exoplatform.ide.codeassistant.storage.lucene.LuceneCodeAssistantStorage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Create TypeInfo from lucene document.
 */
public class TypeInfoExtractor implements ContentExtractor<TypeInfo>
{

   private final LuceneCodeAssistantStorage luceneCodeAssistantStorage;

   private final static String OBJECT_NAME = "java.lang.Object";

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
                  mergeType(result, ClassParser.OBJECT_TYPE);
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
         fields.addAll(recipient.getFields());
         fields.addAll(ancestor.getFields());
         recipient.setFields(fields);

         ArrayList<MethodInfo> methods = new ArrayList<MethodInfo>();
         methods.addAll(recipient.getMethods());

         methods.addAll(ancestor.getMethods());
         recipient.setMethods(methods);
      }
   }
}