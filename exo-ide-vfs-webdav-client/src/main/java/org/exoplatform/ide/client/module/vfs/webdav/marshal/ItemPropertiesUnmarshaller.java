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

import com.google.gwt.http.client.Response;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.gwtframework.commons.webdav.Property;
import org.exoplatform.gwtframework.commons.webdav.PropfindResponse;
import org.exoplatform.gwtframework.commons.webdav.PropfindResponse.Resource;
import org.exoplatform.gwtframework.commons.xml.QName;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.ItemProperty;
import org.exoplatform.ide.client.framework.vfs.NodeTypeUtil;

import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class ItemPropertiesUnmarshaller implements Unmarshallable
{

   private Item item;

   private Map<String, String> images;

   public ItemPropertiesUnmarshaller(Item item, Map<String, String> images)
   {
      this.item = item;
      this.images = images;
   }

   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         parseItemProperties(response.getText());
      }
      catch (Exception exc)
      {
         String message = "Can't parse properties item - <b>" + item.getName() + " </b>";
         throw new UnmarshallerException(message);
      }
   }

   private void parseItemProperties(String body)
   {
      item.getProperties().clear();

      // TODO to fix bug with the Internet Explorer XML Parser, when parsing node with property b:dt="dateTime.rfc1123" (http://markmail.org/message/ai2wypfkbhazhrdp)
      body = body.replace(" b:dt=\"dateTime.rfc1123\"", "");

      PropfindResponse propfindResponse = PropfindResponse.parse(body);
      Resource resource = propfindResponse.getResource();

      item.setHref(resource.getHref());
      item.setName(resource.getDisplayname());

      item.getProperties().clear();
      item.getProperties().addAll(resource.getProperties());

      if (item instanceof File)
      {
         Property prop = getProperty(item, ItemProperty.GETCONTENTTYPE);
         if (prop != null)
         {
            String contentType = prop.getValue();
            ((File)item).setContentType(contentType);
            String jcrNodeType = NodeTypeUtil.getContentNodeType(contentType);
            ((File)item).setJcrContentNodeType(jcrNodeType);
            String icon = getIcon(contentType);
            item.setIcon(icon);
         }
      }
      checkIsSystemItem(item);
   }
   
   /**
    * Checks and sets whether item is system or not.
    * "System" means is used and edited by application, not user.
    *  Now checks if name starts wih dot ".".
    *  For example, ".groovyclasspath" file is system.
    *  
    * @param item item to check
    */
   private void checkIsSystemItem(Item item)
   {
      boolean isSystem = item.getName() != null && item.getName().startsWith(".");
      item.setSystem(isSystem);
   }

   private String getIcon(String mimeType)
   {
      String icon = images.get(mimeType);
      if (icon == null)
      {
         icon = images.get(null);
      }
      return icon;
   }

   private Property getProperty(Item item, QName name)
   {
      for (Property property : item.getProperties())
      {
         if (property.getName().equals(name))
         {
            return property;
         }
      }

      return null;
   }

}
