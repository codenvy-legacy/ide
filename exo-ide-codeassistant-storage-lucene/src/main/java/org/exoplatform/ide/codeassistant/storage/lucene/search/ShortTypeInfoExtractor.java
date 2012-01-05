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
import org.apache.lucene.document.MapFieldSelector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.exoplatform.ide.codeassistant.jvm.bean.ShortTypeInfoBean;
import org.exoplatform.ide.codeassistant.jvm.shared.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.DataIndexFields;

import java.io.IOException;

/**
 * Create ShortTypeInfo from lucene document.
 * 
 */
public class ShortTypeInfoExtractor implements ContentExtractor<ShortTypeInfo>
{

   /**
    * @throws IOException
    * @throws CorruptIndexException
    * @see org.exoplatform.ide.codeassistant.storage.lucene.search.ContentExtractor#getValue(org.apache.lucene.index.IndexReader,
    *      int)
    */
   @Override
   public ShortTypeInfo getValue(IndexReader reader, int doc) throws IOException
   {
      Document document =
         reader.document(doc, new MapFieldSelector(new String[]{DataIndexFields.MODIFIERS,
            DataIndexFields.CLASS_NAME, DataIndexFields.FQN, DataIndexFields.ENTITY_TYPE}));

      int modifier = Integer.valueOf(document.get(DataIndexFields.MODIFIERS));

      return new ShortTypeInfoBean(document.get(DataIndexFields.FQN), modifier,
         document.get(DataIndexFields.ENTITY_TYPE));

   }
}
