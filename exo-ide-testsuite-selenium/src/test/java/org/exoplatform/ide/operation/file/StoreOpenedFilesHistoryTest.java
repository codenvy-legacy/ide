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

import static org.junit.Assert.*;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class StoreOpenedFilesHistoryTest extends BaseTest
{
   private static final String TEST_FOLDER = "Test Folder";
   
   private static final String TEST_FOLDER_TO_DELETE = "Test Folder to Delete";
   
   private final static String URL = BASE_URL + REST_CONTEXT + "/jcr/" + REPO_NAME + "/" + "production" + "/";
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + TEST_FOLDER);
         VirtualFileSystemUtils.delete(URL + TEST_FOLDER_TO_DELETE);
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
   
   //IDE-66
   //Store Opened Files History 
   @Test
   public void storeOpenedFilesHistory() throws Exception
   {
      final String textFile = "Test Text File.txt";
      final String htmlFile = "Test Html File.html";
      final String gadgetFile = "Test Gadget File.xml";
      
      Thread.sleep(TestConstants.SLEEP);
      Thread.sleep(TestConstants.SLEEP);
      Thread.sleep(TestConstants.SLEEP);
      
      //select another workspace
      runTopMenuCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.SELECT_WORKSPACE);
//      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='Window']", "");
//      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), 'Select Workspace')]", "");
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideSelectWorkspaceForm\"]"));
      assertTrue(selenium.isTextPresent("Workspace"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideSelectWorkspaceFormOkButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideSelectWorkspaceFormCancelButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideEntryPointListGrid\"]"));
      assertTrue(selenium.isTextPresent("/rest/private/jcr/repository/production"));
      assertTrue(selenium.isTextPresent("/rest/private/jcr/repository/dev-monit"));
      selenium.click("scLocator=//ListGrid[ID=\"ideEntryPointListGrid\"]/body/row[0]/col[fieldName=entryPoint||0]\"");
      selenium.click("scLocator=//IButton[ID=\"ideSelectWorkspaceFormOkButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isTextPresent("production"));
      assertTrue(selenium.isTextPresent("exo:registry"));
      assertTrue(selenium.isTextPresent("jcr:system"));
      Thread.sleep(TestConstants.SLEEP);
      
      selectItemInWorkspaceTree("production");
      Thread.sleep(TestConstants.SLEEP);
      
      createFolder(TEST_FOLDER);
      Thread.sleep(TestConstants.SLEEP);
      
      selectItemInWorkspaceTree("production");
      Thread.sleep(TestConstants.SLEEP);
      
      createFolder(TEST_FOLDER_TO_DELETE);
      Thread.sleep(TestConstants.SLEEP);
      
      selectItemInWorkspaceTree(TEST_FOLDER_TO_DELETE);
      Thread.sleep(100);
      
      //create txt file
      createFileFromToolbar("Text File");
      Thread.sleep(TestConstants.SLEEP);
      saveAsUsingToolbarButton(textFile);
      Thread.sleep(TestConstants.SLEEP);
      
      selectItemInWorkspaceTree(TEST_FOLDER);
      Thread.sleep(100);
      
      //create html file
      createFileFromToolbar("HTML File");
      Thread.sleep(TestConstants.SLEEP);
      saveAsUsingToolbarButton(htmlFile);
      Thread.sleep(TestConstants.SLEEP);
      
      //create google gadget file
      createFileFromToolbar("Google Gadget");
      Thread.sleep(TestConstants.SLEEP);
      saveAsUsingToolbarButton(gadgetFile);
      Thread.sleep(TestConstants.SLEEP);
      
      //create groovy script file
      createFileFromToolbar("Groovy Script");
      Thread.sleep(TestConstants.SLEEP);
      
      //closing all files
      closeTab("0");
      closeTab("0");
      closeTab("0");
      closeUnsavedFileAndDoNotSave("0");
      Thread.sleep(TestConstants.SLEEP);
      
      openFileFromNavigationTreeWithCodeEditor(textFile, false);
      openFileFromNavigationTreeWithCodeEditor(gadgetFile, false);
      openFileFromNavigationTreeWithCkEditor(htmlFile, true);
      
      checkCkEditorOpened(2);
      
      
      //Open Server window with selected at the step 2 workspace URL
      //and remove folder "Test Folder To Delete"
      selectItemInWorkspaceTree(TEST_FOLDER_TO_DELETE);
      deleteSelectedItems();
      Thread.sleep(TestConstants.SLEEP);
      
      selenium.open("http://www.google.com.ua/");
      selenium.waitForPageToLoad("10000");
      Thread.sleep(TestConstants.SLEEP);
      
      selenium.goBack();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.SLEEP*5);
      
      checkCkEditorOpened(1);
      
      checkOpenedFilesHistory();
      
      closeTab("0");
      closeFileTab("0");
//      closeUnsavedFileAndDoNotSave("0");
      
      //open folder to select html file
      openOrCloseFolder(TEST_FOLDER);
      //return init configuration
      //make code mirror as default editor for html files
      Thread.sleep(TestConstants.SLEEP_SHORT);
      returnCodeEditorAsDefault(htmlFile);
      
      closeTab("0");
      
      //delete test folder
      selectItemInWorkspaceTree(TEST_FOLDER);
      deleteSelectedItems();
      Thread.sleep(TestConstants.SLEEP);
      
      //return init configuration
      //select another workspace
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='Window']", "");
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), 'Select Workspace')]", "");
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideSelectWorkspaceForm\"]"));
      assertTrue(selenium.isTextPresent("Workspace"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideSelectWorkspaceFormOkButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideSelectWorkspaceFormCancelButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideEntryPointListGrid\"]"));
      assertTrue(selenium.isTextPresent("/rest/private/jcr/repository/production"));
      assertTrue(selenium.isTextPresent("/rest/private/jcr/repository/dev-monit"));
      selenium.click("scLocator=//ListGrid[ID=\"ideEntryPointListGrid\"]/body/row[1]/col[fieldName=entryPoint||0]\"");
      selenium.click("scLocator=//IButton[ID=\"ideSelectWorkspaceFormOkButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isTextPresent("dev-monit"));
      Thread.sleep(TestConstants.SLEEP);
   }
   
   /**
    * 
    * @throws Exception
    */
   private void checkOpenedFilesHistory() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      //check that files are opened and in wright order.
      //check that tab with html file is selected
      assertTrue(selenium.isElementPresent("//div[@class='tabBar']/div/div[3]//td[@class='tabTitle']/span[contains(text(),'Test Gadget File.xml')]"));
      assertTrue(selenium.isElementPresent("//div[@class='tabBar']/div/div[5]//td[@class='tabTitleSelected']/span[contains(text(),'Test Html File.html')]"));
      
      //select Gadget file
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/");
      Thread.sleep(TestConstants.SLEEP);
      
      checkMenuCommandState("Run", "Show Preview", true);
      checkMenuCommandState("Run", "Deploy Gadget to GateIn", true);
      checkMenuCommandState("Run", "UnDeploy Gadget from GateIn", true);
      checkMenuCommandState("Edit", "Hide Line Numbers", true);
      checkMenuCommandState("Edit", "Format", true);
      checkMenuCommandState("Edit", "Undo Typing", false);
      checkMenuCommandState("Edit", "Redo Typing", false);
      Thread.sleep(TestConstants.SLEEP_SHORT);
   }
   
   /**
    * Open file with code editor and set this editor as default.
    * 
    * Used for returning initial settings for IDE.
    * 
    * @param fileName
    * @throws Exception
    */
   private void returnCodeEditorAsDefault(String fileName) throws Exception
   {
      //TODO add check form
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=" + fileName + "]/col[1]");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
      Thread.sleep(TestConstants.SLEEP);
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), 'Open With')]", "");
      //select editor
      selenium.click("scLocator=//ListGrid[ID=\"ideOpenFileWithListGrid\"]/body/row[0]/col[0]");
      //click on checkbox Use as default editor
      selenium.click("scLocator=//Window[ID=\"ideallOpenFileWithForm\"]/item[1][Class=\"DynamicForm\"]/item[name=Default]/textbox");
      Thread.sleep(TestConstants.SLEEP);
      selenium.click("scLocator=//IButton[ID=\"ideOpenFileWithOkButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      //TODO add check that editor opened
   }
   
   /**
    * Close file tab.
    * 
    * If file is not saved and warning dialog appears,
    * click No button.
    * 
    * If file is saved and no warning dialog,
    * no exception occurs
    * 
    * @param tabIndex
    * @throws Exception
    */
   private void closeFileTab(String tabIndex) throws Exception
   {
      closeTab(tabIndex);

      //check is warning dialog appears
      if (selenium.isElementPresent(
         "scLocator=//Dialog[ID=\"isc_globalWarn\"]/header[contains(text(), 'Close file')]"))
      {
         //click No button
         selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/");
         Thread.sleep(TestConstants.SLEEP);
      }
   }

}
