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
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Client to remote build server.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class BuilderClient
{
   public static final String BUILD_SERVER_BASE_URL = "exo.ide.builder.build-server-base-url";

   private final String baseURL;

   public BuilderClient(InitParams initParams)
   {
      this(readValueParam(initParams, "build-server-base-url", System.getProperty(BUILD_SERVER_BASE_URL)));
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

   /**
    * Send request to start collect list of dependencies. Process may be started immediately or add in queue.
    *
    * @param vfs
    *    virtual file system
    * @param projectId
    *    identifier of project we want to send for collect dependencies
    * @return ID of build task. It may be used as parameter for method {@link #status(String)} .
    * @throws IOException
    *    if any i/o errors occur
    * @throws BuilderException
    *    if build request was rejected by remote build server
    * @throws VirtualFileSystemException
    *    if any error in VFS
    */
   public String dependenciesList(VirtualFileSystem vfs, String projectId) throws IOException, BuilderException,
      VirtualFileSystemException
   {
      URL url = new URL(baseURL + "/builder/maven/dependencies/list");
      return run(url, vfs.exportZip(projectId));
   }

   /**
    * Send request to start collect project dependencies and add them in zip archive. Process may be started
    * immediately
    * or add in queue.
    *
    * @param vfs
    *    virtual file system
    * @param projectId
    *    identifier of project we want to send for collect dependencies
    * @param classifier
    *    classifier to look for, e.g. : sources. May be <code>null</code>.
    * @return ID of build task. It may be used as parameter for method {@link #status(String)} .
    * @throws IOException
    *    if any i/o errors occur
    * @throws BuilderException
    *    if build request was rejected by remote build server
    * @throws VirtualFileSystemException
    *    if any error in VFS
    */
   public String dependenciesCopy(VirtualFileSystem vfs, String projectId, String classifier) throws IOException,
      BuilderException, VirtualFileSystemException
   {
      String url = baseURL + "/builder/maven/dependencies/copy";
      if (!(classifier == null || classifier.isEmpty()))
      {
         url += "?classifier=" + classifier;
      }
      return run(new URL(url), vfs.exportZip(projectId));
   }

   /**
    * Send request to start new build at remote build server. Build may be started immediately or add in queue.
    *
    * @param vfs
    *    virtual file system
    * @param projectId
    *    identifier of project we want to send for build
    * @return ID of build task. It may be used as parameter for method {@link #status(String)} .
    * @throws IOException
    *    if any i/o errors occur
    * @throws BuilderException
    *    if build request was rejected by remote build server
    * @throws VirtualFileSystemException
    *    if any error in VFS
    */
   public String build(VirtualFileSystem vfs, String projectId) throws IOException, BuilderException,
      VirtualFileSystemException
   {
      URL url = new URL(baseURL + "/builder/maven/build");
      return run(url, vfs.exportZip(projectId));
   }

   private String run(URL url, ContentStream zippedProject) throws IOException, BuilderException,
      VirtualFileSystemException
   {
      HttpURLConnection http = null;
      try
      {
         http = (HttpURLConnection)url.openConnection();
         http.setRequestMethod("POST");
         http.setRequestProperty("content-type", zippedProject.getMimeType());
         authenticate(http);
         http.setDoOutput(true);
         byte[] buff = new byte[8192];
         InputStream data = null;
         OutputStream out = null;
         try
         {
            data = zippedProject.getStream();
            out = http.getOutputStream();
            int r;
            while ((r = data.read(buff)) != -1)
            {
               out.write(buff, 0, r);
            }
         }
         finally
         {
            if (data != null)
            {
               data.close();
            }
            if (out != null)
            {
               out.close();
            }
         }
         int responseCode = http.getResponseCode();
         if (responseCode != 202) // 202 (Accepted) response is expected.
         {
            fail(http);
         }
         String location = http.getHeaderField("location");
         return location.substring(location.lastIndexOf('/') + 1);
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
    * Check status of build.
    *
    * @param buildID
    *    ID of build need to check
    * @return string that contains description of current status of build in JSON format. Do nothing with such string
    *         just re-send result to client
    * @throws IOException
    *    if any i/o errors occur
    * @throws BuilderException
    *    any other errors related to build server internal state or parameter of client request
    */
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

   /**
    * Cancel build.
    *
    * @param buildID
    *    ID of build to be canceled
    * @throws IOException
    *    if any i/o errors occur
    * @throws BuilderException
    *    any other errors related to build server internal state or parameter of client request
    */
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

   /**
    * Read log of build.
    *
    * @param buildID
    *    ID of build
    * @return stream that contains build log
    * @throws IOException
    *    if any i/o errors occur
    * @throws BuilderException
    *    any other errors related to build server internal state or parameter of client request
    */
   public InputStream log(String buildID) throws IOException, BuilderException
   {
      // Download build output.
      return doDownload(baseURL + "/builder/maven/log/" + buildID);
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

   /**
    * Check is URL for download artifact is valid. Artifact may be removed by timeout but GWT client not able to check
    * it because to cross-domain restriction for ajax requests.
    *
    * @param url
    *    URL for checking
    * @throws IOException
    *    if any i/o errors occur
    * @throws BuilderException
    *    URL is not valid or any other errors related to build server internal state
    */
   public void checkDownloadURL(String url) throws IOException, BuilderException
   {
      URL checkURL = new URL(url);
      HttpURLConnection http = null;
      try
      {
         http = (HttpURLConnection)checkURL.openConnection();
         http.setRequestMethod("HEAD"); // don't want to download artifact
         authenticate(http);
         int responseCode = http.getResponseCode();
         if (responseCode != 200)
         {
            // no response body.
            throw new BuilderException(responseCode, "", "text/plain");
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

   /**
    * Add authentication info to the request. By default do nothing. May be reimplemented for particular authentication
    * scheme.
    *
    * @param http
    *    HTTP connection to add authentication info, e.g. Basic authentication headers.
    * @throws IOException
    *    if any i/o errors occur
    */
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

   /** Stream that automatically close HTTP connection when all data ends. */
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
