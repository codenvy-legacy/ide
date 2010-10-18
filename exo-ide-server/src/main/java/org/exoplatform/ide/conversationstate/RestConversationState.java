/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.conversationstate;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
@Path("/ide/conversation-state")
public class RestConversationState implements ResourceContainer
{
   /**
     * Class logger.
     */
   private final Log log = ExoLogger.getLogger("ide.RestConversationState");

   @POST
   @Path("/whoami")
   @Produces(MediaType.APPLICATION_JSON)
   @RolesAllowed("users")
   public IdeUser whoami()
   {
      ConversationState curentState = ConversationState.getCurrent();
      if (curentState != null)
      {
         Identity identity = curentState.getIdentity();
         IdeUser user = new IdeUser(identity.getUserId(), identity.getGroups(), identity.getRoles());
         if (log.isDebugEnabled())
            log.info("Getting user identity: " + identity.getUserId());
         return user;
      }
      else throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).build());
   }

}
