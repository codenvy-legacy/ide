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

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_NAME);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + FOLDER_NAME);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   //IDE-36:Displaying warning message test.
   @Test
   public void displayingWarningMessage() throws Exception
   {
      waitForRootElement();

      IDE.NAVIGATION.selectItem(WS_URL + FOLDER_NAME + "/");

      //--------- 1 -------------------
      //Click on "New->XML File" toolbar button to open new file on Content Panel
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

      //--------- 2,3 -------------------
      //Try to close file tab.
      //Click on "No" button in confirmation dialog.

      //After the step 2: You will see smartGWT Dialogs.showError dialog 
      IDE.EDITOR.closeNewFile(0, false, null);
      
      //After the step 3: new file tab will be closed, Content Panel will become empty, 
      //"Save" and "Save As" buttons, and "File->Save", "File->Save As" top menu commands 
      //will be disabled.
      IDE.EDITOR.checkIsTabPresentInEditorTabset("Untitled file.xml *", false);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SAVE, false);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SAVE_AS, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS, false);

      //--------- 4 -------------------
      //Click on "File->New->XML File" top menu command to open new file on Content Panel
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

      //check is file opened
      assertEquals(XML_FILE_NAME + " *", IDE.EDITOR.getTabTitle(0));

      //--------- 5 -------------------
      //Try to close file tab again.
      IDE.EDITOR.closeNewFile(0, true, null);

      //After the step 6: new file will be saved, and file tab should be closed.

      //check is file appeared in workspace tree
      IDE.NAVIGATION.selectItem(WS_URL + FOLDER_NAME + "/" + XML_FILE_NAME);

      //check is file closed
      IDE.EDITOR.checkIsTabPresentInEditorTabset(XML_FILE_NAME, false);

      //--------- 7 -------------------
      //Open created earlier xml file and change file content. 
      //Open new file by clicking on "New->Java Script File" button.
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER_NAME + "/" + XML_FILE_NAME, false);

      changeFileContent();

      //open javascript file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.JAVASCRIPT_FILE);

      //--------- 8 -------------------
      //Trying to reopen created earlier xml file. 
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER_NAME + "/" + XML_FILE_NAME, false);
      
      IDE.ASK_DIALOG.assertOpened("Info");
      IDE.ASK_DIALOG.clickNo();

      //After the step 8: file tab with created earlier xml file should be opened, 
      //content in this tab should be changed, title will be marked by "*" and buttom "Save" and "File->Save" top menu command will be enabled.

      System.out.println("opened file 1 > " + IDE.EDITOR.getTabTitle(0));
      System.out.println("opened file 2 > " + IDE.EDITOR.getTabTitle(1));
      
      assertEquals(XML_FILE_NAME + " *", IDE.EDITOR.getTabTitle(0));
      IDE.EDITOR.checkIsTabPresentInEditorTabset(XML_FILE_NAME + " *", true);
      
      

      IDE.EDITOR.checkEditorTabSelected(XML_FILE_NAME, true);
      IDE.EDITOR.checkCodeEditorOpened(0);

      //check file content
      final String previousContent =
         "<?xml version='1.0' encoding='UTF-8'?>\n" + "<test>\n" + "  <settings>param</settings>\n" + "  <bean>\n"
            + "    <name>MineBean</name>\n" + "  </bean>\n" + "</test>";

      assertEquals(previousContent, IDE.EDITOR.getTextFromCodeEditor(0));

      //check Save button enabled
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.SAVE, true);
      //check menu Save in File enabled
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, true);

      //---------- 9 -----------------
      //Save, close file tab and open created earlier xml file again.
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.SAVE);
      IDE.EDITOR.closeTab(0);
      
      
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(XML_FILE_NAME, false);

      //After the step 9: there is saved file content in the new file tab with title without mark "*".

      //check file opened and title doesn't mark with *
      assertEquals(XML_FILE_NAME, IDE.EDITOR.getTabTitle(1));
      assertEquals(previousContent, IDE.EDITOR.getTextFromCodeEditor(1));
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
