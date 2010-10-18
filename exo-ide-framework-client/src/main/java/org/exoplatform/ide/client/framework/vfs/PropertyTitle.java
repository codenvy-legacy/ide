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
package org.exoplatform.ide.client.framework.vfs;

import java.util.HashMap;

import org.exoplatform.gwtframework.commons.xml.QName;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class PropertyTitle
{

   private static HashMap<QName, String> titles = new HashMap<QName, String>();

   static
   {
      // WebDAV
      titles.put(ItemProperty.DISPLAYNAME, "Display Name");
      titles.put(ItemProperty.CREATIONDATE, "Creation Date");
      titles.put(ItemProperty.GETCONTENTTYPE, "Content Type");
      titles.put(ItemProperty.GETLASTMODIFIED, "Last Modified");
      titles.put(ItemProperty.GETCONTENTLENGTH, "Content Length");

      // JCR
      titles.put(ItemProperty.JCR_NODETYPE, "Content Node Type");
      titles.put(ItemProperty.JCR_PRIMARYTYPE, "File Node Type");
      titles.put(ItemProperty.JCR_ISCHECKEDOUT, "Is Checked Out");

      // EXO
      titles.put(ItemProperty.EXO_AUTOLOAD, "Autoload");
   }

   public static String getPropertyTitle(QName property)
   {
      return titles.get(property);
   }

   public static boolean containsTitleFor(QName property)
   {
      return titles.containsKey(property);
   }

}
