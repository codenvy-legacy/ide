/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.appfog.server;

import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;
import org.exoplatform.ide.commons.ParsingResponseException;
import org.exoplatform.ide.security.paas.Credential;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class AppfogAuthenticator
{
   private static final String defaultTarget = "http://api.appfog.com";

   /**
    * Obtain appfog API token and store it somewhere (it is dependent to implementation) for next usage. Token
    * should be used instead of username/password for any request to appfog service.
    *
    * @param target
    *    location of Appfog REST API, e.g. http://api.appfog.com
    * @param email
    *    email address that used when signup to appfog.com
    * @param password
    *    password
    * @param credential
    *    use it to sore credential after successful authentication.
    *    <pre>
    *       1. Request to appfog REST API and try login with email/password.
    *       2. Parse JSON response and extract parameter 'token' from it.
    *       String token = ...
    *       credential.setAttribute(target, token);
    *    </pre>
    * @throws org.exoplatform.ide.extension.appfog.server.AppfogException
    *    if appfog server return unexpected or error status for request
    * @throws ParsingResponseException
    *    if any error occurs when parse response body
    * @throws IOException
    *    if any i/o errors occurs
    */
   public final void login(String target, String email, String password, Credential credential) throws AppfogException,
      ParsingResponseException, IOException
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

         if (http.getResponseCode() != 200)
         {
            throw Appfog.fault(http);
         }

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

         credential.setAttribute(target, jsonValue.getElement("token").getStringValue());
         credential.setAttribute("current_target", target);
      }
      catch (JsonException jsonExc)
      {
         throw new ParsingResponseException(jsonExc.getMessage(), jsonExc);
      }
      catch (UnknownHostException exc)
      {
         throw new AppfogException(500, "Can't access target.\n", "text/plain");
      }
      finally
      {
         if (http != null)
         {
            http.disconnect();
         }
      }
   }

   public final void login(Credential credential) throws AppfogException, ParsingResponseException, IOException
   {
      login(getTarget(), getUsername(), getPassword(), credential);
   }

   // For test.

   public String getUsername()
   {
      return null;
   }

   public String getPassword()
   {
      return null;
   }

   public String getTarget()
   {
      return defaultTarget;
   }
}
