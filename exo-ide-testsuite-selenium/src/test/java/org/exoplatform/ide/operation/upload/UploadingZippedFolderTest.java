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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.Locators;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
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

   private static final String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER_NAME;
   
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
      Thread.sleep(TestConstants.SLEEP);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      selectItemInWorkspaceTree(FOLDER_NAME);
      
      uploadZippedFolder(FILE_PATH);
      
      final String testFolder = "test";
      final String folder = "folder";
      final String projectFolder = "project";
      final String exoFolder = "exo";
      final String settingsFile = "settings.xml";
      final String sampleFile = "sample.txt";
      final String mineFile = "mine.xml";
      
      selectItemInWorkspaceTree(FOLDER_NAME);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      assertElementPresentInWorkspaceTree(testFolder);
      assertElementPresentInWorkspaceTree(folder);
      assertElementPresentInWorkspaceTree(sampleFile);
      assertElementPresentInWorkspaceTree(settingsFile);
      
      selectItemInWorkspaceTree(folder);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      assertElementPresentInWorkspaceTree(projectFolder);
      
      selectItemInWorkspaceTree(testFolder);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      assertElementPresentInWorkspaceTree(exoFolder);
      assertElementPresentInWorkspaceTree(mineFile);
      
      openFileFromNavigationTreeWithCodeEditor(settingsFile, false);
      checkCodeEditorOpened(0);

      String text = getTextFromCodeEditor(0);
      assertTrue(text.length() > 0);

      IDE.menu().runCommand(MenuCommands.View.VIEW, MenuCommands.View.SHOW_PROPERTIES);
      assertEquals("nt:resource", selenium.getText(Locators.PropertiesPanel.SC_CONTENT_NODE_TYPE_TEXTBOX));
      assertEquals(MimeType.TEXT_XML, selenium.getText(Locators.PropertiesPanel.SC_CONTENT_TYPE_TEXTBOX));
      
   }
   
   protected void uploadZippedFolder(String filePath) throws Exception
   {
      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.UPLOAD_FOLDER);
      Thread.sleep(TestConstants.SLEEP_SHORT);

      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideUploadForm\"]/body/"));
      assertTrue(selenium.isElementPresent("//div[@class='stretchImgButtonDisabled' and @eventproxy='ideUploadFormUploadButton']"));
      try
      {
         File file = new File(filePath);
         selenium.type("//input[@type='file']", file.getCanonicalPath());
      }
      catch (Exception e)
      {
      }
      Thread.sleep(TestConstants.SLEEP);

      assertEquals(
         filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length()),
         selenium
            .getValue("scLocator=//DynamicForm[ID=\"ideUploadFormDynamicForm\"]/item[name=ideUploadFormFilenameField]/element"));

      assertTrue(selenium
         .isElementPresent("//div[@class='stretchImgButton' and @eventproxy='ideUploadFormUploadButton']"));

      selenium.click("scLocator=//IButton[ID=\"ideUploadFormUploadButton\"]/");
      Thread.sleep(TestConstants.SLEEP);

      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideUploadForm\"]/"));
   }
   
}
