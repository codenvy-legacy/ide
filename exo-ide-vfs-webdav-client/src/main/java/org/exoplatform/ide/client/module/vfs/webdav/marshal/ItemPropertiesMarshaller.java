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
package org.exoplatform.ide.client.module.vfs.webdav.marshal;

import org.exoplatform.gwtframework.commons.rest.Marshallable;
import org.exoplatform.gwtframework.commons.webdav.PropfindResponse.Property;
import org.exoplatform.ide.client.module.vfs.api.Item;
import org.exoplatform.ide.client.module.vfs.property.ItemProperty;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class ItemPropertiesMarshaller implements Marshallable
{

   private Item item;

   public ItemPropertiesMarshaller(Item item)
   {
      this.item = item;
   }

   public String marshal()
   {
      String xml =
         "<?xml version='1.0' encoding='UTF-8' ?>\n" + "<D:propertyupdate xmlns:D=\"DAV:\">\n" + "<D:set>\n"
            + "<D:prop>\n";

      for (Property property : item.getProperties())
      {
         String propertyValue = property.getValue();

         if (property.getName().equals(ItemProperty.JCR_CONTENT))
         {

            xml += "<jcr:content xmlns:jcr='http://www.jcp.org/jcr/1.0'>\n";

            for (Property prop : property.getChildProperties())
            {
               if (prop.getName().equals(ItemProperty.EXO_AUTOLOAD))
               {
                  xml +=
                     "<exo:autoload xmlns:exo='http://www.exoplatform.com/jcr/exo/1.0'>" + prop.getValue()
                        + "</exo:autoload>\n";
               }
            }

            xml += "</jcr:content>";
         }
      }

      xml += "</D:prop>\n" + "</D:set>\n" + "</D:propertyupdate>";
      return xml;
   }

}
