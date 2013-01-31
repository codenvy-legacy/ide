/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.eclipse.resources;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.codenvy.eclipse.resources.FileResource;

import com.codenvy.eclipse.core.resources.IFile;
import com.codenvy.eclipse.core.resources.IFolder;
import com.codenvy.eclipse.core.resources.IProject;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.resources.IWorkspaceRoot;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IPath;
import com.codenvy.eclipse.core.runtime.NullProgressMonitor;
import com.codenvy.eclipse.core.runtime.Path;

import org.junit.Test;

/**
 * Tests moving resources.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: MoveTest.java Jan 3, 2013 11:10:48 AM azatsarynnyy $
 */
public class MoveTest extends ResourcesBaseTest
{
   private IWorkspaceRoot workspaceRootForMove;

   private IProject projectForMove;

   private IFolder nonEmptyFolderForMove;

   private IFolder emptyFolderForMove;

   private IFile fileForMove;

   private IFile nonExistingFileForMove;

   @Override
   public void setUp() throws Exception
   {
      super.setUp();
      workspaceRootForMove = (IWorkspaceRoot)ws.newResource(new Path("/"), IResource.ROOT);

      projectForMove = (IProject)ws.newResource(new Path("/project"), IResource.PROJECT);
      projectForMove.create(new NullProgressMonitor());

      emptyFolderForMove = (IFolder)ws.newResource(projectForMove.getFullPath().append("empty_folder"),
         IResource.FOLDER);
      emptyFolderForMove.create(true, true, new NullProgressMonitor());

      nonEmptyFolderForMove = (IFolder)ws.newResource(projectForMove.getFullPath().append("non_empty_folder"),
         IResource.FOLDER);
      nonEmptyFolderForMove.create(true, true, new NullProgressMonitor());
      ((IFile)ws.newResource(nonEmptyFolderForMove.getFullPath().append("file"), IResource.FILE)).create(null, true,
         new NullProgressMonitor());

      fileForMove = (FileResource)ws.newResource(projectForMove.getFullPath().append("file"), IResource.FILE);
      fileForMove.create(null, true, new NullProgressMonitor());

      nonExistingFileForMove = (FileResource)ws.newResource(projectForMove.getFullPath().append("file_not_exist"),
         IResource.FILE);
   }

   @Test(expected = CoreException.class)
   public void testMoveWorkspaceRoot() throws Exception
   {
      workspaceRootForMove.move(new Path("/"), true, new NullProgressMonitor());
   }

   @Test
   public void testMoveProject() throws Exception
   {
      IPath destinationPath = new Path("/project1_moved");

      projectForMove.move(destinationPath, true, new NullProgressMonitor());
      assertFalse(projectForMove.exists());
      assertTrue(ws.newResource(destinationPath, IResource.PROJECT).exists());
   }

   @Test
   public void testMoveEmptyFolder() throws Exception
   {
      IPath destinationPath = new Path("/project2_moved/folder2_moved/folder3_moved");
      IFolder parentDestinationFolder = (IFolder)ws.newResource(destinationPath.removeLastSegments(1),
         IResource.FOLDER);
      parentDestinationFolder.create(true, true, new NullProgressMonitor());

      emptyFolderForMove.move(destinationPath, true, new NullProgressMonitor());
      assertFalse(emptyFolderForMove.exists());
      assertTrue(ws.newResource(destinationPath, IResource.FOLDER).exists());
   }

   @Test
   public void testMoveNonEmptyFolder() throws Exception
   {
      IPath destinationPath = new Path("/project_moved/folder_moved/folder1_moved");
      IFolder parentDestinationFolder = (IFolder)ws.newResource(destinationPath.removeLastSegments(1),
         IResource.FOLDER);
      parentDestinationFolder.create(true, true, new NullProgressMonitor());

      IResource[] members = nonEmptyFolderForMove.members();
      for (IResource member : members)
      {
         assertTrue(member.exists());
      }

      nonEmptyFolderForMove.move(destinationPath, true, new NullProgressMonitor());
      assertFalse(nonEmptyFolderForMove.exists());
      for (IResource member : members)
      {
         assertFalse(member.exists());
      }
      assertTrue(ws.newResource(destinationPath, IResource.FOLDER).exists());
   }

   @Test
   public void testMoveFile() throws Exception
   {
      IPath destinationPath = new Path("/project_moved/folder_moved/file_moved");
      IFolder parentDestinationFolder = (IFolder)ws.newResource(destinationPath.removeLastSegments(1),
         IResource.FOLDER);
      parentDestinationFolder.create(true, true, new NullProgressMonitor());

      fileForMove.move(destinationPath, true, new NullProgressMonitor());
      assertFalse(fileForMove.exists());
      assertTrue(ws.newResource(destinationPath, IResource.FILE).exists());
   }

   @Test(expected = CoreException.class)
   public void testMoveFile_AlreadyExist() throws Exception
   {
      IPath destinationPath = new Path("/project_moved/folder_moved/file_moved");
      IFile destinationFile = (IFile)ws.newResource(destinationPath, IResource.FILE);
      destinationFile.create(null, true, new NullProgressMonitor());
      fileForMove.move(destinationPath, true, new NullProgressMonitor());
   }

   @Test(expected = CoreException.class)
   public void testMoveFile_ResourceNotExist() throws Exception
   {
      IPath destinationPath = new Path("/project_moved/folder_moved/file_moved");
      IFolder parentDestinationFolder = (IFolder)ws.newResource(destinationPath.removeLastSegments(1),
         IResource.FOLDER);
      parentDestinationFolder.create(true, true, new NullProgressMonitor());
      nonExistingFileForMove.move(destinationPath, true, new NullProgressMonitor());
   }

   @Test(expected = CoreException.class)
   public void testMoveFile_ParentDestinationNotExist() throws Exception
   {
      IPath destinationPath = new Path("/project_moved/folder_moved/file_moved");
      fileForMove.move(destinationPath, true, new NullProgressMonitor());
   }

   @Test(expected = CoreException.class)
   public void testMoveFileToWorkspaceRoot() throws Exception
   {
      fileForMove.move(new Path("/"), true, new NullProgressMonitor());
   }

}
