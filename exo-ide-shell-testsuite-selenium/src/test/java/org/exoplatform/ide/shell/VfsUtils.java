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
package org.exoplatform.ide.shell;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.copy.HTTPMethod;
import org.exoplatform.ide.vfs.shared.Link;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Map;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Dec 27, 2011 10:26:19 AM evgen $
 *
 */
public class VfsUtils
{

   private static VirtualFileSystemInfo vfsInfo;

   private static Map<String, Link> rootLinks;
   
   /**
    * Delete folder by Link
    * @param link
    * @return http status code
    * @throws IOException
    */
   public static int deleteFolder(Link link) throws IOException
   {
      if (link == null)
         throw new IllegalArgumentException("Parameter 'link' can't be null!");

      int status = -1;
      HttpURLConnection connection = null;
      try
      {
         URL url = new URL(link.getHref());
         connection = getConnection(url);
         connection.setRequestMethod(HTTPMethod.POST);
         status = connection.getResponseCode();
      }
      finally
      {
         if (connection != null)
         {
            connection.disconnect();
         }
      }
      return status;
   }

   /**
    * 
    */
   @SuppressWarnings("unchecked")
   private static void initVFS() throws IOException
   {
      String uRl = BaseTest.BASE_URL + BaseTest.REST_CONTEXT + "/ide/vfs/dev-monit";
      HttpURLConnection connection = null;
      try
      {
         URL url = new URL(uRl);
         connection = getConnection(url);
         connection.setRequestMethod("GET");
         JsonParser parser = new JsonParser();
         parser.parse(connection.getInputStream());
         connection.getInputStream().close();
         vfsInfo = ObjectBuilder.createObject(VirtualFileSystemInfo.class, parser.getJsonObject());
         JsonValue element = parser.getJsonObject().getElement("root").getElement("links");

         Field field = VfsUtils.class.getDeclaredField("rootLinks");
         rootLinks = ObjectBuilder.createObject(Map.class, (ParameterizedType)field.getGenericType(), element);
      }
      catch (JsonException e)
      {
         e.printStackTrace();
      }
      catch (SecurityException e)
      {
         e.printStackTrace();
      }
      catch (NoSuchFieldException e)
      {
         e.printStackTrace();
      }
      finally
      {
         if (connection != null)
         {
            connection.disconnect();
         }
      }

   }

   /**
    * @param url
    * @return
    */
   private static HttpURLConnection getConnection(URL url) throws IOException
   {
      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
      String encoded = new String(Base64.encodeBase64((BaseTest.USER_NAME+":"+BaseTest.USER_PASSWORD).getBytes()));
      connection.setRequestProperty("Authorization", "Basic "+encoded); 
      connection.setAllowUserInteraction(false);
      return connection;
   }

   /**
    * Create folder in root.
    * @param name
    * @return
    * @throws IOException
    */
   @SuppressWarnings("unchecked")
   private static Map<String, Link> createFolder(String name) throws IOException
   {
      if (rootLinks == null)
      {
         initVFS();
      }
      HttpURLConnection connection = null;
      try
      {
         String href = rootLinks.get(Link.REL_CREATE_FOLDER).getHref();
         href = URLDecoder.decode(href, "UTF-8").replace("[name]", name);
         URL url = new URL(href);
         connection = getConnection(url);
         connection.setRequestMethod("POST");
         JsonParser parser = new JsonParser();
         parser.parse(connection.getInputStream());
         connection.getInputStream().close();
         Field field = VfsUtils.class.getDeclaredField("rootLinks");

         return (Map<String, Link>)ObjectBuilder.createObject(Map.class, (ParameterizedType)field.getGenericType(),
            parser.getJsonObject().getElement("links"));
      }
      catch (JsonException e)
      {
         e.printStackTrace();
      }
      catch (SecurityException e)
      {
         e.printStackTrace();
      }
      catch (NoSuchFieldException e)
      {
         e.printStackTrace();
      }
      finally
      {
         if (connection != null)
         {
            connection.disconnect();
         }
      }
      return null;
   }

   /**
    * Import project zip to IDE
    * @param projectName name of the project
    * @param zipPath local path to project zip
    * @return map of the Link, related to created project
    * @throws IOException
    */
   public static Map<String, Link> importZipProject(String projectName, String zipPath) throws IOException
   {

      HttpURLConnection connection = null;
      try
      {

         Map<String, Link> folderLiks = createFolder(projectName);
         Link href = folderLiks.get(Link.REL_IMPORT);
         if (href == null)
            throw new RuntimeException("Folder not created or 'import' relation not found.");
         URL url = new URL(href.getHref());
         connection = getConnection(url);
         connection.setRequestMethod("POST");
         connection.setRequestProperty(HTTPHeader.CONTENT_TYPE, "application/zip");
         connection.setDoOutput(true);
         OutputStream outputStream = connection.getOutputStream();
         File f = new File(zipPath);
         FileInputStream inputStream = new FileInputStream(f);
         IOUtils.copy(inputStream, outputStream);
         inputStream.close();
         outputStream.close();
         connection.getResponseCode();
         return folderLiks;
      }
      finally
      {
         if (connection != null)
         {
            connection.disconnect();
         }
      }
   }

}
