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
package org.exoplatform.ideall.client.model.data.marshal;

import java.util.ArrayList;

import org.exoplatform.gwt.commons.rest.Unmarshallable;
import org.exoplatform.gwt.commons.webdav.PropfindResponse;
import org.exoplatform.gwt.commons.webdav.PropfindResponse.Property;
import org.exoplatform.gwt.commons.webdav.PropfindResponse.Resource;
import org.exoplatform.gwt.commons.xml.QName;
import org.exoplatform.ideall.client.Utils;
import org.exoplatform.ideall.client.model.File;
import org.exoplatform.ideall.client.model.Folder;
import org.exoplatform.ideall.client.model.Item;
import org.exoplatform.ideall.client.model.configuration.Configuration;
import org.exoplatform.ideall.client.model.data.DataServiceImpl;
import org.exoplatform.ideall.client.model.property.ItemProperty;
import org.exoplatform.ideall.client.model.util.ImageUtil;
import org.exoplatform.ideall.client.model.util.NodeTypeUtil;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class SearchResultUnmarshaller implements Unmarshallable
{

   private Folder folder;

   public SearchResultUnmarshaller(Folder folder)
   {
      this.folder = folder;
   }

   public void unmarshal(String body)
   {
      String context = Configuration.getInstance().getContext();

      if (context.endsWith("/"))
      {
         context = context.substring(0, context.length() - 1);
      }
      body = body.replace(" b:dt=\"dateTime.rfc1123\"", ""); // TODO to fix bug with the Internet Explorer XML Parser, when parsing node with property b:dt="dateTime.rfc1123" (http://markmail.org/message/ai2wypfkbhazhrdp)

      PropfindResponse response = PropfindResponse.parse(body);

      folder.setChildren(new ArrayList<Item>());

      Resource resource = response.getResource();
      if (resource == null)
      {
         return;
      }

      addResource(folder, resource, context);

      for (Resource child : resource.getChildren())
      {
         addResource(folder, child, context);
      }
   }

   private void addResource(Folder folder, Resource resource, String context)
   {
      String path = resource.getHref();
      path = Utils.unescape(path);
      String prefix = context + DataServiceImpl.CONTEXT;
      if (path.indexOf(prefix) >= 0)
      {
         path = path.substring(path.indexOf(prefix) + prefix.length());
      }
      else
      {
         return;
      }
      
      if (path.endsWith("/"))
      {
         path = path.substring(0, path.length() - 1);
      }

      Item item;
      if (resource.isCollection())
      {
         item = new Folder(path);
      }
      else
      {
         item = new File(path);
      }

      item.getProperties().clear();
      item.getProperties().addAll(resource.getProperties());

      if (item instanceof File)
      {
         String contentType = getProperty(item, ItemProperty.GETCONTENTTYPE).getValue();
         ((File)item).setContentType(contentType);
         String jcrNodeType = NodeTypeUtil.getContentNodeType(contentType);
         ((File)item).setJcrContentNodeType(jcrNodeType);
         String icon = ImageUtil.getIcon(contentType);
         item.setIcon(icon);
      }

      folder.getChildren().add(item);
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
