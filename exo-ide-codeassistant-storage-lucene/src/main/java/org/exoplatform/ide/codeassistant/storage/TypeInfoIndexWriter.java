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

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonGenerator;
import org.everrest.core.impl.provider.json.JsonValue;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Instrument for storing TypeInfo in Lucene Index
 */
public class TypeInfoIndexWriter
{
   private final IndexWriter writer;

   public TypeInfoIndexWriter(String indexDir) throws SaveTypeInfoIndexException
   {
      try
      {
         Directory dir = getDirectory(indexDir);
         writer = new IndexWriter(dir, new SimpleAnalyzer(), true, IndexWriter.MaxFieldLength.UNLIMITED);
      }
      catch (IOException e)
      {
         throw new SaveTypeInfoIndexException(e);
      }
   }

   public void close() throws SaveTypeInfoIndexException
   {
      try
      {
         writer.close();
      }
      catch (CorruptIndexException e)
      {
         throw new SaveTypeInfoIndexException(e.getLocalizedMessage(), e);
      }
      catch (IOException e)
      {
         throw new SaveTypeInfoIndexException(e.getLocalizedMessage(), e);
      }
   }

   public void writeTypeInfo(List<TypeInfo> typeInfos) throws SaveTypeInfoIndexException
   {
      for (TypeInfo typeInfo : typeInfos)
      {
         writeTypeInfo(typeInfo);
      }
   }

   public void writeTypeInfo(TypeInfo typeInfo) throws SaveTypeInfoIndexException
   {
      try
      {
         Document typeInfoDocument = createDocument(typeInfo);
         writer.addDocument(typeInfoDocument);
      }
      catch (CorruptIndexException e)
      {
         throw new SaveTypeInfoIndexException(e.getLocalizedMessage(), e);
      }
      catch (IOException e)
      {
         throw new SaveTypeInfoIndexException(e.getLocalizedMessage(), e);
      }
   }

   public void updateTypeInfo(TypeInfo typeInfo) throws SaveTypeInfoIndexException
   {
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

   private Directory getDirectory(String indexDir) throws IOException
   {
      File dir = new File(indexDir);

      if (!dir.exists())
      {
         if (!dir.mkdirs())
         {
            throw new IOException("Cannot create directory: " + dir);
         }
      }

      return FSDirectory.open(new File(indexDir));
   }
}
