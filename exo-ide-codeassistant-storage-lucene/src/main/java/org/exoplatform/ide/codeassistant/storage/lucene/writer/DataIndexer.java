/*
 * Copyright (C) 2012 eXo Platform SAS.
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

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.ExternalizationTools;
import org.exoplatform.ide.codeassistant.storage.lucene.DataIndexFields;
import org.exoplatform.ide.codeassistant.storage.lucene.IndexType;

import java.io.IOException;

/**
 * Create Lucene Document for JavaDoc or TypeInfo.
 * 
 */
public class DataIndexer
{
   /**
    * Create simple name from "Fully qualified name"
    * 
    * @param fqn
    *           - Fully qualified name of the class
    * @return - name of the class without package.
    */
   public static String simpleName(String fqn)
   {
      return fqn.substring(fqn.lastIndexOf(".") + 1);
   }

   /**
    * Creates lucene document for member's javaDoc.
    * 
    * @param fqn
    *           members fqn (class, field, constructor, method, etc)
    * @param doc
    *           member's javaDoc
    * @return created document
    * @throws IOException
    */
   public Document createJavaDocDocument(String fqn, String javaDoc) throws IOException
   {
      Document javaDocDocument = new Document();

      javaDocDocument.add(new Field(IndexType.DOC.getIndexFieldName(), IndexType.DOC.getIndexFieldValue(), Store.YES,
         Index.NOT_ANALYZED));
      javaDocDocument.add(new Field(DataIndexFields.FQN, fqn, Store.YES, Index.NOT_ANALYZED));
      javaDocDocument.add(new Field(DataIndexFields.JAVA_DOC, javaDoc, Store.YES, Index.NOT_ANALYZED_NO_NORMS));
      return javaDocDocument;
   }

   /**
    * Create lucene document from typeInfo;
    * 
    * @param typeInfo
    * @return
    * @throws IOException
    */
   public Document createTypeInfoDocument(TypeInfo typeInfo) throws IOException
   {
      Document typeInfoDocument = new Document();

      typeInfoDocument.add(new Field(IndexType.JAVA.getIndexFieldName(), IndexType.JAVA.getIndexFieldValue(),
         Store.YES, Index.NOT_ANALYZED));

      String fqn = typeInfo.getName();

      typeInfoDocument.add(new Field(DataIndexFields.CLASS_NAME, simpleName(fqn), Store.YES, Index.NOT_ANALYZED));

      typeInfoDocument.add(new Field(DataIndexFields.MODIFIERS, Integer.toString(typeInfo.getModifiers()), Store.YES,
         Index.NOT_ANALYZED));

      typeInfoDocument.add(new Field(DataIndexFields.FQN, fqn, Store.YES, Index.NOT_ANALYZED));
      typeInfoDocument.add(new Field(DataIndexFields.ENTITY_TYPE, typeInfo.getType(), Store.YES, Index.NOT_ANALYZED));
      typeInfoDocument.add(new Field(DataIndexFields.SUPERCLASS, typeInfo.getSuperClass(), Store.YES,
         Index.NOT_ANALYZED));

      for (String string : typeInfo.getInterfaces())
      {
         typeInfoDocument.add(new Field(DataIndexFields.INTERFACES, string, Store.YES, Index.NOT_ANALYZED));
      }

      typeInfoDocument.add(new Field(DataIndexFields.TYPE_INFO, ExternalizationTools.externalize(typeInfo), Store.YES));
      return typeInfoDocument;

   }

}
