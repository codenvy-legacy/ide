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
package org.exoplatform.ide.operation.edit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class UndoRedoEditingInWysiwygEditorTest extends BaseTest
{

   //IDE-122 Undo/Redo Editing in WYSIWYG editor 
   //@Ignore
   @Test
   public void undoRedoEditingInWysiwydEditor() throws Exception
   {
      final String htmlFile = "testHtmlFile.html";

      final String googleGadgetFile = "testGadgetFile.xml";

      final String FolderName = UndoRedoEditingInWysiwygEditorTest.class.getSimpleName();

      final String URL =
         BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FolderName + "/";

      IDE.WORKSPACE.waitForRootItem();
      VirtualFileSystemUtils.mkcol(URL);

      //step 1
      IDE.WORKSPACE.waitForRootItem();
      checkNoFileOpened();

      //step 2
      IDE.WORKSPACE.selectRootItem();
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);
      //create new html file

      IDE.WORKSPACE.selectItem(URL);

      createSaveAndCloseFile(MenuCommands.New.HTML_FILE, htmlFile, 0);
      //open with WYSIWYG editor and make default
      openFileFromNavigationTreeWithCkEditor(URL + htmlFile, "HTML", false);
      IDE.EDITOR.checkCkEditorOpened(0);

      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.REDO, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);

      //steps 3-5
      //select iframe in first tab
      IDE.EDITOR.selectIFrameWithEditor(0);
      final String defaultText = selenium.getText("//body/");
      assertEquals("", defaultText);

      //type text
      selenium.typeKeys("//body/", "1");
      selenium.keyDown("//body/", "\\13");
      selenium.keyUp("//body/", "\\13");
      selenium.typeKeys("//body/", "2");
      selenium.keyDown("//body/", "\\13");
      selenium.keyUp("//body/", "\\13");
      selenium.typeKeys("//body[@class='cke_show_borders']/", "3");
      final String typedText = selenium.getText("//body/");
      assertEquals("1\n2\n3", typedText);

      IDE.selectMainFrame();

      //step 6
      IDE.TOOLBAR.runCommand(MenuCommands.Edit.UNDO_TYPING);
      IDE.TOOLBAR.runCommand(MenuCommands.Edit.UNDO_TYPING);
      //select iframe in first tab
      IDE.EDITOR.selectIFrameWithEditor(0);
      final String revertedText = selenium.getText("//body/");
      IDE.selectMainFrame();
      assertEquals("1\n2", revertedText);

      //step 7
      IDE.EDITOR.selectIFrameWithEditor(0);
      selenium.controlKeyDown();
      selenium.keyDown("//", String.valueOf(java.awt.event.KeyEvent.VK_Z));
      selenium.keyUp("//", String.valueOf(java.awt.event.KeyEvent.VK_Z));
      selenium.controlKeyUp();
      selenium.controlKeyDown();
      selenium.keyDown("//", String.valueOf(java.awt.event.KeyEvent.VK_Z));
      selenium.keyUp("//", String.valueOf(java.awt.event.KeyEvent.VK_Z));
      selenium.controlKeyUp();
      final String revertedText2 = selenium.getText("//body/");
      IDE.selectMainFrame();

      assertEquals("1", revertedText2);

      //step 8
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);

      //get and check text
      IDE.EDITOR.selectIFrameWithEditor(0);
      final String revertedText3 = selenium.getText("//body/");
      IDE.selectMainFrame();
      assertEquals("", revertedText3);

      //step 9
      IDE.EDITOR.selectIFrameWithEditor(0);
      selenium.controlKeyDown();
      selenium.keyDown("//", String.valueOf(java.awt.event.KeyEvent.VK_Z));
      selenium.keyUp("//", String.valueOf(java.awt.event.KeyEvent.VK_Z));
      selenium.controlKeyUp();
      selenium.controlKeyDown();
      selenium.keyDown("//", String.valueOf(java.awt.event.KeyEvent.VK_Z));
      selenium.keyUp("//", String.valueOf(java.awt.event.KeyEvent.VK_Z));
      selenium.controlKeyUp();
      //get text
      final String revertedText4 = selenium.getText("//body/");
      IDE.selectMainFrame();
      //check text
      assertEquals("", revertedText4);

      //step 10
      IDE.TOOLBAR.runCommand(MenuCommands.Edit.REDO_TYPING);
      //Thread.sleep(TestConstants.SLEEP);
      IDE.TOOLBAR.runCommand(MenuCommands.Edit.REDO_TYPING);
      //Thread.sleep(TestConstants.SLEEP);
      //get and check text

      IDE.EDITOR.selectIFrameWithEditor(0);
      final String restoredText = selenium.getText("//body/");
      IDE.selectMainFrame();
      assertEquals("1", restoredText);

      //step 11
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);

      //get and check text
      IDE.EDITOR.selectIFrameWithEditor(0);
      final String restoredText2 = selenium.getText("//body/");
      IDE.selectMainFrame();
      assertEquals("1\n2", restoredText2);

      //step 12
      IDE.EDITOR.selectIFrameWithEditor(0);
      selenium.controlKeyDown();
      selenium.keyDown("//", String.valueOf(java.awt.event.KeyEvent.VK_Y));
      selenium.keyUp("//", String.valueOf(java.awt.event.KeyEvent.VK_Y));
      selenium.controlKeyUp();
      //Thread.sleep(TestConstants.SLEEP);
      selenium.controlKeyDown();
      selenium.keyDown("//", String.valueOf(java.awt.event.KeyEvent.VK_Y));
      selenium.keyUp("//", String.valueOf(java.awt.event.KeyEvent.VK_Y));
      selenium.controlKeyUp();
      //get text
      final String restoredText3 = selenium.getText("//body/");
      IDE.selectMainFrame();
      //check text
      assertEquals("1\n2\n3", restoredText3);

      //step 13
      IDE.EDITOR.selectIFrameWithEditor(0);
      //click ctrl+Y
      selenium.controlKeyDown();
      selenium.keyDown("//", String.valueOf(java.awt.event.KeyEvent.VK_Y));
      selenium.keyUp("//", String.valueOf(java.awt.event.KeyEvent.VK_Y));
      selenium.controlKeyUp();
      selenium.controlKeyDown();
      selenium.keyDown("//", String.valueOf(java.awt.event.KeyEvent.VK_Y));
      selenium.keyUp("//", String.valueOf(java.awt.event.KeyEvent.VK_Y));
      selenium.controlKeyUp();
      //get text
      final String restoredText4 = selenium.getText("//body/");
      IDE.selectMainFrame();
      //check text
      assertEquals("1\n2\n3", restoredText4);

      //step 14
      IDE.TOOLBAR.runCommand(MenuCommands.Edit.UNDO_TYPING);
      IDE.TOOLBAR.runCommand(MenuCommands.Edit.UNDO_TYPING);

      //check text
      IDE.EDITOR.selectIFrameWithEditor(0);
      final String revertedText5 = selenium.getText("//body/");
      IDE.selectMainFrame();
      assertEquals("1\n2", revertedText5);

      //step 15
      IDE.EDITOR.selectIFrameWithEditor(0);
      //IDE.EDITOR.selectIFrameWithEditor(0);
      //type text
      selenium.typeKeys("//body/", "a");
      IDE.selectMainFrame();

      //step 16
      IDE.EDITOR.selectIFrameWithEditor(0);
      //click ctrl+Y
      selenium.controlKeyDown();
      selenium.keyDown("//", String.valueOf(java.awt.event.KeyEvent.VK_Y));
      selenium.keyUp("//", String.valueOf(java.awt.event.KeyEvent.VK_Y));
      selenium.controlKeyUp();
      //get text
      final String restoredText5 = selenium.getText("//body/");
      IDE.selectMainFrame();
      //check text
      assertEquals("1\n2a", restoredText5);

      //step 17
      IDE.TOOLBAR.runCommand(MenuCommands.Edit.UNDO_TYPING);
      //get text
      IDE.EDITOR.selectIFrameWithEditor(0);
      final String revertedText6 = selenium.getText("//body/");
      IDE.selectMainFrame();
      //check text
      assertEquals("1\n2", revertedText6);

      //step 18
      saveCurrentFile();

      //step 19
      IDE.TOOLBAR.runCommand(MenuCommands.Edit.REDO_TYPING);
      //get text
      IDE.EDITOR.selectIFrameWithEditor(0);
      final String restoredText6 = selenium.getText("//body/");
      IDE.selectMainFrame();
      //check text
      assertEquals("1\n2a", restoredText6);

      //step 20
      IDE.TOOLBAR.runCommand(MenuCommands.Edit.UNDO_TYPING);
      //get text
      IDE.EDITOR.selectIFrameWithEditor(0);
      final String revertedText7 = selenium.getText("//body/");
      IDE.selectMainFrame();
      //check text
      assertEquals("1\n2", revertedText7);

      //step 21
      createSaveAndCloseFile(MenuCommands.New.GOOGLE_GADGET_FILE, googleGadgetFile, 1);
      Thread.sleep(TestConstants.SLEEP / 3);

      //open with WYSIWYG editor
      openFileFromNavigationTreeWithCkEditor(URL + googleGadgetFile, "Google Gadget", false);

      //step 22
      //changed content

      IDE.EDITOR.selectIFrameWithEditor(1);
      selenium.typeKeys("//body/", "1111 ");
      IDE.selectMainFrame();

      //step 23
      //select tab with html file
      IDE.EDITOR.selectTab(0);

      //step 24
      //select tab with google gadget file
      IDE.EDITOR.selectTab(1);

      //step 25
      saveCurrentFile();
      IDE.EDITOR.closeFile(1);

      //step 26
      //     //TODO must be set id in upload meme type form 
      //     String filePath =
      //         "src/test/resources/org/exoplatform/ide/operation/edit/undoRedoEditingInWysiwygEditorTest/Example.html";
      //      uploadFile(MenuCommands.File.OPEN_LOCAL_FILE, filePath, MimeType.TEXT_HTML);
      //      //Thread.SLEEP/3(TestConstants.SLEEP/3);
      //      checkCkEditorOpened(1);
      //
      //      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);
      //      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.REDO, true);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      //      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);

      //step 27

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL + htmlFile, false);

      //TODO may be remove after fix ask dialog after reopen file
      if (selenium.isElementPresent("exoAskDialog"))
      {
         selenium.click("exoAskDialogYesButton");
         waitForElementNotPresent("exoAskDialog");
      }
      saveCurrentFile();
      IDE.EDITOR.closeFile(0);
      IDE.WORKSPACE.selectItem(URL + htmlFile);
      IDE.NAVIGATION.deleteSelectedItems();

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL + googleGadgetFile, false);
      
      saveCurrentFile();
      IDE.EDITOR.closeFile(0);

      IDE.TOOLBAR.assertButtonExistAtLeft(ToolbarCommands.Editor.UNDO, false);
      IDE.TOOLBAR.assertButtonExistAtLeft(ToolbarCommands.Editor.REDO, false);
      IDE.MENU.checkCommandVisibility(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, false);
      IDE.MENU.checkCommandVisibility(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, false);

      IDE.WORKSPACE.selectItem(URL + googleGadgetFile);
      IDE.NAVIGATION.deleteSelectedItems();

   }

   /**
    * @throws Exception
    */
   private void checkNoFileOpened() throws Exception
   {
      String divIndex = "0";

      //check Code Editor is not present in tab 0
      assertFalse(selenium.isElementPresent("//div[@panel-id='editor'and @tab-index=" + "'" + divIndex + "'" + "]"
         + "//div[@class='CodeMirror-wrapping']/iframe"));
      //check CK editor is not present in tab 0
      assertFalse(selenium.isElementPresent("//div[@panel-id='editor'and @tab-index=" + "'" + divIndex + "'" + "]"
         + "//div[@class='CodeMirror-wrapping']/iframe"));
   }

}
