/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.netvibes.service;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.services.jcr.webdav.WebDavService;
import org.exoplatform.services.rest.ExtHttpHeaders;
import org.exoplatform.services.rest.resource.ResourceContainer;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
@Path("/ide/netvibes")
public class NetvibesWidgetPreviewService implements ResourceContainer
{

   private WebDavService webDavService;

   public NetvibesWidgetPreviewService(WebDavService webDavService)
   {
      this.webDavService = webDavService;
   }

   @GET
   @Path("/{repoName}/{repoPath:.*}/")
   public Response showContent(@PathParam("repoName") String repoName, @PathParam("repoPath") String repoPath,
      @HeaderParam(ExtHttpHeaders.RANGE) String rangeHeader,
      @HeaderParam(ExtHttpHeaders.IF_MODIFIED_SINCE) String ifModifiedSince, @QueryParam("version") String version,
      @Context UriInfo uriInfo)
   {

      Response response = webDavService.get(repoName, repoPath, rangeHeader, ifModifiedSince, version, uriInfo);
      
      if(response.getStatus() != HTTPStatus.OK)
      {
         return response;
      }
      
      return Response.fromResponse(response).type("text/html").build();

   }

}
