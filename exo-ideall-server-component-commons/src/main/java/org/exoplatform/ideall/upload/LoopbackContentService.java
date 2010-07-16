/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */

package org.exoplatform.ideall.upload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.fileupload.FileItem;
import org.exoplatform.services.rest.resource.ResourceContainer;

/**
 * Simple REST service which returns the request body wraped in javascript.
 * Created by The eXo Platform SAS
 * 
 * @author <a href="work.visor.ck@gmail.com">Dmytro Katayev</a> ${date}
 */

@Path("/services/loopbackcontent")
public class LoopbackContentService implements ResourceContainer {

  /**
   * POST method that gets the request body and returns it wrapped in the
   * JavaScript.
   * 
   * @param items file items form the request body.
   * @return the request body content wrapped with JavaScript.
   */
  @POST
  @Consumes( { "multipart/*" })
  public Response post(Iterator<FileItem> items) {
    InputStream stream = null;
    while (items.hasNext()) {
      FileItem fitem = items.next();
      if (!fitem.isFormField()) {
        try {
          stream = fitem.getInputStream();
        } catch (IOException ioe) {
          ioe.printStackTrace();
          return Response.serverError().build();
        }
      }
    }

    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    StringBuilder sb = new StringBuilder();

    String line = null;

    try {
      while ((line = reader.readLine()) != null) {
        String str = URLEncoder.encode(line + "\n", "UTF-8");
        sb.append(str);
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
      return Response.serverError().build();
    }

    String bodyString = sb.toString();

    String responceString = "<pre>" + bodyString + "</pre>";

    return Response.ok(responceString, MediaType.TEXT_HTML).build();
  }

}
