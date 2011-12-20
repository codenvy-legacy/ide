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
package org.exoplatform.ide.codeassistant.storage.lucene;

import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage;
import org.exoplatform.ide.codeassistant.jvm.JavaType;
import org.exoplatform.ide.codeassistant.jvm.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.search.LuceneTypeInfoSearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Implementation of CodeAssistantStorage based on Lucene
 */
public class LuceneCodeAssistantStorage implements CodeAssistantStorage
{

   private static final Logger LOG = LoggerFactory.getLogger(LuceneCodeAssistantStorage.class);

   private final LuceneTypeInfoSearcher typeInfoSearcher;

   public LuceneCodeAssistantStorage(LuceneTypeInfoSearcher typeInfoSearcher)
   {
      this.typeInfoSearcher = typeInfoSearcher;
   }

   /**
    * 
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getAnnotations(java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> getAnnotations(String prefix) throws CodeAssistantException
   {
      return typeInfoSearcher.searchJavaTypesByPrefix(JavaType.ANNOTATION, prefix);
   }

   /**
    * 
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getClasses(java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> getClasses(String prefix) throws CodeAssistantException
   {
      return typeInfoSearcher.searchJavaTypesByPrefix(JavaType.CLASS, prefix);
   }

   /**
    * 
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getClassJavaDoc(java.lang.String)
    */
   @Override
   public String getClassJavaDoc(String fqn) throws CodeAssistantException
   {
      return null;
   }

   /**
    * 
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getIntefaces(java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> getIntefaces(String prefix) throws CodeAssistantException
   {
      return typeInfoSearcher.searchJavaTypesByPrefix(JavaType.INTERFACE, prefix);
   }

   /**
    * 
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getMemberJavaDoc(java.lang.String)
    */
   @Override
   public String getMemberJavaDoc(String fqn) throws CodeAssistantException
   {
      LOG.error("Method getMemberJavaDoc not implemented");
      throw new RuntimeException("Not implemented");
   }

   /**
    * 
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getTypeByFqn(java.lang.String)
    */
   @Override
   public TypeInfo getTypeByFqn(String fqn) throws CodeAssistantException
   {
      return typeInfoSearcher.searchDocumentByTerm(TypeInfoIndexFields.FQN, fqn);
   }

   /**
    * 
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getTypesByFqnPrefix(java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> getTypesByFqnPrefix(String fqnPrefix) throws CodeAssistantException
   {
      return typeInfoSearcher.searchFieldByPrefix(TypeInfoIndexFields.FQN, fqnPrefix);
   }

   /**
    * 
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getTypesByFqnPrefix(java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> getTypesByNamePrefix(String namePrefix) throws CodeAssistantException
   {
      return typeInfoSearcher.searchFieldByPrefix(TypeInfoIndexFields.CLASS_NAME, namePrefix);
   }

}
