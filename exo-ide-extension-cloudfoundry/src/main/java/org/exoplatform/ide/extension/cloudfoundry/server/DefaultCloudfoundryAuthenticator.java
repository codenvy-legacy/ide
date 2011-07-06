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
package org.exoplatform.ide.extension.cloudfoundry.server;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

/**
 * Cloudfoundry authenticator. Default implementation saves authentication key on file system in file {$HOME}/.vmc_token
 * .
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class DefaultCloudfoundryAuthenticator extends CloudfoundryAuthenticator
{
   protected CloudfoundryCredentials readCredentials() throws IOException
   {
      File credentialsFile = new File(getUserHome(), ".vmc_token");
      if (!credentialsFile.exists())
         return null;
      Reader credentialsReader = new FileReader(credentialsFile);
      try
      {
         return CloudfoundryCredentials.readFrom(credentialsReader);
      }
      finally
      {
         credentialsReader.close();
      }
   }

   protected void writeCredentials(CloudfoundryCredentials credentials) throws IOException
   {
      File credentialsFile = new File(getUserHome(), ".vmc_token");
      FileWriter credetialsWriter = new FileWriter(credentialsFile);
      try
      {
         credentials.writeTo(credetialsWriter);
         credetialsWriter.flush();
      }
      finally
      {
         credetialsWriter.close();
      }
   }

   protected void removeCredentials()
   {
      File credentialsFile = new File(getUserHome(), ".vmc_token");
      if (!credentialsFile.delete())
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
