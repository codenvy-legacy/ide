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

      Thread.sleep(TestConstants.SLEEP);

      VirtualFileSystemUtils.mkcol(URL);

      //step 1
      checkNoFileOpened();
      //step 2
      selectRootOfWorkspaceTree();
      //create new html file

      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      
      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      IDE.navigator().selectItem(URL);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

      createSaveAndCloseFile(MenuCommands.New.HTML_FILE, htmlFile, 0);
      //open with WYSIWYG editor and make default
      openFileFromNavigationTreeWithCkEditor(htmlFile, true);
      checkCkEditorOpened(0);

      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.REDO, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);

      //steps 3-5
      //select iframe in first tab
      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[2]//iframe");
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
      Thread.sleep(TestConstants.SLEEP);
      final String typedText = selenium.getText("//body/");
      assertEquals("1\n2\n3", typedText);

      selectMainFrame();

      //step 6
      IDE.toolbar().runCommand(MenuCommands.Edit.UNDO_TYPING);
      Thread.sleep(TestConstants.SLEEP);
      IDE.toolbar().runCommand(MenuCommands.Edit.UNDO_TYPING);
      Thread.sleep(TestConstants.SLEEP);
      //select iframe in first tab
      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[2]//iframe");
      final String revertedText = selenium.getText("//body/");
      selectMainFrame();
      assertEquals("1\n2", revertedText);

      //step 7
      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[2]//iframe");
      selenium.controlKeyDown();
      selenium.keyDown("//", String.valueOf(java.awt.event.KeyEvent.VK_Z));
      selenium.keyUp("//", String.valueOf(java.awt.event.KeyEvent.VK_Z));
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP);
      selenium.controlKeyDown();
      selenium.keyDown("//", String.valueOf(java.awt.event.KeyEvent.VK_Z));
      selenium.keyUp("//", String.valueOf(java.awt.event.KeyEvent.VK_Z));
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP);
      final String revertedText2 = selenium.getText("//body/");
      selectMainFrame();

      assertEquals("1", revertedText2);

      //step 8
      IDE.menu().runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING);
      
      //get and check text
      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[2]//iframe");
      final String revertedText3 = selenium.getText("//body/");
      selectMainFrame();
      assertEquals("", revertedText3);

      //step 9
      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[2]//iframe");
      selenium.controlKeyDown();
      selenium.keyDown("//", String.valueOf(java.awt.event.KeyEvent.VK_Z));
      selenium.keyUp("//", String.valueOf(java.awt.event.KeyEvent.VK_Z));
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP);
      selenium.controlKeyDown();
      selenium.keyDown("//", String.valueOf(java.awt.event.KeyEvent.VK_Z));
      selenium.keyUp("//", String.valueOf(java.awt.event.KeyEvent.VK_Z));
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP);
      //get text
      final String revertedText4 = selenium.getText("//body/");
      selectMainFrame();
      //check text
      assertEquals("", revertedText4);

      //step 10
      IDE.toolbar().runCommand(MenuCommands.Edit.REDO_TYPING);
      Thread.sleep(TestConstants.SLEEP);
      IDE.toolbar().runCommand(MenuCommands.Edit.REDO_TYPING);
      Thread.sleep(TestConstants.SLEEP);
      //get and check text
      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[2]//iframe");
      final String restoredText = selenium.getText("//body/");
      selectMainFrame();
      assertEquals("1", restoredText);

      //step 11
      IDE.menu().runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING);
      
      //get and check text
      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[2]//iframe");
      final String restoredText2 = selenium.getText("//body/");
      selectMainFrame();
      assertEquals("1\n2", restoredText2);

      //step 12
      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[2]//iframe");
      selenium.controlKeyDown();
      selenium.keyDown("//", String.valueOf(java.awt.event.KeyEvent.VK_Y));
      selenium.keyUp("//", String.valueOf(java.awt.event.KeyEvent.VK_Y));
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP);
      selenium.controlKeyDown();
      selenium.keyDown("//", String.valueOf(java.awt.event.KeyEvent.VK_Y));
      selenium.keyUp("//", String.valueOf(java.awt.event.KeyEvent.VK_Y));
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP);
      //get text
      final String restoredText3 = selenium.getText("//body/");
      selectMainFrame();
      //check text
      assertEquals("1\n2\n3", restoredText3);

      //step 13
      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[2]//iframe");
      //click ctrl+Y
      selenium.controlKeyDown();
      selenium.keyDown("//", String.valueOf(java.awt.event.KeyEvent.VK_Y));
      selenium.keyUp("//", String.valueOf(java.awt.event.KeyEvent.VK_Y));
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP);
      selenium.controlKeyDown();
      selenium.keyDown("//", String.valueOf(java.awt.event.KeyEvent.VK_Y));
      selenium.keyUp("//", String.valueOf(java.awt.event.KeyEvent.VK_Y));
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP);
      //get text
      final String restoredText4 = selenium.getText("//body/");
      selectMainFrame();
      //check text
      assertEquals("1\n2\n3", restoredText4);

      //step 14
      IDE.toolbar().runCommand(MenuCommands.Edit.UNDO_TYPING);
      Thread.sleep(TestConstants.SLEEP);
      IDE.toolbar().runCommand(MenuCommands.Edit.UNDO_TYPING);
      Thread.sleep(TestConstants.SLEEP);
      //check text
      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[2]//iframe");
      final String revertedText5 = selenium.getText("//body/");
      selectMainFrame();
      assertEquals("1\n2", revertedText5);

      //step 15
      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[2]//iframe");
      //type text
      selenium.typeKeys("//body/", "a");
      Thread.sleep(TestConstants.SLEEP);
      selectMainFrame();

      //step 16
      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[2]//iframe");
      //click ctrl+Y
      selenium.controlKeyDown();
      selenium.keyDown("//", String.valueOf(java.awt.event.KeyEvent.VK_Y));
      selenium.keyUp("//", String.valueOf(java.awt.event.KeyEvent.VK_Y));
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP);
      //get text
      final String restoredText5 = selenium.getText("//body/");
      selectMainFrame();
      //check text
      assertEquals("1\n2a", restoredText5);

      //step 17
      IDE.toolbar().runCommand(MenuCommands.Edit.UNDO_TYPING);
      Thread.sleep(TestConstants.SLEEP);
      //get text
      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[2]//iframe");
      final String revertedText6 = selenium.getText("//body/");
      selectMainFrame();
      //check text
      assertEquals("1\n2", revertedText6);

      //step 18
      saveCurrentFile();

      //step 19
      IDE.toolbar().runCommand(MenuCommands.Edit.REDO_TYPING);
      //get text
      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[2]//iframe");
      final String restoredText6 = selenium.getText("//body/");
      selectMainFrame();
      //check text
      assertEquals("1\n2a", restoredText6);

      //step 20
      IDE.toolbar().runCommand(MenuCommands.Edit.UNDO_TYPING);
      //get text
      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[2]//iframe");
      final String revertedText7 = selenium.getText("//body/");
      selectMainFrame();
      //check text
      assertEquals("1\n2", revertedText7);

      //step 21
      createSaveAndCloseFile(MenuCommands.New.GOOGLE_GADGET_FILE, googleGadgetFile, 1);
      Thread.sleep(TestConstants.SLEEP);

      Thread.sleep(TestConstants.SLEEP);

      //open with WYSIWYG editor
      openFileFromNavigationTreeWithCkEditor(googleGadgetFile, true);

      //step 22
      //changed content

      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[3]//iframe");
      Thread.sleep(TestConstants.SLEEP);
      selenium.typeKeys("//body/", "1111 ");
      selectMainFrame();
      Thread.sleep(TestConstants.SLEEP);

      //step 23
      //select tab with html file
      IDE.editor().selectTab(0);
      Thread.sleep(TestConstants.SLEEP);

      //step 24
      //select tab with google gadget file
      IDE.editor().selectTab(1);
      Thread.sleep(TestConstants.SLEEP);

      //step 25
      saveCurrentFile();
      Thread.sleep(TestConstants.SLEEP);
      IDE.editor().closeTabWithNonSaving(1);
      Thread.sleep(TestConstants.SLEEP);

      //step 26
      String filePath =
         "src/test/resources/org/exoplatform/ide/operation/edit/undoRedoEditingInWysiwygEditorTest/Example.html";
      uploadFile(MenuCommands.File.OPEN_LOCAL_FILE, filePath, MimeType.TEXT_HTML);
      Thread.sleep(TestConstants.SLEEP);
      checkCkEditorOpened(1);

      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.UNDO, true);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Editor.REDO, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, true);

      //step 27

      //****Todo***fix
      IDE.editor().closeUnsavedFileAndDoNotSave(1);
      //**************

      //step 28

      openFileFromNavigationTreeWithCodeEditor(htmlFile, true);

      saveCurrentFile();
      IDE.editor().closeTabWithNonSaving(0);
      IDE.navigator().selectItem(WS_URL + htmlFile);
      deleteSelectedItems();
      Thread.sleep(TestConstants.SLEEP);

      openFileFromNavigationTreeWithCodeEditor(googleGadgetFile, true);
      saveCurrentFile();
      IDE.editor().closeTabWithNonSaving(0);

      IDE.toolbar().assertButtonExistAtLeft(ToolbarCommands.Editor.UNDO, false);
      IDE.toolbar().assertButtonExistAtLeft(ToolbarCommands.Editor.REDO, false);
      IDE.menu().checkCommandVisibility(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.UNDO_TYPING, false);
      IDE.menu().checkCommandVisibility(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.REDO_TYPING, false);

      IDE.navigator().selectItem(WS_URL + googleGadgetFile);
      deleteSelectedItems();
      Thread.sleep(TestConstants.SLEEP);
   }

   /**
    * @throws Exception
    */
   private void checkNoFileOpened() throws Exception
   {
      String divIndex = "2";

      //check Code Editor is not present in tab 0
      assertFalse(selenium.isElementPresent("//div[@class='tabSetContainer']/div/div[" + divIndex
         + "]//div[@class='CodeMirror-wrapping']/iframe"));
      //check CK editor is not present in tab 0
      assertFalse(selenium.isElementPresent("//div[@class='tabSetContainer']/div/div[" + divIndex
         + "]//table[@class='cke_editor']//td[@class='cke_contents']/iframe"));
   }

}
