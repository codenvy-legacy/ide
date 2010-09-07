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
import java.util.Map;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.gwtframework.commons.webdav.PropfindResponse;
import org.exoplatform.gwtframework.commons.webdav.PropfindResponse.Property;
import org.exoplatform.gwtframework.commons.webdav.PropfindResponse.Resource;
import org.exoplatform.gwtframework.commons.xml.QName;
import org.exoplatform.ide.client.module.vfs.api.File;
import org.exoplatform.ide.client.module.vfs.api.Folder;
import org.exoplatform.ide.client.module.vfs.api.Item;
import org.exoplatform.ide.client.module.vfs.property.ItemProperty;
import org.exoplatform.ide.client.module.vfs.webdav.NodeTypeUtil;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.Response;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class SearchResultUnmarshaller implements Unmarshallable
{

   private Folder folder;
   
   private String context;
   
   private Map<String, String> images;

   public SearchResultUnmarshaller(String restContext,HandlerManager eventBus, Folder folder,Map<String, String> images)
   {
      this.folder = folder;
      this.context = restContext;
      this.images = images;
   }

   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         parseSearchResult(response.getText());
      }
      catch (Exception exc)
      {
         String message = "Can't parse search result at <b>" + folder.getHref() + "</b>";
         throw new UnmarshallerException(message);
      }
   }

   private void parseSearchResult(String body)
   {
      
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

   public static native String javaScriptDecodeURI(String url) /*-{
      return decodeURI(url);
   }-*/;

   private void addResource(Folder folder, Resource resource, String context)
   {
      String path = resource.getHref();
      path = javaScriptDecodeURI(path);
      path = unescape(path);
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
         String icon = getIcon(contentType);
         item.setIcon(icon);
      }

      folder.getChildren().add(item);
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
    * @param url
    * @return result of javaScript function <code>unescape(url)</code>
    */
   public static native String unescape(String text) /*-{
     return unescape(text);
   }-*/;

}
