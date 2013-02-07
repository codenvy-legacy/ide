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
package org.exoplatform.ide.invite;

import org.exoplatform.container.configuration.ConfigurationException;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * This class contains services for sending email messages to users of bundle
 * eXo Cloud + IDE
 */

@Path("/invite")
public class IdeInviteService
{
   private static final Log LOG = ExoLogger.getLogger(IdeInviteService.class);

   private final String sender = "Cloud-IDE <noreply@cloud-ide.com>";

   private final InviteService inviteService;

   private DSASignatureChecker dsaSignatureChecker;


   public IdeInviteService(InviteService inviteService, DSASignatureChecker dsaSignatureChecker)
   {
      this.inviteService = inviteService;
      this.dsaSignatureChecker = dsaSignatureChecker;
   }

   /**
    * Sends email message to invited person and creates record about sent invite
    * in RegistryService. Count of invites for each tenant is limited. NOTE:
    * This service not available for default tenant.
    * <p/>
    * <table>
    * <tr>
    * <th>Status</th>
    * <th>Error description</th>
    * </tr>
    * <tr>
    * <td>400</td>
    * <td>email address of the recipient is invalid</td>
    * </tr>
    * <tr>
    * <td>403</td>
    * <td>user from main tenant tries to use this service</td>
    * </tr>
    * <tr>
    * <td>403</td>
    * <td>limit of sent invitations was exceeded</td>
    * </tr>
    * <tr>
    * <td>403</td>
    * <td>user with specified email address is already registered in system</td>
    * </tr>
    * <tr>
    * <td>403</td>
    * <td>user with specified email address received invitation letter and its
    * invitation is active</td>
    * </tr>
    * <tr>
    * <td>500</td>
    * <td>impossible to write information about created invite in
    * RegistryService</td>
    * </tr>
    * <tr>
    * <td>500</td>
    * <td>error during sending message, it possible when MailService was
    * configured not correctly</td>
    * </tr>
    * </table>
    *
    * @param mailRecipient
    *           - address of invited person
    * @param mailBody
    *           - message what will receive invited person
    * @return the Response with corresponded status (200)
    * @throws com.exoplatform.cloudide.mail.SendingIdeMailException
    */
   @POST
   @Path("{mailrecipient}")
   @RolesAllowed("users")
   @Consumes("text/*")
   public Response sendInvite(@PathParam("mailrecipient") String mailRecipient,
                              @QueryParam("mailsender") String mailSender, String mailBody) throws SendingIdeMailException, InviteException
   {
      String userId = null;
      if (ConversationState.getCurrent() != null)
      {
         userId = ConversationState.getCurrent().getIdentity().getUserId();
      }
      else
      {
         userId = sender;
      }
      String from = (mailSender == null || mailSender.isEmpty()) ? userId : mailSender;
      inviteService.sendInviteByMail(from, mailRecipient, mailBody);
      return Response.ok().entity("Invitation mail sent successfully").build();
   }


   /**
    * Sends email message to invited person and creates record about sent invite
    * in RegistryService. Count of invites for each tenant is limited. NOTE:
    * This service not available for default tenant.
    * <p/>
    * <table>
    * <tr>
    * <th>Status</th>
    * <th>Error description</th>
    * </tr>
    * <tr>
    * <td>400</td>
    * <td>email address of the recipient is invalid</td>
    * </tr>
    * <tr>
    * <td>403</td>
    * <td>user from main tenant tries to use this service</td>
    * </tr>
    * <tr>
    * <td>403</td>
    * <td>limit of sent invitations was exceeded</td>
    * </tr>
    * <tr>
    * <td>403</td>
    * <td>user with specified email address is already registered in system</td>
    * </tr>
    * <tr>
    * <td>403</td>
    * <td>Signature verification failed </td>
    * </tr>
    * <tr>
    * <td>403</td>
    * <td>user with specified email address received invitation letter and its
    * invitation is active</td>
    * </tr>
    * <tr>
    * <td>500</td>
    * <td>impossible to write information about created invite in
    * RegistryService</td>
    * </tr>
    * <tr>
    * <td>500</td>
    * <td>error during sending message, it possible when MailService was
    * configured not correctly</td>
    * </tr>
    * </table>
    *
    * @param mailRecipient
    *           - address of invited person
    * @param mailBody
    *           - message what will receive invited person
    * @return the Response with corresponded status (200)
    * @throws com.exoplatform.cloudide.mail.SendingIdeMailException
    */

   @POST
   @Path("signed/{mailrecipient}")
   @Consumes("text/*")
   public Response sendSignedInvite(@PathParam("mailrecipient") String mailRecipient, @QueryParam("signature") String signature,
                                    @QueryParam("mailsender") String mailsender, String mailBody) throws SendingIdeMailException, InviteException
   {
      try
      {
         dsaSignatureChecker.checkSignature(mailsender, signature);
//         if (SignatureDSA.isSignatureValid(mailsender, signature))
//         {
//            LOG.debug("Signature verification for {} successful.", mailsender);
//         }
//         else
//         {
//            LOG.error("Signature verification for {} failed ", mailsender);
//            throw new InviteException(403, "Signature verification for " + mailsender + " failed.");
//         }
      }
      catch (Exception e)
      {
         LOG.error("Signature verification for {} failed ", mailsender, e);
         throw new InviteException(403, "Signature verification for " + mailsender + " failed.");
      }

      String from = (mailsender == null || mailsender.isEmpty()) ? sender : mailsender;

      inviteService.sendInviteByMail(from, mailRecipient, mailBody);
      return Response.ok().entity("Invitation mail sent successfully").build();
   }


   @POST
   @Path("accept/{id}")
   @Produces(MediaType.APPLICATION_JSON)
   public Response acceptInvite(@PathParam("id") String id) throws InviteException
   {
      Invite invite = inviteService.acceptInvite(id);
      return Response.ok().entity(invite).build();
   }

   /**
    * Gets list of registered invited messages sent by users from current
    * tenant. NOTE: This service not available for default tenant.
    * <p/>
    * <table>
    * <tr>
    * <th>Status</th>
    * <th>Error description</th>
    * </tr>
    * <tr>
    * <td>403</td>
    * <td>user from main tenant tries to use this service</td>
    * </tr>
    * <tr>
    * <td>500</td>
    * <td>error during processing request</td>
    * </tr>
    * </table>
    * <p/>
    * Example of Response body: { "valid":true,
    * "registrationDate":1296719524469, "email":"test@gmail.com",
    * "activated":false, "uuid":"ba544821-2105-4233-85a0-022d21683eff",
    * "password":"***" }
    *
    * @return the Response with corresponded status (200)
    */
   @GET
   @Path("users")
   @Produces(MediaType.APPLICATION_JSON)
   public List<Invite> getListOfInvitedUsers() throws InviteException
   {
      return inviteService.getInvites(false);
   }

   /**
    * Find user among already invited earlier but do not confirm invite in
    * RegistryService. NOTE: This service not available for default tenant.
    * <p/>
    * <table>
    * <tr>
    * <th>Status</th>
    * <th>Error description</th>
    * </tr>
    * <tr>
    * <td>403</td>
    * <td>This service is for domain only.</td>
    * </tr>
    * <tr>
    * <td>403</td>
    * <td>user has already confirmed invitation</td>
    * </tr>
    * <tr>
    * <td>404</td>
    * <td>user is not invited</td>
    * </tr>
    * <tr>
    * <td>500</td>
    * <td>error during processing request</td>
    * </tr>
    * </table>
    *
    * @param userName
    *           - user name of target person
    * @return the Response with corresponded status (200)
    * @throws com.exoplatform.cloudide.mail.SendingIdeMailException
    */
   @GET
   @Path("find-non-confirmed/{username}")
   public Response findInvite(@PathParam("username") String userName) throws InviteException
   {
      Invite invite = inviteService.getInviteByUserName(userName);

      if (invite.isActivated())
      {
         throw new InviteException(403, userName + " already registered in the system");
      }

      return Response.ok("User " + userName + " is invited but did not confirm invitation the system.",
         MediaType.TEXT_PLAIN).build();
   }

   @POST
   @Path("/invalidate/{username}")
   public void invalidateInvite(@PathParam("username") String userName) throws InviteException
   {
      Invite invite = inviteService.getInviteByUserName(userName);

      if (invite.isActivated() || invite.getEmail().equals(ConversationState.getCurrent().getIdentity().getUserId()))
      {
         throw new InviteException("You can't revoke access from already accepted user or itself.");
      }

      inviteService.removeInvite(invite.getUuid());
   }

   private static String getParameterValue(InitParams params, String parameterName) throws ConfigurationException
   {
      ValueParam parameterValueParam = params.getValueParam(parameterName);

      if (parameterValueParam == null)
      {
         throw new ConfigurationException("Parameter " + parameterName + "is not configured");
      }
      return parameterValueParam.getValue();
   }

}
