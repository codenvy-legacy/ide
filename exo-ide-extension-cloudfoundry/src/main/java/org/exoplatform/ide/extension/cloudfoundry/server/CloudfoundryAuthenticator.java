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
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.AccessControlEntry;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.exoplatform.services.security.ConversationState;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CloudfoundryAuthenticator
{
   private static final String defaultTarget = "http://api.cloudfoundry.com";
   
   private final VirtualFileSystemRegistry vfsRegistry;
   private final String workspace;
   private String config = "/ide-home/users/";

   public CloudfoundryAuthenticator(VirtualFileSystemRegistry vfsRegistry, InitParams initParams)
   {
      this(vfsRegistry, readValueParam(initParams, "workspace"), readValueParam(initParams, "user-config"));
   }

   protected CloudfoundryAuthenticator(VirtualFileSystemRegistry vfsRegistry, String workspace, String config)
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
    * Obtain cloudfoundry API token and store it somewhere (it is dependent to implementation) for next usage. Token
    * should be used by {@link #authenticate(HttpURLConnection)} instead of username/password for any request to
    * cloudfoundry service.
    * 
    * @param target location of Cloud Foundry REST API, e.g. http://api.cloudfoundry.com
    * @param email email address that used when signup to cloudfoundry.com
    * @param password password
    * @throws CloudfoundryException if cloudfoundry server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    * @throws VirtualFileSystemException
    * @throws IOException if any i/o errors occurs
    */
   public final void login(String target, String email, String password) throws CloudfoundryException,
      ParsingResponseException, VirtualFileSystemException, IOException
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
            throw Cloudfoundry.fault(http);
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

         CloudfoundryCredentials credentials = readCredentials();
         credentials.addToken(target, jsonValue.getElement("token").getStringValue());
         writeCredentials(credentials);
      }
      catch (JsonException jsone)
      {
         throw new ParsingResponseException(jsone.getMessage(), jsone);
      }
      finally
      {
         if (http != null)
         {
            http.disconnect();
         }
      }
   }

   /**
    * Remove local saved credentials for remote Cloud Foundry server. After logout need login again to be able work with
    * remote server.
    * 
    * @param target location of Cloud Foundry REST API, e.g. http://cloudfoundry.com
    * @see #login(String, String, String)
    */
   public final void logout(String target) throws VirtualFileSystemException, IOException
   {
      CloudfoundryCredentials credentials = readCredentials();
      if (credentials.removeToken(target))
      {
         writeCredentials(credentials);
      }
   }

   public String readTarget() throws VirtualFileSystemException, IOException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null);
      String user = ConversationState.getCurrent().getIdentity().getUserId();
      String path = config + user + "/cloud_foundry/vmc_target";
      String target = FilesHelper.readFile(vfs, path);
      if (target == null || target.isEmpty())
      {
         return defaultTarget;
      }
      return target;
   }

   public CloudfoundryCredentials readCredentials() throws VirtualFileSystemException, IOException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null);
      String user = ConversationState.getCurrent().getIdentity().getUserId();
      String path = config + user + "/cloud_foundry/vmc_token";
      String str = FilesHelper.readFile(vfs, path);
      if (str == null)
      {
         return new CloudfoundryCredentials();
      }
      BufferedReader r = null;
      CloudfoundryCredentials credentials;
      try
      {
         r = new BufferedReader(new StringReader(str));
         credentials = CloudfoundryCredentials.readFrom(r);
      }
      finally
      {
         if (r != null)
         {
            r.close();
         }
      }
      return credentials;
   }

   public void writeTarget(String target) throws VirtualFileSystemException, IOException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null);
      writeFile(vfs, getConfigParent(vfs), "vmc_target", target);
   }

   public void writeCredentials(CloudfoundryCredentials credentials) throws VirtualFileSystemException, IOException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null);
      Writer w = new JsonHelper.FastStrWriter();
      credentials.writeTo(w);
      writeFile(vfs, getConfigParent(vfs), "vmc_token", w.toString());
   }
   
   private Item getConfigParent(VirtualFileSystem vfs) throws VirtualFileSystemException
   {
      String user = ConversationState.getCurrent().getIdentity().getUserId();
      String cloudFoundryPath = config + user + "/cloud_foundry";
      VirtualFileSystemInfo info = vfs.getInfo();
      Item cloudFoundry = null;
      try
      {
         cloudFoundry = vfs.getItemByPath(cloudFoundryPath, null, PropertyFilter.NONE_FILTER);
      }
      catch (ItemNotFoundException e)
      {
         cloudFoundry = vfs.createFolder(info.getRoot().getId(), cloudFoundryPath.substring(1));
      }
      return cloudFoundry;
   }

   private void writeFile(VirtualFileSystem vfs, Item parent, String file, String data) throws VirtualFileSystemException, IOException
   {
      String user = ConversationState.getCurrent().getIdentity().getUserId();
      try
      {
         Item credentialsFile =
            vfs.getItemByPath(parent.getPath() + "/" + file, null, PropertyFilter.NONE_FILTER);
         InputStream newcontent = new ByteArrayInputStream(data.getBytes());
         vfs.updateContent(credentialsFile.getId(), MediaType.TEXT_PLAIN_TYPE, newcontent, null);
      }
      catch (ItemNotFoundException e)
      {
         InputStream content = new ByteArrayInputStream(data.getBytes());
         Item credentialsFile = vfs.createFile(parent.getId(), file, MediaType.TEXT_PLAIN_TYPE, content);
         List<AccessControlEntry> acl = new ArrayList<AccessControlEntry>(3);
         acl.add(new AccessControlEntry(user, new HashSet<String>(vfs.getInfo().getPermissions())));
         vfs.updateACL(credentialsFile.getId(), acl, true, null);
      }
   }
}
