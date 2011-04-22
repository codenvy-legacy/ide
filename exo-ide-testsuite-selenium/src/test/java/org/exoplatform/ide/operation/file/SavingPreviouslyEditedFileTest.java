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
package org.exoplatform.ide.operation.file;

import static org.junit.Assert.assertEquals;

import org.exoplatform.common.http.client.HTTPResponse;
import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * IDE-13:Saving previously edited file.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */

public class SavingPreviouslyEditedFileTest extends BaseTest
{

   private static final String FOLDER_NAME = SavingPreviouslyEditedFileTest.class.getSimpleName();

   private static final String FILE_NAME = "TestXmlFile";

   private static final String DEFAULT_XML_CONTENT = "<?xml version='1.0' encoding='UTF-8'?>";

   private static final String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   private final static String XML_TEXT = "<?xml version='1.0' encoding='UTF-8'?>\n" + "<test>\n"
      + "<settings>param</settings>\n" + "<bean>\n" + "<name>MineBean</name>\n" + "</bean>\n" + "</test>";

   private static final String FORMATTED_XML_TEXT = "<?xml version='1.0' encoding='UTF-8'?>\n" + "<test>\n"
      + "  <settings>param</settings>\n" + "  <bean>\n" + "    <name>MineBean</name>\n" + "  </bean>\n" + "</test>";

   @Before
   public void setUp()
   {

      try
      {
         VirtualFileSystemUtils.mkcol(URL + FOLDER_NAME + "/");
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

   @After
   public void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + FOLDER_NAME + "/");
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
   public void testSavePreviouslyEditedFile() throws Exception
   {
      //----- 1 ------------
      //Create and select "Test" in "Workspace" panel.

      Thread.sleep(TestConstants.SLEEP);
      IDE.NAVIGATION.selectItem(WS_URL);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);

      //----- 2 ------------
      //Click "New -> XML File" button.
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);

      //You will see default XML content  in the new file tab of "Content" panel.
      //is file openededitor()
      assertEquals("Untitled file.xml *",IDE.EDITOR.getTabTitle(0));
      assertEquals(DEFAULT_XML_CONTENT,IDE.EDITOR.getTextFromCodeEditor(0));

      //----- 3-4 ------------
      //Click "Save As" button.
      //Enter "RepoFile.xml" as name of the file and click "Ok" button.
      saveAsUsingToolbarButton(FILE_NAME);

      //is file saved
      IDE.NAVIGATION.assertItemPresent(WS_URL + FOLDER_NAME + "/" + FILE_NAME);
      IDE.NAVIGATION.clickOpenIconOfFolder(WS_URL + FOLDER_NAME + "/");
      
      IDE.NAVIGATION.assertItemNotPresent(WS_URL + FOLDER_NAME + "/" + FILE_NAME);

      IDE.NAVIGATION.clickOpenIconOfFolder(WS_URL + FOLDER_NAME + "/");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertEquals(FILE_NAME,IDE.EDITOR.getTabTitle(0));
     IDE.EDITOR.closeTab(0);

      //----- 5 ------------
      //Go to server window and check that the files created on the server
      assertEquals(200, VirtualFileSystemUtils.get(URL + FOLDER_NAME).getStatusCode());
      HTTPResponse response = VirtualFileSystemUtils.get(URL + FOLDER_NAME + "/" + FILE_NAME);
      assertEquals(200, response.getStatusCode());
      assertEquals(DEFAULT_XML_CONTENT + "\n", response.getText());

      //----- 6 ------------
      //Go back to gadget window, do some changes in "Content" panel, click "Save" button.
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
     IDE.EDITOR.selectIFrameWithEditor(0);
     IDE.EDITOR.deleteFileContent();
      IDE.selectMainFrame();
     IDE.EDITOR.typeTextIntoEditor(0, XML_TEXT);
      saveCurrentFile();

     IDE.EDITOR.closeTab(0);

      //----- 7 ------------
      //Refresh page, go to "Test" in "Workspace" panel.
      //You'll see "RepoFile.xml" in file list
      refresh();

      //Test folder is closed, no file in navigation tree
      IDE.NAVIGATION.assertItemNotPresent(WS_URL + FOLDER_NAME + "/" + FILE_NAME);
      //open Test folder
      IDE.NAVIGATION.clickOpenIconOfFolder(WS_URL + FOLDER_NAME + "/");
      //see xml file in navigation tree
      IDE.NAVIGATION.assertItemPresent(WS_URL + FOLDER_NAME + "/" + FILE_NAME);

      //----- 8 ------------
      //Open "RepoFile.xml" file
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      //You must see the content of your file in "Content" panel.
      assertEquals(FORMATTED_XML_TEXT,IDE.EDITOR.getTextFromCodeEditor(0));

      //----- 9 ------------
      //Make some changes in file content and then click on "File->Save" top menu command.
      final String typeText = "<root>" + "admin" + "</root>";
     IDE.EDITOR.typeTextIntoEditor(0, typeText);
      //The "Save" button and "File->Save" command must become enabled.
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SAVE, true);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE);
   }

   /**
    * Bug http://jira.exoplatform.org/browse/IDE-342.
    * 
    * Type one letter to just created and saved file.
    * @throws Exception
    */
   @Test
   public void testEditAndSaveJustCreatedFile() throws Exception
   {
      refresh();

      IDE.NAVIGATION.selectItem(WS_URL);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);

      IDE.NAVIGATION.assertItemPresent(WS_URL + FOLDER_NAME + "/");

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);

      saveAsUsingToolbarButton(FILE_NAME);
     IDE.EDITOR.typeTextIntoEditor(0, "X");
      Thread.sleep(TestConstants.SLEEP_SHORT);

      assertEquals(FILE_NAME + " *",IDE.EDITOR.getTabTitle(0));

      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SAVE, true);
   }

   /**
    * Bug http://jira.exoplatform.org/browse/IDE-342.
    * 
    * Create, save and close file. Than open file and type one letter.
    * @throws Exception
    */
   @Test
   public void testOpenEditAndSaveJustCreatedFile() throws Exception
   {
      refresh();

      IDE.NAVIGATION.selectItem(WS_URL);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);

      IDE.NAVIGATION.assertItemPresent(WS_URL + FOLDER_NAME + "/");

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);

      saveAsUsingToolbarButton(FILE_NAME);
      Thread.sleep(TestConstants.SLEEP_SHORT);

     IDE.EDITOR.closeTab(0);

      IDE.NAVIGATION.assertItemPresent(WS_URL + FOLDER_NAME + "/" + FILE_NAME);
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
     IDE.EDITOR.typeTextIntoEditor(0, "X");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      assertEquals(FILE_NAME + " *",IDE.EDITOR.getTabTitle(0));
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SAVE, true);
   }

}
