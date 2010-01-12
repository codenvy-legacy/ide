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
package org.exoplatform.ideall.client.model;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public enum ItemProperty {

   DISPLAYNAME("displayname", "Display Name"),
   CREATIONDATE("creationdate", "Creation Date"),
   
   GETCONTENTTYPE("getcontenttype", "Content Type"),
   
   
   AUTOLOAD("autoload", "Autoload"),
   LASTMODIFIED("getlastmodified", "Last Modified"),
   RESOURCETYPE("resourcetype", "Resource Type"),
   GETCONTENTLENGTH("getcontentlength", "Content Length"),
   PRIMARYTYPE("primaryType", "Primary Type"),
   NODETYPE("nodeType", "Node Type");

   private final String localName;

   private final String title;

   private ItemProperty(String localName, String title)
   {
      this.localName = localName;
      this.title = title;
   }

   public static ItemProperty getProperty(String localName)
   {
      for (ItemProperty property : ItemProperty.values())
      {
         if (property.localName.equalsIgnoreCase(localName))
         {
            return property;
         }
      }

      return null;
   }

   public String getTitle()
   {
      return title;
   }

   public String getLocalName()
   {
      return localName;
   }

}
