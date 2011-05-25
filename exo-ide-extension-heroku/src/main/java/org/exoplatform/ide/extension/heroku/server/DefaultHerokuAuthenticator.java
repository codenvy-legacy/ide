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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Heroku API authenticator. Default implementation saves authentication key on file system in file
 * {$HOME}/.heroku/credentials .
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class DefaultHerokuAuthenticator implements HerokuAuthenticator
{
   protected static class HerokuCredentials
   {
      private final String email;
      private final String apiKey;

      public HerokuCredentials(String email, String apiKey)
      {
         this.email = email;
         this.apiKey = apiKey;
      }

      public String getEmail()
      {
         return email;
      }

      public String getApiKey()
      {
         return apiKey;
      }
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.server.HerokuAuthenticator#logout()
    */
   @Override
   public final void logout()
   {
      removeCredentials();
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.server.HerokuAuthenticator#login(java.lang.String, java.lang.String)
    */
   @Override
   public final void login(String email, String password) throws HerokuException, IOException
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
            throw HerokuCommand.fault(http);

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
         throw new IOException(jsone.getMessage(), jsone);
      }
      finally
      {
         if (http != null)
            http.disconnect();
      }
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.server.HerokuAuthenticator#authenticate(java.net.HttpURLConnection)
    */
   @Override
   public final void authenticate(HttpURLConnection http) throws IOException
   {
      HerokuCredentials herokuCredentials = readCredentials();
      byte[] base64 = org.apache.commons.codec.binary.Base64.encodeBase64( //
         (herokuCredentials.getEmail() + ":" + herokuCredentials.getApiKey()).getBytes("ISO-8859-1"));
      http.setRequestProperty("Authorization", "Basic " + new String(base64, "ISO-8859-1"));
   }

   protected HerokuCredentials readCredentials() throws IOException
   {
      File herokuCredentials = new File(getUserHome(), ".heroku/credentials");
      if (!herokuCredentials.exists())
         throw new IllegalStateException("Credentials file not found. Use method 'login' first. ");
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
      File herokuCredentials = new File(getUserHome(), ".heroku/credentials");
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
      herokuCredentials.delete();
   }

   private File getUserHome()
   {
      String home = System.getProperty("user.home");
      if (home != null && !home.isEmpty())
         return new File(home).getAbsoluteFile();
      throw new RuntimeException("Can't detect user.home directory. ");
   }
}
