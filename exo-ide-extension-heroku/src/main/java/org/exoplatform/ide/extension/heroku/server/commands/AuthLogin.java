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
package org.exoplatform.ide.extension.heroku.server.commands;

import org.exoplatform.ide.extension.heroku.server.CommandException;
import org.exoplatform.ide.extension.heroku.server.HerokuAuthenticator;
import org.exoplatform.ide.extension.heroku.server.HerokuCommand;
import org.exoplatform.ide.extension.heroku.server.HerokuException;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;

/**
 * Log in with specified email/password.
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 * @see HerokuAuthenticator
 * @see HerokuAuthenticator#login(String, String)
 * @see HerokuAuthenticator#logout()
 */
public class AuthLogin extends HerokuCommand
{
   /**
    * Result of command execution is saved 'heroku API key', see {@link HerokuAuthenticator#login(String, String)} for
    * details.
    * 
    * @param email email address that used when create account at heroku.com
    * @param password password
    * @throws HerokuException if heroku server return unexpected or error status for request
    * @throws CommandException if any other exception occurs
    */
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void login(Map<String, String> credentials) throws HerokuException, CommandException
   {
      try
      {
         HerokuAuthenticator.getInstance().login(credentials.get("email"), credentials.get("password"));
      }
      catch (IOException ioe)
      {
         throw new CommandException(ioe.getMessage(), ioe);
      }
   }
}
