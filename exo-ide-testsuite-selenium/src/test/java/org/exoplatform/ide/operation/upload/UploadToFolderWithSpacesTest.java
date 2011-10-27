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
package org.exoplatform.ide.operation.upload;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jul 5, 2011 10:31:55 AM anya $
 *
 */
public class UploadToFolderWithSpacesTest extends BaseTest
{
   private static final String TEST_FOLDER = UploadToFolderWithSpacesTest.class.getSimpleName();

   private static String TEST_FILE = "Test File@.txt";

   private static String FOLDER = "Folder @1";

   private static final String FILE_PATH =
      "src/test/resources/org/exoplatform/ide/operation/file/upload/Test File@.txt";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + TEST_FOLDER);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testUploadingFileWithSpaces() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      //Create folder with spaces:
      IDE.NAVIGATION.createFolder(FOLDER);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + FOLDER + "/");

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + FOLDER + "/");

       IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FILE);
       IDE.UPLOAD.waitUploadViewOpened();
       try
       {
          File file = new File(FILE_PATH);
          IDE.UPLOAD.setUploadFilePath(file.getCanonicalPath());
       }
       catch (Exception e)
       {
       }

       IDE.UPLOAD.clickUploadButton();
       IDE.UPLOAD.waitUploadViewClosed();

       IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + FOLDER + "/" + TEST_FILE);
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + TEST_FOLDER);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
}
