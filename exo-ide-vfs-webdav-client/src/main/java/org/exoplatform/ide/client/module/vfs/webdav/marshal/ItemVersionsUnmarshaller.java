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

import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.gwtframework.commons.webdav.PropfindResponse;
import org.exoplatform.gwtframework.commons.webdav.PropfindResponse.Property;
import org.exoplatform.gwtframework.commons.webdav.PropfindResponse.Resource;
import org.exoplatform.gwtframework.commons.xml.QName;
import org.exoplatform.ide.client.module.vfs.api.Item;
import org.exoplatform.ide.client.module.vfs.api.Version;
import org.exoplatform.ide.client.module.vfs.property.ItemProperty;
import org.exoplatform.ide.client.module.vfs.webdav.NodeTypeUtil;

import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 24, 2010 $
 *
 */
public class ItemVersionsUnmarshaller implements Unmarshallable
{

   private List<Version> versions;

   private Item item;

   private Map<String, String> images;

   /**
    * @param versions
    */
   public ItemVersionsUnmarshaller(Item item, List<Version> versions, Map<String, String> images)
   {
      this.versions = versions;
      this.item = item;
      this.images = images;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         parseVarsions(response.getText());
      }

      catch (Exception exc)
      {
         exc.printStackTrace();

         String message = "Can't parse versions <b>" + item.getHref() + "</b>!";
         throw new UnmarshallerException(message);
      }
   }

   private void parseVarsions(String body)
   {
      body = body.replace(" b:dt=\"dateTime.rfc1123\"", ""); // TODO to fix bug with the Internet Explorer XML Parser, when parsing node with property b:dt="dateTime.rfc1123" (http://markmail.org/message/ai2wypfkbhazhrdp)

      List<Resource> resources = PropfindResponse.getResources(body);

      if (resources.size() == 0)
      {
         return;
      }

      for (Resource ver : resources)
      {
         Version version = new Version(URL.decode(ver.getHref()));

         version .getProperties().clear();
         version .getProperties().addAll(ver.getProperties());
         
         String contentType = getProperty(version, ItemProperty.GETCONTENTTYPE).getValue();
         version.setContentType(contentType);
         String jcrNodeType = NodeTypeUtil.getContentNodeType(contentType);
         version.setJcrContentNodeType(jcrNodeType);
         String icon = getIcon(contentType);
         version.setIcon(icon);
         String creationDate = getProperty(version, ItemProperty.CREATIONDATE).getValue();
         version.setCreationDate(creationDate);
         String displayName = getProperty(version, ItemProperty.DISPLAYNAME).getValue();
         version.setDisplayName(displayName);
         int length = Integer.parseInt(getProperty(version, ItemProperty.GETCONTENTLENGTH).getValue());
         version.setContentLength(length);
        
         versions.add(version);
      }

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
   
   private String getIcon(String mimeType)
   {      
      String icon = images.get(mimeType);
      if (icon == null)
      {
         icon = images.get(null);
      }
      return icon;
   }
}
