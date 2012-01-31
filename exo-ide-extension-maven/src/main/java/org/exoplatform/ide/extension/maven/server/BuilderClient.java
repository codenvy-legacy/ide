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
package org.exoplatform.ide.extension.maven.server;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;

import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class BuilderClient
{
   private final String baseURL;

   public BuilderClient(InitParams initParams)
   {
      this(readValueParam(initParams, "build-server-base-url", null));
   }

   private static String readValueParam(InitParams initParams, String paramName, String defaultValue)
   {
      if (initParams != null)
      {
         ValueParam vp = initParams.getValueParam(paramName);
         if (vp != null)
         {
            return vp.getValue();
         }
      }
      return defaultValue;
   }

   protected BuilderClient(String baseURL)
   {
      if (baseURL == null || baseURL.isEmpty())
      {
         throw new IllegalArgumentException("Base URL of build server may not be null or empty string. ");
      }
      this.baseURL = baseURL;
   }

   public URI build(String remoteURI) throws IOException, BuilderException
   {
      URL url = new URL(baseURL + "/builder/maven/build?remoteuri=" + remoteURI);
      HttpURLConnection http = null;
      try
      {
         http = (HttpURLConnection)url.openConnection();
         http.setRequestMethod("GET");
         authenticate(http);
         int responseCode = http.getResponseCode();
         if (responseCode != 202) // 202 (Accepted) response is expected.
         {
            fail(http);
         }
         return URI.create(http.getHeaderField("location"));
      }
      finally
      {
         if (http != null)
         {
            http.disconnect();
         }
      }
   }

   public String status(String buildID) throws IOException, BuilderException
   {
      URL url = new URL(baseURL + "/builder/maven/status/" + buildID);
      HttpURLConnection http = null;
      try
      {
         http = (HttpURLConnection)url.openConnection();
         http.setRequestMethod("GET");
         authenticate(http);
         int responseCode = http.getResponseCode();
         if (responseCode != 200)
         {
            fail(http);
         }

         InputStream data = http.getInputStream();
         try
         {
            return readBody(data, http.getContentLength());
         }
         finally
         {
            data.close();
         }
      }
      finally
      {
         if (http != null)
         {
            http.disconnect();
         }
      }
   }

   public void cancel(String buildID) throws IOException, BuilderException
   {
      URL url = new URL(baseURL + "/builder/maven/cancel/" + buildID);
      HttpURLConnection http = null;
      try
      {
         http = (HttpURLConnection)url.openConnection();
         http.setRequestMethod("GET");
         authenticate(http);
         int responseCode = http.getResponseCode();
         if (responseCode != 204)
         {
            fail(http);
         }
      }
      finally
      {
         if (http != null)
         {
            http.disconnect();
         }
      }
   }

   public InputStream log(String buildID) throws IOException, BuilderException
   {
      // Download build output.
      return doDownload(baseURL + "/builder/maven/log/" + buildID);
   }

   public InputStream download(String buildID) throws IOException, BuilderException
   {
      // Download artifact.
      return doDownload(baseURL + "/builder/maven/download/" + buildID);
   }

   private InputStream doDownload(String downloadURL) throws IOException, BuilderException
   {
      URL url = new URL(downloadURL);
      HttpURLConnection http = null;
      try
      {
         http = (HttpURLConnection)url.openConnection();
         http.setRequestMethod("GET");
         authenticate(http);
         int responseCode = http.getResponseCode();
         if (responseCode != 200)
         {
            fail(http);
         }
         // Connection closed automatically when input stream closed.
         // If IOException or BuilderException occurs then connection closed immediately.
         return new HttpStream(http);
      }
      catch (IOException ioe)
      {
         if (http != null)
         {
            http.disconnect();
         }
         throw ioe;
      }
      catch (BuilderException be)
      {
         http.disconnect();
         throw be;
      }
   }

   protected void authenticate(HttpURLConnection http) throws IOException
   {
   }

   private void fail(HttpURLConnection http) throws IOException, BuilderException
   {
      InputStream errorStream = null;
      try
      {
         int responseCode = http.getResponseCode();
         int length = http.getContentLength();
         errorStream = http.getErrorStream();
         String body = null;
         if (errorStream != null)
         {
            body = readBody(errorStream, length);
         }
         throw new BuilderException(responseCode, body, body != null ? http.getContentType() : null);
      }
      finally
      {
         if (errorStream != null)
         {
            errorStream.close();
         }
      }
   }

   private String readBody(InputStream input, int contentLength) throws IOException
   {
      String body = null;
      if (contentLength > 0)
      {
         byte[] b = new byte[contentLength];
         int off = 0;
         int i;
         while ((i = input.read(b, off, contentLength - off)) > 0)
         {
            off += i;
         }
         body = new String(b);
      }
      else if (contentLength < 0)
      {
         ByteArrayOutputStream bout = new ByteArrayOutputStream();
         byte[] buf = new byte[1024];
         int i;
         while ((i = input.read(buf)) != -1)
         {
            bout.write(buf, 0, i);
         }
         body = bout.toString();
      }
      return body;
   }

   private static class HttpStream extends FilterInputStream
   {
      private final HttpURLConnection http;
      private boolean closed;

      private HttpStream(HttpURLConnection http) throws IOException
      {
         super(http.getInputStream());
         this.http = http;
      }

      @Override
      public int read() throws IOException
      {
         int r = super.read();
         if (r == -1)
         {
            close();
         }
         return r;
      }

      @Override
      public int read(byte[] b) throws IOException
      {
         int r = super.read(b);
         if (r == -1)
         {
            close();
         }
         return r;
      }

      @Override
      public int read(byte[] b, int off, int len) throws IOException
      {
         int r = super.read(b, off, len);
         if (r == -1)
         {
            close();
         }
         return r;
      }

      @Override
      public void close() throws IOException
      {
         if (closed)
         {
            return;
         }
         try
         {
            super.close();
         }
         finally
         {
            http.disconnect();
            closed = true;
         }
      }
   }
}
