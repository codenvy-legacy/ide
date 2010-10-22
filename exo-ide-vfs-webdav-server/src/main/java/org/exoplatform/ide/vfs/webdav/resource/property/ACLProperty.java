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
   
   public static QName GRAND = new QName("DAV:", "grand");
   
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
      System.out.println("get ACL for " + node.getPath());

      HierarchicalProperty property = new HierarchicalProperty(NAME);

      AccessControlList acl = node.getACL();

      HashMap<String, List<String>> principals = new HashMap<String, List<String>>();

      List<AccessControlEntry> entryList = acl.getPermissionEntries();
      for (AccessControlEntry entry : entryList)
      {
         String principal = entry.getIdentity();
         String grand = entry.getPermission();
         System.out.println("principal > " + principal);
         System.out.println("grand > " + grand);

         List<String> grandList = principals.get(principal);
         if (grandList == null)
         {
            grandList = new ArrayList<String>();
            principals.put(principal, grandList);
         }

         grandList.add(grand);
      }

      Iterator<String> principalIter = principals.keySet().iterator();
      while (principalIter.hasNext())
      {
         HierarchicalProperty aceProperty = new HierarchicalProperty(ACE);

         String curPrincipal = principalIter.next();

         aceProperty.addChild(getPrincipalProperty(curPrincipal));
         
         aceProperty.addChild(getGrandProperty(principals.get(curPrincipal)));

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
   
   private static HierarchicalProperty getGrandProperty(List<String> grandList) {
      HierarchicalProperty grand = new HierarchicalProperty(GRAND);
      
      if (grandList.contains("add_node") ||
               grandList.contains("set_property") ||
               grandList.contains("remove")) {
         
         HierarchicalProperty privilege = new HierarchicalProperty(PRIVILEGE);
         privilege.addChild(new HierarchicalProperty(WRITE));
         grand.addChild(privilege);
      }
      
      if (grandList.contains("read")) {
         HierarchicalProperty privilege = new HierarchicalProperty(PRIVILEGE);
         privilege.addChild(new HierarchicalProperty(READ));
         grand.addChild(privilege);         
      }
      
      return grand;
   }

}
