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

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.exoplatform.ide.codeassistant.storage.lucene.IndexType;
import org.exoplatform.ide.codeassistant.storage.lucene.JavaDocIndexFields;

import java.io.IOException;

/**
 * Creates lucene document for member's javaDoc.
 */
public class JavaDocIndexer
{

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
   public Document createDocument(String fqn, String javaDoc) throws IOException
   {
      Document javaDocDocument = new Document();

      javaDocDocument.add(new Field(IndexType.DOC.getIndexFieldName(), IndexType.DOC.getIndexFieldValue(), Store.YES,
         Index.NOT_ANALYZED));
      javaDocDocument.add(new Field(JavaDocIndexFields.FQN, fqn, Store.YES, Index.NOT_ANALYZED));
      javaDocDocument.add(new Field(JavaDocIndexFields.DOC, javaDoc, Store.YES, Index.NOT_ANALYZED));
      return javaDocDocument;
   }

}
