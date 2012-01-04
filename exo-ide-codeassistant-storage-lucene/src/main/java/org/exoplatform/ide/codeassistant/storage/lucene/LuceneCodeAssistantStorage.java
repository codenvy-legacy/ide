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

import static org.exoplatform.ide.codeassistant.storage.lucene.search.AndLuceneSearchConstraint.and;
import static org.exoplatform.ide.codeassistant.storage.lucene.search.FieldPrefixSearchConstraint.prefix;
import static org.exoplatform.ide.codeassistant.storage.lucene.search.SearchByFieldConstraint.eq;
import static org.exoplatform.ide.codeassistant.storage.lucene.search.SearchByFieldConstraint.eqJavaType;

import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage;
import org.exoplatform.ide.codeassistant.jvm.shared.JavaType;
import org.exoplatform.ide.codeassistant.jvm.shared.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.search.JavaDocExtractor;
import org.exoplatform.ide.codeassistant.storage.lucene.search.LuceneQueryExecutor;
import org.exoplatform.ide.codeassistant.storage.lucene.search.ShortTypeInfoExtractor;
import org.exoplatform.ide.codeassistant.storage.lucene.search.TypeInfoExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Implementation of CodeAssistantStorage based on Lucene
 */
public class LuceneCodeAssistantStorage implements CodeAssistantStorage
{
   private final int DEFAULT_RESULT_LIMIT = 100;

   private static final Logger LOG = LoggerFactory.getLogger(LuceneCodeAssistantStorage.class);

   private final LuceneQueryExecutor queryExecutor;

   public LuceneCodeAssistantStorage(LuceneInfoStorage infoStorage)
   {

      this.queryExecutor = new LuceneQueryExecutor(infoStorage);
   }

   /**
    * 
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getAnnotations(java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> getAnnotations(String prefix) throws CodeAssistantException
   {

      return queryExecutor.executeQuery(new ShortTypeInfoExtractor(), IndexType.JAVA,
         and(eqJavaType(JavaType.ANNOTATION), prefix(TypeInfoIndexFields.CLASS_NAME, prefix)), DEFAULT_RESULT_LIMIT, 0);

   }

   /**
    * 
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getClasses(java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> getClasses(String prefix) throws CodeAssistantException
   {
      return queryExecutor.executeQuery(new ShortTypeInfoExtractor(), IndexType.JAVA,
         and(eqJavaType(JavaType.CLASS), prefix(TypeInfoIndexFields.CLASS_NAME, prefix)), DEFAULT_RESULT_LIMIT, 0);
   }

   /**
    * 
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getClassJavaDoc(java.lang.String)
    */
   @Override
   public String getClassJavaDoc(String fqn) throws CodeAssistantException
   {
      return getMemberJavaDoc(fqn);
   }

   /**
    * 
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getIntefaces(java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> getIntefaces(String prefix) throws CodeAssistantException
   {
      return queryExecutor.executeQuery(new ShortTypeInfoExtractor(), IndexType.JAVA,
         and(eqJavaType(JavaType.INTERFACE), prefix(TypeInfoIndexFields.CLASS_NAME, prefix)), DEFAULT_RESULT_LIMIT, 0);

   }

   /**
    * 
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getMemberJavaDoc(java.lang.String)
    */
   @Override
   public String getMemberJavaDoc(String fqn) throws CodeAssistantException
   {
      List<String> searchResult =
         queryExecutor.executeQuery(new JavaDocExtractor(), IndexType.DOC, eq(JavaDocIndexFields.FQN, fqn), 1, 0);
      if (searchResult.isEmpty())
      {
         throw new CodeAssistantException(404, "Not found");
      }
      else
      {
         return searchResult.get(0);
      }
   }

   /**
    * 
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getTypeByFqn(java.lang.String)
    */
   @Override
   public TypeInfo getTypeByFqn(String fqn) throws CodeAssistantException
   {

      List<TypeInfo> searchResult =
         queryExecutor
            .executeQuery(new TypeInfoExtractor(this), IndexType.JAVA, eq(TypeInfoIndexFields.FQN, fqn), 1, 0);
      return searchResult.size() == 1 ? searchResult.get(0) : null;

   }

   /**
    * 
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getTypesByFqnPrefix(java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> getTypesByFqnPrefix(String fqnPrefix) throws CodeAssistantException
   {
      return queryExecutor.executeQuery(new ShortTypeInfoExtractor(), IndexType.JAVA,
         prefix(TypeInfoIndexFields.FQN, fqnPrefix), DEFAULT_RESULT_LIMIT, 0);
   }

   /**
    * 
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getTypesByFqnPrefix(java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> getTypesByNamePrefix(String namePrefix) throws CodeAssistantException
   {

      return queryExecutor.executeQuery(new ShortTypeInfoExtractor(), IndexType.JAVA,
         prefix(TypeInfoIndexFields.CLASS_NAME, namePrefix), DEFAULT_RESULT_LIMIT, 0);
   }

}
