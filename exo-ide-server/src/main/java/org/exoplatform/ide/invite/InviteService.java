/*
 * Copyright (C) 2012 eXo Platform SAS.
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

import org.codenvy.mail.MailSenderClient;
import org.exoplatform.container.configuration.ConfigurationException;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.registry.RegistryEntry;
import org.exoplatform.services.jcr.ext.registry.RegistryService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.mail.MessagingException;

/**
 * Sends invites to user join to the cloud-ide domain.
 */
public class InviteService
{

   private static final Log LOG = ExoLogger.getLogger(InviteService.class);

   private final static String IDE = RegistryService.EXO_APPLICATIONS + "/IDE";

   private final static String INVITES_ROOT = IDE + "/invites";

   private final RegistryService registry;

   private final MailSenderClient mailSender;

   private final TemplateResolver templateResolver;

   private final SessionProviderService sessionProviderService;

   private final InviteUserService inviteUserService;

   private final InviteMessagePropertiesProvider inviteMessagePropertiesProvider;

   public InviteService(RegistryService registry, SessionProviderService sessionProviderService,
                        MailSenderClient mailSender, InviteUserService inviteUserService,
                        InviteMessagePropertiesProvider messagePropertiesProvider, TemplateResolver templateResolver)
   {
      this.registry = registry;
      this.mailSender = mailSender;
      this.sessionProviderService = sessionProviderService;
      this.inviteUserService = inviteUserService;
      this.inviteMessagePropertiesProvider = messagePropertiesProvider;
      this.templateResolver = templateResolver;
   }

   /**
    * Accept invite.
    *
    * @param inviteId
    * @return - information about accepted invite.
    * @throws InviteException
    */
   public Invite acceptInvite(String inviteId) throws InviteException
   {
      if (inviteId == null || inviteId.isEmpty())
      {
         throw new InviteException("Invalid inviteId :" + inviteId);
      }

      Invite invite = getInvite(inviteId);
      if (invite.isActivated())
      {
         throw new InviteException("Invite already accepted");
      }
      if (!invite.isValid())
      {
         throw new InviteException("Term of invite action was exceeded");
      }
      try
      {
         inviteUserService.addUser(invite);
      }
      catch (Exception e)
      {
         LOG.error(e.getLocalizedMessage(), e);
         throw new InviteException("Enable to create user: " + invite.getEmail() + "profile for received "
            + "invitation", e);
      }
      invite.setActivated(true);
      saveInvite(invite);
      return invite;

   }

   /**
    * Check if user already registered in the organization service.
    *
    * @param userName
    *           - name of the user
    * @return -true if user already registered in organization service
    * @throws InviteException
    */
   private boolean isUserRegisteredGlobally(String userName) throws InviteException
   {
      try
      {
         return inviteUserService.isUserRegisteredGlobally(userName);
      }
      catch (Exception e)//NOSONAR
      {
         LOG.error(e.getLocalizedMessage(), e);
         throw new InviteException(403, "Error during searching user with email address: " + userName, e);
      }

   }

   /**
    * Check if user already registered in the organization service.
    *
    * @param userName
    *           - name of the user
    * @return -true if user already registered in organization service
    * @throws InviteException
    */
   private boolean isUserRegisteredInOrganization(String userName) throws InviteException
   {
      try
      {
         return inviteUserService.isUserRegistered(userName);
      }
      catch (Exception e)//NOSONAR
      {
         LOG.error(e.getLocalizedMessage(), e);
         throw new InviteException(403, "Error during searching user with email address: " + userName, e);
      }

   }

   /**
    * Check if invite is valid. After predefined period of time invite can
    * become invalid
    *
    * @param userMail
    *           - user email
    * @return - invite information corresponding to the argument.
    */
   public Invite getInviteByUserName(String userMail) throws InviteException
   {
      if (userMail != null && !userMail.isEmpty())
      {
         try
         {
            List<Invite> invites = getInvites(true);
            for (Invite invite : invites)
            {
               if (userMail.equals(invite.getEmail()))
               {
                  return invite;
               }
            }
         }
         catch (InviteException e)
         {
            LOG.error(e.getLocalizedMessage(), e);
         }
      }
      throw new InviteException(404, "Invite with specific id is not found");
   }

   /**
    * Send invite to specific user.
    *
    * @param from
    *           - email of invite initiator.
    * @param to
    *           - email of invited user.
    * @param mailBody
    *           - additional mail bode. added to the template.
    * @throws InviteException
    */
   public void sendInviteByMail(String from, String to, String mailBody) throws InviteException
   {
      // check if specified user is already registered

      Invite newInvite1 = new Invite();
      newInvite1.setEmail(Math.random() + "@site.com");
      newInvite1.setActivated(false);
      newInvite1.setInvitationTime(System.currentTimeMillis());
      newInvite1.setPassword(NameGenerator.generate(null, 12));
      newInvite1.setUuid(UUID.randomUUID().toString());

      saveInvite(newInvite1);

      /*if (isUserRegisteredInOrganization(to))
      {
         throw new InviteException(403, to + " already registered in the system");
      }

      Invite newInvite = new Invite();
      newInvite.setEmail(to);
      newInvite.setActivated(false);
      newInvite.setInvitationTime(System.currentTimeMillis());
      newInvite.setPassword(NameGenerator.generate(null, 12));
      newInvite.setUuid(UUID.randomUUID().toString());

      saveInvite(newInvite);

      // send mail
      try
      {
         Map<String, Object> inviteMessageProperties = inviteMessagePropertiesProvider.getInviteMessageProperties();
         inviteMessageProperties.put("id", newInvite.getUuid());
         inviteMessageProperties.put("user.name", to);
         inviteMessageProperties.put("user.password", newInvite.getPassword());
         inviteMessageProperties.put("inviter.email", from);

         if (mailBody != null && mailBody.length() > 0)
         {
            inviteMessageProperties.put("personal-message", "<td><p><strong>Personal message</strong></p><p>"
               + mailBody + "</p></td>");
         }

         doSendMail(to, "Codenvy <noreply@codenvy.com>", inviteMessageProperties);
      }
      catch (SendingIdeMailException e)
      {
         LOG.error(e.getLocalizedMessage(), e);

         // remove invite from registry if sending failed.
         try
         {
            removeInvite(newInvite.getUuid());
         }
         catch (InviteException ignored)
         {
            LOG.error(ignored.getLocalizedMessage(), ignored);
         }

         throw new InviteException(e.getStatus(), e.getLocalizedMessage());
      }*/
   }

   private void doSendMail(String to, String from, Map<String, Object> inviteMessageProperties) throws InviteException,
      SendingIdeMailException
   {
      try
      {
         String templateContent;
         if (isUserRegisteredGlobally(to))
         {
            templateContent =
               templateResolver.resolveTemplate("template-mail-invitation-registered-user", inviteMessageProperties);
         }
         else
         {
            templateContent = templateResolver.resolveTemplate("template-mail-invitation", inviteMessageProperties);
         }
         mailSender.sendMail(from, to, null, "You've been invited to use Codenvy", "text/html; charset=utf-8",
            templateContent);
      }
      catch (IOException e)
      {
         throw new SendingIdeMailException(e.getLocalizedMessage(), e);
      }
      catch (MessagingException e)
      {
         throw new SendingIdeMailException(e.getLocalizedMessage(), e);
      }
      catch (ConfigurationException e)
      {
         throw new SendingIdeMailException(e.getLocalizedMessage(), e);
      }
   }

   /**
    * Save invite in the Registry. If invite for user already exist - it will be
    * removed.
    *
    * @param invite
    *           - invite to save.
    * @throws InviteException
    */
   protected void saveInvite(Invite invite) throws InviteException
   {
      SessionProvider sessionProvider = sessionProviderService.getSystemSessionProvider(null);

      try
      {
         RegistryEntry invitesEntry = getOrCreateInviteRoot(sessionProvider);
         Document invitesDocument = invitesEntry.getDocument();
         Element rootInvitesElement = invitesDocument.getDocumentElement();
         NodeList allInvites = rootInvitesElement.getChildNodes();
         int length = allInvites.getLength();

         if (length > 0)
         {
            //remove existed user invite and replace it with new
            List<Node> inactiveInvites = new ArrayList<Node>();
            for (int i = 0; i < length; i++)
            {
               Node invitation = allInvites.item(i);
               NamedNodeMap attributes = invitation.getAttributes();
               if (attributes != null)
               {
                  Invite newInvite = Invite.valueOf(attributes);
                  if (invite.getEmail().equals(newInvite.getEmail()) && !newInvite.isActivated())
                  {
                     //user have not activated invite
                     inactiveInvites.add(invitation);
                  }
               }
            }

            for (Node inv : inactiveInvites)
            {
               rootInvitesElement.removeChild(inv);
            }
         }

         rootInvitesElement.appendChild(invite.asElementAttributes(invitesDocument));

         // update registry service.
         registry.recreateEntry(sessionProvider, IDE, new RegistryEntry(invitesDocument));
      }
      catch (RepositoryException e)
      {
         throw new InviteException("Unable to register received invite. Please contact support.", e);
      }
   }

   /**
    * Return invites root path in registry service and creates if not exists.
    */
   protected RegistryEntry getOrCreateInviteRoot(SessionProvider sessionProvider) throws InviteException
   {
      try
      {
         return registry.getEntry(sessionProvider, INVITES_ROOT);
      }
      catch (PathNotFoundException e)
      {
         // create root element for invites if not exists yet.
         try
         {
            registry.createEntry(sessionProvider, IDE, new RegistryEntry("invites"));
            return registry.getEntry(sessionProvider, INVITES_ROOT);
         }
         catch (Exception e1)
         {
            LOG.error(e1.getLocalizedMessage(), e1);
            throw new InviteException("Unable to save information about invites");//NOSONAR
         }
      }
      catch (RepositoryException e)
      {
         LOG.error(e.getLocalizedMessage(), e);
         throw new InviteException("Unable to get information about invites");//NOSONAR
      }
   }

   /**
    * Remove invite from persistent storage.
    *
    * @param inviteUuid
    *           - id of invite to remove.
    * @throws InviteException
    */
   protected void removeInvite(String inviteUuid) throws InviteException
   {
      try
      {
         SessionProvider sessionProvider = sessionProviderService.getSystemSessionProvider(null);
         RegistryEntry entry = getOrCreateInviteRoot(sessionProvider);
         Document inviteDocument = entry.getDocument();
         Element root = inviteDocument.getDocumentElement();
         NodeList invitedUsers = root.getChildNodes();

         for (int i = 0; i < invitedUsers.getLength(); i++)
         {
            Node invitedUser = invitedUsers.item(i);
            if (inviteUuid.equals(invitedUser.getAttributes().getNamedItem("uuid").getNodeValue()))
            {
               registry.removeEntry(sessionProvider, INVITES_ROOT + '/' + invitedUser.getNodeName());
               return;
            }
         }

         throw new InviteException("Invite doesn't exist.");
      }
      catch (RepositoryException e)
      {
         LOG.error(e.getLocalizedMessage(), e);
         throw new InviteException("Unable to remove existed invite.", e);
      }
   }

   /**
    * Check if invite is valid. After predefined period of time invite can
    * become invalid
    *
    * @param inviteId
    *           - id of invite
    * @return - invite information corresponding to the argument.
    */
   public Invite getInvite(String inviteId) throws InviteException
   {
      if (inviteId != null && !inviteId.isEmpty())
      {
         try
         {
            List<Invite> invites = getInvites(true);
            for (Invite invite : invites)
            {
               if (inviteId.equals(invite.getUuid()))
               {
                  return invite;
               }
            }
         }
         catch (InviteException e)
         {
            LOG.error(e.getLocalizedMessage(), e);
         }
      }
      throw new InviteException(404, "Invite with specific id is not found");
   }

   /**
    * @param includePassword
    *           - indicates the need to include password in invite result.
    * @return List of all invites in tenant.
    * @throws InviteException
    */
   public List<Invite> getInvites(boolean includePassword) throws InviteException
   {
      SessionProvider sessionProvider = sessionProviderService.getSystemSessionProvider(null);

      RegistryEntry entry = getOrCreateInviteRoot(sessionProvider);
      Document inviteDocument = entry.getDocument();
      Element root = inviteDocument.getDocumentElement();
      NodeList invitedUsers = root.getChildNodes();
      List<Invite> listOfInvites = new ArrayList<Invite>(invitedUsers.getLength());
      for (int i = 0; i < invitedUsers.getLength(); i++)
      {
         Node invitedUser = invitedUsers.item(i);
         NamedNodeMap attributes = invitedUser.getAttributes();

         Invite invite = Invite.valueOf(attributes);
         if (!includePassword)
         {
            invite.setPassword(null);
         }
         listOfInvites.add(invite);
      }
      LOG.debug("Number of invites {} in the system", listOfInvites.size());
      return listOfInvites;
   }
}
