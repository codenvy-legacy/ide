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

import org.exoplatform.ide.BaseTest;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class EditFiles extends BaseTest
{
   //IDE-66
   //Store Opened Files History 
   @Test
   public void storeOpenedFilesHistory() throws Exception
   {
      final String testFolder = "Test Folder";
      final String testFolderToDelete = "Test Folder to Delete";
      
      final String textFile = "Test Text File.txt";
      final String htmlFile = "Test Html File.html";
      final String gadgetFile = "Test Gadget File.xml";
      
      Thread.sleep(5000);
      
      //select another workspace
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='Window']", "");
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), 'Select Workspace')]", "");
      Thread.sleep(1000);
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideSelectWorkspaceForm\"]"));
      assertTrue(selenium.isTextPresent("Workspace"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideSelectWorkspaceFormOkButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideSelectWorkspaceFormCancelButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideEntryPointListGrid\"]"));
      assertTrue(selenium.isTextPresent("/rest/private/jcr/repository/production"));
      assertTrue(selenium.isTextPresent("/rest/private/jcr/repository/dev-monit"));
      selenium.click("scLocator=//ListGrid[ID=\"ideEntryPointListGrid\"]/body/row[0]/col[fieldName=entryPoint||0]\"");
      selenium.click("scLocator=//IButton[ID=\"ideSelectWorkspaceFormOkButton\"]");
      Thread.sleep(1000);
      assertTrue(selenium.isTextPresent("production"));
      assertTrue(selenium.isTextPresent("exo:registry"));
      assertTrue(selenium.isTextPresent("jcr:system"));
      Thread.sleep(1000);
      
      selectItemInWorkspaceTree("production");
      Thread.sleep(1000);
      
      createFolder(testFolder);
      Thread.sleep(1000);
      
      selectItemInWorkspaceTree("production");
      Thread.sleep(1000);
      
      createFolder(testFolderToDelete);
      Thread.sleep(1000);
      
      selectItemInWorkspaceTree(testFolderToDelete);
      Thread.sleep(100);
      
      //create txt file
      openNewFileFromToolbar("Text File");
      Thread.sleep(1000);
      saveAsUsingToolbarButton(textFile);
      Thread.sleep(1000);
      
      selectItemInWorkspaceTree(testFolder);
      Thread.sleep(100);
      
      //create html file
      openNewFileFromToolbar("HTML File");
      Thread.sleep(1000);
      saveAsUsingToolbarButton(htmlFile);
      Thread.sleep(1000);
      
      //create google gadget file
      openNewFileFromToolbar("Google Gadget");
      Thread.sleep(1000);
      saveAsUsingToolbarButton(gadgetFile);
      Thread.sleep(1000);
      
      //create groovy script file
      openNewFileFromToolbar("Groovy Script");
      Thread.sleep(1000);
      
      //closing all files
      closeTab("0");
      closeTab("0");
      closeTab("0");
      closeUnsavedFileAndDoNotSave("0");
      Thread.sleep(1000);
      
      openFileFromNavigationTreeWithCodeEditor(textFile);
      openFileFromNavigationTreeWithCodeEditor(gadgetFile);
      openFileWithCkEditorAndSetAsDefault(htmlFile);
      
      checkCkEditorOpened(2);
      
      
      //Open “Server” window with selected at the step 2 workspace URL 
      //and remove folder “Test Folder to Delete”.
      selectItemInWorkspaceTree(testFolderToDelete);
      deleteSelectedItem();
      Thread.sleep(1000);
      
      selenium.open("http://www.google.com.ua/");
      selenium.waitForPageToLoad("10000");
      Thread.sleep(5000);
      
      selenium.goBack();
      selenium.waitForPageToLoad("30000");
      Thread.sleep(5000);
      
      checkCkEditorOpened(1);
      
      checkOpenedFilesHistory();
      
      closeTab("0");
      closeUnsavedFileAndDoNotSave("0");
      
      //open folder to select html file
      openOrCloseFolder(testFolder);
      //return init configuration
      //make code mirror as default editor for html files
      returnCodeEditorAsDefault(htmlFile);
      
      closeTab("0");
      
      //delete test folder
      selectItemInWorkspaceTree(testFolder);
      deleteSelectedItem();
      Thread.sleep(3000);
      
      //return init configuration
      //select another workspace
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='Window']", "");
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), 'Select Workspace')]", "");
      Thread.sleep(5000);
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideSelectWorkspaceForm\"]"));
      assertTrue(selenium.isTextPresent("Workspace"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideSelectWorkspaceFormOkButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideSelectWorkspaceFormCancelButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideEntryPointListGrid\"]"));
      assertTrue(selenium.isTextPresent("/rest/private/jcr/repository/production"));
      assertTrue(selenium.isTextPresent("/rest/private/jcr/repository/dev-monit"));
      selenium.click("scLocator=//ListGrid[ID=\"ideEntryPointListGrid\"]/body/row[1]/col[fieldName=entryPoint||0]\"");
      selenium.click("scLocator=//IButton[ID=\"ideSelectWorkspaceFormOkButton\"]");
      Thread.sleep(1000);
      assertTrue(selenium.isTextPresent("dev-monit"));
      Thread.sleep(1000);
   }
   
   /**
    * 
    * @throws Exception
    */
   private void checkOpenedFilesHistory() throws Exception
   {
      Thread.sleep(3000);
      //check that files are opened and in wright order.
      //check that tab with html file is selected
      assertTrue(selenium.isElementPresent("//div[@class='tabBar']/div/div[3]//td[@class='tabTitle']/span[contains(text(),'Test Gadget File.xml')]"));
      assertTrue(selenium.isElementPresent("//div[@class='tabBar']/div/div[5]//td[@class='tabTitleSelected']/span[contains(text(),'Test Html File.html')]"));
      
      //select Gadget file
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/");
      Thread.sleep(1000);
      
      checkMenuCommandState("Run", "Show Preview", true);
      checkMenuCommandState("Run", "Deploy Gadget to GateIn", true);
      checkMenuCommandState("Run", "UnDeploy Gadget from GateIn", true);
      checkMenuCommandState("Edit", "Hide Line Numbers", true);
      checkMenuCommandState("Edit", "Format", true);
      checkMenuCommandState("Edit", "Undo Typing", false);
      checkMenuCommandState("Edit", "Redo Typing", false);
      Thread.sleep(500);
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
      Thread.sleep(500);
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
      Thread.sleep(1000);
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), 'Open With')]", "");
      //select editor
      selenium.click("scLocator=//ListGrid[ID=\"ideOpenFileWithListGrid\"]/body/row[0]/col[0]");
      //click on checkbox Use as default editor
      selenium.click("scLocator=//Window[ID=\"ideallOpenFileWithForm\"]/item[1][Class=\"DynamicForm\"]/item[name=Default]/textbox");
      Thread.sleep(1000);
      selenium.click("scLocator=//IButton[ID=\"ideOpenFileWithOkButton\"]");
      Thread.sleep(2000);
      //TODO add check that editor opened
   }

}
