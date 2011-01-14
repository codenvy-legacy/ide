/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.module.vfs.webdav.marshal;

import org.exoplatform.gwtframework.commons.rest.Marshallable;
import org.exoplatform.gwtframework.commons.webdav.Property;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.ItemProperty;

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
