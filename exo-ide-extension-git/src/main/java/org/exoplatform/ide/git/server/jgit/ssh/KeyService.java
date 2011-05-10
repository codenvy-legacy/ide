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
package org.exoplatform.ide.git.server.jgit.ssh;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * REST interface to SshKeyProvider.
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("ide/keys")
public class KeyService
{
   private SshKeyProvider keyProvider;

   public KeyService(SshKeyProvider keyProvider)
   {
      this.keyProvider = keyProvider;
   }

   @POST
   @Path("generate")
   @RolesAllowed({"users"})
   @Consumes(MediaType.APPLICATION_JSON)
   public Response genKeys(@Context SecurityContext security, GenKeyRequest request) throws Exception
   {
      /*if (!security.isSecure())
         throw new WebApplicationException(Response.status(400)
            .entity("Secure connection required to be able generate key. ").type(MediaType.TEXT_PLAIN).build());*/
      keyProvider.genKeyFiles(request.getHost(), request.getComment(), request.getPassphrase());
      return Response.ok().build();
   }
}
