/**
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.exoplatform.ide.download;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class DirectoryContentEntity implements StreamingOutput, Const
{

   private String pathPrefix;

   private Node nodeToPack;

   public DirectoryContentEntity(Node nodeToPack)
   {
      this.nodeToPack = nodeToPack;
   }

   /**
    * Writing packed folder content to output.
    * 
    * @see javax.ws.rs.core.StreamingOutput#write(java.io.OutputStream)
    */
   public void write(OutputStream outputStream) throws IOException
   {
      ZipArchiveOutputStream zipOut = new ZipArchiveOutputStream(outputStream);
      zipOut.setEncoding("UTF-8");
      
      try
      {
         pathPrefix = nodeToPack.getPath();
         pathPrefix = pathPrefix.substring(0, pathPrefix.length() - nodeToPack.getName().length());

         zipNodeContent(nodeToPack, zipOut);
      }
      catch (RepositoryException exc)
      {
         throw new IOException(exc.getMessage());
      }

      zipOut.close();
   }

   /**
    * Recursively packing node's content.
    *  
    * 
    * @param node
    * @param zipOutputStream
    * @throws RepositoryException
    * @throws IOException
    */
   private void zipNodeContent(Node node, ZipArchiveOutputStream zipOutputStream) throws RepositoryException, IOException
   {
      String path = node.getPath();
      path = path.substring(pathPrefix.length());

      if (NodeTypeUtil.isFile(node))
      {
         InputStream inputStream = node.getNode(JCR_CONTENT).getProperty(JCR_DATA).getStream();

         ZipArchiveEntry zipEntry = new ZipArchiveEntry(path);
         zipOutputStream.putArchiveEntry(zipEntry);
         flushJCRData(inputStream, zipOutputStream);

         zipOutputStream.closeArchiveEntry();
      }
      else
      {
         if (!"/".equals(node.getPath()))
         {
            path += "/";

            ZipArchiveEntry zipEntry = new ZipArchiveEntry(path);
            zipOutputStream.putArchiveEntry(zipEntry);
            zipOutputStream.closeArchiveEntry();
         }

         NodeIterator nodeIterator = node.getNodes();
         while (nodeIterator.hasNext())
         {
            Node child = nodeIterator.nextNode();
            zipNodeContent(child, zipOutputStream);
         }
      }

   }

   /**
    * Writing jcr:data property value to zip output.
    * 
    * @param inputStream
    * @param zipOutputStream
    * @throws IOException
    */
   private void flushJCRData(InputStream inputStream, ZipArchiveOutputStream zipOutputStream) throws IOException
   {
      byte[] buffer = new byte[4096];
      while (true)
      {
         int readed = inputStream.read(buffer);
         if (readed < 0)
         {
            break;
         }
         zipOutputStream.write(buffer, 0, readed);
      }
   }

}
