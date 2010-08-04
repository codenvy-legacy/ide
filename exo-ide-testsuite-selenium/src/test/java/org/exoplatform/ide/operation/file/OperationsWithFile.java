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
public class OperationsWithFile extends BaseTest
{
   
   private final static String FOLDER_NAME = "Test";
   
   private final static String FOLDER_NAME_2 = "Test 2";
   
   private final static String FILE_NAME = "RepoFile.xml";
   
   private final static String SAVED_FILE_XML = "Saved File.xml";
   
   private final static String SAVED_FILE_GROOVY = "Saved File.groovy";
   
   //IDE-13:Saving previously edited file.
   //@Ignore
   @Test
   public void savePreviouslyEditedFile() throws Exception
   {
      createFolder(FOLDER_NAME);
      
      Thread.sleep(1000);
      
      assertElementPresentInWorkspaceTree(FOLDER_NAME);
      
      openNewFileFromToolbar("XML File");
      Thread.sleep(1000);
      
      //is file opened
      assertTrue(selenium.isTextPresent("Untitled file.xml *"));
      
      saveAsUsingToolbarButton(FILE_NAME);
      
      Thread.sleep(1000);
      
      //is file saved
      assertFalse(selenium.isTextPresent("Untitled file.xml *"));
      assertTrue(selenium.isTextPresent(FILE_NAME));
      
      assertElementPresentInWorkspaceTree(FILE_NAME);
      
      //close Test folder
      selenium.click("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=" 
         + FOLDER_NAME + "]/col[0]/open");
      Thread.sleep(1000);
      //checks, then new file created in Test folder.
      //When Test folder is closed, RepoFile.xml doesn't shown in Workspace tree
      assertFalse(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=" 
         + FILE_NAME + "]/col[0]"));
      
      //open Test folder
      selenium.click("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=" 
         + FOLDER_NAME + "]/col[0]/open");
      
      Thread.sleep(1000);
      
      //go to server window and check file
      checkFileOnWebDav();
      
      Thread.sleep(5000);
      
      //type something in file
      changeFileContent();
      
      Thread.sleep(1000);
      
      saveCurrentFile();
      
      Thread.sleep(1000);
      
      closeTab("0");
      
      Thread.sleep(1000);
      
      //check file content after refresh
      selenium.refresh();
      
      selenium.waitForPageToLoad("30000");
      
      Thread.sleep(5000);
      
      //open Test folder
      openOrCloseFolder(FOLDER_NAME);
      
      Thread.sleep(5000);
      
      assertElementPresentInWorkspaceTree(FILE_NAME);
      
      selectItemInWorkspaceTree(FILE_NAME);
      
      Thread.sleep(1000);
      
      openFileWithCodeEditor(FILE_NAME);
      
      Thread.sleep(3000);
      
      changeOpenedFileContent();
      
//      saveCurrentFile();
      Thread.sleep(1000);
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
      Thread.sleep(1000);
      assertTrue(selenium.isElementPresent("//td[@class='exo-popupMenuTitleField']/nobr[text()='Save']"));
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[text()='Save']", "");
      Thread.sleep(1000);
      
      closeTab("0");
      Thread.sleep(500);
      
      selectItemInWorkspaceTree(FOLDER_NAME);
      
      Thread.sleep(1000);
      
      deleteSelectedItem();
      
      Thread.sleep(5000);
   }
   
   //TODO:
   //when you will be able to find iframe by id
   //changed this test
   //IDE-54:Save All Files
   //@Ignore
   @Test
   public void saveAllFiles() throws Exception
   {
      createFolder(FOLDER_NAME);
      Thread.sleep(1000);
      
      selectItemInWorkspaceTree("dev-monit");
      createFolder(FOLDER_NAME_2);
      Thread.sleep(1000);
      
      selectItemInWorkspaceTree(FOLDER_NAME);
      Thread.sleep(500);
      
      openNewFileFromToolbar("XML File");
      Thread.sleep(1000);
      
      //open menu File
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
      Thread.sleep(1000);
      
      //check is Save All disabled
      assertTrue(selenium.isElementPresent(
         "//td[@class='exo-popupMenuTitleFieldDisabled']/nobr[contains(text(), 'Save All')]"));
      
      //save file as
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), 'Save As')]", "");
      selenium.click("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField||title=ideAskForValueDialogValueField||Class=TextItem]/element");
      selenium.type("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/" 
         + "item[name=ideAskForValueDialogValueField||title=ideAskForValueDialogValueField||Class=TextItem]/element", "");
      selenium.type("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/" 
         + "item[name=ideAskForValueDialogValueField||title=ideAskForValueDialogValueField||Class=TextItem]/element", SAVED_FILE_XML);
      selenium.click("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/");
      
      Thread.sleep(2000);
      
      closeTab("0");
      Thread.sleep(1000);
      
      selectItemInWorkspaceTree(FOLDER_NAME_2);
      Thread.sleep(500);
      
      openNewFileFromToolbar("Groovy Script");
      Thread.sleep(1000);
      
      //open menu File
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
      Thread.sleep(1000);
      
      //check is Save All disabled
      assertTrue(selenium.isElementPresent(
         "//td[@class='exo-popupMenuTitleFieldDisabled']/nobr[text()='Save All...']"));
      
      //save file as
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), 'Save As')]", "");
      selenium.click("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/item[name=ideAskForValueDialogValueField||title=ideAskForValueDialogValueField||Class=TextItem]/element");
      selenium.type("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/" 
         + "item[name=ideAskForValueDialogValueField||title=ideAskForValueDialogValueField||Class=TextItem]/element", "");
      selenium.type("scLocator=//Window[ID=\"ideAskForValueDialog\"]/item[0][Class=\"DynamicForm\"]/" 
         + "item[name=ideAskForValueDialogValueField||title=ideAskForValueDialogValueField||Class=TextItem]/element", SAVED_FILE_GROOVY);
      selenium.click("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/");
      Thread.sleep(2000);
      
      closeTab("0");
      Thread.sleep(1000);
      
      openFileWithCodeEditor(SAVED_FILE_XML);
      Thread.sleep(1000);
      //at the end of line
      selenium.keyDown("//body[@class='editbox']/", "\\35");
      //enter
      selenium.keyDown("//body[@class='editbox']/", "\\13");
      selenium.keyUp("//body[@class='editbox']/", "\\13");
      
      Thread.sleep(100);
      selenium.typeKeys("//body[@class='editbox']/", "<root>");
      selenium.typeKeys("//body[@class='editbox']/", "admin");
      selenium.typeKeys("//body[@class='editbox']/", "</root>");
      Thread.sleep(1000);
      
      //create html file from template
      selenium.mouseDownAt("//div[@title='New']//img", "");
      selenium.mouseUpAt("//div[@title='New']//img", "");
      selenium.mouseDownAt("//td[@class=\"exo-popupMenuTitleField\"]//nobr[contains(text(), \"" 
         + "From Template" + "\")]", "");
      Thread.sleep(1000);
      selenium.click("scLocator=//ListGrid[ID=\"ideCreateFileFromTemplateFormListGrid\"]/body/row[1]/col[1]");
      selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCreateButton\"]/");
      Thread.sleep(1000);
      
      //create text file from template
      selenium.mouseDownAt("//div[@title='New']//img", "");
      selenium.mouseUpAt("//div[@title='New']//img", "");
      selenium.mouseDownAt("//td[@class=\"exo-popupMenuTitleField\"]//nobr[contains(text(), \"" 
         + "From Template" + "\")]", "");
      Thread.sleep(1000);
      selenium.click("scLocator=//ListGrid[ID=\"ideCreateFileFromTemplateFormListGrid\"]/body/row[2]/col[1]");
      selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCreateButton\"]/");
      Thread.sleep(1000);
      
      //open menu File
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
      Thread.sleep(1000);
      
      //check is Save All enabled
      assertTrue(selenium.isElementPresent(
         "//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), 'Save All')]"));
      
      //click Save All
      selenium.mouseDownAt(
         "//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), 'Save All')]", "");
      Thread.sleep(2000);
      
      //is file opened
      assertTrue(selenium.isTextPresent("Untitled file.html *"));
      assertTrue(selenium.isTextPresent("Untitled file.txt *"));
      assertFalse(selenium.isTextPresent(SAVED_FILE_XML + " *"));
      assertTrue(selenium.isTextPresent(SAVED_FILE_XML));
      
      //Close Saved file.xml
      closeTab("0");
      
      //close Untitled file.txt
      closeTab("1");
      Thread.sleep(500);
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/");
      Thread.sleep(1000);
      
      //close Untitled file.html
      closeTab("0");
      Thread.sleep(500);
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/");
      Thread.sleep(1000);
      
      selectItemInWorkspaceTree(FOLDER_NAME);
      deleteSelectedItem();
      Thread.sleep(1000);
      
      selectItemInWorkspaceTree(FOLDER_NAME_2);
      deleteSelectedItem();
      Thread.sleep(5000);
   }
   
   @Test
   public void displayingWarningMessage() throws Exception
   {
      openNewFileFromToolbar("XML File");
      Thread.sleep(1000);
      
      closeUnsavedFileAndDoNotSave("0");
      
      assertFalse(selenium.isTextPresent("Untitled file.xml"));
      
      openNewFileFromToolbar("XML File");
      Thread.sleep(5000);
      
      //check is file opened
      assertTrue(selenium.isTextPresent("Untitled file.xml *"));
      
      closeTab("0");
      Thread.sleep(500);
      
      //check is warning dialog appears
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/header[contains(text(), 'Close file')]"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"][contains(text(), 'Do you want to save Untitled file.xml before closing?')]"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/"));
      //click Yes button
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/");
      //check is Save As dialog appears
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideAskForValueDialog\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogCancelButton\"]/"));
      //save file with default name
      selenium.click("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/");
      Thread.sleep(1000);
      
      //check is file appeard in workspace tree
      assertElementPresentInWorkspaceTree("Untitled file.xml");
      
      //check is file closed
      assertFalse(selenium.isTextPresent("Untitled file.xml *"));
      
      selectItemInWorkspaceTree("Untitled file.xml");
      Thread.sleep(500);
      
      openFileWithCodeEditor("Untitled file.xml");
      
      changeFileContent();
      
      openNewFileFromToolbar("JavaScript File");
      Thread.sleep(1000);
      
      openFileWithCodeEditor("Untitled file.xml");
      
      //check file opened
      assertTrue(selenium.isTextPresent("Untitled file.xml *"));
      
      //check file content
      final String previousContent = "<?xml version='1.0' encoding='UTF-8'?>\n"
         +"<test>\n"
         +"  <settings>param</settings>\n"
         +"  <bean>\n"
         +"    <name>MineBean</name>\n"
         +"  </bean>\n"
         +"</test>";
      
      
      selectEditor(0);
      String text = selenium.getText("//body[@class='editbox']/");
      assertTrue(text.startsWith("<?xml version='1.0' encoding='UTF-8'?>"));
      
      assertTrue(text.equals(previousContent));
      selectMainFrame();
      
      //check Save button enabled
      assertTrue(selenium.isElementPresent("//div[@title='Save']/div[@elementenabled='true']"));
      
      //open menu File
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
      Thread.sleep(1000);
      
      //check is Save enabled
      assertTrue(selenium.isElementPresent(
         "//td[@class='exo-popupMenuTitleField']/nobr[text()='Save']"));
      
      //save file
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[text()='Save']", "");
      Thread.sleep(1000);
      
      closeTab("0");
      Thread.sleep(500);
      
      openFileWithCodeEditor("Untitled file.xml");
      
      //check file opened and title doesn't mark with *
      assertFalse(selenium.isTextPresent("Untitled file.xml *"));
      
      assertTrue(selenium.isTextPresent("Untitled file.xml"));
      
      selectEditor(1);
      String savedText = selenium.getText("//body[@class='editbox']/");
      assertTrue(savedText.startsWith("<?xml version='1.0' encoding='UTF-8'?>"));
      
      assertTrue(savedText.equals(previousContent));
      selectMainFrame();
      
      closeTab("1");
      
      //close untitled JavaScript file
      closeTab("0");
      Thread.sleep(500);
      
      //check is warning dialog appears
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/header[contains(text(), 'Close file')]"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"][contains(text(), 'Do you want to save Untitled file.xml before closing?')]"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/"));
      
      //click No button
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/");
      Thread.sleep(1000);
      
      //delete Untitled file.xml
      selectItemInWorkspaceTree("Untitled file.xml");
      Thread.sleep(500);
      
      deleteSelectedItem();
      Thread.sleep(1000);
   }
   
   private void changeOpenedFileContent() throws Exception
   {
      
      final String previousContent = "<?xml version='1.0' encoding='UTF-8'?>\n"
         +"<test>\n"
         +"  <settings>param</settings>\n"
         +"  <bean>\n"
         +"    <name>MineBean</name>\n"
         +"  </bean>\n"
         +"</test>";
      
      
      
      //change file content
      
      String text = selenium.getText("//body[@class='editbox']/");
      assertTrue(text.startsWith("<?xml version='1.0' encoding='UTF-8'?>"));
      
      assertTrue(text.equals(previousContent));
      
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
      selenium.typeKeys("//body[@class='editbox']/", "<root>");
      selenium.typeKeys("//body[@class='editbox']/", "admin");
      selenium.typeKeys("//body[@class='editbox']/", "</root>");
   }
   
   private void changeFileContent() throws Exception
   {
//      String text = selenium.getText("//body[@class='editbox']/");
//      assertTrue(text.startsWith("<?xml version='1.0' encoding='UTF-8'?>"));
//     
      selenium.mouseDownAt("//body[@class='editbox']//span[2]", "");
      selenium.mouseUpAt("//body[@class='editbox']//span[2]", "");
      
      //change file content
      selenium.keyDown("//body[@class='editbox']/", "\\35");
      selenium.keyDown("//body[@class='editbox']/", "\\13");
      selenium.keyUp("//body[@class='editbox']/", "\\13");
      Thread.sleep(100);
      selenium.typeKeys("//body[@class='editbox']/", "<test>");
      selenium.keyDown("//body[@class='editbox']/", "\\13");
      selenium.keyUp("//body[@class='editbox']/", "\\13");
      selenium.typeKeys("//body[@class='editbox']/", "<settings>");
      selenium.typeKeys("//body[@class='editbox']/", "param");
      selenium.typeKeys("//body[@class='editbox']/", "</settings>");
      selenium.keyDown("//body[@class='editbox']/", "\\13");
      selenium.keyUp("//body[@class='editbox']/", "\\13");
      selenium.typeKeys("//body[@class='editbox']/", "<bean>");
      selenium.keyDown("//body[@class='editbox']/", "\\13");
      selenium.keyUp("//body[@class='editbox']/", "\\13");
      selenium.typeKeys("//body[@class='editbox']/", "<name>");
      selenium.typeKeys("//body[@class='editbox']/", "MineBean");
      selenium.typeKeys("//body[@class='editbox']/", "</name>");
      selenium.keyDown("//body[@class='editbox']/", "\\13");
      selenium.keyUp("//body[@class='editbox']/", "\\13");
      selenium.typeKeys("//body[@class='editbox']/", "</bean>");
      selenium.keyDown("//body[@class='editbox']/", "\\13");
      selenium.keyUp("//body[@class='editbox']/", "\\13");
      selenium.typeKeys("//body[@class='editbox']/", "</test>");
   }
   
   private void checkFileOnWebDav() throws Exception
   {
//      selenium.openWindow("http://127.0.0.1:8888/rest/private/jcr/repository/dev-monit/", "WEBDAV Browser");
//      selenium.waitForPopUp("WEBDAV Browser", "10000");
//      selenium.selectPopUp("WEBDAV Browser");
      selenium.open("http://127.0.0.1:8888/rest/private/jcr/repository/dev-monit/");
      
      selenium.waitForPageToLoad("10000");
      
      assertTrue(selenium.isElementPresent("link=" + FOLDER_NAME));
      
      selenium.click("link=" + FOLDER_NAME);
      
      Thread.sleep(1000);
      
      assertTrue(selenium.isElementPresent("link=" + FILE_NAME));
      
      selenium.goBack();
      selenium.waitForPageToLoad("10000");
      selenium.goBack();
      selenium.waitForPageToLoad("30000");
      
//      selenium.getEval("selenium.browserbot.getCurrentWindow().close()");
//      selenium.selectWindow("IDEall");
   }
     

}
