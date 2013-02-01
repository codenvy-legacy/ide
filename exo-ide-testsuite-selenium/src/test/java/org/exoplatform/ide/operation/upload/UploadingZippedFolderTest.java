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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Make possibility upload ZIPed folder (IDE-482).
 * 
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Dec 15, 2010 $
 * 
 */
public class UploadingZippedFolderTest extends BaseTest
{
   private static final String PROJECT = UploadingZippedFolderTest.class.getSimpleName();

   private static final String FILE_PATH = "src/test/resources/org/exoplatform/ide/operation/file/upload/sample.zip";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
      }
      catch (IOException e)
      {
      }
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (IOException e)
      {
      }
   }

   @Test
   public void testUploadingZippedFolder() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      uploadZippedFolder(FILE_PATH);

      final String testFolder = "test";
      final String folder = "folder";
      final String projectFolder = "project";
      final String exoFolder = "exo";
      final String settingsFile = "settings.xml";
      final String sampleFile = "sample.txt";
      final String mineFile = "mine.xml";

      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      //   IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      // IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + testFolder);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + testFolder);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + folder);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + sampleFile);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + settingsFile);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + folder);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + folder + "/" + projectFolder);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + testFolder);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + testFolder + "/" + mineFile);

      IDE.PROJECT.EXPLORER.waitItemVisible(PROJECT + "/" + testFolder + "/" + exoFolder);
      IDE.PROJECT.EXPLORER.waitItemVisible(PROJECT + "/" + testFolder + "/" + mineFile);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + settingsFile);
      IDE.EDITOR.waitActiveFile();

      String text = IDE.EDITOR.getTextFromCodeEditor();
      assertTrue(text.length() > 0);

      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.SHOW_PROPERTIES);
      IDE.PROPERTIES.waitOpened();
      assertEquals(MimeType.TEXT_XML, IDE.PROPERTIES.getContentType());
   }

   protected void uploadZippedFolder(String filePath) throws Exception
   {
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FOLDER);
      IDE.UPLOAD.waitOpened();
      try
      {
         File file = new File(filePath);
         IDE.UPLOAD.setUploadFilePath(file.getCanonicalPath());
      }
      catch (Exception e)
      {
      }

      assertEquals(filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length()), IDE.UPLOAD.getFilePathValue());
      IDE.UPLOAD.clickUploadButton();
      IDE.UPLOAD.waitClosed();
   }
}
