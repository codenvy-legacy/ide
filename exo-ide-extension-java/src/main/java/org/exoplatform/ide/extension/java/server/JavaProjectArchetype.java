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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */
public class JavaProjectArchetype
{

   private final String parentId;

   /**
    * Project's name.
    */
   private final String projectName;

   /**
    * Project's group ID.
    */
   private final String groupId;

   /**
    * Project's artifact ID.
    */
   private final String artifactId;

   /**
    * Project's version.
    */
   private final String version;

   private final VirtualFileSystem vfs;
   
   private MimeTypeResolver mimeTypeResolver;

   public JavaProjectArchetype(String projectName, String groupId, String artifactId, String version, String parentId,
      VirtualFileSystem vfs)
   {
      this.parentId = parentId;
      this.projectName = projectName;
      this.groupId = groupId;
      this.artifactId = artifactId;
      this.version = version;
      this.vfs = vfs;
      mimeTypeResolver = new MimeTypeResolver();
   }

   public void exportResources(URL url) throws IOException, URISyntaxException, ItemNotFoundException,
      InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      File res = new File(url.toURI());
      Response response = vfs.createProject(parentId, projectName, projectName, null);
      Project project = (Project)response.getEntity();
      File[] files = res.listFiles();
      for (int i = 0; i < files.length; i++)
      {
         exportFile(project.getId(), files[i]);
      }

   }

   private void exportFile(String parentId, File file) throws ItemNotFoundException, InvalidArgumentException,
      PermissionDeniedException, VirtualFileSystemException, IOException
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
               exportFile(folder.getId(), files[i]);
            }
         }
      }
      
      else
      {
         if ("pom.xml".equals(file.getName()))
            vfs.createFile(parentId, file.getName(), MediaType.TEXT_XML_TYPE, copyPomXML(new FileInputStream(file)));
         else
            vfs.createFile(parentId, file.getName(), MediaType.valueOf(mimeTypeResolver.getMimeType(file.getName())), new FileInputStream(file));
      }

   }


   private InputStream copyPomXML(InputStream is) throws IOException
   {
      StringBuilder buffer = new StringBuilder(is.available());
      InputStreamReader streamReader = new InputStreamReader(is);
      BufferedReader reader = new BufferedReader(streamReader);

      String line = reader.readLine();
      while (line != null)
      {
         // replacing ${name} ${groupId}, ${artifactId}, ${version}
         while (line.indexOf("${name}") >= 0)
         {
            line = line.replace("${name}", projectName);
         }

         while (line.indexOf("${groupId}") >= 0)
         {
            line = line.replace("${groupId}", groupId);
         }

         while (line.indexOf("${artifactId}") >= 0)
         {
            line = line.replace("${artifactId}", artifactId);
         }

         while (line.indexOf("${version}") >= 0)
         {
            line = line.replace("${version}", version);
         }

         buffer.append(line + "\n");

         line = reader.readLine();
      }
      is.close();
      return new ByteArrayInputStream(buffer.toString().getBytes());
   }
}
