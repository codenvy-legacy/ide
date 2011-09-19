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

import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class CloudfoundryAuthenticator
{
   protected static final String defaultTarget = "http://api.cloudfoundry.com";
   
   /**
    * Obtain cloudfoundry API token and store it somewhere (it is dependent to implementation) for next usage. Token
    * should be used by {@link #authenticate(HttpURLConnection)} instead of username/password for any request to
    * cloudfoundry service.
    * 
    * @param target location of Cloud Foundry REST API, e.g. http://api.cloudfoundry.com
    * @param email email address that used when signup to cloudfoundry.com
    * @param password password
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws IOException if any i/o errors occurs
    */
   public final void login(String target, String email, String password) throws CloudfoundryException, IOException,
      ParsingResponseException
   {
      HttpURLConnection http = null;
      try
      {
         URL url = new URL(target + "/users/" + email + "/tokens");
         http = (HttpURLConnection)url.openConnection();
         http.setRequestMethod("POST");
         http.setRequestProperty("Accept", "application/json, */*");
         http.setRequestProperty("Content-type", "application/json");
         http.setDoOutput(true);
         OutputStream output = http.getOutputStream();
         try
         {
            output.write(("{\"password\":\"" + password + "\"}").getBytes());
            output.flush();
         }
         finally
         {
            output.close();
         }
         output.close();

         if (http.getResponseCode() != 200)
            throw Cloudfoundry.fault(http);

         InputStream input = http.getInputStream();
         JsonValue jsonValue;
         try
         {
            JsonParser jsonParser = new JsonParser();
            jsonParser.parse(input);
            jsonValue = jsonParser.getJsonObject();
         }
         finally
         {
            input.close();
         }

         CloudfoundryCredentials credentials = readCredentials();
         credentials.addToken(target, jsonValue.getElement("token").getStringValue());
         writeCredentials(credentials);
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
    * Remove local saved credentials for remote Cloud Foundry server. After logout need login again to be able work with
    * remote server.
    * 
    * @param target location of Cloud Foundry REST API, e.g. http://cloudfoundry.com
    * @see #login(String, String, String)
    */
   public final void logout(String target) throws IOException
   {
      CloudfoundryCredentials credentials = readCredentials();
      if (credentials.removeToken(target))
         writeCredentials(credentials);
   }

   public abstract String readTarget() throws IOException;

   public abstract void writeTarget(String target) throws IOException;

   public abstract CloudfoundryCredentials readCredentials() throws IOException;

   public abstract void writeCredentials(CloudfoundryCredentials credentials) throws IOException;
}
