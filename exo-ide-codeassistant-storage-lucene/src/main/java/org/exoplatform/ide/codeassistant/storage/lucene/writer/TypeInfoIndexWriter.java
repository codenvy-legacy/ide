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

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonGenerator;
import org.everrest.core.impl.provider.json.JsonValue;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.SaveTypeInfoIndexException;
import org.exoplatform.ide.codeassistant.storage.lucene.TypeInfoIndexFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Instrument for storing TypeInfo in Lucene Index
 */
public class TypeInfoIndexWriter
{

   private static final Logger LOG = LoggerFactory.getLogger(TypeInfoIndexWriter.class);

   private final Directory indexDirectory;

   public TypeInfoIndexWriter(Directory indexDirectory) throws SaveTypeInfoIndexException
   {
      this.indexDirectory = indexDirectory;

   }

   public void addTypeInfo(List<TypeInfo> typeInfos) throws SaveTypeInfoIndexException
   {

      try
      {
         IndexWriter writer =
            new IndexWriter(indexDirectory, new SimpleAnalyzer(), true, IndexWriter.MaxFieldLength.UNLIMITED);
         for (TypeInfo typeInfo : typeInfos)
         {
            Document typeInfoDocument = createDocument(typeInfo);
            writer.addDocument(typeInfoDocument);
         }
         writer.commit();
         writer.close();
      }
      catch (IOException e)
      {
         LOG.error(e.getLocalizedMessage());
         throw new SaveTypeInfoIndexException(e.getLocalizedMessage(), e);
      }
   }

   private Document createDocument(TypeInfo typeInfo) throws SaveTypeInfoIndexException
   {
      try
      {
         Document typeInfoDocument = new Document();
         typeInfoDocument.add(new Field(TypeInfoIndexFields.CLASS_NAME, typeInfo.getName(), Store.YES,
            Index.NOT_ANALYZED));
         typeInfoDocument.add(new Field(TypeInfoIndexFields.MODIFIERS, Integer.toString(typeInfo.getModifiers()),
            Store.YES, Index.NOT_ANALYZED));
         typeInfoDocument.add(new Field(TypeInfoIndexFields.FQN, typeInfo.getQualifiedName(), Store.YES,
            Index.NOT_ANALYZED));
         typeInfoDocument.add(new Field(TypeInfoIndexFields.ENTITY_TYPE, typeInfo.getType(), Store.YES,
            Index.NOT_ANALYZED));
         typeInfoDocument.add(new Field(TypeInfoIndexFields.SUPERCLASS, typeInfo.getSuperClass(), Store.YES,
            Index.NOT_ANALYZED));

         for (String string : typeInfo.getInterfaces())
         {
            typeInfoDocument.add(new Field(TypeInfoIndexFields.INTERFACES, string, Store.YES, Index.NOT_ANALYZED));
         }

         JsonValue jsonValue = JsonGenerator.createJsonObject(typeInfo);
         typeInfoDocument
            .add(new Field(TypeInfoIndexFields.TYPE_INFO_JSON, jsonValue.toString().getBytes(), Store.YES));

         return typeInfoDocument;
      }
      catch (JsonException e)
      {
         throw new SaveTypeInfoIndexException("Can't to get json representation of TypeInfo", e);
      }
   }
}
