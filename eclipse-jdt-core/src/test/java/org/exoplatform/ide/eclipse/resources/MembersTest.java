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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests getting members of container resource.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: MembersTest.java Jan 3, 2013 11:10:48 AM azatsarynnyy $
 *
 */
public class MembersTest extends ResourcesBaseTest
{
   private WorkspaceResource ws;

   private IProject projectResource;

   private IFolder children1;

   private IFile children2;

   private IFile children3;

   @Override
   public void setUp() throws Exception
   {
      super.setUp();
      ws = new WorkspaceResource(vfs);

      projectResource = (IProject)ws.newResource(new Path("/project"), IResource.PROJECT);
      projectResource.create(new NullProgressMonitor());

      children1 = (IFolder)ws.newResource(projectResource.getFullPath().append("folder"), IResource.FOLDER);
      children1.create(true, true, new NullProgressMonitor());
      children2 = (IFile)ws.newResource(projectResource.getFullPath().append("file"), IResource.FILE);
      children2.create(null, true, new NullProgressMonitor());
      children3 = (IFile)ws.newResource(projectResource.getFullPath().append("folder/file"), IResource.FILE);
      children3.create(null, true, new NullProgressMonitor());
   }

   @Test
   public void testGetMembers() throws Exception
   {
      IResource[] members = projectResource.members();
      List<String> memberPathList = new ArrayList<String>(members.length);
      for (IResource member : members)
      {
         memberPathList.add(member.getFullPath().toString());
      }

      assertTrue(memberPathList.contains(children1.getFullPath().toString()));
      assertTrue(memberPathList.contains(children2.getFullPath().toString()));
      assertFalse("Members of a project or folder are the files and folders immediately contained within it.",
         memberPathList.contains(children3.getFullPath().toString()));
   }

   @Test
   public void testFindMember() throws Exception
   {
      IResource foundMember1 = projectResource.findMember(children1.getFullPath());
      assertEquals(children1.getFullPath(), foundMember1.getFullPath());

      IResource foundMember2 = projectResource.findMember(children2.getFullPath());
      assertEquals(children2.getFullPath(), foundMember2.getFullPath());

      IResource foundMember3 = projectResource.findMember(children3.getFullPath());
      assertNull("Members of a project or folder are the files and folders immediately contained within it.",
         foundMember3);
   }

}
