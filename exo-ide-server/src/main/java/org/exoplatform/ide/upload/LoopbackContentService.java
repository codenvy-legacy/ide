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
package org.exoplatform.ide.upload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.fileupload.FileItem;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * Uses for receiving the content of local file through server.
 * 
 * Created by The eXo Platform SAS
 * 
 * @author <a href="work.visor.ck@gmail.com">Dmytro Katayev</a> ${date}
 */

@Path("/ide/loopbackcontent")
public class LoopbackContentService
{

   private static Log log = ExoLogger.getLogger(LoopbackContentService.class);

   /**
    * POST method that gets the request body and returns it wrapped in the JavaScript.
    * 
    * @param items file items form the request body.
    * @return the request body content wrapped with JavaScript.
    * @throws UploadServiceException
    */
   @POST
   @Consumes({"multipart/*"})
   @Produces(MediaType.TEXT_HTML)
   public String post(Iterator<FileItem> items) throws UploadServiceException
   {
      InputStream stream = null;
      while (items.hasNext())
      {
         FileItem fitem = items.next();
         if (!fitem.isFormField())
         {
            try
            {
               stream = fitem.getInputStream();
            }
            catch (IOException ioe)
            {
               log.error(ioe.getMessage(), ioe);
               throw new UploadServiceException(ioe.getMessage());
            }
         }
      }

      BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
      StringBuilder sb = new StringBuilder();

      String line = null;

      try
      {
         while ((line = reader.readLine()) != null)
         {
            String str = URLEncoder.encode(line + "\n", "UTF-8");
            sb.append(str);
         }
      }
      catch (IOException ioe)
      {
         log.error(ioe.getMessage(), ioe);
         throw new UploadServiceException(ioe.getMessage());
      }

      String bodyString = sb.toString();

      return "<filecontent>" + bodyString + "</filecontent>";
   }

}
