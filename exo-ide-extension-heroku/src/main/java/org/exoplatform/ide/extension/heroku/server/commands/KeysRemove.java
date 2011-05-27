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
import org.exoplatform.ide.extension.heroku.server.CredentialsNotFoundException;
import org.exoplatform.ide.extension.heroku.server.Heroku;
import org.exoplatform.ide.extension.heroku.server.HerokuCommand;
import org.exoplatform.ide.extension.heroku.server.HerokuException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;

/**
 * Remove SSH key for current user.
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class KeysRemove extends HerokuCommand
{
   /**
    * Remove all SSH keys for current user.
    * 
    * @param keyName key name to remove typically in form 'user@host'. NOTE: If <code>null</code> then all keys for
    *           current user removed
    * @throws HerokuException if heroku server return unexpected or error status for request
    * @throws CredentialsNotFoundException if cannot get access to heroku.com server since user is not login yet and has
    *            not credentials. Must use {@link AuthLogin#execute(String, String)} first.
    * @throws CommandException if any other exception occurs
    * @see KeysClear
    */
   @POST
   public void remove(@QueryParam("keyName") String keyName) throws HerokuException, CredentialsNotFoundException,
      CommandException
   {
      HttpURLConnection http = null;
      try
      {
         URL url = new URL(Heroku.HEROKU_API + ((keyName != null) //
            ? ("/user/keys/" + URLEncoder.encode(keyName, "utf-8")) //
            : "/user/keys"));
         http = (HttpURLConnection)url.openConnection();
         http.setRequestMethod("DELETE");
         http.setRequestProperty("Accept", "application/xml, */*");
         authenticate(http);

         if (http.getResponseCode() != 200)
            throw fault(http);
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
