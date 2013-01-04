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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
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

      projectForMove = (ProjectResource)ws.newResource(new Path("/project1"), IResource.PROJECT);
      ws.createResource(projectForMove);

      folderForMove = (FolderResource)ws.newResource(new Path("/project2/folder2"), IResource.FOLDER);
      ws.createResource(folderForMove);

      fileForMove = (FileResource)ws.newResource(new Path("/project3/folder3/file3"), IResource.FILE);
      ws.createResource(fileForMove);
   }

   @Test
   public void testMoveProject() throws Exception
   {
      IPath destinationPath = new Path("/project1_moved");

      projectForMove.move(destinationPath, true, new NullProgressMonitor());
      assertFalse(projectForMove.exists());
      vfs.getItemByPath(destinationPath.toString(), null, PropertyFilter.NONE_FILTER).getPath();
   }

   @Test
   public void testMoveFolder() throws Exception
   {
      IPath destinationPath = new Path("/project2_moved/folder2_moved/folder3_moved");
      IResource parentDestinationFolder = ws.newResource(destinationPath.removeLastSegments(1), IResource.FOLDER);
      ws.createResource(parentDestinationFolder);

      folderForMove.move(destinationPath, true, new NullProgressMonitor());
      assertFalse(folderForMove.exists());
      vfs.getItemByPath(destinationPath.toString(), null, PropertyFilter.NONE_FILTER).getPath();
   }

   @Test
   public void testMoveFile() throws Exception
   {
      IPath destinationPath = new Path("/project3_testMoveFile/folder3_testMoveFile/file3_testMoveFile");
      IResource parentDestinationFolder = ws.newResource(destinationPath.removeLastSegments(1), IResource.FOLDER);
      ws.createResource(parentDestinationFolder);

      fileForMove.move(destinationPath, true, new NullProgressMonitor());
      assertFalse(fileForMove.exists());
      vfs.getItemByPath(destinationPath.toString(), null, PropertyFilter.NONE_FILTER).getPath();
   }

}
