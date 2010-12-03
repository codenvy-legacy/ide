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

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Test;

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

   private static String SECOND_WORKSPACE_URL;
   
   private String secondWorkspaceName;
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         cleanRegistry();
         VirtualFileSystemUtils.delete(SECOND_WORKSPACE_URL + TEST_FOLDER);
         VirtualFileSystemUtils.delete(SECOND_WORKSPACE_URL + TEST_FOLDER_TO_DELETE);
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
    
      secondWorkspaceName = getNonActiveWorkspaceName();
      SECOND_WORKSPACE_URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + secondWorkspaceName + "/";
      
      //select another workspace
      selectWorkspace(secondWorkspaceName);
      
      selectItemInWorkspaceTree(secondWorkspaceName);
      Thread.sleep(TestConstants.SLEEP);
      
      createFolder(TEST_FOLDER);
      Thread.sleep(TestConstants.SLEEP);
      
      selectItemInWorkspaceTree(secondWorkspaceName);
      Thread.sleep(TestConstants.SLEEP);
      
      createFolder(TEST_FOLDER_TO_DELETE);
      Thread.sleep(TestConstants.SLEEP);
      
      selectItemInWorkspaceTree(TEST_FOLDER_TO_DELETE);
      Thread.sleep(100);
      
      //create txt file
      IDE.toolbar().runCommandFromNewPopupMenu("Text File");
      Thread.sleep(TestConstants.SLEEP);
      saveAsUsingToolbarButton(textFile);
      Thread.sleep(TestConstants.SLEEP);
      
      selectItemInWorkspaceTree(TEST_FOLDER);
      Thread.sleep(100);
      
      //create html file
      IDE.toolbar().runCommandFromNewPopupMenu("HTML File");
      Thread.sleep(TestConstants.SLEEP);
      saveAsUsingToolbarButton(htmlFile);
      Thread.sleep(TestConstants.SLEEP);
      
      //create google gadget file
      IDE.toolbar().runCommandFromNewPopupMenu("Google Gadget");
      Thread.sleep(TestConstants.SLEEP);
      saveAsUsingToolbarButton(gadgetFile);
      Thread.sleep(TestConstants.SLEEP);
      
      //create groovy script file
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.GROOVY_SCRIPT_FILE);
      Thread.sleep(TestConstants.SLEEP);
      
      //closing all files
      IDE.editor().closeTab(0);
      IDE.editor().closeTab(0);
      IDE.editor().closeTab(0);
      IDE.editor().closeUnsavedFileAndDoNotSave(0);
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
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.SLEEP);
      
      selenium.goBack();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.IDE_LOAD_PERIOD);
      
      checkCkEditorOpened(1);
      
      checkOpenedFilesHistory();
      
      IDE.editor().closeTab(0);
      IDE.editor().closeTabWithNonSaving(0);
//      closeUnsavedFileAndDoNotSave("0");
      
      //open folder to select html file
      openOrCloseFolder(TEST_FOLDER);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      // restore configuration - make code mirror as default editor for html files

//      selectItemInWorkspaceTree(htmlFile);
//      openFileFromNavigationTreeWithCodeEditor(htmlFile, true);      
//      Thread.sleep(TestConstants.IDE_LOAD_PERIOD);
//      CloseFileUtils.closeTab(0);
            
      //delete test folder
      selectItemInWorkspaceTree(TEST_FOLDER);
      deleteSelectedItems();
      Thread.sleep(TestConstants.SLEEP);
      
      //return init configuration
      //select initial workspace
      selectWorkspace(WS_NAME);
      assertTrue(selenium.isTextPresent(WS_NAME));
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
      
      IDE.menu().checkCommandEnabled("Run", "Show Preview", true);
      IDE.menu().checkCommandEnabled("Run", "Deploy Gadget to GateIn", true);
      IDE.menu().checkCommandEnabled("Run", "UnDeploy Gadget from GateIn", true);
      IDE.menu().checkCommandEnabled("Edit", "Hide Line Numbers", true);
      IDE.menu().checkCommandEnabled("Edit", "Format", true);
      IDE.menu().checkCommandEnabled("Edit", "Undo Typing", false);
      IDE.menu().checkCommandEnabled("Edit", "Redo Typing", false);
      Thread.sleep(TestConstants.SLEEP_SHORT);
   }
   
}
