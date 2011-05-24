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
import org.exoplatform.ide.extension.heroku.server.Heroku;
import org.exoplatform.ide.extension.heroku.server.HerokuCommand;
import org.exoplatform.ide.extension.heroku.server.HerokuException;
import org.exoplatform.ide.extension.heroku.server.Option;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class AppsDestroy extends HerokuCommand
{
   @Option(name = "--app")
   private String app;

   public AppsDestroy(File gitWorkDir)
   {
      super(gitWorkDir);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.server.HerokuCommand#execute()
    */
   @Override
   public Object execute() throws HerokuException, CommandException
   {
      if (this.app == null)
      {
         String detectedApp = detectAppName();
         if (detectedApp == null || detectedApp.isEmpty())
            throw new CommandException("Application name is not defined. ");
         this.app = detectedApp;
      }
      HttpURLConnection http = null;
      try
      {
         URL url = new URL(Heroku.HEROKU_API + "/apps/" + app);
         http = (HttpURLConnection)url.openConnection();
         http.setRequestMethod("DELETE");
         http.setRequestProperty("Accept", "application/xml, */*");
         authenticate(http);
         
         if (http.getResponseCode() != 200)
            throw fault(http);
         
         return null;
      }
      catch (IOException ioe)
      {
         throw new CommandException(ioe.getMessage(), ioe);
      }
      finally
      {
         if (http != null)
            http.disconnect();
      }
   }

}
