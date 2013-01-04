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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
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

   private IContainer containerResource;

   private IResource children1;

   private IResource children2;

   private IResource children3;

   @Override
   public void setUp() throws Exception
   {
      super.setUp();
      ws = new WorkspaceResource(vfs);

      containerResource = (IContainer)ws.newResource(new Path("/project"), IResource.PROJECT);
      ws.createResource(containerResource);

      children1 = ws.newResource(containerResource.getFullPath().append("folder"), IResource.FOLDER);
      ws.createResource(children1);
      children2 = ws.newResource(containerResource.getFullPath().append("file"), IResource.FILE);
      ws.createResource(children2);
      children3 = ws.newResource(containerResource.getFullPath().append("folder/file"), IResource.FILE);
      ws.createResource(children3);
   }

   @Test
   public void testGetMembers() throws Exception
   {
      IResource[] members = containerResource.members();
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

}
