/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.io.IOUtils;
import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.everrest.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.copy.HTTPMethod;
import org.exoplatform.ide.core.Response;
import org.exoplatform.ide.vfs.shared.Link;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class VirtualFileSystemUtils
{

   private static VirtualFileSystemInfo vfsInfo;

   private static Map<String, Link> rootLinks;

   public static int put(String filePath, String mimeType, String contentNodeType, String storageUrl)
      throws IOException
   {
      HttpURLConnection connection = null;
      try
      {
         URL url = new URL(storageUrl);
         connection = Utils.getConnection(url);
         String data = Utils.readFileAsString(filePath);
         connection.setRequestMethod(HTTPMethod.PUT);
         connection.setRequestProperty(HTTPHeader.CONTENT_TYPE, mimeType);
         connection.setRequestProperty(HTTPHeader.CONTENT_LENGTH, String.valueOf(data.length()));
         connection.setRequestProperty(HTTPHeader.CONTENT_NODETYPE, contentNodeType);
         connection.setDoOutput(true);
         OutputStream output = connection.getOutputStream();
         output.write(data.getBytes());
         output.close();
         return connection.getResponseCode();
      }
      finally
      {
         if (connection != null)
         {
            connection.disconnect();
         }
      }
   }

   public static int put(byte[] data, String mimeType, String contentNodeType, String storageUrl) throws IOException
   {
      HttpURLConnection connection = null;
      try
      {
         URL url = new URL(storageUrl);
         connection = Utils.getConnection(url);
         connection.setRequestMethod(HTTPMethod.PUT);
         connection.setRequestProperty(HTTPHeader.CONTENT_TYPE, mimeType);
         connection.setRequestProperty(HTTPHeader.CONTENT_LENGTH, String.valueOf(data.length));
         connection.setRequestProperty(HTTPHeader.CONTENT_NODETYPE, contentNodeType);
         connection.setDoOutput(true);
         OutputStream output = connection.getOutputStream();
         output.write(data);
         output.close();
         return connection.getResponseCode();
      }
      finally
      {
         if (connection != null)
         {
            connection.disconnect();
         }
      }
   }

   public static int put(byte[] data, String storageUrl) throws IOException
   {
      HttpURLConnection connection = null;
      try
      {
         URL url = new URL(storageUrl);
         connection = Utils.getConnection(url);
         connection.setRequestMethod(HTTPMethod.PUT);
         connection.setRequestProperty(HTTPHeader.CONTENT_TYPE, "application/xml");
         connection.setDoOutput(true);
         OutputStream output = connection.getOutputStream();
         output.write(data);
         output.close();
         return connection.getResponseCode();
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
    * @param filePath
    * @param mimeType
    * @param storageUrl
    * @return HTTPStatus code
    * @throws IOException
    */
   public static int put(byte[] data, String mimeType, String storageUrl) throws IOException
   {
      return put(data, mimeType, TestConstants.NodeTypes.NT_RESOURCE, storageUrl);
   }

   /**
    * @param filePath
    * @param mimeType
    * @param storageUrl
    * @return HTTPStatus code
    * @throws IOException
    */
   public static int put(String filePath, String mimeType, String storageUrl) throws IOException
   {
      return put(filePath, mimeType, TestConstants.NodeTypes.NT_RESOURCE, storageUrl);
   }

   /**
    * @param storageUrl
    * @return HTTPStatus code
    * @throws IOException
    * @throws ModuleException
    */
   public static int delete(String storageUrl) throws IOException
   {
      int status = -1;
      HttpURLConnection connection = null;
      try
      {
         URL url = new URL(storageUrl);
         connection = Utils.getConnection(url);
         connection.setRequestMethod(HTTPMethod.DELETE);
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
    * @param storageUrl
    * @return HTTPStatus code
    * @throws IOException
    */
   public static Response get(String storageUrl) throws IOException
   {
      URL url = new URL(storageUrl);
      HttpURLConnection connection = null;
      int status = -1;
      String data = "";
      BufferedReader reader = null;
      try
      {
         connection = Utils.getConnection(url);
         connection.setRequestMethod(HTTPMethod.GET);
         status = connection.getResponseCode();
         InputStream in = connection.getInputStream();
         reader = new BufferedReader(new InputStreamReader(in));
         StringBuilder sb = new StringBuilder();

         String line = null;
         while ((line = reader.readLine()) != null)
         {
            sb.append(line);
            sb.append('\n');
         }
         data = sb.toString();
         in.close();
         if (reader != null)
         {
            reader.close();
         }
      }
      catch (Exception e)
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
      return new Response(status, data);
   }

   /**
    * @param storageUrl
    * @return HTTPStatus code
    * @throws IOException
    */
   public static int mkcol(String storageUrl) throws IOException
   {
      int status = -1;
      HttpURLConnection connection = null;
      try
      {
         URL url = new URL(storageUrl);
         connection = Utils.getConnection(url);
         connection.setRequestMethod("GET");
         connection.setRequestProperty("X-HTTP-Method-Override", "MKCOL");
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

   public static int upoadZipFolder(String zipPath, String storageUrl) throws HttpException, IOException
   {
      File f = new File(zipPath);
      PostMethod filePost = new PostMethod(BaseTest.BASE_URL + BaseTest.REST_CONTEXT + "/ide/upload/folder");
      Part[] parts = {new StringPart("location", storageUrl), new FilePart("file", f)};
      filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));
      HttpClient client = Utils.getHttpClient();
      return client.executeMethod(filePost);
   }

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
         connection = Utils.getConnection(url);
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

   public static Map<String, Link> createDefaultProject(String name) throws IOException
   {
      return importZipProject(name, "src/test/resources/org/exoplatform/ide/project/default-selenium-test.zip");
   }

   public static int createFile(Link link, String name, String mimeType, String content) throws IOException
   {
      if (link == null)
         throw new IllegalArgumentException("Parameter 'link' can't be null!");
      int status = -1;
      HttpURLConnection connection = null;
      try
      {
         String href = URLDecoder.decode(link.getHref(), "UTF-8");
         href = href.replace("[name]", name);
         URL url = new URL(href);
         connection = Utils.getConnection(url);
         connection.setRequestMethod(HTTPMethod.POST);
         connection.setRequestProperty(HTTPHeader.CONTENT_TYPE, mimeType);
         connection.setDoOutput(true);
         OutputStream out = connection.getOutputStream();
         out.write(content.getBytes());
         out.close();
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
         connection = Utils.getConnection(url);
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
         connection = Utils.getConnection(url);
         connection.setRequestMethod("POST");
         JsonParser parser = new JsonParser();
         parser.parse(connection.getInputStream());
         connection.getInputStream().close();
         Field field = VirtualFileSystemUtils.class.getDeclaredField("rootLinks");

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
         connection = Utils.getConnection(url);
         connection.setRequestMethod("GET");
         JsonParser parser = new JsonParser();
         parser.parse(connection.getInputStream());
         connection.getInputStream().close();
         vfsInfo = ObjectBuilder.createObject(VirtualFileSystemInfo.class, parser.getJsonObject());
         JsonValue element = parser.getJsonObject().getElement("root").getElement("links");

         Field field = VirtualFileSystemUtils.class.getDeclaredField("rootLinks");
         rootLinks = ObjectBuilder.createObject(Map.class, (ParameterizedType)field.getGenericType(), element);
         System.out.println("VFS initialized - " + vfsInfo.getId());
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
}
