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
package org.exoplatform.ide.extension.java.server;

import org.exoplatform.commons.utils.MimeTypeResolver;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Project;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Provide exporting resources via VirtailFileSystem.
 * For example copy template of project.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */
public class JavaProjectArchetype
{


   private MimeTypeResolver mimeTypeResolver;

   private Templates transfomRules;

   public JavaProjectArchetype()
   {
      mimeTypeResolver = new MimeTypeResolver();
   }

   /**
    * @param url
    * @param projectType
    * @param projectName
    * @param groupId
    * @param artifactId
    * @param version
    * @param parentId
    * @param vfs
    * @throws IOException
    * @throws URISyntaxException
    * @throws ItemNotFoundException
    * @throws InvalidArgumentException
    * @throws PermissionDeniedException
    * @throws VirtualFileSystemException
    */
   public void exportResources(URL url, String projectType, String projectName, String groupId, String artifactId,
      String version, String parentId, VirtualFileSystem vfs) throws IOException, URISyntaxException,
      ItemNotFoundException, InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      File res = new File(url.toURI());
      Response response = vfs.createProject(parentId, projectName, projectType, null);
      Project project = (Project)response.getEntity();
      File[] files = res.listFiles();
      for (int i = 0; i < files.length; i++)
      {
         export(project.getId(), files[i], groupId, artifactId, version, projectName, vfs);
      }

   }

   /**
    * @param parentId
    * @param file
    * @param groupId
    * @param artifactId
    * @param version
    * @param projectName
    * @param vfs
    * @throws ItemNotFoundException
    * @throws InvalidArgumentException
    * @throws PermissionDeniedException
    * @throws VirtualFileSystemException
    * @throws IOException
    */
   private void export(String parentId, File file, String groupId, String artifactId, String version, String projectName, VirtualFileSystem vfs)
      throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException,
      IOException
   {
      if (file.isDirectory())
      {
         Response response = vfs.createFolder(parentId, file.getName());
         if (response.getEntity() instanceof Folder)
         {
            Folder folder = (Folder)response.getEntity();
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++)
            {
               export(folder.getId(), files[i], groupId, artifactId, version, projectName, vfs);
            }
         }
      }
      else
      {
         if ("pom.xml".equals(file.getName()))
            vfs.createFile(parentId, file.getName(), MediaType.TEXT_XML_TYPE,
               transformPomXml(file, groupId, artifactId, version, projectName));
         else
            vfs.createFile(parentId, file.getName(), MediaType.valueOf(mimeTypeResolver.getMimeType(file.getName())),
               new FileInputStream(file));
      }

   }

   /**
    * @param file
    * @param groupId
    * @param artifactId
    * @param version
    * @param projectName
    * @return
    * @throws IOException
    */
   private InputStream transformPomXml(File file, String groupId, String artifactId, String version, String projectName)
      throws IOException
   {
      if (transfomRules == null)
         loadTransformRules(); // Load XSLT transformation rules.  
      try
      {
         Transformer tr = transfomRules.newTransformer();
         tr.setParameter("groupId", groupId);
         tr.setParameter("artifactId", artifactId);
         tr.setParameter("version", version);
         tr.setParameter("name", projectName);
         StringWriter output = new StringWriter();
         tr.transform(new StreamSource(file), new StreamResult(output));
         return new ByteArrayInputStream(output.toString().getBytes());
      }
      catch (TransformerConfigurationException e)
      {
         throw new RuntimeException(e.getMessage(), e);
      }
      catch (TransformerException e)
      {
         throw new RuntimeException(e.getMessage(), e);
      }
   }

   /**
    * 
    */
   private void loadTransformRules()
   {
      if (transfomRules != null)
         return;
      synchronized (this)
      {
         if (transfomRules != null)
            return;
         try
         {
            final String fileName = "pom.xslt";
            InputStream xsltSource = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            if (xsltSource == null)
               throw new RuntimeException("File " + fileName + " not found.");
            transfomRules = TransformerFactory.newInstance().newTemplates(new StreamSource(xsltSource));
         }
         catch (TransformerConfigurationException e)
         {
            throw new RuntimeException(e.getMessage(), e);
         }
         catch (TransformerFactoryConfigurationError e)
         {
            throw new RuntimeException(e.getMessage(), e);
         }
      }
   }
}
