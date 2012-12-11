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
package com.google.collide.server;

import com.google.collide.dto.server.DtoServerImpls.GetWorkspaceParticipantsResponseImpl;
import com.google.collide.server.participants.LoggedInUser;
import com.google.collide.server.participants.Participants;

import java.security.Principal;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

@Path("ide/collab_editor/participants")
public class ParticipantsService
{
   @Inject
   private Participants participants;

   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("list")
   public String getParticipants()
   {
      return ((GetWorkspaceParticipantsResponseImpl)participants.getParticipants()).toJson();
   }

   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("add")
   public String addParticipant(@Context SecurityContext securityContext)
   {
      Principal principal = securityContext.getUserPrincipal();
      LoggedInUser user = new LoggedInUser(principal != null ? principal.getName() : "anonymous",
         UUID.randomUUID().toString());
      participants.addParticipant(user);
      return String.format("{\"userId\":\"%s\",\"activeClientId\":\"%s\"}", user.getId(), user.getId());
   }

   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("delete")
   public void removeParticipant()
   {
      // TODO : pass activeClientId here to be able remove participant
//      if (!participants.removeParticipant(activeClientId))
//      {
//         throw new WebApplicationException(
//            Response.status(400).entity("Not logged in").type(MediaType.TEXT_PLAIN).build());
//      }
   }
}
