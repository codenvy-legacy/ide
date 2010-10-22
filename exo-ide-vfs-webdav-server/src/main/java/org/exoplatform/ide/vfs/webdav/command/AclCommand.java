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

package org.exoplatform.ide.vfs.webdav.command;

import java.security.AccessControlException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.common.util.HierarchicalProperty;
import org.exoplatform.ide.vfs.webdav.resource.property.ACLProperty;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.impl.core.NodeImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class AclCommand
{

   /**
    * logger.
    */
   private static Log log = ExoLogger.getLogger(AclCommand.class);

   public Response acl(Session session, String path, HierarchicalProperty property)
   {
      Node node;
      try
      {
         node = (Node)session.getItem(path);

         boolean isNeedToSaveSession = false;

         if (!node.isNodeType("exo:owneable"))
         {
            node.addMixin("exo:owneable");
            isNeedToSaveSession = true;
         }

         if (!node.isNodeType("exo:privilegeable"))
         {
            node.addMixin("exo:privilegeable");
            isNeedToSaveSession = true;
         }

         if (isNeedToSaveSession)
         {
            node.getSession().save();
         }

         setNodeACL((NodeImpl)node, property);

      }
      catch (PathNotFoundException e)
      {
         return Response.status(HTTPStatus.NOT_FOUND).entity(e.getMessage()).build();
      }
      catch (RepositoryException exc)
      {
         log.error(exc.getMessage(), exc);
         return Response.serverError().entity(exc.getMessage()).build();
      }
      catch (AccessControlException exc)
      {
         return Response.status(HTTPStatus.BAD_REQUEST).entity(exc.getMessage()).build();
      }

      return Response.ok().build();
   }

   protected void setNodeACL(NodeImpl node, HierarchicalProperty acl) throws AccessControlException,
      RepositoryException
   {
      Map<String, String[]> nodePermissions = new HashMap<String, String[]>();

      for (HierarchicalProperty ace : acl.getChildren())
      {
         HierarchicalProperty principalProperty = ace.getChild(ACLProperty.PRINCIPAL);

         String principal;
         if (principalProperty.getChild(new QName("DAV:", "href")) != null)
         {
            principal = principalProperty.getChild(new QName("DAV:", "href")).getValue();
         }
         else if (principalProperty.getChild(new QName("DAV:", "all")) != null)
         {
            principal = "any";
         }
         else
         {
            throw new AccessControlException("Can not set permissions for " + node.getPath());
         }

         Set<String> permissions = new HashSet<String>();

         HierarchicalProperty grantProperty = ace.getChild(ACLProperty.GRANT);

         for (HierarchicalProperty privilegeProperty : grantProperty.getChildren())
         {
            if (!ACLProperty.PRIVILEGE.equals(privilegeProperty.getName()))
            {
               throw new AccessControlException("Can not set permissions for " + node.getPath());
            }

            HierarchicalProperty permissionProperty = privilegeProperty.getChild(0);

            if (ACLProperty.READ.equals(permissionProperty.getName()))
            {
               permissions.add(PermissionType.READ);

            }
            else if (ACLProperty.WRITE.equals(permissionProperty.getName()))
            {
               permissions.add(PermissionType.ADD_NODE);
               permissions.add(PermissionType.SET_PROPERTY);
               permissions.add(PermissionType.REMOVE);

            }
            else if (ACLProperty.ALL.equals(permissionProperty.getName()))
            {
               permissions.add(PermissionType.READ);
               permissions.add(PermissionType.ADD_NODE);
               permissions.add(PermissionType.SET_PROPERTY);
               permissions.add(PermissionType.REMOVE);
            }
         }

         String[] permissionList = permissions.toArray(new String[0]);
         nodePermissions.put(principal, permissionList);
      }

      node.setPermissions(nodePermissions);
      node.getSession().save();
   }

}
