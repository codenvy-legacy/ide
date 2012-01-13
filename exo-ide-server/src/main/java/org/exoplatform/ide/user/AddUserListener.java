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

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.organization.OrganizationServiceException;
import org.exoplatform.services.jcr.impl.core.NodeImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.Group;
import org.exoplatform.services.organization.MembershipType;
import org.exoplatform.services.organization.MembershipTypeHandler;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserEventListener;

/**
 * This class handles creation of the user and automatically adds it to the "/ide/users" and "/ide/developers" groups.
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
public class AddUserListener extends UserEventListener
{

   /**
    * ExoLogger instance.
    */
   private static final Log log = ExoLogger.getExoLogger(AddUserListener.class);

   /**
    * Organization Service instance.
    */
   private OrganizationService organizationService;

   /**
    * JCR Repository Service instance.
    */
   private RepositoryService repositoryService;

   /**
    * Creates a new instance of AddUserListener.
    * 
    * @param organizationService Organization Service
    */
   public AddUserListener(OrganizationService organizationService, RepositoryService repositoryService)
   {
      this.organizationService = organizationService;
      this.repositoryService = repositoryService;
   }

   /**
    * Adds user to "/ide/developers" group.
    * 
    * @param userid
    * @param membership
    * @param groupid
    * @throws Exception
    */
   public void addMember(String userid, String groupid) throws Exception
   {
      String membership = "member";

      User user = organizationService.getUserHandler().findUserByName(userid);
      if (user == null)
         throw new OrganizationServiceException("User '" + userid + "' does not exist. ");
      if (!groupid.startsWith("/"))
         groupid = "/" + groupid;
      Group group = organizationService.getGroupHandler().findGroupById(groupid);
      if (group == null)
         throw new OrganizationServiceException("Group '" + groupid + "' does not exist. ");
      org.exoplatform.services.organization.MembershipType membershipType =
         organizationService.getMembershipTypeHandler().findMembershipType(membership);
      if (membershipType == null)
         throw new OrganizationServiceException("MembershipType '" + membership + "' is not defined. ");
      organizationService.getMembershipHandler().linkMembership(user, group, membershipType, true);
   }

   /**
    * Creates user's directory in workspace "dev-monit".
    * 
    * @param userId user's ID
    * @throws RepositoryException
    */
   private void ensureUserFolderCreated(String userId) throws RepositoryException
   {
      ManageableRepository repository = repositoryService.getCurrentRepository();

      Session session = repository.getSystemSession(Constants.WORKSPACE_NAME);
      Node rootNode = session.getRootNode();

      if (rootNode.hasNode(userId))
      {
         return;
      }

      Node userNode = rootNode.addNode(userId, "nt:folder");

      userNode.addMixin("exo:owneable");
      userNode.addMixin("exo:privilegeable");

      ((NodeImpl)userNode).setPermission(userId, PermissionType.ALL);
      ((NodeImpl)userNode).setPermission("*:" + Constants.IDE_ADMINISTRATORS_GROUP, PermissionType.ALL);
      ((NodeImpl)userNode).removePermission("any");

      session.save();
      session.logout();
   }

   /**
    * Ensure memberships creates.
    * 
    * @throws Exception
    */
   private void ensureMembershipsCreated() throws Exception
   {
      MembershipTypeHandler membershipTypeHandler = organizationService.getMembershipTypeHandler();
      if (membershipTypeHandler.findMembershipType("member") == null)
      {
         MembershipType membershipType = membershipTypeHandler.createMembershipTypeInstance();
         membershipType.setName("member");
         membershipTypeHandler.createMembershipType(membershipType, true);
      }

      if (membershipTypeHandler.findMembershipType("administrator") == null)
      {
         MembershipType membershipType = membershipTypeHandler.createMembershipTypeInstance();
         membershipType.setName("administrator");
         membershipTypeHandler.createMembershipType(membershipType, true);
      }
   }

   /**
    * @see org.exoplatform.services.organization.UserEventListener#postSave(org.exoplatform.services.organization.User, boolean)
    */
   @Override
   public void postSave(User user, boolean isNew) throws Exception
   {
      super.postSave(user, isNew);

      if (!isNew)
      {
         return;
      }

      ensureMembershipsCreated();

      try
      {
         addMember(user.getUserName(), Constants.IDE_DEVELOPERS_GROUP);
         addMember(user.getUserName(), Constants.IDE_USERS_GROUP);
      }
      catch (Exception e)
      {
         log.error("User " + user.getUserName()
            + " can not be added as a member to /ide/developers and /ide/users groups.", e);
      }

      try
      {
         ensureUserFolderCreated(user.getUserName());
      }
      catch (Exception e)
      {
         log.error("Folder for '" + user.getUserName() + "' can not be created.", e);
      }

   }

}
