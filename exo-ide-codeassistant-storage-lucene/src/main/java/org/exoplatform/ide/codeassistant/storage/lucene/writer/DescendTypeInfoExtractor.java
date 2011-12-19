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

import org.exoplatform.ide.codeassistant.storage.lucene.LuceneCodeAssistantStorage;
import org.exoplatform.ide.codeassistant.storage.lucene.search.LuceneTypeInfoSearcher;

/**
 * 
 */
public class DescendTypeInfoExtractor extends LuceneCodeAssistantStorage
{

   /**
    * @param typeInfoSearcher
    */
   public DescendTypeInfoExtractor(LuceneTypeInfoSearcher typeInfoSearcher)
   {
      super(typeInfoSearcher);
      // TODO Auto-generated constructor stub
   }
   //
   //   private static final Log LOG = ExoLogger.getLogger(DescendTypeInfoExtractor.class.getName());
   //
   //   private static final String OBJECT_TYPE_FQN = "java.lang.Object";
   //
   //   /**
   //    * @param typeInfoSearcher
   //    */
   //   public DescendTypeInfoExtractor(LuceneTypeInfoSearcher typeInfoSearcher)
   //   {
   //      super(typeInfoSearcher);
   //   }
   //
   //   /**
   //    * Get TypeInfo object which will also contain all inherited methods and
   //    * fields
   //    */
   //   @Override
   //   public TypeInfo getTypeByFqn(String fqn) throws CodeAssistantException
   //   {
   //      try
   //      {
   //         Document document = searchDocumentByTerm(TypeInfoIndexFields.FQN, fqn);
   //         if (document != null)
   //         {
   //            byte[] jsonField = document.getBinaryValue(TypeInfoIndexFields.TYPE_INFO_JSON);
   //            TypeInfo typeInfo = createTypeInfoObject(jsonField);
   //
   //            // try to extract all inherited methods and fields
   //            String superTypeFqn = typeInfo.getSuperClass();
   //            if (!isEndOfTree(superTypeFqn))
   //            {
   //               Map<String, MethodInfo> typeMethods = new HashMap<String, MethodInfo>();
   //               Map<String, FieldInfo> typeFields = new HashMap<String, FieldInfo>();
   //
   //               for (MethodInfo methodInfo : typeInfo.getMethods())
   //               {
   //                  typeMethods.put(methodInfo.getGeneric(), methodInfo);
   //               }
   //
   //               for (FieldInfo fieldInfo : typeInfo.getFields())
   //               {
   //                  typeFields.put(fieldInfo.getName(), fieldInfo);
   //               }
   //
   //               while (!isEndOfTree(superTypeFqn))
   //               {
   //                  Document superClassDocument = searchDocumentByTerm(TypeInfoIndexFields.FQN, superTypeFqn);
   //
   //                  if (superClassDocument != null)
   //                  {
   //                     byte[] superClassJsonField = document.getBinaryValue(TypeInfoIndexFields.TYPE_INFO_JSON);
   //                     TypeInfo supperClassTypeInfo = createTypeInfoObject(superClassJsonField);
   //
   //                     // add only available for this class methods and fields
   //                     for (MethodInfo methodInfo : supperClassTypeInfo.getMethods())
   //                     {
   //                        if (!typeMethods.containsKey(methodInfo.getGeneric())
   //                           && isAvailableMember(typeInfo, methodInfo, supperClassTypeInfo))
   //                        {
   //                           typeMethods.put(methodInfo.getGeneric(), methodInfo);
   //                        }
   //                     }
   //
   //                     for (FieldInfo fieldInfo : supperClassTypeInfo.getFields())
   //                     {
   //                        if (!typeMethods.containsKey(fieldInfo.getName())
   //                           && isAvailableMember(typeInfo, fieldInfo, supperClassTypeInfo))
   //                        {
   //                           typeFields.put(fieldInfo.getName(), fieldInfo);
   //                        }
   //                     }
   //
   //                     superTypeFqn = supperClassTypeInfo.getSuperClass();
   //                  }
   //                  else
   //                  {
   //                     break;
   //                  }
   //               }
   //
   //               typeInfo.setDeclaredMethods((MethodInfo[])typeMethods.values().toArray());
   //               typeInfo.setDeclaredFields((FieldInfo[])typeFields.values().toArray());
   //            }
   //            return typeInfo;
   //         }
   //         else
   //         {
   //            return null;
   //         }
   //      }
   //      catch (CorruptIndexException e)
   //      {
   //         LOG.error("Error during searching Type by FQN", e);
   //         throw new CodeAssistantException(404, "");
   //      }
   //      catch (IOException e)
   //      {
   //         LOG.error("Error during searching Type by FQN", e);
   //         throw new CodeAssistantException(404, "");
   //      }
   //      catch (JsonException e)
   //      {
   //         LOG.error("Error during transforming stored in index binary field into TypeInfo object", e);
   //         throw new CodeAssistantException(404, "");
   //      }
   //   }
   //
   //   private boolean isEndOfTree(String fqn)
   //   {
   //      return fqn.equals(OBJECT_TYPE_FQN) || fqn.isEmpty();
   //   }
   //
   //   private boolean isAvailableMember(TypeInfo superTypeInfo, Member member, TypeInfo typeInfo)
   //   {
   //      // is method private?
   //      if ((member.getModifiers() & Modifier.PRIVATE) == 0)
   //      {
   //         return false;
   //      }
   //
   //      // if method protected check availability through packages
   //      if ((member.getModifiers() & Modifier.PROTECTED) == 0)
   //      {
   //         String superTypePackage = getPackageName(superTypeInfo.getName(), superTypeInfo.getQualifiedName());
   //         String typePackage = getPackageName(typeInfo.getName(), typeInfo.getQualifiedName());
   //         if (superTypePackage.equals(typePackage))
   //         {
   //            return true;
   //         }
   //         else
   //         {
   //            return false;
   //         }
   //      }
   //
   //      return true;
   //   }
   //
   //   private String getPackageName(String className, String fqn)
   //   {
   //      String packageName = fqn.substring(fqn.length() - className.length(), fqn.length());
   //      return packageName;
   //   }
}
