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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.exoplatform.ide.commons.StringUtils;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Tests creating resources.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: CreateTest.java Jan 3, 2013 11:10:48 AM azatsarynnyy $
 *
 */
public class CreateTest extends ResourcesBaseTest
{
   private WorkspaceResource ws;

   @Override
   public void setUp() throws Exception
   {
      super.setUp();
      ws = new WorkspaceResource(vfs);
   }

   @Test
   public void testCreateProject() throws Exception
   {
      String path = "/project";

      ProjectResource projectResource = (ProjectResource)ws.newResource(new Path(path), IResource.PROJECT);
      assertFalse(projectResource.exists());

      projectResource.create(new NullProgressMonitor());
      assertTrue(projectResource.exists());

      assertTrue(projectResource.getType() == IResource.PROJECT);
      assertTrue(projectResource.getFullPath().toString().equals(path));
   }

   @Test
   public void testCreateFolder() throws Exception
   {
      String path = "/project/folder";

      FolderResource folderResource = (FolderResource)ws.newResource(new Path(path), IResource.FOLDER);
      assertFalse(folderResource.exists());

      folderResource.create(true, true, new NullProgressMonitor());
      assertTrue(folderResource.exists());

      assertTrue(folderResource.getType() == IResource.FOLDER);
      assertTrue(folderResource.getFullPath().toString().equals(path));
   }

   @Test
   public void testCreateFile() throws Exception
   {
      String path = "/project/folder/file";
      String content = "test create file";

      FileResource fileResource = (FileResource)ws.newResource(new Path(path), IResource.FILE);
      assertFalse(fileResource.exists());

      InputStream contentsStream = new ByteArrayInputStream(content.getBytes());
      fileResource.create(contentsStream, true, new NullProgressMonitor());
      assertTrue(fileResource.exists());

      assertTrue(fileResource.getType() == IResource.FILE);
      assertTrue(fileResource.getFullPath().toString().equals(path));

      String actualContents = StringUtils.toString(fileResource.getContents());
      assertEquals(actualContents, content);
   }

}
