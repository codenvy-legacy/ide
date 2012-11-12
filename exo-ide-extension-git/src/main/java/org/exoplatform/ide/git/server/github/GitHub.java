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
package org.exoplatform.ide.git.server.github;

import static org.apache.commons.codec.binary.Base64.encodeBase64;

import org.everrest.core.impl.provider.json.ArrayValue;
import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonValue;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.everrest.core.impl.provider.json.ObjectValue;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.ide.commons.JsonHelper;
import org.exoplatform.ide.commons.ParsingResponseException;
import org.exoplatform.ide.git.shared.Collaborators;
import org.exoplatform.ide.git.shared.Credentials;
import org.exoplatform.ide.git.shared.GitHubCredentials;
import org.exoplatform.ide.git.shared.GitHubRepository;
import org.exoplatform.ide.security.oauth.OAuthTokenProvider;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.services.security.ConversationState;

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
public class GitHub
{
   private String userName;

   private Pattern pattern = Pattern.compile("_");

   private final GitHubAuthenticator authenticator;

   private final OAuthTokenProvider oauthTokenProvider;;

   public GitHub(InitParams initParams, GitHubAuthenticator authenticator, OAuthTokenProvider oauthTokenProvider)
   {
      this(readValueParam(initParams, "github-user"), authenticator, oauthTokenProvider);
   }

   public GitHub(String userName, GitHubAuthenticator authenticator, OAuthTokenProvider oauthTokenProvider)
   {
      this.userName = userName;
      this.authenticator = authenticator;
      this.oauthTokenProvider = oauthTokenProvider;
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
    * @throws GitHubException if GitHub server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws InvalidArgumentException
    */
   public GitHubRepository[] listRepositories(String user) throws IOException, GitHubException,
      ParsingResponseException, InvalidArgumentException
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
         GitHubRepository[] repos = (GitHubRepository[])ObjectBuilder.createArray(GitHubRepository[].class, reposArray);
         return repos;
      }
      catch (JsonException jsone)
      {
         throw new ParsingResponseException(jsone.getMessage(), jsone);
      }
   }

   public Collaborators getCollaborators(String user, String repository) throws IOException, ParsingResponseException,
      GitHubException
   {
      String url = "https://api.github.com/repos/" + user + "/" + repository + "/collaborators";
      String method = "GET";
      String response = doJsonRequest(url, method, null, 200);
      JsonValue jsonArray = JsonHelper.parseJson(response);
      jsonArray = formatJsonArray(jsonArray);
      try
      {
         Iterator<JsonValue> iterator = jsonArray.getElements();
         Collaborators collaborators = new CollaboratorsImpl();
         while (iterator.hasNext())
         {
            JsonValue obj = iterator.next();
            if (obj.isObject())
            {
               url = obj.getElement("url").getStringValue();
               response = doJsonRequest(url, method, null, 200);
               JsonValue jsonUser = JsonHelper.parseJson(response);
               jsonUser = formatObject(jsonUser);
               GitHubUserImpl userImpl = ObjectBuilder.createObject(GitHubUserImpl.class, jsonUser);
               collaborators.getCollaborators().add(userImpl);
            }
         }
         return collaborators;
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
    * @throws GitHubException
    * @throws VirtualFileSystemException
    */
   public void login(Credentials credentials) throws IOException, GitHubException, VirtualFileSystemException
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
    * @throws GitHubException
    * @throws ParsingResponseException
    * @throws VirtualFileSystemException
    */
   public GitHubRepository[] listRepositories() throws IOException, GitHubException, ParsingResponseException,
      VirtualFileSystemException
   {
      String oauthToken = oauthTokenProvider.getToken("github", getUserId());
      GitHubCredentials credentials = authenticator.readCredentials();

      if (credentials == null && (oauthToken == null || oauthToken.isEmpty()))
      {
         throw new GitHubException(401, "Authentication required.\n", "text/plain");
      }
      
      return getRepositories(credentials, oauthToken);
   }

   /**
    * @param credentials
    * @return
    * @throws ParsingResponseException
    * @throws IOException
    * @throws GitHubException
    */
   private GitHubRepository[] getRepositories(GitHubCredentials credentials, String oauthToken)
      throws ParsingResponseException, IOException, GitHubException
   {
      String url = "https://api.github.com/user/repos";
      url += (oauthToken != null) ? "?access_token=" + oauthToken : "";
      
      String response = doJsonRequest(url, "GET", credentials, 200);
      JsonValue reposArray = JsonHelper.parseJson(response);
      if (reposArray == null || !reposArray.isArray())
         return null;
      reposArray = formatJsonArray(reposArray);
      try
      {
         GitHubRepository[] repos = (GitHubRepository[])ObjectBuilder.createArray(GitHubRepository[].class, reposArray);
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
            ObjectValue objectValue = formatObject(obj);
            array.addElement(objectValue);
         }
      }
      return array;
   }

   private ObjectValue formatObject(JsonValue obj)
   {
      Iterator<String> keysIterator = obj.getKeys();
      ObjectValue objectValue = new ObjectValue();
      while (keysIterator.hasNext())
      {
         String key = keysIterator.next();
         objectValue.addElement(formatKey(key), obj.getElement(key));
      }
      return objectValue;
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
    * @param success expected success code of request
    * @return response
    * @throws IOException
    * @throws GitHubException
    */
   private String doJsonRequest(String url, String method, GitHubCredentials credentials, int success)
      throws IOException, GitHubException
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

   static GitHubException fault(HttpURLConnection http) throws IOException
   {
      InputStream errorStream = null;
      try
      {
         int responseCode = http.getResponseCode();
         errorStream = http.getErrorStream();
         if (errorStream == null)
            return new GitHubException(responseCode, null, null);

         int length = http.getContentLength();
         String body = readBody(errorStream, length);

         if (body != null)
            return new GitHubException(responseCode, body, http.getContentType());

         return new GitHubException(responseCode, null, null);
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
    * @param credentials GitHub account credentials
    * @param http HttpURLConnection
    * @throws IOException if any i/o errors occurs
    */
   private static void authenticate(Credentials credentials, HttpURLConnection http) throws IOException
   {
      byte[] base64 = encodeBase64((credentials.getLogin() + ":" + credentials.getPassword()).getBytes("ISO-8859-1"));
      http.setRequestProperty("Authorization", "Basic " + new String(base64, "ISO-8859-1"));
   }

   private String getUserId()
   {
      return ConversationState.getCurrent().getIdentity().getUserId();
   }
}
