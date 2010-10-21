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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.gwtframework.commons.webdav.PropfindResponse;
import org.exoplatform.gwtframework.commons.webdav.PropfindResponse.Property;
import org.exoplatform.gwtframework.commons.webdav.PropfindResponse.Resource;
import org.exoplatform.gwtframework.commons.xml.QName;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.ItemProperty;
import org.exoplatform.ide.client.framework.vfs.ACL.AccessControlEntry;
import org.exoplatform.ide.client.framework.vfs.ACL.Permissions;

import com.google.gwt.http.client.Response;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Oct 18, 2010 $
 *
 */
public class ItemGetACLUnmarshaller implements Unmarshallable
{

   private static QName HREF = new QName("href", "DAV:");

   private static QName PROPERTY = new QName("property", "DAV:");

   private static QName ALL = new QName("all", "DAV:");

   private static QName OWNER = new QName("owner", "DAV:");

   private static QName READ = new QName("read", "DAV:");

   private static QName WRITE = new QName("write", "DAV:");

   private static QName PRIVILEGE = new QName("privilege", "DAV:");

   private Item item;

   /**
    * @param item
    */
   public ItemGetACLUnmarshaller(Item item)
   {
      this.item = item;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         parseItemACL(response.getText());
      }
      catch (Exception e)
      {
         String message = "Can't parse ACL item - <b>" + item.getName() + " </b>";
         throw new UnmarshallerException(message);
      }
   }

   /**
    * @param text
    * @throws Exception 
    */
   private void parseItemACL(String body) throws Exception
   {
      PropfindResponse propfindResponse = PropfindResponse.parse(body);
      Resource resource = propfindResponse.getResource();

      Property acl = resource.getProperty(ItemProperty.ACL.ACL);

      if (acl == null)
         throw new Exception();

      item.getAcl().clear();
      for (Property aceProperty : acl.getChildProperties())
      {
         
         String entity = "";
         List<Permissions> permissionList = new ArrayList<Permissions>();
         //         List<Permissions> deny = new ArrayList<Permissions>();

         for (Property p : aceProperty.getChildProperties())
         {
            if (p.getName().equals(ItemProperty.ACL.PRINCIPAL))
            {
               // parse principal
               entity = getEntity(p.getChildProperties());
            }
            else if (p.getName().equals(ItemProperty.ACL.GRANT))
            {
               //parse grant
               permissionList = getPermission(p.getChildProperties());
            }
            //            else if (p.getName().equals(ItemProperty.ACL.DENY))
            //            {
            //               //parse deny
            //               deny = getPermission(p.getChildProperties());
            //            }
         }
         
         
         item.getAcl().addPermission(new AccessControlEntry(entity, permissionList));
      }

//      for (String key : item.getAcl().getPermissionsMap().keySet())
//      {
//         System.out.println("UserID - " + key + ", per ="
//            + item.getAcl().getPermisions(key).getPermissionsList());
//      }
   }

   /**
    * @param childProperties
    * @return
    */
   private List<Permissions> getPermission(Collection<Property> childProperties)
   {
      List<Permissions> permissions = new ArrayList<Permissions>();

      for (Property p : childProperties)
      {
         if (p.getName().equals(PRIVILEGE))
         {
            for (Property per : p.getChildProperties())
            {
               if (per.getName().getLocalName().equals(Permissions.READ.toString()))
               {
                  permissions.add(Permissions.READ);
               }
               else if (per.getName().getLocalName().equals(Permissions.ALL.toString()))
                  permissions.add(Permissions.ALL);
            }
         }
      }

      return permissions;
   }

   /**
    * @param childProperties
    * @return userID
    */
   private String getEntity(Collection<Property> childProperties)
   {
      for (Property p : childProperties)
      {
         if (p.getName().equals(HREF))
            return p.getValue();
         else if (p.getName().equals(PROPERTY))
            return p.getChildProperty(OWNER).getName().getLocalName();
         else if (p.getName().equals(ALL))
            return p.getName().getLocalName();
      }
      return "";
   }

   private static String body = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + "<D:multistatus xmlns:D=\"DAV:\">"
      + "<D:response>" + "<D:href>http://www.example.com/top/container/</D:href>" + "<D:propstat>" + "<D:prop>"
      + "<D:acl>" + "<D:ace>" + "<D:principal><D:href>http://www.example.com/users/esedlar</D:href></D:principal>"
      + "<D:grant>" + "<D:privilege><D:read/></D:privilege>" + "<D:privilege><D:write/></D:privilege>"
      + "<D:privilege><D:read-acl/></D:privilege>" + "</D:grant>" + "</D:ace>" + "<D:ace>"
      + "<D:principal><D:href>http://www.example.com/groups/mrktng</D:href></D:principal>"
      + "<D:deny><D:privilege><D:read/></D:privilege></D:deny>" + "</D:ace>" + "<D:ace>"
      + "<D:principal><D:property><D:owner/></D:property></D:principal>" + "<D:grant>"
      + "<D:privilege><D:read/></D:privilege>" + "<D:privilege><D:write/></D:privilege>" + "</D:grant>"
      + "</D:ace>" + "<D:ace>" + "<D:principal><D:all/></D:principal>" + "<D:grant>"
      + "<D:privilege><D:read/></D:privilege>" + "</D:grant>" + "<D:inherited>"
      + "<D:href>http://www.example.com/top</D:href>" + "</D:inherited>" + "</D:ace>" + "</D:acl>" + "</D:prop>"
      + "<D:status>HTTP/1.1 200 OK</D:status>" + "</D:propstat>" + "</D:response>" + "</D:multistatus>";

}
