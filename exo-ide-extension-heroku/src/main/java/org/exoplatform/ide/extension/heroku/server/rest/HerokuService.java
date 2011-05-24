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
package org.exoplatform.ide.extension.heroku.server.rest;

import org.exoplatform.ide.extension.heroku.server.CommandException;
import org.exoplatform.ide.extension.heroku.server.Heroku;
import org.exoplatform.ide.extension.heroku.server.HerokuException;
import org.exoplatform.ide.extension.heroku.shared.HerokuRequest;

import java.io.File;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("ide/heroku")
public class HerokuService
{
   @POST
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   public Response execute(HerokuRequest request) throws HerokuException
   {
      Heroku heroku = Heroku.getInstance();
      try
      {
         Object result =
            heroku.execute(request.getCommand(), request.getOptions(), request.getArgs(),
               new File(request.getGitWorkDir()));
         return Response.ok(result).type(MediaType.APPLICATION_JSON).build();
      }
      catch (CommandException e)
      {
         throw new WebApplicationException(Response.serverError().entity(e.getMessage()).type(MediaType.TEXT_PLAIN)
            .build());
      }
   }
}
