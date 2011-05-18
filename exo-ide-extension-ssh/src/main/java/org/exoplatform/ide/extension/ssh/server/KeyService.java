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
package org.exoplatform.ide.extension.ssh.server;

import org.exoplatform.ide.extension.ssh.shared.GenKeyRequest;
import org.exoplatform.ide.extension.ssh.shared.KeyItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

/**
 * REST interface to SshKeyProvider.
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("ide/ssh-keys")
public class KeyService
{
   private SshKeyProvider delegate;

   public KeyService(SshKeyProvider keyProvider)
   {
      this.delegate = keyProvider;
   }

   /**
    * Generate SSH key pair.
    */
   @POST
   @Path("gen")
   @RolesAllowed({"users"})
   @Consumes(MediaType.APPLICATION_JSON)
   public Response genKeyPair(@Context SecurityContext security, GenKeyRequest request)
   {
      if (!security.isSecure())
         throw new WebApplicationException(Response.status(400)
            .entity("Secure connection required to be able generate key. ").type(MediaType.TEXT_PLAIN).build());
      try
      {
         delegate.genKeyPair(request.getHost(), request.getComment(), request.getPassphrase());
      }
      catch (IOException ioe)
      {
         throw new WebApplicationException(Response.serverError().entity(ioe.getMessage()).type(MediaType.TEXT_PLAIN)
            .build());
      }
      return Response.ok().build();
   }

   //   /**
   //    * Add prepared private key.
   //    */
   //   @POST
   //   @Path("add")
   //   @RolesAllowed({"users"})
   //   public Response addPrivateKey(@Context SecurityContext security, @QueryParam("host") String host, byte[] keyBody)
   //   {
   //      if (!security.isSecure())
   //         throw new WebApplicationException(Response.status(400)
   //            .entity("Secure connection required to be able generate key. ").type(MediaType.TEXT_PLAIN).build());
   //      try
   //      {
   //         delegate.addPrivateKey(host, keyBody);
   //      }
   //      catch (IOException ioe)
   //      {
   //         throw new WebApplicationException(Response.serverError().entity(ioe.getMessage()).type(MediaType.TEXT_PLAIN)
   //            .build());
   //      }
   //      return Response.ok().build();
   //   }

   /**
    * Get public key.
    * 
    * @see {@link SshKeyProvider#genKeyPair(String, String, String)}
    * @see {@link SshKeyProvider#getPublicKey(String)}
    */
   @GET
   @RolesAllowed({"users"})
   @Produces(MediaType.TEXT_PLAIN)
   public Response getPublicKey(@Context SecurityContext security, @QueryParam("host") String host)
   {
      if (!security.isSecure())
         throw new WebApplicationException(Response.status(400)
            .entity("Secure connection required to be able generate key. ").type(MediaType.TEXT_PLAIN).build());
      try
      {
         Key publicKey = delegate.getPublicKey(host);
         byte[] bytes = publicKey.getBytes();
         if (bytes != null)
            return Response.ok().entity(bytes).type(MediaType.TEXT_PLAIN).build();
         throw new WebApplicationException(Response.status(404).entity("Public key for host " + host + " not found. ")
            .type(MediaType.TEXT_PLAIN).build());
      }
      catch (IOException ioe)
      {
         throw new WebApplicationException(Response.serverError().entity(ioe.getMessage()).type(MediaType.TEXT_PLAIN)
            .build());
      }
   }

   /**
    * Remove SSH keys.
    */
   @POST
   @Path("remove")
   @RolesAllowed({"users"})
   public void removeKeys(@QueryParam("host") String host)
   {
      delegate.removeKeys(host);
   }

   @GET
   @Path("all")
   @RolesAllowed({"users"})
   @Produces(MediaType.APPLICATION_JSON)
   public Response getKeys(@Context UriInfo uriInfo)
   {
      Set<String> all = delegate.getAll();
      if (all.size() == 0)
         return Response.ok().entity(Collections.emptyList()).type(MediaType.APPLICATION_JSON).build();
      List<KeyItem> result = new ArrayList<KeyItem>(all.size());
      for (String host : all)
      {
         byte[] bytes = null;
         try
         {
            bytes = delegate.getPublicKey(host).getBytes();
         }
         catch (IOException ioe)
         {
            throw new WebApplicationException(Response.serverError().entity(ioe.getMessage())
               .type(MediaType.TEXT_PLAIN).build());
         }
         result.add((bytes != null) //
            ? new KeyItem(host, uriInfo.getBaseUriBuilder().path(getClass()).queryParam("host", host).build().toString(), uriInfo.getBaseUriBuilder().path(getClass(),"removeKeys").queryParam("host", host).build().toString()) //
            : new KeyItem(host, null, uriInfo.getBaseUriBuilder().path(getClass(),"removeKeys").queryParam("host", host).build().toString()));
      }
      return Response.ok().entity(result).type(MediaType.APPLICATION_JSON).build();
   }
}
