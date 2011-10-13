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
package org.exoplatform.ide.extension.ruby.server;

import org.exoplatform.commons.utils.MimeTypeResolver;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.ws.rs.core.MediaType;

/**
 * Provide exporting resources via VirtailFileSystem. For example copy template of project.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */
public class RubyProjectArchetype
{

   private MimeTypeResolver mimeTypeResolver;

   public RubyProjectArchetype()
   {
      mimeTypeResolver = new MimeTypeResolver();
   }

   /**
    * @param url
    * @param projectType
    * @param projectName
    * @param vfs
    * @throws IOException
    * @throws URISyntaxException
    * @throws ItemNotFoundException
    * @throws InvalidArgumentException
    * @throws PermissionDeniedException
    * @throws VirtualFileSystemException
    */
   public Project exportResources(URL url, String projectName, String projectType, String parentId, 
      VirtualFileSystem vfs) throws IOException, URISyntaxException, ItemNotFoundException, InvalidArgumentException,
      PermissionDeniedException, VirtualFileSystemException
   {
      File res = new File(url.toURI());
      Project project = vfs.createProject(parentId, projectName, projectType, null);
      File[] files = res.listFiles();
      for (int i = 0; i < files.length; i++)
      {
         export(project.getId(), files[i], projectName, vfs);
      }
      return project;

   }

   /**
    * @param parentId
    * @param file
    * @param projectName
    * @param vfs
    * @throws ItemNotFoundException
    * @throws InvalidArgumentException
    * @throws PermissionDeniedException
    * @throws VirtualFileSystemException
    * @throws IOException
    */
   private void export(String parentId, File file, String projectName, VirtualFileSystem vfs)
      throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException,
      IOException
   {
      if (file.isDirectory())
      {
         Folder folder = vfs.createFolder(parentId, file.getName());
         File[] files = file.listFiles();
         for (int i = 0; i < files.length; i++)
         {
            export(folder.getId(), files[i], projectName, vfs);
         }
      }
      else
      {
         vfs.createFile(parentId, file.getName(), MediaType.valueOf(mimeTypeResolver.getMimeType(file.getName())),
            new FileInputStream(file));
      }

   }

}
