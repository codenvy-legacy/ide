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

import static org.apache.commons.codec.binary.Base64.encodeBase64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class HttpChunkReader
{
   private static final byte[] NO_DATA = new byte[0];
   private URL nextChunk;

   public HttpChunkReader(URL firstChunk)
   {
      this.nextChunk = firstChunk;
   }

   /**
    * @return <code>true</code> if end of file reached <code>false</code> otherwise
    */
   public boolean eof()
   {
      return nextChunk == null;
   }

   /**
    * Read next portion of data.
    * 
    * @return set of bytes from heroku server or empty array if data not ready yet. If empty array returned then caller
    *         should check method {@link #eof()} and try again if end of file is not reached yet
    * @throws IOException if any i/o error occurs
    */
   public byte[] next() throws IOException
   {
      if (eof())
         throw new IllegalStateException("End of output reached. ");

      HttpURLConnection http = null;
      try
      {
         http = (HttpURLConnection)nextChunk.openConnection();
         http.setRequestMethod("GET");

         http.setRequestProperty("Authorization",
            "Basic " + new String(encodeBase64((nextChunk.getUserInfo() + ":").getBytes("ISO-8859-1")), "ISO-8859-1"));

         int status = http.getResponseCode();

         if (!(status == 200 || status == 204))
            Heroku.fault(http);

         byte[] data = NO_DATA;

         if (status == 200)
         {
            InputStream input = http.getInputStream();
            try
            {
               final int length = http.getContentLength();
               if (length > 0)
               {
                  data = new byte[length];
                  for (int r = -1, off = 0; (r = input.read(data, off, length - off)) > 0; off += r) //
                  ;
               }
               else if (length < 0)
               {
                  byte[] buf = new byte[1024];
                  ByteArrayOutputStream bout = new ByteArrayOutputStream();
                  int point = -1;
                  while ((point = input.read(buf)) != -1)
                     bout.write(buf, 0, point);
                  data = bout.toByteArray();
               }
            }
            finally
            {
               input.close();
            }
         }

         String location = http.getHeaderField("Location");
         if (location == null && status != 204)
            nextChunk = null;
         else if (location != null)
            nextChunk = new URL(location);
         return data;
      }
      finally
      {
         /*System.out.println("status:       "
            + ((http != null) ? ("" + http.getResponseCode()) : "HTTP CONNECTION FAILED. "));
         System.out.println("nextChunk:    " + nextChunk);*/
         if (http != null)
            http.disconnect();
      }
   }
}
