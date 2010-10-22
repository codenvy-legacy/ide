/**
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
 *
 */

package org.exoplatform.ide.vfs.webdav;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.exoplatform.ide.vfs.webdav.command.propfind.PropFindResponseEntity;
import org.exoplatform.services.jcr.webdav.util.TextUtil;
import org.exoplatform.services.rest.impl.ContainerResponse;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Utils
{

   public static Node createNTFile(Session session, String path, InputStream inputStream, String nodeType,
      String contentType, String mimeType) throws RepositoryException
   {
      Node node = session.getRootNode().addNode(TextUtil.relativizePath(path), nodeType);
      node.addNode("jcr:content", contentType);
      Node content = node.getNode("jcr:content");
      content.setProperty("jcr:mimeType", mimeType);
      content.setProperty("jcr:lastModified", Calendar.getInstance());
      content.setProperty("jcr:data", inputStream);
      session.save();
      return node;
   }

   public static void printMultistatusResponse(ContainerResponse response) throws IOException
   {
      System.out.println("STATUS: " + response.getStatus());

      if (response.getEntity() instanceof PropFindResponseEntity)
      {
         printPropFindResponseEntity((PropFindResponseEntity)response.getEntity());
      }
   }

   public static void printPropFindResponseEntity(PropFindResponseEntity entity) throws IOException
   {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      entity.write(outputStream);

      String s = new String(outputStream.toByteArray());
      //s = s.replaceAll(">", ">\r\n");
      System.out.println(s);
   }
   
   public static InputStream getResponseAsStream(ContainerResponse response) throws IOException {
      if (response.getEntity() instanceof PropFindResponseEntity) {
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         ((PropFindResponseEntity)response.getEntity()).write(outputStream);
         return new ByteArrayInputStream(outputStream.toByteArray());
      }
      
      return null;
   }

}
