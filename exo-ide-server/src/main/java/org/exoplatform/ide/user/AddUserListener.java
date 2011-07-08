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

import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.core.SecurityContext;

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
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.rest.tools.DummySecurityContext;


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
   
   public static final String WORKSPACE_NAME = "dev-monit";

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
    * Adds user as "member" to specified group.
    * 
    * @param userid
    * @param membership
    * @param groupid
    * @throws Exception
    */
   public void addMember(String userid, String groupid) throws Exception
   {
      String membership = "member";
      try
      {
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
      catch (Exception e)
      {
         log.error(e.getMessage(), e);
         throw e;
      }
   }
   
   /**
    * Creates a user's directory in workspace 'dev-monit'.
    * 
    * @param userId user's ID
    * @throws RepositoryException 
    */
   private void createUserFolder(String userId) throws RepositoryException {
      ManageableRepository repository = repositoryService.getCurrentRepository();
      Session session = repository.getSystemSession(WORKSPACE_NAME);
      Node rootNode = session.getRootNode();

      Node userNode = rootNode.addNode(userId, "nt:folder");
      userNode.addMixin("exo:owneable");
      userNode.addMixin("exo:privilegeable");
      session.save();
      
      ((NodeImpl)userNode).setPermission (userId, PermissionType.ALL);
      ((NodeImpl)userNode).removePermission("any");
      session.save();
   }

   @Override
   public void postSave(User user, boolean isNew) throws Exception
   {
      super.postSave(user, isNew);

      EnvironmentContext env = new EnvironmentContext();
      env.put(SecurityContext.class, new DummySecurityContext(new Principal()
      {
         public String getName()
         {
            return "admin";
         }
      }, new HashSet<String>(Arrays.asList("administrators"))));

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

      try
      {
         addMember(user.getUserName(), "/ide/developers");
         addMember(user.getUserName(), "/ide/users");
      }
      catch (Exception e)
      {
         System.out.println("Unhandled exception > " + e.getMessage());
         e.printStackTrace();
      }
      
      try
      {
         createUserFolder(user.getUserName());
      }
      catch (Exception e)
      {
         System.out.println("Unhandled exception > " + e.getMessage());
         e.printStackTrace();
      }

   }

}
