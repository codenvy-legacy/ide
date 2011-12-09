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
package org.exoplatform.ide.codeassistant.storage;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.everrest.core.impl.provider.json.JsonException;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.jvm.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.RoutineInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class DescendTypeInfoExtractor extends LuceneCodeAssistantStorage
{
   private static final Log LOG = ExoLogger.getLogger(DescendTypeInfoExtractor.class.getName());

   private static final String OBJECT_TYPE_FQN = "java.lang.Object";

   public DescendTypeInfoExtractor(String indexDirPath)
   {
      super(indexDirPath);
   }

   /**
    * ${inherited doc}
    */
   @Override
   public TypeInfo getTypeByFqn(String fqn) throws CodeAssistantException
   {
      try
      {
         Document document = searchDocumentByTerm(TypeInfoIndexFields.FQN, fqn);
         if (document != null)
         {
            byte[] jsonField = document.getBinaryValue(TypeInfoIndexFields.TYPE_INFO_JSON);
            TypeInfo typeInfo = createTypeInfoObject(jsonField);

            String superTypeFqn = typeInfo.getSuperClass();
            if (!isEndOfTree(superTypeFqn))
            {
               List<MethodInfo> declaredMethods = new ArrayList<MethodInfo>();
               List<RoutineInfo> declaredConstructors = new ArrayList<RoutineInfo>();
               List<FieldInfo> declaredFields = new ArrayList<FieldInfo>();

               while (!isEndOfTree(superTypeFqn))
               {
                  Document superClassDocument = searchDocumentByTerm(TypeInfoIndexFields.FQN, superTypeFqn);

                  if (superClassDocument != null)
                  {
                     byte[] superClassJsonField = document.getBinaryValue(TypeInfoIndexFields.TYPE_INFO_JSON);
                     TypeInfo supperClassTypeInfo = createTypeInfoObject(superClassJsonField);

                     // TODO: add only available for this class methods
                     declaredConstructors.addAll(Arrays.asList(supperClassTypeInfo.getConstructors()));
                     declaredMethods.addAll(Arrays.asList(supperClassTypeInfo.getMethods()));
                     declaredFields.addAll(Arrays.asList(supperClassTypeInfo.getFields()));

                     superTypeFqn = supperClassTypeInfo.getSuperClass();
                  }
                  else
                  {
                     break;
                  }
               }

               typeInfo.setDeclaredConstructors((RoutineInfo[])declaredConstructors.toArray());
               typeInfo.setDeclaredMethods((MethodInfo[])declaredMethods.toArray());
               typeInfo.setDeclaredFields((FieldInfo[])declaredFields.toArray());
            }
            return typeInfo;
         }
         else
         {
            return null;
         }
      }
      catch (CorruptIndexException e)
      {
         LOG.error("Error during searching Type by FQN", e);
         throw new CodeAssistantException(404, "");
      }
      catch (IOException e)
      {
         LOG.error("Error during searching Type by FQN", e);
         throw new CodeAssistantException(404, "");
      }
      catch (JsonException e)
      {
         LOG.error("Error during transforming stored in index binary field into TypeInfo object", e);
         throw new CodeAssistantException(404, "");
      }
   }

   private boolean isEndOfTree(String fqn)
   {
      return fqn.equals(OBJECT_TYPE_FQN) || fqn.isEmpty();
   }
}
