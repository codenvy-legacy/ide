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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.gwtframework.commons.webdav.Property;
import org.exoplatform.gwtframework.commons.webdav.PropfindResponse;
import org.exoplatform.gwtframework.commons.webdav.PropfindResponse.Resource;
import org.exoplatform.gwtframework.commons.xml.QName;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.ItemProperty;
import org.exoplatform.ide.client.framework.vfs.NodeTypeUtil;

import com.google.gwt.http.client.Response;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class FolderContentUnmarshaller implements Unmarshallable
{

   private Folder folder;
   
   private Map<String, String> images;

   public FolderContentUnmarshaller(Folder folder,Map<String, String> images)
   {
      this.folder = folder;
      this.images = images;
   }

   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         parseFolderContent(response.getText());
      }
      catch (Exception exc)
      {
         exc.printStackTrace();

         String message = "Can't parse folder content at <b>" + folder.getHref() + "</b>!";
         throw new UnmarshallerException(message);
      }

   }

   private void parseFolderContent(String body)
   {
      body = body.replace(" b:dt=\"dateTime.rfc1123\"", ""); // TODO to fix bug with the Internet Explorer XML Parser, when parsing node with property b:dt="dateTime.rfc1123" (http://markmail.org/message/ai2wypfkbhazhrdp)

      List<Resource> resources = PropfindResponse.getResources(body);
      
      //PropfindResponse response = PropfindResponse.parse(body);

      //Resource resource = response.getResource();
      folder.setChildren(new ArrayList<Item>());

      if (resources.size() == 0)
      {
         return;
      }
      
      for (int i = 0; i < resources.size(); i++) {
         if (i == 0) {
            continue;
         }
         
         Resource child = resources.get(i);

         String href = child.getHref();

         Item item;
         if (child.isCollection())
         {
            item = new Folder(href);
         }
         else
         {
            item = new File(href);
         }

         item.getProperties().clear();
         item.getProperties().addAll(child.getProperties());

         if (item instanceof File)
         {
            String contentType = getProperty(item, ItemProperty.GETCONTENTTYPE).getValue();
            ((File)item).setContentType(contentType);
            String jcrNodeType = NodeTypeUtil.getContentNodeType(contentType);
            ((File)item).setJcrContentNodeType(jcrNodeType);
            String icon = getIcon(contentType);
            item.setIcon(icon);
         }
         checkIsSystemItem(item);
         folder.getChildren().add(item);
      }
      
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
   
   /**
    * Checks and sets whether item is system or not.
    * "System" means is used and edited by application, not user.
    *  For example, ".groovyclasspath" file is system.
    *  
    * @param item item to check
    */
   private void checkIsSystemItem(Item item)
   {
      boolean isSystem = false;
      if (item instanceof File) 
      {
         isSystem = MimeType.APPLICATION_GROOVY_CLASSPATH.equals(((File)item).getContentType());
      }
      item.setSystem(isSystem);
   }

}
