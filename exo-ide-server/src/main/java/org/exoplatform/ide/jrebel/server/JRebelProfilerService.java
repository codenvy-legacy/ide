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

import java.security.Principal;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

/**
 * @author <a href="vzhukovskii@exoplatform.com">Vladyslav Zhukovskii</a>
 * @version $Id: JRebelProfilerService.java 34027 19.12.12 17:02Z vzhukovskii $
 */
@Path("ide/jrebel")
public class JRebelProfilerService
{
   @Inject
   JRebelProfiler profiler;

   @Path("profile/send")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void sendProfileInfo(Map<String, String> values,
                               @Context SecurityContext sctx)
      throws JRebelProfilerException
   {
      Principal principal = sctx.getUserPrincipal();
      String firstName = values.get("first_name");
      String lastName = values.get("last_name");
      String phone = values.get("phone");

      if (principal != null && firstName != null && !firstName.isEmpty() && lastName != null && !lastName.isEmpty()
         && phone != null && !phone.isEmpty())
      {
         profiler.sendProfileInfo(principal.getName(), firstName, lastName, phone);
      }
      else
      {
         throw new JRebelProfilerException("Fail to get user profile information");
      }
   }

   @Path("profile/get")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Map<String, String> getProfileInfo() throws JRebelProfilerException
   {
      return profiler.getProfileInfo();
   }
}