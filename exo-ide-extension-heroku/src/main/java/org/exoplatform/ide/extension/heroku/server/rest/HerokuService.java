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

import org.exoplatform.ide.extension.heroku.server.Heroku;
import org.exoplatform.ide.extension.heroku.server.HerokuCommand;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * REST interface to {@link Heroku}.
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("ide/heroku")
public class HerokuService
{
   @Path("login")
   public HerokuCommand login()
   {
      return Heroku.getInstance().getCommand("auth:login");
   }

   @Path("logout")
   public HerokuCommand logout()
   {
      return Heroku.getInstance().getCommand("auth:logout");
   }

   @Path("apps{sl:(/)?}{command:.*}")
   public HerokuCommand appsSub(@PathParam("command") String command)
   {
      if (command != null & !command.isEmpty())
         return Heroku.getInstance().getCommand("apps:" + command);
      return Heroku.getInstance().getCommand("apps");
   }

   @Path("keys{sl:(/)?}{command:.*}")
   public HerokuCommand keysSub(@PathParam("command") String command)
   {
      if (command != null & !command.isEmpty())
         return Heroku.getInstance().getCommand("keys:" + command);
      return Heroku.getInstance().getCommand("keys");
   }
}
