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

import static org.junit.Assert.assertEquals;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

/**
 * Tests moving resources.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: MoveTest.java Jan 3, 2013 11:10:48 AM azatsarynnyy $
 */
public class MoveTest extends ResourcesBaseTest
{
   private WorkspaceResource ws;

   private ProjectResource projectForMove;

   private FolderResource folderForMove;

   private FileResource fileForMove;

   @Override
   public void setUp() throws Exception
   {
      super.setUp();
      ws = new WorkspaceResource(vfs);

      projectForMove = (ProjectResource)ws.newResource(new Path("/project"), IResource.PROJECT);
      ws.createResource(projectForMove);

      folderForMove = (FolderResource)ws.newResource(new Path("/project/folder"), IResource.FOLDER);
      ws.createResource(folderForMove);

      fileForMove = (FileResource)ws.newResource(new Path("/project/folder/file"), IResource.FILE);
      ws.createResource(fileForMove);
   }

   @Test
   public void testMoveProject() throws Exception
   {
      IPath destinationPath = new Path("/project1");

      projectForMove.move(destinationPath, true, new NullProgressMonitor());
      assertEquals(projectForMove.getFullPath(), destinationPath);
   }

   @Test
   public void tetsMoveFolder() throws Exception
   {
      IPath destinationPath = new Path("/project1/folder1");

      folderForMove.move(destinationPath, true, new NullProgressMonitor());
      assertEquals(folderForMove.getFullPath(), destinationPath);
   }

   @Test
   public void testMoveFile() throws Exception
   {
      IPath destinationPath = new Path("/project1/folder1/file1");

      fileForMove.move(destinationPath, true, new NullProgressMonitor());
      assertEquals(fileForMove.getFullPath(), destinationPath);
   }

}
