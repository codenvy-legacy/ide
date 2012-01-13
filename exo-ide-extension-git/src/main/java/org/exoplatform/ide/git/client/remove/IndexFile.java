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
package org.exoplatform.ide.git.client.remove;

import org.exoplatform.ide.git.shared.GitFile;

/**
 * Git file in index. Used for work with index (remove, reset).
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 13, 2011 11:57:38 AM anya $
 * 
 */
public class IndexFile extends GitFile
{
   /**
    * File is indexed by Git.
    */
   private boolean indexed;

   /**
    * @param file git file
    * @param indexed if <code>true</code> file is in index
    */
   public IndexFile(GitFile file, boolean indexed)
   {
      super(file.getPath(), file.getStatus());
      this.indexed = indexed;
   }

   /**
    * @return the indexed if <code>true</code> file is in index
    */
   public boolean isIndexed()
   {
      return indexed;
   }

   /**
    * @param indexed the indexed if <code>true</code> file is in index
    */
   public void setIndexed(boolean indexed)
   {
      this.indexed = indexed;
   }
}
