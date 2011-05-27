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
import java.util.concurrent.atomic.AtomicReference;

/**
 * Heroku API authenticator.
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class HerokuAuthenticator
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

   private static AtomicReference<HerokuAuthenticator> ainst = new AtomicReference<HerokuAuthenticator>();

   public static HerokuAuthenticator getInstance()
   {
      HerokuAuthenticator t = ainst.get();
      if (t == null)
      {
         ainst.compareAndSet(null, new DefaultHerokuAuthenticator());
         t = ainst.get();
      }
      return t;
   }

   public static void setInstance(HerokuAuthenticator inst)
   {
      if (inst == null)
         ainst.set(new DefaultHerokuAuthenticator());
      else
         ainst.set(inst);
   }

   /**
    * Obtain heroku API key and store it somewhere (it is dependent to implementation) for next usage. Key should be
    * used by {@link #authenticate(HttpURLConnection)} instead of password for any request to heroku service.
    * 
    * @param email email address that used when create account at heroku.com
    * @param password password
    * @throws HerokuException if heroku server return unexpected or error status for request
    * @throws IOException if any i/o errors occurs
    */
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
    * Remove local saved credentials.
    * 
    * @see #login(String, String)
    */
   public final void logout()
   {
      removeCredentials();
   }

   /**
    * Add Basic authentication headers to HttpURLConnection. Typically key obtained with {@link #login(String, String)}
    * should be used instead of password.
    * 
    * @param http HttpURLConnection
    * @throws IOException if any i/o errors occurs
    * @throws CredentialsNotFoundException if credentials is not available (user do not login yet) or corrupted
    */
   public final void authenticate(HttpURLConnection http) throws CredentialsNotFoundException, IOException
   {
      HerokuCredentials herokuCredentials = readCredentials();
      if (herokuCredentials == null)
         throw new CredentialsNotFoundException("Credentials not found. Use method 'login' first. ");
      byte[] base64 = org.apache.commons.codec.binary.Base64.encodeBase64( //
         (herokuCredentials.getEmail() + ":" + herokuCredentials.getApiKey()).getBytes("ISO-8859-1"));
      http.setRequestProperty("Authorization", "Basic " + new String(base64, "ISO-8859-1"));
   }

   protected abstract HerokuCredentials readCredentials() throws IOException;

   protected abstract void writeCredentials(HerokuCredentials credentials) throws IOException;

   protected abstract void removeCredentials();
}
