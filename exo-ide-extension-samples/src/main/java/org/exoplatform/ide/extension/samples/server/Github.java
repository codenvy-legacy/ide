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
package org.exoplatform.ide.extension.samples.server;

import static org.apache.commons.codec.binary.Base64.encodeBase64;

import org.everrest.core.impl.provider.json.ArrayValue;
import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonValue;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.everrest.core.impl.provider.json.ObjectValue;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.ide.extension.samples.shared.Credentials;
import org.exoplatform.ide.extension.samples.shared.GitHubCredentials;
import org.exoplatform.ide.extension.samples.shared.Repository;
import org.exoplatform.ide.helper.JsonHelper;
import org.exoplatform.ide.helper.ParsingResponseException;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Github.java Sep 5, 2011 12:08:04 PM vereshchaka $
 */
public class Github
{
   private String userName;

   private Pattern pattern = Pattern.compile("_");

   private final GitHubAuthenticator authenticator;

   public Github(InitParams initParams, GitHubAuthenticator authenticator)
   {
      this(readValueParam(initParams, "github-user"), authenticator);
   }

   public Github(String userName, GitHubAuthenticator authenticator)
   {
      this.userName = userName;
      this.authenticator = authenticator;
   }

   private static String readValueParam(InitParams initParams, String paramName)
   {
      if (initParams != null)
      {
         ValueParam vp = initParams.getValueParam(paramName);
         if (vp != null)
            return vp.getValue();
      }
      return null;
   }

   /**
    * Get the list of public repositories by user's name.
    * 
    * @param user name of user
    * @return an array of repositories
    * @throws IOException if any i/o errors occurs
    * @throws GithubException if GitHub server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws InvalidArgumentException
    */
   public Repository[] listRepositories(String user) throws IOException, GithubException, ParsingResponseException,
      InvalidArgumentException
   {
      user = (user == null || user.isEmpty()) ? userName : user;
      if (user == null)
      {
         throw new InvalidArgumentException("'User's name must not be null.");
      }
      
      String url = "https://api.github.com/users/" + user + "/repos";
      String method = "GET";
      String response = doJsonRequest(url, method, null, 200);
      JsonValue reposArray = JsonHelper.parseJson(response);
      reposArray = formatJsonArray(reposArray);
      if (reposArray == null || !reposArray.isArray())
         return null;

      try
      {
         Repository[] repos = (Repository[])ObjectBuilder.createArray(Repository[].class, reposArray);
         return repos;
      }
      catch (JsonException jsone)
      {
         throw new ParsingResponseException(jsone.getMessage(), jsone);
      }
   }

   /**
    * Log in GitHub.
    * 
    * @param credentials user's credentials
    * @throws IOException
    * @throws GithubException
    * @throws VirtualFileSystemException
    */
   public void login(Credentials credentials) throws IOException, GithubException, VirtualFileSystemException
   {
      HttpURLConnection http = null;
      try
      {
         http = (HttpURLConnection)new URL("https://api.github.com").openConnection();
         http.setInstanceFollowRedirects(false);
         http.setRequestMethod("GET");
         authenticate(credentials, http);

         if (http.getResponseCode() != 204)
            throw fault(http);
         authenticator.writeCredentials(credentials);
      }
      finally
      {
         if (http != null)
            http.disconnect();
      }
   }

   /**
    * Get the array of the extended repositories of the authorized user.
    * 
    * @return array of the repositories
    * @throws IOException
    * @throws GithubException
    * @throws ParsingResponseException
    * @throws VirtualFileSystemException
    */
   public Repository[] listRepositories() throws IOException, GithubException, ParsingResponseException,
      VirtualFileSystemException
   {
      GitHubCredentials credentials = authenticator.readCredentials();
      if (credentials == null)
      {
         throw new GithubException(401, "Authentication required.\n", "text/plain");
      }
      return getRepositories(credentials);
   }

   /**
    * @param credentials
    * @return
    * @throws ParsingResponseException
    * @throws IOException
    * @throws GithubException
    */
   private Repository[] getRepositories(GitHubCredentials credentials) throws ParsingResponseException, IOException,
      GithubException
   {
      String url = "https://api.github.com/user/repos";
      String response = doJsonRequest(url, "GET", credentials, 200);

      JsonValue reposArray = JsonHelper.parseJson(response);
      if (reposArray == null || !reposArray.isArray())
         return null;
      reposArray = formatJsonArray(reposArray);
      try
      {
         Repository[] repos = (Repository[])ObjectBuilder.createArray(Repository[].class, reposArray);
         return repos;
      }
      catch (JsonException jsone)
      {
         throw new ParsingResponseException(jsone.getMessage(), jsone);
      }

   }

   /**
    * Formats the keys of JSON array objects for them to be represented as beans.
    * 
    * @param source JSON value
    * @return {@link JsonValue} formated JSON array
    */
   private JsonValue formatJsonArray(JsonValue source)
   {
      ArrayValue array = new ArrayValue();
      if (!source.isArray())
      {
         return array;
      }

      Iterator<JsonValue> objIterator = source.getElements();

      while (objIterator.hasNext())
      {
         JsonValue obj = objIterator.next();
         if (obj.isObject())
         {
            Iterator<String> keysIterator = obj.getKeys();
            ObjectValue objectValue = new ObjectValue();
            while (keysIterator.hasNext())
            {
               String key = keysIterator.next();
               objectValue.addElement(formatKey(key), obj.getElement(key));
            }
            array.addElement(objectValue);
         }
      }
      return array;
   }

   /**
    * Format key in the following way: <code>ssh_url</code> to <code>sshUrl</code>, <code>pushed_at</code> to
    * <code>pushedAt</code>.
    * 
    * @param key source key
    * @return {@link String} formated key
    */
   private String formatKey(String key)
   {
      if (key.contains("_"))
      {
         String[] parts = pattern.split(key);
         StringBuilder str = new StringBuilder(parts[0]);
         for (int i = 1; i < parts.length; i++)
         {
            if ((parts[i].length() > 1))
            {
               str.append(Character.toUpperCase(parts[i].charAt(0))).append(parts[i].substring(1));
            }
            else
            {
               str.append(parts[i].toUpperCase());
            }
         }
         return str.toString();
      }
      else
      {
         return key;
      }
   }

   // ---------Implementation-----------------

   /**
    * Do json request (without authorization!)
    * 
    * @param url the request url
    * @param method the request method
    * @param body body of request
    * @param success expected success code of request
    * @return response
    * @throws IOException
    * @throws GithubException
    */
   private String doJsonRequest(String url, String method, GitHubCredentials credentials, int success)
      throws IOException, GithubException
   {
      HttpURLConnection http = null;
      try
      {
         http = (HttpURLConnection)new URL(url).openConnection();
         http.setInstanceFollowRedirects(false);
         http.setRequestMethod(method);
         http.setRequestProperty("Accept", "application/json");
         if (credentials != null)
         {
            authenticate(credentials, http);
         }

         if (http.getResponseCode() != success)
            throw fault(http);

         InputStream input = http.getInputStream();
         String result;
         try
         {
            result = readBody(input, http.getContentLength());
         }
         finally
         {
            input.close();
         }
         return result;
      }
      finally
      {
         if (http != null)
            http.disconnect();
      }
   }

   static GithubException fault(HttpURLConnection http) throws IOException
   {
      InputStream errorStream = null;
      try
      {
         int responseCode = http.getResponseCode();
         errorStream = http.getErrorStream();
         if (errorStream == null)
            return new GithubException(responseCode, null, null);

         int length = http.getContentLength();
         String body = readBody(errorStream, length);

         if (body != null)
            return new GithubException(responseCode, body, http.getContentType());

         return new GithubException(responseCode, null, null);
      }
      finally
      {
         if (errorStream != null)
            errorStream.close();
      }
   }

   private static String readBody(InputStream input, int contentLength) throws IOException
   {
      String body = null;
      if (contentLength > 0)
      {
         byte[] b = new byte[contentLength];
         for (int point = -1, off = 0; (point = input.read(b, off, contentLength - off)) > 0; off += point) //
         ;
         body = new String(b);
      }
      else if (contentLength < 0)
      {
         ByteArrayOutputStream bout = new ByteArrayOutputStream();
         byte[] buf = new byte[1024];
         int point = -1;
         while ((point = input.read(buf)) != -1)
            bout.write(buf, 0, point);
         body = bout.toString();
      }
      return body;
   }

   /**
    * Add Basic authentication headers to HttpURLConnection.
    * 
    * @param gitHubCredentials GitHub account credentials
    * @param http HttpURLConnection
    * @throws IOException if any i/o errors occurs
    */
   private static void authenticate(Credentials credentials, HttpURLConnection http) throws IOException
   {
      byte[] base64 = encodeBase64((credentials.getLogin() + ":" + credentials.getPassword()).getBytes("ISO-8859-1"));
      http.setRequestProperty("Authorization", "Basic " + new String(base64, "ISO-8859-1"));
   }

}
