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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * IDE-36:Displaying warning message test.
 * 
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class DisplayingWarningMessageTest extends BaseTest
{
   
   private static final String XML_FILE_NAME = "Untitled file.xml";
   
   private static final String FOLDER_NAME = DisplayingWarningMessageTest.class.getSimpleName();
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" +FOLDER_NAME);
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
   
   //IDE-36:Displaying warning message test.
   //@Ignore
   @Test
   public void displayingWarningMessage() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      createFolder(FOLDER_NAME);
      //--------- 1 -------------------
      //Click on "New->XML File" toolbar button to open new file on Content Panel
      runCommandFromMenuNewOnToolbar(MenuCommands.New.XML_FILE);
      Thread.sleep(TestConstants.SLEEP);
      
      //--------- 2,3 -------------------
      //Try to close file tab.
      //Click on "No" button in confirmation dialog.
      
      //After the step 2: You will see smartGWT â€œDialogs.showErrorâ€� dialog 
      //windows â€œDo you want to save <default XML file name> before closing?".
      closeUnsavedFileAndDoNotSave("0");
      
      //After the step 3: new file tab will be closed, Content Panel will become empty, 
      //"Save" and "Save As" buttons, and "File->Save", "File->Save As" top menu commands 
      //will be disabled.
      checkIsTabPresentInEditorTabset("Untitled file.xml", false);
      checkToolbarButtonState(ToolbarCommands.File.SAVE, false);
      checkToolbarButtonState(ToolbarCommands.File.SAVE_AS, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, false);
      
      //--------- 4 -------------------
      //Click on "File->New->XML File" top menu command to open new file on Content Panel
      runCommandFromMenuNewOnToolbar(MenuCommands.New.XML_FILE);
      Thread.sleep(TestConstants.SLEEP);
      
      //check is file opened
      assertEquals(XML_FILE_NAME + " *", getTabTitle(0));
      
      //--------- 5 -------------------
      //Try to close file tab again.
      closeTab("0");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      //--------- 6 -------------------
      //Click on "Yes" button in confirmation dialog and save file with default name.
      
//      //check is warning dialog appears
//      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/header[contains(text(), 'Close file')]"));
//      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"][contains(text(), 'Do you want to save " 
//         + XML_FILE_NAME + " before closing?')]"));
//      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/"));
//      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/"));
//      //click Yes button
//      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/");
//      Thread.sleep(TestConstants.SLEEP);
      //check is Save As dialog appears
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideAskForValueDialog\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideAskForValueDialogCancelButton\"]/"));
      //save file with default name
      selenium.click("scLocator=//IButton[ID=\"ideAskForValueDialogOkButton\"]/");
      Thread.sleep(TestConstants.SLEEP);
      
      //After the step 6: new file will be saved, and file tab should be closed.
      
      //check is file appeared in workspace tree
      assertElementPresentInWorkspaceTree(XML_FILE_NAME);
      
      //check is file closed
      checkIsTabPresentInEditorTabset("Untitled file.xml", false);
      
      //--------- 7 -------------------
      //Open created earlier xml file and change file content. 
      //Open new file by clicking on "New->Java Script File" button.
      openFileFromNavigationTreeWithCodeEditor(XML_FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);
      
      changeFileContent();
      
      //open javascript file
      runCommandFromMenuNewOnToolbar("JavaScript File");
      Thread.sleep(TestConstants.SLEEP);
      
      //--------- 8 -------------------
      //Trying to reopen created earlier   xml file. 
      openFileFromNavigationTreeWithCodeEditor(XML_FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);
      
      
      //check warning dialog
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/"));
      assertEquals("Info", selenium.getText("scLocator=//Dialog[ID=\"isc_globalWarn\"]/header/"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/"));
      
      //click Ok button
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/");
      Thread.sleep(TestConstants.SLEEP);
      
      //After the step 8: file tab with created earlier xml file should be opened, 
      //content in this tab should be changed, title will be marked by "*" and buttom "Save" and "File->Save" top menu command will be enabled.
      
      checkIsTabPresentInEditorTabset(XML_FILE_NAME, true);
      checkCodeEditorOpened(0);
      
      //check file content
      final String previousContent = "<?xml version='1.0' encoding='UTF-8'?>\n"
         +"<test>\n"
         +"  <settings>param</settings>\n"
         +"  <bean>\n"
         +"    <name>MineBean</name>\n"
         +"  </bean>\n"
         +"</test>";
      
      
      selectIFrameWithEditor(0);
      String text = selenium.getText("//body[@class='editbox']/");
      assertEquals(previousContent, text);
      selectMainFrame();
      Thread.sleep(TestConstants.SLEEP);
      
      //check Save button enabled
      checkToolbarButtonState(ToolbarCommands.File.SAVE, true);
      //check menu Save in File enabled
      checkMenuCommandState(MenuCommands.File.FILE, MenuCommands.File.SAVE, true);
      
      //---------- 9 -----------------
      //Save, close file tab and open created earlier xml file again.
      runToolbarButton(ToolbarCommands.File.SAVE);
      Thread.sleep(TestConstants.SLEEP);
      closeTab("0");
      openFileFromNavigationTreeWithCodeEditor(XML_FILE_NAME, false);
      
      //After the step 9: there is saved file content in the new file tab with title without mark "*".
      
      //check file opened and title doesn't mark with *
//      assertFalse(selenium.isTextPresent(XML_FILE_NAME + " *"));
      
      assertEquals(XML_FILE_NAME, getTabTitle(1));
      
      selectIFrameWithEditor(1);
      String savedText = selenium.getText("//body[@class='editbox']/");
      assertEquals(previousContent, savedText);
      selectMainFrame();
      closeTab("1");
      
      //close untitled JavaScript file
//      closeTab("0");
//      Thread.sleep(TestConstants.SLEEP_SHORT);
//      
//      //check is warning dialog appears
//      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/header[contains(text(), 'Close file')]"));
//      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"][contains(text(), 'Do you want to save Untitled file.xml before closing?')]"));
//      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/"));
//      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/"));
//      
//      //click No button
//      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/");
      closeUnsavedFileAndDoNotSave("0");
      Thread.sleep(TestConstants.SLEEP);
      
      //-------- 10 ---------------
      //Remove created files.
      
      //delete Untitled file.xml
      selectItemInWorkspaceTree(XML_FILE_NAME);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      deleteSelectedItems();
      Thread.sleep(TestConstants.SLEEP);
   }
   private void changeFileContent() throws Exception
   {
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
   
}
