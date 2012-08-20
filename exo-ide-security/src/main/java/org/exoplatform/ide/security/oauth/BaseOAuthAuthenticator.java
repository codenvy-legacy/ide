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
package org.exoplatform.ide.security.oauth;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpParser;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.json.JsonHttpParser;
import com.google.api.client.json.jackson.JacksonFactory;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.commons.ContainerUtils;
import org.exoplatform.ide.commons.JsonHelper;
import org.exoplatform.ide.commons.ParsingResponseException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class BaseOAuthAuthenticator implements OAuthAuthenticator
{
   public static GoogleClientSecrets loadClientSecrets(String configName) throws IOException
   {
      InputStream secrets = Thread.currentThread().getContextClassLoader().getResourceAsStream(configName);
      if (secrets != null)
      {
         // stream closed after parsing by JsonFactory.
         return GoogleClientSecrets.load(new JacksonFactory(), secrets);
      }
      throw new IOException("Cannot load client secrets. File '" + configName + "' not found. ");
   }

   public static GoogleClientSecrets createClientSecrets(InitParams initParams)
   {
      final String type = ContainerUtils.readValueParam(initParams, "type");
      if (!("installed".equals(type) || "web".equals(type)))
      {
         throw new IllegalArgumentException("Invalid credentials type " + type + " .Must be 'web' or 'installed'. ");
      }
      GoogleClientSecrets.Details cfg = new GoogleClientSecrets.Details();
      cfg.setClientId(ContainerUtils.readValueParam(initParams, "client-id"))
         .setClientSecret(ContainerUtils.readValueParam(initParams, "client-secret"))
         .setAuthUri(ContainerUtils.readValueParam(initParams, "auth-uri"))
         .setTokenUri(ContainerUtils.readValueParam(initParams, "token-uri"))
         .setRedirectUris(ContainerUtils.readValuesParam(initParams, "redirect-uris"));
      return "web".equals(type) ? new GoogleClientSecrets().setWeb(cfg) : new GoogleClientSecrets().setInstalled(cfg);
   }

   protected final AuthorizationCodeFlow flow;
   private final Map<Pattern, String> redirectUrisMap;

   public BaseOAuthAuthenticator(AuthorizationCodeFlow flow, Set<String> redirectUris)
   {
      this.flow = flow;
      this.redirectUrisMap = new HashMap<Pattern, String>(redirectUris.size());
      for (String uri : redirectUris)
      {
         // Redirect URI may be in form urn:ietf:wg:oauth:2.0:oob os use java.net.URI instead of java.net.URL
         this.redirectUrisMap.put(Pattern.compile("([a-z0-9\\-]+\\.)?" + URI.create(uri).getHost()), uri);
      }
   }

   @Override
   public final String getAuthenticateUrl(URL requestUrl, String userId, List<String> scopes)
   {
      AuthorizationCodeRequestUrl url = flow.newAuthorizationUrl()
         .setRedirectUri(findRedirectUrl(requestUrl))
         .setScopes(scopes);
      StringBuilder state = new StringBuilder();
      addState(state);
      String query = requestUrl.getQuery();
      if (query != null)
      {
         if (state.length() > 0)
         {
            state.append('&');
         }
         state.append(query);
      }
      if (userId != null)
      {
         if (state.length() > 0)
         {
            state.append('&');
         }
         state.append("userId=");
         state.append(userId);
      }
      url.setState(state.toString());
      return url.build();
   }

   private String findRedirectUrl(URL requestUrl)
   {
      final String requestHost = requestUrl.getHost();
      for (Map.Entry<Pattern, String> e : redirectUrisMap.entrySet())
      {
         if (e.getKey().matcher(requestHost).matches())
         {
            return e.getValue();
         }
      }
      return null; // TODO : throw exception instead of return null ???
   }

   protected void addState(StringBuilder state)
   {
   }

   @Override
   public final String callback(URL requestUrl, List<String> scopes) throws OAuthAuthenticationException
   {
      AuthorizationCodeResponseUrl authorizationCodeResponseUrl = new AuthorizationCodeResponseUrl(requestUrl.toString());
      final String error = authorizationCodeResponseUrl.getError();
      if (error != null)
      {
         throw new OAuthAuthenticationException("Authentication failed: " + error);
      }
      final String code = authorizationCodeResponseUrl.getCode();
      if (code == null)
      {
         throw new OAuthAuthenticationException("Missing authorization code. ");
      }

      try
      {
         final HttpParser parser = getParser();
         TokenResponse tokenResponse = flow.newTokenRequest(code)
            .setRequestInitializer(new HttpRequestInitializer()
            {
               @Override
               public void initialize(HttpRequest request) throws IOException
               {
                  if (request.getParser(parser.getContentType()) == null)
                  {
                     request.addParser(parser);
                  }
                  request.getHeaders().setAccept(parser.getContentType());
               }
            })
            .setRedirectUri(findRedirectUrl(requestUrl))
            .setScopes(scopes).execute();
         String userId = getUserFromUrl(authorizationCodeResponseUrl);
         if (userId == null)
         {
            userId = getUser(tokenResponse.getAccessToken()).getId();
         }
         flow.createAndStoreCredential(tokenResponse, userId);
         return userId;
      }
      catch (IOException ioe)
      {
         throw new OAuthAuthenticationException(ioe.getMessage());
      }
   }

   private String getUserFromUrl(AuthorizationCodeResponseUrl authorizationCodeResponseUrl) throws IOException
   {
      String state = authorizationCodeResponseUrl.getState();
      if (!(state == null || state.isEmpty()))
      {
         String decoded = URLDecoder.decode(state, "UTF-8");
         String[] items = decoded.split("&");
         for (String str : items)
         {
            if (str.startsWith("userId="))
            {
               return str.substring(7, str.length());
            }
         }
      }
      return null;
   }

   /**
    * Get suitable implementation of HttpParser.
    *
    * @return instance  of HttpParser
    */
   protected HttpParser getParser()
   {
      return new JsonHttpParser(flow.getJsonFactory());
   }

   protected <O> O getJson(String getUserUrl, Class<O> userClass) throws OAuthAuthenticationException
   {
      HttpURLConnection urlConnection = null;
      InputStream urlInputStream = null;

      try
      {
         urlConnection = (HttpURLConnection)new URL(getUserUrl).openConnection();
         urlInputStream = urlConnection.getInputStream();
         return JsonHelper.fromJson(urlInputStream, userClass, null);
      }
      catch (ParsingResponseException e)
      {
         throw new OAuthAuthenticationException(e.getMessage(), e);
      }
      catch (IOException e)
      {
         throw new OAuthAuthenticationException(e.getMessage(), e);
      }
      finally
      {
         if (urlInputStream != null)
         {
            try
            {
               urlInputStream.close();
            }
            catch (IOException ignored)
            {
            }
         }

         if (urlConnection != null)
         {
            urlConnection.disconnect();
         }
      }
   }

   @Override
   public final String getToken(String userId) throws IOException
   {
      Credential credential = flow.loadCredential(userId);
      if (credential != null)
      {
         Long expirationTime = credential.getExpiresInSeconds();
         if (expirationTime != null && expirationTime < 0)
         {
            credential.refreshToken();
         }
         return credential.getAccessToken();
      }
      return null;
   }

   @Override
   public final boolean invalidateToken(String userId)
   {
      Credential credential = flow.loadCredential(userId);
      if (credential != null)
      {
         flow.getCredentialStore().delete(userId, credential);
         return true;
      }
      return false;
   }
}
