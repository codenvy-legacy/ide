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
import org.everrest.http.client.HTTPConnection;
import org.everrest.http.client.HTTPResponse;
import org.everrest.http.client.ModuleException;
import org.everrest.http.client.NVPair;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class VirtualFileSystemUtils
{

   public static int put(String filePath, String mimeType, String contentNodeType, String storageUrl)
      throws IOException, ModuleException
   {
      URL url = new URL(storageUrl);
      HTTPConnection connection = Utils.getConnection(url);
      String data = Utils.readFileAsString(filePath);
      NVPair[] headers = new NVPair[3];
      headers[0] = new NVPair(HTTPHeader.CONTENT_TYPE, mimeType);
      headers[1] = new NVPair(HTTPHeader.CONTENT_LENGTH, String.valueOf(data.length()));
      headers[2] = new NVPair(HTTPHeader.CONTENT_NODETYPE, contentNodeType);
      HTTPResponse response = connection.Put(url.getFile(), data, headers);
      return response.getStatusCode();
   }

   public static int put(byte[] data, String mimeType, String contentNodeType, String storageUrl) throws IOException,
      ModuleException
   {
      URL url = new URL(storageUrl);
      HTTPConnection connection = Utils.getConnection(url);
      NVPair[] headers = new NVPair[3];
      headers[0] = new NVPair(HTTPHeader.CONTENT_TYPE, mimeType);
      headers[1] = new NVPair(HTTPHeader.CONTENT_LENGTH, String.valueOf(data.length));
      headers[2] = new NVPair(HTTPHeader.CONTENT_NODETYPE, contentNodeType);
      HTTPResponse response = connection.Put(url.getFile(), data, headers);
      return response.getStatusCode();
   }

   public static int put(byte[] data, String storageUrl) throws IOException, ModuleException
   {
      URL url = new URL(storageUrl);
      HTTPConnection connection = Utils.getConnection(url);
      NVPair[] headers = new NVPair[1];
      headers[0] = new NVPair(HTTPHeader.CONTENT_TYPE, "application/xml");
      HTTPResponse response = connection.Put(url.getFile(), data, headers);
      return response.getStatusCode();

   }

   /**
    * @param filePath
    * @param mimeType
    * @param storageUrl
    * @return HTTPStatus code
    * @throws IOException
    * @throws ModuleException
    */
   public static int put(byte[] data, String mimeType, String storageUrl) throws IOException, ModuleException
   {
      return put(data, mimeType, TestConstants.NodeTypes.NT_RESOURCE, storageUrl);
   }

   /**
    * @param filePath
    * @param mimeType
    * @param storageUrl
    * @return HTTPStatus code
    * @throws IOException
    * @throws ModuleException
    */
   public static int put(String filePath, String mimeType, String storageUrl) throws IOException, ModuleException
   {
      return put(filePath, mimeType, TestConstants.NodeTypes.NT_RESOURCE, storageUrl);
   }

   /**
    * @param storageUrl
    * @return HTTPStatus code
    * @throws IOException
    * @throws ModuleException
    */
   public static int delete(String storageUrl) throws IOException, ModuleException
   {
      URL url = new URL(storageUrl);
      HTTPConnection connection = Utils.getConnection(url);
      HTTPResponse response = connection.Delete(url.getFile());
      return response.getStatusCode();
   }

   /**
    * @param storageUrl
    * @return HTTPStatus code
    * @throws IOException
    * @throws ModuleException
    */
   public static HTTPResponse get(String storageUrl) throws IOException, ModuleException
   {
      System.out.println("GET on > " + storageUrl);
      URL url = new URL(storageUrl);
      HTTPConnection connection = Utils.getConnection(url);
      HTTPResponse response = connection.Get(url.getFile());
      return response;
   }

   /**
    * @param storageUrl
    * @return HTTPStatus code
    * @throws IOException
    * @throws ModuleException
    */
   public static int mkcol(String storageUrl) throws IOException, ModuleException
   {
      URL url = new URL(storageUrl);
      HTTPConnection connection = Utils.getConnection(url);
      HTTPResponse response = connection.MkCol(url.getFile());
      return response.getStatusCode();
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
}
