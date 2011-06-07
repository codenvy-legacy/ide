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
package org.exoplatform.ide.extension.openshift.server.rest;

import org.exoplatform.ide.extension.openshift.server.Express;
import org.exoplatform.ide.extension.openshift.server.ExpressException;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;
import org.exoplatform.ide.extension.openshift.shared.RHUserInfo;
import org.exoplatform.ide.git.server.rest.GitLocation;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("ide/openshift/express")
public class ExpressService
{
   @Inject
   private Express express;

   @POST
   @Path("login")
   @Consumes(MediaType.APPLICATION_JSON)
   public void login(Map<String, String> credentials) throws ExpressException, IOException
   {
      express.login(credentials.get("rhlogin"), credentials.get("password"));
   }

   @POST
   @Path("logout")
   public void logout()
   {
      express.logout();
   }

   @POST
   @Path("domain/create")
   public void createDomain(@QueryParam("namespace") String namespace, @QueryParam("alter") boolean alter)
      throws ExpressException, IOException
   {
      express.createDomain(namespace, alter);
   }

   @POST
   @Path("apps/create")
   public AppInfo createApplication(@QueryParam("app") String app, @QueryParam("type") String type,
      @QueryParam("workdir") GitLocation workDir, @Context UriInfo uriInfo) throws ExpressException, IOException
   {
      return express.createApplication(app, type, new File(workDir.getLocalPath(uriInfo)));
   }

   @POST
   @Path("apps/destroy")
   public void destroyApplication(@QueryParam("app") String app) throws ExpressException, IOException
   {
      express.destroyApplication(app);
   }

   @GET
   @Path("userinfo")
   public RHUserInfo userInfo(@QueryParam("appsInfo") boolean appsInfo) throws ExpressException, IOException
   {
      return express.userInfo(appsInfo);
   }
}
