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

import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.AccessControlEntry;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.exoplatform.services.security.ConversationState;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ws.rs.core.MediaType;

/**
 * Heroku API authenticator.
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class HerokuAuthenticator
{
   private final VirtualFileSystemRegistry vfsRegistry;
   private final String workspace;
   private String config = "/ide-home/users/";

   public HerokuAuthenticator(VirtualFileSystemRegistry vfsRegistry, InitParams initParams)
   {
      this(vfsRegistry, readValueParam(initParams, "workspace"), readValueParam(initParams, "user-config"));
   }

   protected HerokuAuthenticator(VirtualFileSystemRegistry vfsRegistry, String workspace, String config)
   {
      this.vfsRegistry = vfsRegistry;
      this.workspace = workspace;
      if (config != null)
      {
         if (!(config.startsWith("/")))
         {
            throw new IllegalArgumentException("Invalid path " + config + ". Absolute path to configuration required. ");
         }
         this.config = config;
         if (!this.config.endsWith("/"))
         {
            this.config += "/";
         }
      }
   }

   private static String readValueParam(InitParams initParams, String paramName)
   {
      if (initParams != null)
      {
         ValueParam vp = initParams.getValueParam(paramName);
         if (vp != null)
         {
            return vp.getValue();
         }
      }
      return null;
   }

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
   public final void login(String email, String password) throws HerokuException, ParsingResponseException,
      IOException, VirtualFileSystemException
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
            JsonParser parser = new JsonParser();
            parser.parse(input);
            jsonValue = parser.getJsonObject();
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
   public final void logout() throws VirtualFileSystemException, IOException
   {
      removeCredentials();
   }

   public HerokuCredentials readCredentials() throws VirtualFileSystemException, IOException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null);
      String user = ConversationState.getCurrent().getIdentity().getUserId();
      String keyPath = config + user + "/heroku/heroku-credentials";
      ContentStream content = null;
      InputStream in = null;
      BufferedReader r = null;
      try
      {
         content = vfs.getContent(keyPath, null);
         in = content.getStream();
         r = new BufferedReader(new InputStreamReader(in));
         String email = r.readLine();
         String apiKey = r.readLine();
         return new HerokuCredentials(email, apiKey);
      }
      catch (ItemNotFoundException e)
      {
      }
      finally
      {
         if (r != null)
         {
            r.close();
         }
         if (in != null)
         {
            in.close();
         }
      }
      return null;
   }

   public void writeCredentials(HerokuCredentials credentials) throws VirtualFileSystemException, IOException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null);
      Folder heroku = getConfigParent(vfs);
      try
      {
         Item credentialsFile =
            vfs.getItemByPath(heroku.createPath("heroku-credentials"), null, PropertyFilter.NONE_FILTER);
         InputStream newcontent =
            new ByteArrayInputStream((credentials.getEmail() + "\n" + credentials.getApiKey()).getBytes());
         vfs.updateContent(credentialsFile.getId(), MediaType.TEXT_PLAIN_TYPE, newcontent, null);
      }
      catch (ItemNotFoundException e)
      {
         InputStream content =
            new ByteArrayInputStream((credentials.getEmail() + "\n" + credentials.getApiKey()).getBytes());
         Item credentialsFile =
            vfs.createFile(heroku.getId(), "heroku-credentials", MediaType.TEXT_PLAIN_TYPE, content);
         List<AccessControlEntry> acl = new ArrayList<AccessControlEntry>(3);
         String user = ConversationState.getCurrent().getIdentity().getUserId();
         acl.add(new AccessControlEntry(user, new HashSet<String>(vfs.getInfo().getPermissions())));
         vfs.updateACL(credentialsFile.getId(), acl, true, null);
      }
   }

   public void removeCredentials() throws VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null);
      String user = ConversationState.getCurrent().getIdentity().getUserId();
      String keyPath = config + user + "/heroku/heroku-credentials";
      Item credentialsFile = vfs.getItemByPath(keyPath, null, PropertyFilter.NONE_FILTER);
      vfs.delete(credentialsFile.getId(), null);
   }

   private Folder getConfigParent(VirtualFileSystem vfs) throws VirtualFileSystemException
   {
      String user = ConversationState.getCurrent().getIdentity().getUserId();
      String herokuPath = config + user + "/heroku";
      VirtualFileSystemInfo info = vfs.getInfo();
      Folder heroku = null;
      try
      {
         Item item = vfs.getItemByPath(herokuPath, null, PropertyFilter.NONE_FILTER);
         if (ItemType.FOLDER != item.getItemType())
         {
            throw new RuntimeException("Item " + herokuPath + " is not a Folder. ");
         }
         heroku = (Folder)item;
      }
      catch (ItemNotFoundException e)
      {
         heroku = vfs.createFolder(info.getRoot().getId(), herokuPath.substring(1));
      }
      return heroku;
   }
}
