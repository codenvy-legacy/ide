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
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.ACL.AccessControlEntry;
import org.exoplatform.ide.client.framework.vfs.ACL.Permissions;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Oct 18, 2010 $
 *
 */
public class ItemSetACLMarshaller implements Marshallable
{

   private Item item;

   /**
    * @param item
    */
   public ItemSetACLMarshaller(Item item)
   {
      this.item = item;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Marshallable#marshal()
    */
   public String marshal()
   {
      //remove ace with empty permissions 
      item.getAcl().removeEmptyPermissions();

      String xml = "<?xml version='1.0' encoding='UTF-8' ?>";
      xml += "<D:acl xmlns:D=\"DAV:\">";

      for (AccessControlEntry e : item.getAcl().getPermissionsList())
      {
         xml += "<D:ace>";
         xml += "<D:principal>";
         
         if (e.getIdentity().equals("owner"))
            xml += "<D:property><D:owner /></D:property>";
         else if (e.getIdentity().equals("all"))
            xml += "<D:all />";
         else
            xml += "<D:href>" + e.getIdentity() + "</D:href>";

         xml += "</D:principal>";
         xml += "<D:grant>";
         for (Permissions p : e.getPermissionsList())
         {
            xml += "<D:privilege>" + "<D:" + p.toString() + " />" + "</D:privilege>";
         }

         xml += "</D:grant>";
         xml += "</D:ace>";
      }

      xml += "</D:acl>";
      return xml;
   }

}
