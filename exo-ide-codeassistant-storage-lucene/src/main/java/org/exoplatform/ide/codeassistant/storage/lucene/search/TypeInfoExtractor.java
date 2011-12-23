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
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.document.FieldSelectorResult;
import org.apache.lucene.index.IndexReader;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.TypeInfoIndexFields;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * 
 */
public class TypeInfoExtractor implements ContentExtractor<TypeInfo>
{

   /**
    * @see org.exoplatform.ide.codeassistant.storage.lucene.search.ContentExtractor#getValue(int)
    */
   @Override
   public TypeInfo getValue(IndexReader reader, int doc) throws IOException
   {
      Document document = reader.document(doc, new FieldSelector()
      {

         @Override
         public FieldSelectorResult accept(String fieldName)
         {
            if (TypeInfoIndexFields.TYPE_INFO.equals(fieldName))
            {
               return FieldSelectorResult.LOAD;
            }
            else
            {
               return FieldSelectorResult.NO_LOAD;
            }
         }
      });
      try
      {
         byte[] contentField = document.getBinaryValue(TypeInfoIndexFields.TYPE_INFO);
         TypeInfo result = new TypeInfo();
         ObjectInputStream io = new ObjectInputStream(new ByteArrayInputStream(contentField));
         result.readExternal(io);
         io.close();
         return result;
      }
      catch (ClassNotFoundException e)
      {
         throw new IOException(e.getLocalizedMessage(), e);
      }

   }
}