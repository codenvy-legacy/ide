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

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.ide.extension.heroku.server.CommandException;
import org.exoplatform.ide.extension.heroku.server.HerokuAuthenticator;
import org.exoplatform.ide.extension.heroku.server.HerokuCommand;
import org.exoplatform.ide.extension.heroku.server.HerokuException;
import org.exoplatform.ide.extension.heroku.server.Option;

import java.io.File;
import java.io.IOException;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class AuthLogin extends HerokuCommand
{
   @Option(name = "email", required = true)
   private String email;

   @Option(name = "password", required = true)
   private String password;

   public AuthLogin(File gitWorkDir)
   {
      super(gitWorkDir);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.server.HerokuCommand#execute()
    */
   @Override
   public Object execute() throws HerokuException, CommandException
   {
      /*HerokuAuthenticator herokuAuthenticator = new DefaultHerokuAuthenticator();*/
      HerokuAuthenticator herokuAuthenticator =
         (HerokuAuthenticator)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(
            HerokuAuthenticator.class);
      try
      {
         herokuAuthenticator.login(email, password);
         return null;
      }
      catch (IOException ioe)
      {
         throw new CommandException(ioe.getMessage(), ioe);
      }
   }
}
