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

import org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.exoplatform.ide.codeassistant.storage.lucene.SaveTypeInfoIndexException;

import java.util.List;

public class LuceneCachedTypeInfoResolver extends CachedTypeInfoResolver
{

   private final TypeInfoIndexWriter writer;

   public LuceneCachedTypeInfoResolver(CodeAssistantStorage storage, TypeInfoIndexWriter writer)
   {
      super(storage);
      this.writer = writer;
   }

   @Override
   protected boolean saveTypeInfo(List<TypeInfo> typeInfos)
   {
      try
      {
         /*
          * Try to add typeInfo to writer
          */
         if (writer != null)
         {
            writer.addTypeInfo(typeInfos);
            return true;
         }
         else
         {
            return false;
         }
      }
      catch (SaveTypeInfoIndexException e)
      {
         // if typeInfo couldn't add to writer return false
         return false;
      }
   }

}
