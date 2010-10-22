/**
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
 *
 */

package org.exoplatform.ide.vfs.webdav.resource.property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.xml.namespace.QName;

import org.exoplatform.common.util.HierarchicalProperty;
import org.exoplatform.services.jcr.access.AccessControlEntry;
import org.exoplatform.services.jcr.access.AccessControlList;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.impl.core.NodeImpl;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ACLProperty
{

   /**
    * dav:acl property for ACL supporting
    */
   public static QName NAME = new QName("DAV:", "acl");

   public static QName ACE = new QName("DAV:", "ace");

   public static QName PRINCIPAL = new QName("DAV:", "principal");

   public static QName ALL = new QName("DAV:", "all");

   public static QName HREF = new QName("DAV:", "href");

   public static QName PRIVILEGE = new QName("DAV:", "privilege");

   public static QName GRANT = new QName("DAV:", "grant");

   public static QName WRITE = new QName("DAV:", "write");

   public static QName READ = new QName("DAV:", "read");

   //private static HashMap<String, QName>

   /*
    * read
    * add_node
    * read
    */

   public static HierarchicalProperty getACL(NodeImpl node) throws RepositoryException
   {
      HierarchicalProperty property = new HierarchicalProperty(NAME);

      AccessControlList acl = node.getACL();

      HashMap<String, List<String>> principals = new HashMap<String, List<String>>();

      List<AccessControlEntry> entryList = acl.getPermissionEntries();
      for (AccessControlEntry entry : entryList)
      {
         String principal = entry.getIdentity();
         String grant = entry.getPermission();

         List<String> grantList = principals.get(principal);
         if (grantList == null)
         {
            grantList = new ArrayList<String>();
            principals.put(principal, grantList);
         }

         grantList.add(grant);
      }

      Iterator<String> principalIter = principals.keySet().iterator();
      while (principalIter.hasNext())
      {
         HierarchicalProperty aceProperty = new HierarchicalProperty(ACE);

         String curPrincipal = principalIter.next();

         aceProperty.addChild(getPrincipalProperty(curPrincipal));

         aceProperty.addChild(getGrantProperty(principals.get(curPrincipal)));

         property.addChild(aceProperty);
      }

      return property;
   }

   private static HierarchicalProperty getPrincipalProperty(String principal)
   {
      HierarchicalProperty principalProperty = new HierarchicalProperty(PRINCIPAL);

      if ("any".equals(principal))
      {
         HierarchicalProperty all = new HierarchicalProperty(ALL);
         principalProperty.addChild(all);
      }
      else
      {
         HierarchicalProperty href = new HierarchicalProperty(HREF);
         href.setValue(principal);
         principalProperty.addChild(href);
      }

      return principalProperty;
   }

   private static HierarchicalProperty getGrantProperty(List<String> grantList)
   {
      HierarchicalProperty grant = new HierarchicalProperty(GRANT);

      if (grantList.contains(PermissionType.ADD_NODE) || grantList.contains(PermissionType.SET_PROPERTY)
         || grantList.contains(PermissionType.REMOVE))
      {
         HierarchicalProperty privilege = new HierarchicalProperty(PRIVILEGE);
         privilege.addChild(new HierarchicalProperty(WRITE));
         grant.addChild(privilege);
      }

      if (grantList.contains(PermissionType.READ))
      {
         HierarchicalProperty privilege = new HierarchicalProperty(PRIVILEGE);
         privilege.addChild(new HierarchicalProperty(READ));
         grant.addChild(privilege);
      }

      return grant;
   }

}
