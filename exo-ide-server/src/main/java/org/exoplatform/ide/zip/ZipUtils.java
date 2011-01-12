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
import org.exoplatform.commons.utils.MimeTypeResolver;
import org.exoplatform.ide.download.NodeTypeUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

/**
 * Manipulates with zip files: unzip folder and package folder as zip.
 * 
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Dec 15, 2010 $
 *
 */
public class ZipUtils
{
   private static final String DEFAULT_FILE_NODE_TYPE = "nt:file";
   
   public static final String DEFAULT_JCR_CONTENT_NODE_TYPE = "nt:resource";
   
   /**
    * Unzip folder and creates structure of folders and files.
    * 
    * @param session - the session
    * @param inputStream - input stream of zipped file
    * @param parentFolderPath - path to parent folder. If archive will be unziped in root folder of 
    * workspace, that parentFolderPath is null
    * 
    * @throws AccessDeniedException
    * @throws ItemExistsException
    * @throws ConstraintViolationException
    * @throws InvalidItemStateException
    * @throws VersionException
    * @throws LockException
    * @throws NoSuchNodeTypeException
    * @throws RepositoryException
    * @throws IOException
    */
   public static void unzip(Session session, InputStream inputStream, String parentFolderPath)
      throws AccessDeniedException, ItemExistsException, ConstraintViolationException, InvalidItemStateException,
      VersionException, LockException, NoSuchNodeTypeException, RepositoryException, IOException, IllegalArgumentException
   {
      byte[] buf = new byte[1024];

      ZipInputStream zin = new ZipInputStream(inputStream);

      ZipEntry zipentry;

      zipentry = zin.getNextEntry();
      if (zipentry == null)
         throw new IllegalArgumentException("Zip archive is empty");
      
      while (zipentry != null)
      {
         //for each entry to be extracted
         String entryName = zipentry.getName();
         if (zipentry.isDirectory())
         {
            putFolder(session, parentFolderPath, entryName);
         }
         else
         {
            int bytesRead;
            ByteArrayOutputStream outS = new ByteArrayOutputStream();

            while ((bytesRead = zin.read(buf, 0, 1024)) > -1)
            {
               outS.write(buf, 0, bytesRead);
            }

            ByteArrayInputStream data = new ByteArrayInputStream(outS.toByteArray());
            outS.close();

            MimeTypeResolver resolver = new MimeTypeResolver();
            putFile(session, parentFolderPath, entryName, data, resolver.getMimeType(entryName), null, null);

         }
         zin.closeEntry();
         zipentry = zin.getNextEntry();

      }//while

      zin.close();

      session.save();
   }
   
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
   
   /**
    * Creates new folder node.
    * 
    * @param session - the session 
    * @param parentFolderPath - path to the parent node
    * @param folderPath - path to the new folder node (from parent node)
    * 
    * @throws ItemExistsException
    * @throws PathNotFoundException
    * @throws NoSuchNodeTypeException
    * @throws LockException
    * @throws VersionException
    * @throws ConstraintViolationException
    * @throws RepositoryException
    */
   private static void putFolder(Session session, String parentFolderPath, String folderPath) throws ItemExistsException,
      PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException, ConstraintViolationException,
      RepositoryException
   {
      Node base;
      if (parentFolderPath != null)
      {
         base = session.getRootNode().getNode(parentFolderPath);
      }
      else
      {
         base = session.getRootNode();
      }

      base.addNode(folderPath, "nt:folder");

   }

   /**
    * Creates new file node.
    * 
    * @param session - the session
    * @param resourcePath - path to parent node.
    * @param filePath - path to new file (from parent node)
    * @param data - file's data
    * @param mimeType - mime type of file
    * @param fileNodeType - file node type
    * @param jcrContentNodeType - jcr:content node type
    * 
    * @throws PathNotFoundException
    * @throws RepositoryException
    */
   public static void putFile(Session session, String resourcePath, String filePath, InputStream data, String mimeType,
      String fileNodeType, String jcrContentNodeType)
      throws PathNotFoundException, RepositoryException
   {
      Node base;
      if (resourcePath != null)
      {
         base = session.getRootNode().getNode(resourcePath);
      }
      else
      {
         base = session.getRootNode();
      }
      
      if (fileNodeType == null)
      {
         fileNodeType = DEFAULT_FILE_NODE_TYPE;
      }
      
      if (jcrContentNodeType == null)
      {
         jcrContentNodeType = DEFAULT_JCR_CONTENT_NODE_TYPE;
      }
      
      base = base.addNode(filePath, fileNodeType);
      base = base.addNode("jcr:content", jcrContentNodeType);
      base.setProperty("jcr:data", data);
      base.setProperty("jcr:lastModified", Calendar.getInstance());
      base.setProperty("jcr:mimeType", mimeType);
   }

}
