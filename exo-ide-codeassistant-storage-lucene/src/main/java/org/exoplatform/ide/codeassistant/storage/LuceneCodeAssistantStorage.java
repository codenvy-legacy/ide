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

import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage;
import org.exoplatform.ide.codeassistant.jvm.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;

import java.util.List;

/**
 * Implementation of CodeAssistantStorage based on Lucene
 */
public class LuceneCodeAssistantStorage implements CodeAssistantStorage
{

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getTypeByFqn(java.lang.String)
    */
   @Override
   public TypeInfo getTypeByFqn(String fqn) throws CodeAssistantException
   {
      //         IndexReader reader = IndexReader.open(dir,true);
      //         TermDocs docs = reader.termDocs(new Term("fld_fqn", "org.exo.."));
      //         
      //         IndexSearcher searcher = new IndexSearcher(dir, true);
      //         searcher.search(new TermQuery(new Term("fld_fqn", "org.exo..")), results);
      return null;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getTypesByNamePrefix(java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> getTypesByNamePrefix(String namePrefix) throws CodeAssistantException
   {
      return null;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getTypesByFqnPrefix(java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> getTypesByFqnPrefix(String fqnPrefix) throws CodeAssistantException
   {
      return null;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getAnnotations(java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> getAnnotations(String prefix) throws CodeAssistantException
   {
      return null;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getIntefaces(java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> getIntefaces(String prefix) throws CodeAssistantException
   {
      return null;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getClasses(java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> getClasses(String prefix) throws CodeAssistantException
   {
      return null;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getClassJavaDoc(java.lang.String)
    */
   @Override
   public String getClassJavaDoc(String fqn) throws CodeAssistantException
   {
      return null;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getMemberJavaDoc(java.lang.String)
    */
   @Override
   public String getMemberJavaDoc(String fqn) throws CodeAssistantException
   {
      return null;
   }

}
