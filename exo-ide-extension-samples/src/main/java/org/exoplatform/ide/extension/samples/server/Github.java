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

import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonValue;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.ide.extension.samples.shared.Repository;
import org.exoplatform.ide.helper.JsonHelper;
import org.exoplatform.ide.helper.ParsingResponseException;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Github.java Sep 5, 2011 12:08:04 PM vereshchaka $
 */
public class Github
{
   private String userName;

   public Github(InitParams initParams)
   {
      this(readValueParam(initParams, "github-user"));
   }

   public Github(String userName)
   {
      this.userName = userName;
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
    * Get the list of repositories.
    * 
    * @return an array of repositories.
    * @throws IOException if any i/o errors occurs
    * @throws GithubException if github server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    */
   public Repository[] listRepositories() throws IOException, GithubException, ParsingResponseException
   {
      return listRepositories(this.userName);
   }

   /**
    * Get the list of repositories for User Name.
    * 
    * @param userName name of user
    * @return an array of repositories.
    * @throws IOException if any i/o errors occurs
    * @throws GithubException if github server return unexpected or error status for request
    * @throws ParsingResponseException if any error occurs when parse response body
    */
   public Repository[] listRepositories(String userName) throws IOException, GithubException, ParsingResponseException
   {
      String url = "http://github.com/api/v2/json/repos/show/" + userName;
      String method = "GET";
      String response = doJsonRequest(url, method, null, 200);
      JsonValue repositoryJsValue = JsonHelper.parseJson(response);
      JsonValue reposArray = repositoryJsValue.getElement("repositories");
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
   private String doJsonRequest(String url, String method, String body, int success) throws IOException,
      GithubException
   {
      HttpURLConnection http = null;
      try
      {
         http = (HttpURLConnection)new URL(url).openConnection();
         http.setInstanceFollowRedirects(false);
         http.setRequestMethod(method);
         http.setRequestProperty("Accept", "application/json");
         if (body != null && body.length() > 0)
         {
            http.setRequestProperty("Content-type", "application/json");
            http.setDoOutput(true);
            BufferedWriter writer = null;
            try
            {
               writer = new BufferedWriter(new OutputStreamWriter(http.getOutputStream()));
               writer.write(body);
            }
            finally
            {
               if (writer != null)
                  writer.close();
            }
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
         // System.err.println("fault : " + responseCode);
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

}
