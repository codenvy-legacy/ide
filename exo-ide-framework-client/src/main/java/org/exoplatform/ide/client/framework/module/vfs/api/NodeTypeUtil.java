/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.framework.module.vfs.api;

import java.util.HashMap;

import org.exoplatform.gwtframework.commons.rest.MimeType;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 * 
 * This class is container.
 * It contains the types of jcr:content nodes according to them mime-types.
 * 
 */

public class NodeTypeUtil
{

   private static final String DEFAULT = "nt:resource";

   private static HashMap<String, String> nodeTypes = new HashMap<String, String>();

   static
   {
      nodeTypes.put(MimeType.GROOVY_SERVICE, "exo:groovyResourceContainer");
//      nodeTypes.put(MimeType.APPLICATION_GROOVY, "exo:groovyResourceContainer");
      nodeTypes.put(MimeType.GOOGLE_GADGET, "exo:googleGadget");
   }

   public static String getContentNodeType(String mimeType)
   {
      String nodeType = nodeTypes.get(mimeType);
      if (nodeType == null)
      {
         return DEFAULT;
      }

      return nodeType;
   }

}
