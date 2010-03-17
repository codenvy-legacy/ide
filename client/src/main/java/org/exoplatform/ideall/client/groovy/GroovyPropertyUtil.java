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
package org.exoplatform.ideall.client.groovy;

import java.util.Collection;

import org.exoplatform.gwtframework.commons.webdav.PropfindResponse.Property;
import org.exoplatform.gwtframework.commons.xml.QName;
import org.exoplatform.ideall.client.model.property.ItemProperty;
import org.exoplatform.ideall.client.model.vfs.api.File;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class GroovyPropertyUtil
{

   public static Property getProperty(Collection<Property> properties, QName name)
   {
      for (Property property : properties)
      {
         if (property.getName().equals(name))
         {
            return property;
         }
      }

      return null;
   }

   public static String getAutoloadPropertyValue(File file)
   {
      for (Property property : file.getProperties())
      {
         QName propertyName = property.getName();
         if (propertyName.equals(ItemProperty.JCR_CONTENT))
         {
            Collection<Property> children = property.getChildProperties();
            for (Property child : children)
            {
               if (child.getName().equals(ItemProperty.EXO_AUTOLOAD))
               {
                  return child.getValue();
               }

            }

         }
      }

      return null;
   }

}
