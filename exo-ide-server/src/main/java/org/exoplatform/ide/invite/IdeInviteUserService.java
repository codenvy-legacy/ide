/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.invite;

import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.GroupHandler;
import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserHandler;

/**
 * @see InviteUserService
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: IdeInviteUserService.java Jan 3, 2013 vetal $
 *
 */
public class IdeInviteUserService implements InviteUserService
{

   private final OrganizationService organizationService;

   public IdeInviteUserService(OrganizationService organizationService)
   {
      this.organizationService = organizationService;

   }

   @Override
   public void addUser(Invite invite) throws Exception
   {
      UserHandler userHandler = organizationService.getUserHandler();
      User newUser = userHandler.createUserInstance(invite.getEmail());
      newUser.setPassword(invite.getPassword());
      newUser.setFirstName(" ");
      newUser.setLastName(" ");
      newUser.setEmail(invite.getEmail());
      userHandler.createUser(newUser, true);

      // register user in groups '/platform/developers' and '/platform/users'
      GroupHandler groupHandler = organizationService.getGroupHandler();
      Group developersGroup = groupHandler.findGroupById("/platform/developers");
      MembershipType membership = organizationService.getMembershipTypeHandler().findMembershipType("member");
      organizationService.getMembershipHandler().linkMembership(newUser, developersGroup, membership, true);

      Group usersGroup = groupHandler.findGroupById("/platform/users");
      organizationService.getMembershipHandler().linkMembership(newUser, usersGroup, membership, true);

   }
   
   @Override
   public boolean isUserRegistered(String userId) throws Exception
   {
      UserHandler userHandler = organizationService.getUserHandler();
      return userHandler.findUserByName(userId) != null;
   }

   @Override
   public boolean isUserRegisteredGlobally(String userId) throws Exception
   {
      return isUserRegistered(userId);
   }

}
