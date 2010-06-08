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
package org.exoplatform.ideall.conversationstate;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
@Path("/conversation-state")
public class RestConversationState implements ResourceContainer
{
   /**
     * Class logger.
     */
   private final Log log = ExoLogger.getLogger("rest.RestConversationState");

   @GET
   @Path("/whoami")
   @Produces(MediaType.TEXT_PLAIN)
   @RolesAllowed("users")
   public Response whoami()
   {
      CacheControl cc = new CacheControl();
      cc.setNoCache(true);
      cc.setNoStore(true);
      ConversationState curentState = ConversationState.getCurrent();
      if (curentState != null)
      {
         String username = curentState.getIdentity().getUserId();
         if (log.isDebugEnabled())
            log.info("Getting userid: " + username);
         return Response.ok(username, MediaType.TEXT_PLAIN).cacheControl(cc).build();
      }
      return Response.status(Status.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, "exo-domain").build();
   }

}
