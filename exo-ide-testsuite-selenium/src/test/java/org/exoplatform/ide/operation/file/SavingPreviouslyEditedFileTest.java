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
import static org.junit.Assert.assertTrue;

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
import java.util.UUID;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */

//IDE-13:Saving previously edited file.
public class SavingPreviouslyEditedFileTest extends BaseTest
{
   
   private static String FOLDER_NAME;// = UUID.randomUUID().toString();
   
   private static final String FILE_NAME = UUID.randomUUID().toString();
   
   private static final String DEFAULT_XML_CONTENT = "<?xml version='1.0' encoding='UTF-8'?>";
   
   private static final String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";
   
   private final static String XML_TEXT = "<test>\n"
      + "<settings>param</settings>\n"
      + "<bean>\n"
      + "<name>MineBean</name>\n"
      + "</bean>\n"
      + "</test>";
   
   private static final String FORMATTED_XML_TEXT = "<?xml version='1.0' encoding='UTF-8'?>\n"
      + "<test>\n"
      + "  <settings>param</settings>\n"
      + "  <bean>\n"
      + "    <name>MineBean</name>\n"
      + "  </bean>\n"
      + "</test>";
   
   @Before
   public void setUp()
   {

      try
      {
         FOLDER_NAME = UUID.randomUUID().toString();
         VirtualFileSystemUtils.mkcol(URL +  FOLDER_NAME + "/");
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
//      createFolder(FOLDER_NAME);
      
      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(WS_NAME);
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      
      assertElementPresentInWorkspaceTree(FOLDER_NAME);
      selectItemInWorkspaceTree(FOLDER_NAME);
      
      //----- 2 ------------
      //Click "New -> XML File" button.
      runCommandFromMenuNewOnToolbar("XML File");
      Thread.sleep(TestConstants.SLEEP);
      
      //You will see default XML content  in the new file tab of "Content" panel.
      //is file opened
      assertEquals("Untitled file.xml *", getTabTitle(0));
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertEquals(DEFAULT_XML_CONTENT, getTextFromCodeEditor(0));
      
      //----- 3-4 ------------
      //Click "Save As" button.
      //Enter "RepoFile.xml" as name of the file and click "Ok" button.
      saveAsUsingToolbarButton(FILE_NAME);
      Thread.sleep(TestConstants.SLEEP);
      
      //is file saved
      assertElementPresentInWorkspaceTree(FILE_NAME);
      openOrCloseFolder(FOLDER_NAME);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertElementNotPresentInWorkspaceTree(FILE_NAME);
      openOrCloseFolder(FOLDER_NAME);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertEquals(FILE_NAME, getTabTitle(0));
      
      //----- 5 ------------
      //Go to server window and check that the files created on the server
      checkFileOnWebDav();
//      assertEquals(200, VirtualFileSystemUtils.get(URL+ FILE_NAME).getStatusCode());
      Thread.sleep(TestConstants.SLEEP*5);
      
      //----- 6 ------------
      //Go back to gadget window, do some changes in "Content" panel, click "Save" button.
      changeFileContent();
      Thread.sleep(TestConstants.SLEEP);
      
      saveCurrentFile();
      Thread.sleep(TestConstants.SLEEP);
      closeTab("0");
      
      //----- 7 ------------
      //Refresh page, go to "Test" in "Workspace" panel.
      //You'll see "RepoFile.xml" in file list
      selenium.refresh();
      selenium.waitForPageToLoad("30000");
      Thread.sleep(TestConstants.SLEEP*5);
      
      //Test folder is closed, no file in navigation tree
      assertElementNotPresentInWorkspaceTree(FILE_NAME);
      //open Test folder
      openOrCloseFolder(FOLDER_NAME);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //see xml file in navigation tree
      assertElementPresentInWorkspaceTree(FILE_NAME);
      
      //----- 8 ------------
      //Open "RepoFile.xml" file
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      //You must see the content of your file in "Content" panel.
      assertEquals(FORMATTED_XML_TEXT, getTextFromCodeEditor(0));
      
      //----- 9 ------------
      //Make some changes in file content and then click on "File->Save" top menu command.
      changeOpenedFileContent();
      //The "Save" button and "File->Save" command must become enabled.
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE, true);
      checkToolbarButtonState(ToolbarCommands.File.SAVE, true);
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE);
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
      Thread.sleep(TestConstants.SLEEP);
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.SLEEP);
      
      selectItemInWorkspaceTree(WS_NAME);
      runToolbarButton(ToolbarCommands.File.REFRESH);
      
      assertElementPresentInWorkspaceTree(FOLDER_NAME);
      selectItemInWorkspaceTree(FOLDER_NAME);
      
      runCommandFromMenuNewOnToolbar(MenuCommands.New.TEXT_FILE);
      Thread.sleep(TestConstants.SLEEP);
      
      saveAsUsingToolbarButton(FILE_NAME);
      Thread.sleep(TestConstants.SLEEP);
      typeTextIntoEditor(0, "X");
      Thread.sleep(TestConstants.SLEEP);
      assertEquals(FILE_NAME + " *", getTabTitle(0));
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE, true);
      checkToolbarButtonState(ToolbarCommands.File.SAVE, true);
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
      Thread.sleep(TestConstants.SLEEP);
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.SLEEP);
      
      selectItemInWorkspaceTree(WS_NAME);
      runToolbarButton(ToolbarCommands.File.REFRESH);
      
      assertElementPresentInWorkspaceTree(FOLDER_NAME);
      selectItemInWorkspaceTree(FOLDER_NAME);
      
      runCommandFromMenuNewOnToolbar(MenuCommands.New.TEXT_FILE);
      Thread.sleep(TestConstants.SLEEP);
      
      saveAsUsingToolbarButton(FILE_NAME);
      Thread.sleep(TestConstants.SLEEP);
      closeTab("0");
      selectItemInWorkspaceTree(FILE_NAME);
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      typeTextIntoEditor(0, "X");
      Thread.sleep(TestConstants.SLEEP);
      assertEquals(FILE_NAME + " *", getTabTitle(0));
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE, true);
      checkToolbarButtonState(ToolbarCommands.File.SAVE, true);
   }
   
   
   private void changeOpenedFileContent() throws Exception
   {
      //change file content
      //at the end of line
      selenium.keyDown("//body[@class='editbox']/", "\\35");
      //enter
      selenium.keyDown("//body[@class='editbox']/", "\\13");
      selenium.keyUp("//body[@class='editbox']/", "\\13");
      
      //before tag test
      selenium.keyPress("//body[@class='editbox']/", "\\46");
      
      //at the end of line
      selenium.keyDown("//body[@class='editbox']/", "\\35");
      
      //enter
      selenium.keyDown("//body[@class='editbox']/", "\\13");
      selenium.keyUp("//body[@class='editbox']/", "\\13");
      
      Thread.sleep(100);
      final String typeText = "<root>" + "admin" + "</root>";
      typeTextIntoEditor(0, typeText);
   }
   
   private void changeFileContent() throws Exception
   {
      selenium.mouseDownAt("//body[@class='editbox']//span[2]", "");
      selenium.mouseUpAt("//body[@class='editbox']//span[2]", "");
      Thread.sleep(100);
      selenium.keyDown("//body[@class='editbox']/", "\\35");
      Thread.sleep(100);
      selenium.keyDown("//body[@class='editbox']/", "\\13");
      selenium.keyUp("//body[@class='editbox']/", "\\13");
      Thread.sleep(100);
      
      typeTextIntoEditor(0, XML_TEXT);
   }
   
   private void checkFileOnWebDav()
   {
      selenium.open(BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/");
      selenium.waitForPageToLoad("10000");
      
      assertTrue(selenium.isElementPresent("//div[@id='main']/a[@href='" 
         + BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" 
         + FOLDER_NAME + "' and text()=' " + FOLDER_NAME + "']"));
      
      selenium.click("link=" + FOLDER_NAME);
      selenium.waitForPageToLoad("10000");
      
      assertTrue(selenium.isElementPresent("//div[@id='main']/a[@href='" 
         + BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" 
         + FOLDER_NAME + "/" + FILE_NAME + "' and text()=' " + FILE_NAME + "']"));
      
      selenium.goBack();
      selenium.waitForPageToLoad("10000");
      selenium.goBack();
      selenium.waitForPageToLoad("30000");
   }
     

}
