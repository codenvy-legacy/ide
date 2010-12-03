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
package org.exoplatform.ide.miscellaneous;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.utils.AbstractTextUtil;
import org.junit.After;
import org.junit.Test;

/**
 * IDE-156:HotKeys customization.
 * 
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:musienko.maxim@gmail.com">Musienko Maxim</a>
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class HotkeysInCodeMirrorTest extends AbstractHotkeysTest
{
   private static final String FIND_REPLACE_FORM_LOCATOR = "scLocator=//Window[ID=\"ideFindReplaceForm\"]";

   private static final String GO_TO_LINE_FORM_LOCATOR = "scLocator=//Window[ID=\"ideGoToLineForm\"]";
   
   @After
   public void tearDown()
   {
      cleanRegistry();
      deleteCookies();
   }
   
   /**
    * IDE-156:HotKeys customization
    * ----- 6-12 ------------
    * @throws Exception
    */
   @Test
   public void testHotkeysWithinCodeEditor() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      //----- 1 ------------
      //Create new text file
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      //type text to editor
      
      final String text = "Text File";
      typeTextIntoEditor(0, text);
      //----- 2 ------------
      //Press Ctrl+F
      //Find-replace form appeared
      selectIFrameWithEditor(0);
      selenium.click("//body");
      Thread.sleep(TestConstants.SLEEP);
      selenium.controlKeyDown();
      selenium.keyDown("//body[@class='editbox']", "F");
      selenium.keyUp("//body[@class='editbox']", "F");
      selenium.controlKeyUp();
      selectMainFrame();
      Thread.sleep(TestConstants.SLEEP);
      //check find-replace form appeared
      assertTrue(selenium.isElementPresent(FIND_REPLACE_FORM_LOCATOR));
      //close form
      closeForm(FIND_REPLACE_FORM_LOCATOR);
      
      //----- 3 ------------
      //check Ctrl+D
      selectIFrameWithEditor(0);
      selenium.controlKeyDown();
      selenium.keyDown("//body[@class='editbox']", "D");
      selenium.keyUp("//body[@class='editbox']", "D");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selectMainFrame();
      assertEquals("", getTextFromCodeEditor(0));
      Thread.sleep(TestConstants.SLEEP);
      
      //----- 4 ------------
      //check Ctrl+L
      selectIFrameWithEditor(0);
      selenium.controlKeyDown();
      selenium.keyDown("//body[@class='editbox']", "L");
      selenium.keyUp("//body[@class='editbox']", "L");
      selenium.controlKeyUp();
      selectMainFrame();
      Thread.sleep(TestConstants.SLEEP);
      //check go to line window dialog appeared
      assertTrue(selenium.isElementPresent(GO_TO_LINE_FORM_LOCATOR));
      //close
      closeForm(GO_TO_LINE_FORM_LOCATOR);
      selectMainFrame();
      //TODO: Ctrl+Home, Ctrl+End
      
      IDE.editor().closeUnsavedFileAndDoNotSave(0);
   }
   
   @Test
   public void testCopyPasetHotkeys() throws Exception
   {
      refresh();
      //----- 1 ------------
      //Create new text file
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      //type text to editor
      
      //----- 10 ------------
      //check Ctrl+C, Ctrl+V
      Thread.sleep(TestConstants.SLEEP);
      
      final String textToEdit = "text to edit";
      typeTextIntoEditor(0, textToEdit);
      
      Thread.sleep(TestConstants.SLEEP);
      selectIFrameWithEditor(0);
      selenium.click("//body");
      
      //select all text
      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_A);
      selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //copy text by Ctrl+C
      selenium.controlKeyDown();
      selenium.keyPress("//body[@class='editbox']", "c");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, "\n");
      
      //paste text by pressing Ctrl+V
      selenium.controlKeyDown();
      selenium.keyPress("//body[@class='editbox']", "v");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP_SHORT );
      
      //check text
      assertEquals(textToEdit + "\n" + textToEdit, selenium.getText("//body[@class='editbox']"));
      
      //----- 11 ------------
      //check Ctrl+X
      //delete all text
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      selenium.controlKeyDown();
      selenium.keyDown("//body[@class='editbox']", "D");
      selenium.keyUp("//body[@class='editbox']", "D");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium.controlKeyDown();
      selenium.keyDown("//body[@class='editbox']", "D");
      selenium.keyUp("//body[@class='editbox']", "D");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      final String textToCut = "text to cut";
      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, textToCut);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      //select all text
      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_A);
      selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      Thread.sleep(TestConstants.SLEEP);
      
      selenium.controlKeyDown();
      selenium.keyPress("//body[@class='editbox']", "x");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP);
      
      assertEquals("", selenium.getText("//body[@class='editbox']"));
      
      //paste text by pressing Ctrl+V
      selenium.controlKeyDown();
      selenium.keyPress("//body[@class='editbox']", "v");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      assertEquals(textToCut, selenium.getText("//body[@class='editbox']"));
      
      Thread.sleep(TestConstants.SLEEP);
      
      
      selectMainFrame();
      //TODO: Ctrl+Home, Ctrl+End
      
      IDE.editor().closeUnsavedFileAndDoNotSave(0);
   }
   
   @Test
   public void testUndoRedoHotkeys() throws Exception
   {
      refresh();
      //----- 1 -------
      //Create new text file
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      
      final String textToRevert = "a";
      
      //----- 2 -------
      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, textToRevert);
      Thread.sleep(TestConstants.SLEEP);
      Thread.sleep(TestConstants.SLEEP);
      
      //----- 3 -------
      //change text
      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, "5");
      Thread.sleep(TestConstants.SLEEP);
      
      assertEquals(textToRevert + "5", selenium.getText("//body[@class='editbox']"));
      selectMainFrame();
      Thread.sleep(TestConstants.SLEEP);
      //press Ctrl+Z
      selectIFrameWithEditor(0);
      selenium.click("//body");
      
      
      //----- 4 -------
      //ctrl+z
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium.controlKeyDown();
      selenium.keyDown("//", "90");
      selenium.keyUp("//", "90");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      assertEquals(textToRevert, selenium.getText("//body[@class='editbox']"));
      selectMainFrame();
      Thread.sleep(TestConstants.SLEEP);
      
      //----- 5 -------
      //press Ctrl+Y
      selectIFrameWithEditor(0);
      Thread.sleep(TestConstants.SLEEP);
      selenium.controlKeyDown();
      selenium.keyDown("//body[@class='editbox']", "89");
      selenium.keyUp("//body[@class='editbox']", "89");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP);
      assertEquals(textToRevert + "5", selenium.getText("//body[@class='editbox']"));
      Thread.sleep(TestConstants.SLEEP);
      
      selectMainFrame();
      //TODO: Ctrl+Home, Ctrl+End
      
      IDE.editor().closeUnsavedFileAndDoNotSave(0);
   }
   
   /**
    * IDE-156:HotKeys customization
    * ----- 14 ------------
    * 
    * @throws Exception
    */
   @Test
   public void testCtrlSpaceForAutoComplete() throws Exception
   {
      refresh();
      //----- 14 ------------
      //Create JavaScript file and press Ctrl+Space
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.JAVASCRIPT_FILE);
      Thread.sleep(TestConstants.SLEEP);
      
      runHotkeyWithinEditor(0, true, false, 32);
      Thread.sleep(TestConstants.SLEEP);
      
      //check autocomplete form appears
      assertTrue(selenium.isElementPresent("//td/div[@class='gwt-Label']"));
      
      //close autocoplete form
      selenium.keyDown("//", "13");
      selenium.keyUp("//", "13");
      Thread.sleep(TestConstants.SLEEP);
      
      //close file
      IDE.editor().closeUnsavedFileAndDoNotSave(0);

   }
 
}