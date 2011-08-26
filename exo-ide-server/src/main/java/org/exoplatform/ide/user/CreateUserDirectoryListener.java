/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.user;

import org.apache.commons.chain.Context;
import org.exoplatform.services.command.action.Action;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.impl.core.NodeImpl;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CreateUserDirectoryListener implements Action
{
   
   private OrganizationService organizationService;
   
   public CreateUserDirectoryListener(OrganizationService organizationService) {
      this.organizationService = organizationService;
   }

   @Override
   public boolean execute(Context context) throws Exception
   {
      if (organizationService == null) {
         return false;
      }

      NodeImpl node = (NodeImpl)context.get("currentItem");
      if (!"/".equals(node.getParent().getPath())) {
         return false;
      }
      
      String userId = node.getName();
      User user = organizationService.getUserHandler().findUserByName(userId);
      if (user == null) {
         return false;
      }

      if (!node.isNodeType("exo:owneable")) {
         node.addMixin("exo:owneable");
      }
      
      if (!node.isNodeType("exo:privilegeable")) {
         node.addMixin("exo:privilegeable");
      }
      
      node.setPermission(userId, PermissionType.ALL);
      node.setPermission("*:" + Constants.IDE_ADMINISTRATORS_GROUP, PermissionType.ALL);
      node.removePermission("any");
      
      return false;
   }

}
