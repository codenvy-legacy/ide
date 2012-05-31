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
package org.exoplatform.ide.codeassistant.storage;

import org.exoplatform.ide.codeassistant.storage.api.DataWriter;
import org.exoplatform.ide.codeassistant.storage.api.InfoStorage;
import org.exoplatform.ide.codeassistant.storage.lucene.LuceneInfoStorage;
import org.exoplatform.ide.codeassistant.storage.lucene.writer.LuceneDataWriter;

import java.io.IOException;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class LocalInfoStorage implements InfoStorage
{

   private LuceneInfoStorage infoStorage;

   /**
    * @param infoStorage
    */
   public LocalInfoStorage(LuceneInfoStorage infoStorage)
   {
      super();
      this.infoStorage = infoStorage;
   }

   /**
    * @throws IOException 
    * @see org.exoplatform.ide.codeassistant.storage.api.InfoStorage#getWriter()
    */
   @Override
   public DataWriter getWriter() throws IOException
   {
      return new LocalDataWriter(new LuceneDataWriter(infoStorage));
   }

}
