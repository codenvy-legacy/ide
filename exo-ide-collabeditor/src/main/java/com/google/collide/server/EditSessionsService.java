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

import com.google.collide.dto.FileContents;
import com.google.collide.dto.server.DtoServerImpls.ClientToServerDocOpImpl;
import com.google.collide.dto.server.DtoServerImpls.FileContentsImpl;
import com.google.collide.dto.server.DtoServerImpls.GetFileContentsImpl;
import com.google.collide.dto.server.DtoServerImpls.GetFileContentsResponseImpl;
import com.google.collide.dto.server.DtoServerImpls.ServerToClientDocOpsImpl;
import com.google.collide.server.documents.EditSessions;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("ide/collab_editor/documents")
public class EditSessionsService
{
   @Inject
   private EditSessions editSessions;

   @POST
   @Path("open")
   public String openSession(String message)
   {
      GetFileContentsImpl request = GetFileContentsImpl.fromJsonString(message);
      final String vfsId = request.getWorkspaceId();
      final String path = request.getPath();
      FileContents fileContents = editSessions.openSession(vfsId, path);
      return GetFileContentsResponseImpl.make()
         .setFileExists(fileContents != null)
         .setFileContents((FileContentsImpl)fileContents)
         .toJson();
   }

   @POST
   @Path("mutate")
   public String mutate(String message)
   {
      return ((ServerToClientDocOpsImpl)editSessions.mutate(ClientToServerDocOpImpl.fromJsonString(message))).toJson();
   }
}
