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

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests moving resources.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: MoveTest.java Jan 3, 2013 11:10:48 AM azatsarynnyy $
 * 
 */
public class MoveTest extends ResourcesBaseTest
{
   private WorkspaceResource ws;

   private ProjectResource projectForMove;

   private FolderResource nonEmptyFolderForMove;

   private FolderResource emptyFolderForMove;

   private FileResource fileForMove;

   @Override
   public void setUp() throws Exception
   {
      super.setUp();
      ws = new WorkspaceResource(vfs);

      projectForMove = (ProjectResource)ws.newResource(new Path("/project"), IResource.PROJECT);
      ws.createResource(projectForMove);

      emptyFolderForMove =
         (FolderResource)ws.newResource(projectForMove.getFullPath().append("empty_folder"), IResource.FOLDER);
      ws.createResource(emptyFolderForMove);

      nonEmptyFolderForMove =
         (FolderResource)ws.newResource(projectForMove.getFullPath().append("non_empty_folder"), IResource.FOLDER);
      ws.createResource(nonEmptyFolderForMove);
      ws.createResource(ws.newResource(nonEmptyFolderForMove.getFullPath().append("file"), IResource.FILE));

      fileForMove = (FileResource)ws.newResource(projectForMove.getFullPath().append("file"), IResource.FILE);
      ws.createResource(fileForMove);
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
      IResource parentDestinationFolder = ws.newResource(destinationPath.removeLastSegments(1), IResource.FOLDER);
      ws.createResource(parentDestinationFolder);

      emptyFolderForMove.move(destinationPath, true, new NullProgressMonitor());
      assertFalse(emptyFolderForMove.exists());
      assertTrue(ws.newResource(destinationPath, IResource.FOLDER).exists());
   }

   @Test
   @Ignore
   public void testMoveNonEmptyFolder() throws Exception
   {
      IPath destinationPath = new Path("/project_moved/folder_moved/folder1_moved");
      IFolder parentDestinationFolder =
         (IFolder)ws.newResource(destinationPath.removeLastSegments(1), IResource.FOLDER);
      ws.createResource(parentDestinationFolder);

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
      IResource parentDestinationFolder = ws.newResource(destinationPath.removeLastSegments(1), IResource.FOLDER);
      ws.createResource(parentDestinationFolder);

      fileForMove.move(destinationPath, true, new NullProgressMonitor());
      assertFalse(fileForMove.exists());
      assertTrue(ws.newResource(destinationPath, IResource.FILE).exists());
   }

}
