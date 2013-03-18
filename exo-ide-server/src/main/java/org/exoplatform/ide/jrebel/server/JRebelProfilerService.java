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
package org.exoplatform.ide.jrebel.server;

import com.exoplatform.cloudide.userdb.Profile;
import com.exoplatform.cloudide.userdb.User;
import com.exoplatform.cloudide.userdb.client.UserDBServiceClient;
import com.exoplatform.cloudide.userdb.exception.AccountExistenceException;
import com.exoplatform.cloudide.userdb.exception.DaoException;
import com.exoplatform.cloudide.userdb.exception.UserDBServiceException;
import com.exoplatform.cloudide.userdb.exception.UserExistenceException;
import com.exoplatform.cloudide.userdb.exception.WorkspaceExistenceException;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;

import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="vzhukovskii@exoplatform.com">Vladyslav Zhukovskii</a>
 * @version $Id: JRebelProfilerService.java 34027 19.12.12 17:02Z vzhukovskii $
 */
@Path("ide/jrebel")
public class JRebelProfilerService
{
   @Inject
   UserDBServiceClient userDBServiceClient;

   private static final Log LOG = ExoLogger.getLogger("JRebel");

   @Path("profile/send")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void sendProfileInfo(Map<String, String> values) throws JRebelProfilerException
   {
      String userId = ConversationState.getCurrent().getIdentity().getUserId();
      try
      {
         User user = userDBServiceClient.getUser(userId);
         if (user.getProfile().getAttribute("firstName") != null && user.getProfile().getAttribute("lastName") != null
            && user.getProfile().getAttribute("phone") != null)
         {
            return; //no need to send already filled profile to ZTA
         }
         user.getProfile().setAttributes(values);
         userDBServiceClient.updateUser(user);

         String formatted =
            String.format("\"userId\",\"firstName\",\"lastName\",\"phone\"\n\"%s\",\"%s\",\"%s\",\"%s\"", userId,
               values.get("firstName").replaceAll("\"", "'"), values.get("lastName").replaceAll("\"", "'"),
               values.get("phone"));

         LOG.error(formatted);
      }
      catch (UserDBServiceException e)
      {
         throw new JRebelProfilerException("Unable to register profile info. Please contact support.", e);
      }
      catch (DaoException e)
      {
         throw new JRebelProfilerException("Unable to register profile info. Please contact support.", e);
      }
      catch (WorkspaceExistenceException e)
      {
         throw new JRebelProfilerException("Unable to register profile info. Please contact support.", e);
      }
      catch (UserExistenceException e)
      {
         throw new JRebelProfilerException("Unable to register profile info. Please contact support.", e);
      }
      catch (AccountExistenceException e)
      {
         throw new JRebelProfilerException("Unable to register profile info. Please contact support.", e);
      }
   }

   @Path("profile/get")
   @GET
   public String getProfileInfo() throws JRebelProfilerException
   {
      String userId = ConversationState.getCurrent().getIdentity().getUserId();
      try
      {
         StringBuilder builder = new StringBuilder("{");
         Profile profile = userDBServiceClient.getUser(userId).getProfile();
         
         builder.append(profile.getAttribute("firstName") != null ? "\"firstName\":\""
            + profile.getAttribute("firstName").replaceAll("\"", "'") + "\"," : "");
         
         builder.append(profile.getAttribute("lastName") != null ? "\"lastName\":\""
            + profile.getAttribute("lastName").replaceAll("\"", "'") + "\"," : "");
         
         builder.append(profile.getAttribute("phone") != null ? "\"phone\":\"" + profile.getAttribute("phone") + "\","
            : "");
         
         if (builder.charAt(builder.length() - 1) == ',')
            builder.setLength(builder.lastIndexOf(","));
         builder.append("}");
         return builder.toString();
      }
      catch (DaoException e)
      {
         throw new JRebelProfilerException("Unable to get profile info. Please contact support.", e);
      }
      catch (UserDBServiceException e)
      {
         throw new JRebelProfilerException("Unable to get profile info. Please contact support.", e);
      }
   }
}