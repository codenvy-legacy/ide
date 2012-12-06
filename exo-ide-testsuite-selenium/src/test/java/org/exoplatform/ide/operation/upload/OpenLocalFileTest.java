/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.operation.upload;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Test for "Open local file" form.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 * 
 */
public class OpenLocalFileTest extends BaseTest
{
   private static final String FILE_PATH = "src/test/resources/org/exoplatform/ide/operation/file/upload/test";

   private static final String PROJECT = OpenLocalFileTest.class.getSimpleName();

   @Before
   public void beforeTest()
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
      }
      catch (IOException e)
      {
      }
   }

   @Test
   public void testOpenFileWithoutExtention() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      // call Open Local File form
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE);
      IDE.UPLOAD.waitOpenLocalFileViewOpened();
      assertFalse(IDE.UPLOAD.isUploadButtonEnabled());

      // select file from local driver without file extention
      try
      {
         File file = new File(FILE_PATH);
         IDE.UPLOAD.setUploadFilePath(file.getCanonicalPath());
      }
      catch (Exception e)
      {
      }

      String fileName = FILE_PATH.substring(FILE_PATH.lastIndexOf("/") + 1, FILE_PATH.length());
      assertEquals(fileName, IDE.UPLOAD.getFilePathValue());

      assertEquals("text/plain", IDE.UPLOAD.getMimeTypeValue());
      assertTrue(IDE.UPLOAD.isUploadButtonEnabled());

      IDE.UPLOAD.setMimeType(MimeType.TEXT_HTML);

      assertTrue(IDE.UPLOAD.isUploadButtonEnabled());

      IDE.UPLOAD.clickUploadButton();
      IDE.UPLOAD.waitClosed();

      IDE.EDITOR.waitActiveFile(PROJECT + "/" + fileName);
   }

   @After
   public void afterTest()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (IOException e)
      {
      }
   }
}
