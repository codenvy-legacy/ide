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
package org.exoplatform.ide.zip;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.exoplatform.ide.download.NodeTypeUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

/**
 * Manipulates with zip files: unzip folder and package folder as zip.
 * 
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Dec 15, 2010 $
 *
 */
public class ZipUtils
{
   /**
    * Writing packed folder content to output.
    * 
    * @see javax.ws.rs.core.StreamingOutput#write(java.io.OutputStream)
    * @param nodeToPack - the node to pack
    * @throws IOException
    */
   public static void writeZip(OutputStream outputStream, Node nodeToPack) throws IOException
   {
      String pathPrefix = null;
      ZipArchiveOutputStream zipOut = new ZipArchiveOutputStream(outputStream);
      zipOut.setEncoding("UTF-8");
      
      try
      {
         pathPrefix = nodeToPack.getPath();
         pathPrefix = pathPrefix.substring(0, pathPrefix.length() - nodeToPack.getName().length());

         zipNodeContent(nodeToPack, zipOut, pathPrefix);
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
    * @param node node to get content
    * @param zipOutputStream output stream
    * @throws RepositoryException
    * @throws IOException
    */
   private static void zipNodeContent(Node node, ZipArchiveOutputStream zipOutputStream, String pathPrefix) throws RepositoryException, IOException
   {
      String path = node.getPath();
      path = path.substring(pathPrefix.length());

      if (NodeTypeUtil.isFile(node))
      {
         InputStream inputStream = node.getNode(NodeTypeUtil.JCR_CONTENT).getProperty(NodeTypeUtil.JCR_DATA).getStream();

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
            zipNodeContent(child, zipOutputStream, pathPrefix);
         }
      }

   }

   /**
    * Writing jcr:data property value to zip output.
    * 
    * @param inputStream input stream
    * @param zipOutputStream output stream
    * @throws IOException
    */
   private static void flushJCRData(InputStream inputStream, ZipArchiveOutputStream zipOutputStream) throws IOException
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
