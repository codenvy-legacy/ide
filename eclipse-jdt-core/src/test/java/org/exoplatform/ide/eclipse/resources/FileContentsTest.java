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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.exoplatform.ide.commons.StringUtils;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Tests for getting, setting and updating contents of file resources.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: FileContentsTest.java Jan 3, 2013 11:10:48 AM azatsarynnyy $
 *
 */
public class FileContentsTest extends ResourcesBaseTest
{
   private WorkspaceResource ws;

   private IFile fileResourceWithoutContent;

   private IFile fileResourceWithContent;

   private static final String DEFAULT_CONTENT = "test_content";

   @Override
   public void setUp() throws Exception
   {
      super.setUp();
      ws = new WorkspaceResource(vfs);

      fileResourceWithoutContent =
         (IFile)ws.newResource(new Path("/project/folder/file_without_content"), IResource.FILE);
      ws.createResource(fileResourceWithoutContent);

      fileResourceWithContent = (IFile)ws.newResource(new Path("/project/folder/file_with_content"), IResource.FILE);
      InputStream contentsStream = new ByteArrayInputStream(DEFAULT_CONTENT.getBytes());
      ws.createResource(fileResourceWithContent, contentsStream);
   }

   @Test
   public void testGetContents() throws Exception
   {
      String actualContents = StringUtils.toString(fileResourceWithContent.getContents());
      assertEquals(DEFAULT_CONTENT, actualContents);
   }

   @Test
   public void testSetContents() throws Exception
   {
      InputStream contentsStream = new ByteArrayInputStream(DEFAULT_CONTENT.getBytes());
      fileResourceWithoutContent.setContents(contentsStream, true, true, new NullProgressMonitor());

      String actualContents = StringUtils.toString(fileResourceWithoutContent.getContents());
      assertEquals(DEFAULT_CONTENT, actualContents);
   }

   @Test
   public void testUpdateContents() throws Exception
   {
      String existingContents = StringUtils.toString(fileResourceWithContent.getContents());
      String expectedContents = "test_content_origin";
      assertFalse(existingContents.equals(expectedContents));

      InputStream contentStream = new ByteArrayInputStream(expectedContents.getBytes());
      fileResourceWithContent.setContents(contentStream, true, true, new NullProgressMonitor());

      String actualContents = StringUtils.toString(fileResourceWithContent.getContents());
      assertEquals(expectedContents, actualContents);
   }

}
