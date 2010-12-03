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

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
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
   
   private static final String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";
   
   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(URL + FOLDER_NAME);
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
         VirtualFileSystemUtils.delete(URL + FOLDER_NAME);
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
   @Test
   public void displayingWarningMessage() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      selectItemInWorkspaceTree(FOLDER_NAME);
      //--------- 1 -------------------
      //Click on "New->XML File" toolbar button to open new file on Content Panel
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      Thread.sleep(TestConstants.SLEEP);
      
      //--------- 2,3 -------------------
      //Try to close file tab.
      //Click on "No" button in confirmation dialog.
      
      //After the step 2: You will see smartGWT Dialogs.showError dialog 
      IDE.editor().closeNewFile(0, false, null);
      
      //After the step 3: new file tab will be closed, Content Panel will become empty, 
      //"Save" and "Save As" buttons, and "File->Save", "File->Save As" top menu commands 
      //will be disabled.
      checkIsTabPresentInEditorTabset("Untitled file.xml", false);
      IDE.toolbar().checkButtonEnabled(ToolbarCommands.File.SAVE, false);
      IDE.toolbar().checkButtonEnabled(ToolbarCommands.File.SAVE_AS, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, false);
      
      //--------- 4 -------------------
      //Click on "File->New->XML File" top menu command to open new file on Content Panel
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      Thread.sleep(TestConstants.SLEEP);
      
      //check is file opened
      assertEquals(XML_FILE_NAME + " *", IDE.editor().getTabTitle(0));
      
      //--------- 5 -------------------
      //Try to close file tab again.
      IDE.editor().closeNewFile(0, true, null);
      
      //After the step 6: new file will be saved, and file tab should be closed.
      
      //check is file appeared in workspace tree
      assertElementPresentInWorkspaceTree(XML_FILE_NAME);
      
      //check is file closed
      checkIsTabPresentInEditorTabset(XML_FILE_NAME, false);
      
      //--------- 7 -------------------
      //Open created earlier xml file and change file content. 
      //Open new file by clicking on "New->Java Script File" button.
      openFileFromNavigationTreeWithCodeEditor(XML_FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);
      
      changeFileContent();
      
      //open javascript file
      IDE.toolbar().runCommandFromNewPopupMenu("JavaScript File");
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
      
      assertEquals(previousContent, getTextFromCodeEditor(0));
      
      //check Save button enabled
      IDE.toolbar().checkButtonEnabled(ToolbarCommands.File.SAVE, true);
      //check menu Save in File enabled
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, true);
      
      //---------- 9 -----------------
      //Save, close file tab and open created earlier xml file again.
      IDE.toolbar().runCommand(ToolbarCommands.File.SAVE);
      Thread.sleep(TestConstants.SLEEP);
      IDE.editor().closeTab(0);
      openFileFromNavigationTreeWithCodeEditor(XML_FILE_NAME, false);
      
      //After the step 9: there is saved file content in the new file tab with title without mark "*".
      
      //check file opened and title doesn't mark with *
      assertEquals(XML_FILE_NAME, IDE.editor().getTabTitle(1));

      assertEquals(previousContent, getTextFromCodeEditor(1));
      IDE.editor().closeTab(1);
      
      IDE.editor().closeUnsavedFileAndDoNotSave(0);
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
