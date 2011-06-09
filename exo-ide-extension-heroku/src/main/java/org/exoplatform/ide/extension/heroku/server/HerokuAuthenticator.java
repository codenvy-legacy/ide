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

import org.exoplatform.ws.frameworks.json.impl.JsonDefaultHandler;
import org.exoplatform.ws.frameworks.json.impl.JsonException;
import org.exoplatform.ws.frameworks.json.impl.JsonParserImpl;
import org.exoplatform.ws.frameworks.json.value.JsonValue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Heroku API authenticator.
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class HerokuAuthenticator
{
   /**
    * Obtain heroku API key and store it somewhere (it is dependent to implementation) for next usage. Key should be
    * used by {@link #authenticate(HttpURLConnection)} instead of password for any request to heroku service.
    * 
    * @param email email address that used when create account at heroku.com
    * @param password password
    * @throws HerokuException if heroku server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws IOException if any i/o errors occurs
    */
   public final void login(String email, String password) throws HerokuException, IOException, ParsingResponseException
   {
      HttpURLConnection http = null;
      try
      {
         URL url = new URL(Heroku.HEROKU_API + "/login");
         http = (HttpURLConnection)url.openConnection();
         http.setRequestMethod("POST");
         http.setRequestProperty("Accept", "application/json, */*");
         http.setDoOutput(true);
         OutputStream output = http.getOutputStream();
         try
         {
            output.write(("username=" + email + "&password=" + password).getBytes());
            output.flush();
         }
         finally
         {
            output.close();
         }
         output.close();

         if (http.getResponseCode() != 200)
            throw Heroku.fault(http);

         InputStream input = http.getInputStream();
         JsonValue jsonValue;
         try
         {
            JsonDefaultHandler handler = new JsonDefaultHandler();
            new JsonParserImpl().parse(input, handler);
            jsonValue = handler.getJsonObject();
         }
         finally
         {
            input.close();
         }

         email = jsonValue.getElement("email").getStringValue();
         String apiKey = jsonValue.getElement("api_key").getStringValue();
         writeCredentials(new HerokuCredentials(email, apiKey));
      }
      catch (JsonException jsone)
      {
         // Parsing error.
         throw new ParsingResponseException(jsone.getMessage(), jsone);
      }
      finally
      {
         if (http != null)
            http.disconnect();
      }
   }

   /**
    * Remove local saved credentials.
    * 
    * @see #login(String, String)
    */
   public final void logout()
   {
      removeCredentials();
   }

   protected abstract HerokuCredentials readCredentials() throws IOException;

   protected abstract void writeCredentials(HerokuCredentials credentials) throws IOException;

   protected abstract void removeCredentials();
}
