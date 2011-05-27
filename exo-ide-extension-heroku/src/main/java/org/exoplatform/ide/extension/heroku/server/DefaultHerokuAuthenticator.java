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
package org.exoplatform.ide.extension.heroku.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Heroku API authenticator. Default implementation saves authentication key on file system in file
 * {$HOME}/.heroku/credentials .
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class DefaultHerokuAuthenticator extends HerokuAuthenticator
{
   protected HerokuCredentials readCredentials() throws IOException
   {
      File herokuCredentials = new File(getUserHome(), ".heroku/credentials");
      if (!herokuCredentials.exists())
         return null;
      BufferedReader credentialsReader = new BufferedReader(new FileReader(herokuCredentials));
      try
      {
         String email = credentialsReader.readLine();
         String apiKey = credentialsReader.readLine();
         return new HerokuCredentials(email, apiKey);
      }
      finally
      {
         credentialsReader.close();
      }
   }

   protected void writeCredentials(HerokuCredentials credentials) throws IOException
   {
      File heroku = new File(getUserHome(), ".heroku");
      if (!heroku.mkdir())
         throw new IOException("Cannot create directory " + heroku.getAbsolutePath());
      File herokuCredentials = new File(heroku, "credentials");
      FileWriter credetialsWriter = new FileWriter(herokuCredentials);
      try
      {
         credetialsWriter.write(credentials.getEmail());
         credetialsWriter.write('\n');
         credetialsWriter.write(credentials.getApiKey());
         credetialsWriter.flush();
      }
      finally
      {
         credetialsWriter.close();
      }
   }

   protected void removeCredentials()
   {
      File herokuCredentials = new File(getUserHome(), ".heroku/credentials");
      if (!herokuCredentials.delete())
         throw new RuntimeException("Cannot delete credentials. ");
   }

   private File getUserHome()
   {
      String home = System.getProperty("user.home");
      if (home != null && !home.isEmpty())
         return new File(home).getAbsoluteFile();
      throw new RuntimeException("Can't detect user.home directory. ");
   }
}
