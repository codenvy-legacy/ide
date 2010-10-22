/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ide.download;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class NodeTypeUtil
{
   static final String NT_FILE = "nt:file";

   static final String NT_RESOURCE = "nt:resource";

   static final String JCR_CONTENT = "jcr:content";

   static final String JCR_DATA = "jcr:data";

   static final String JCR_MIMETYPE = "jcr:mimeType";

   static final String JCR_LASTMODIFIED = "jcr:lastModified";

   /**
    * If the node is file.
    * 
    * @param node node
    * @return true if node is file false if not
    */
   public static boolean isFile(Node node)
   {
      try
      {
         if (!node.isNodeType(NT_FILE))
            return false;
         if (!node.getNode(JCR_CONTENT).isNodeType(NT_RESOURCE))
            return false;
         return true;
      }
      catch (RepositoryException exc)
      {
         return false;
      }
   }

}
