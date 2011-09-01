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

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Make possibility upload ZIPed folder (IDE-482).
 * 
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Dec 15, 2010 $
 *
 */
public class UploadingZippedFolderTest extends BaseTest
{
   private static final String FOLDER_NAME = UploadingZippedFolderTest.class.getSimpleName();

   private static final String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + FOLDER_NAME;

   private static final String FILE_PATH = "src/test/resources/org/exoplatform/ide/operation/file/upload/sample.zip";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(URL);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testUploadingHtml() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);

      uploadZippedFolder(FILE_PATH);

      final String testFolder = "test";
      final String folder = "folder";
      final String projectFolder = "project";
      final String exoFolder = "exo";
      final String settingsFile = "settings.xml";
      final String sampleFile = "sample.txt";
      final String mineFile = "mine.xml";

      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);

      IDE.WORKSPACE.waitForItem(WS_URL + testFolder + "/");
      
      IDE.NAVIGATION.assertItemVisible(WS_URL + testFolder + "/");
      IDE.NAVIGATION.assertItemVisible(WS_URL + folder + "/");
      IDE.NAVIGATION.assertItemVisible(WS_URL + sampleFile);
      IDE.NAVIGATION.assertItemVisible(WS_URL +  settingsFile);

      IDE.WORKSPACE.selectItem(WS_URL +  folder + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      
      //add timeout for reading content from folder (fix for cloud-IDE-assembly)
      IDE.WORKSPACE.waitForItem(WS_URL + folder + "/" + projectFolder + "/");
      IDE.NAVIGATION.assertItemVisible(WS_URL + folder + "/" + projectFolder + "/");

      IDE.WORKSPACE.selectItem(WS_URL + testFolder + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(WS_URL + testFolder + "/" + mineFile);
      IDE.NAVIGATION.assertItemVisible(WS_URL +  testFolder + "/" + exoFolder + "/");
      IDE.NAVIGATION.assertItemVisible(WS_URL + testFolder + "/" + mineFile);

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + settingsFile, false);
      IDE.EDITOR.waitTabPresent(0);

      String text = IDE.EDITOR.getTextFromCodeEditor(0);
      assertTrue(text.length() > 0);

      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.SHOW_PROPERTIES);
      IDE.PROPERTIES.waitForPropertiesViewOpened();
      assertEquals("nt:resource", IDE.PROPERTIES.getContentNodeType());
      assertEquals(MimeType.TEXT_XML, IDE.PROPERTIES.getContentType());

   }

   protected void uploadZippedFolder(String filePath) throws Exception
   {
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FOLDER);
      IDE.UPLOAD.waitUploadViewOpened();
      
      try
      {
         File file = new File(filePath);
         IDE.UPLOAD.setUploadFilePath(file.getCanonicalPath());
      }
      catch (Exception e)
      {
      }

      assertEquals(
         filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length()), IDE.UPLOAD.getFilePathValue());
      IDE.UPLOAD.clickUploadButton();
      IDE.UPLOAD.waitUploadViewClosed();
   }
}
