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
import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.Keys;

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
   private static final String FIND_REPLACE_FORM_LOCATOR = "//div[@view-id=\"ideFindReplaceTextView\"]";

   private static final String GO_TO_LINE_FORM_LOCATOR = "//div[@view-id=\"ideGoToLineForm\"]";

   @After
   public void tearDown()
   {
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
      IDE.WORKSPACE.waitForRootItem();
      //----- 1 ------------
      //Create new text file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      //type text to editor
      final String text = "Text File";
     IDE.EDITOR.typeTextIntoEditor(0, text);
      //----- 2 ------------
      //Press Ctrl+F
      //Find-replace form appeared
     IDE.EDITOR.selectIFrameWithEditor(0);
      selenium().click("//body");
      selenium().controlKeyDown();
      selenium().keyDown("//body[@class='editbox']", "F");
      selenium().keyUp("//body[@class='editbox']", "F");
      selenium().controlKeyUp();
      IDE.selectMainFrame();
      //check find-replace form appeared
      assertTrue(selenium().isElementPresent(FIND_REPLACE_FORM_LOCATOR));
      //close form
      selenium().click("ideFindReplaceTextFormCancelButton");
      waitForElementNotPresent(FIND_REPLACE_FORM_LOCATOR);

      //----- 3 ------------
      //check Ctrl+D
     IDE.EDITOR.selectIFrameWithEditor(0);
      selenium().controlKeyDown();
      selenium().keyDown("//body[@class='editbox']", "D");
      selenium().keyUp("//body[@class='editbox']", "D");
      selenium().controlKeyUp();
      IDE.selectMainFrame();
      assertEquals("",IDE.EDITOR.getTextFromCodeEditor(0));

      //----- 4 ------------
      //check Ctrl+L
     IDE.EDITOR.selectIFrameWithEditor(0);
      selenium().controlKeyDown();
      selenium().keyDown("//body[@class='editbox']", "L");
      selenium().keyUp("//body[@class='editbox']", "L");
      selenium().controlKeyUp();
      IDE.selectMainFrame();
      //check go to line window dialog appeared
      assertTrue(selenium().isElementPresent(GO_TO_LINE_FORM_LOCATOR));
      //close
      IDE.GOTOLINE.pressCancelButton();
      IDE.selectMainFrame();
      //TODO: Ctrl+Home, Ctrl+End
     //IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
     IDE.EDITOR.closeTabIgnoringChanges(0);
   }

   @Test
   public void testCopyPasetHotkeys() throws Exception
   {
      refresh();
      //----- 1 ------------
      //Create new text file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      //type text to editor

      //----- 10 ------------
      //check Ctrl+C, Ctrl+V

      final String textToEdit = "text to edit";
     IDE.EDITOR.typeTextIntoEditor(0, textToEdit);

     IDE.EDITOR.selectIFrameWithEditor(0);
      selenium().click("//body");

      //select all text
      selenium().keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_A);
      selenium().keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      //copy text by Ctrl+C
      selenium().controlKeyDown();
      selenium().keyPress("//body[@class='editbox']", "c");
      selenium().controlKeyUp();

      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      IDE.EDITOR.typeTextIntoEditor(0, "\n");

      //paste text by pressing Ctrl+V
      selenium().controlKeyDown();
      selenium().keyPress("//body[@class='editbox']", "v");
      selenium().controlKeyUp();

      //check text
      assertEquals(textToEdit + "\n" + textToEdit, selenium().getText("//body[@class='editbox']"));

      //----- 11 ------------
      //check Ctrl+X
      //delete all text
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_UP);

      selenium().controlKeyDown();
      selenium().keyDown("//body[@class='editbox']", "D");
      selenium().keyUp("//body[@class='editbox']", "D");
      selenium().controlKeyUp();
      selenium().controlKeyDown();
      selenium().keyDown("//body[@class='editbox']", "D");
      selenium().keyUp("//body[@class='editbox']", "D");
      selenium().controlKeyUp();

      final String textToCut = "text to cut";
      IDE.EDITOR.typeTextIntoEditor(0, textToCut);

      //select all text
      selenium().keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_A);
      selenium().keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);

      selenium().controlKeyDown();
      selenium().keyPress("//body[@class='editbox']", "x");
      selenium().controlKeyUp();

      assertEquals("", selenium().getText("//body[@class='editbox']"));
      //paste text by pressing Ctrl+V
      selenium().controlKeyDown();
      selenium().keyPress("//body[@class='editbox']", "v");
      selenium().controlKeyUp();
      assertEquals(textToCut, selenium().getText("//body[@class='editbox']"));
      IDE.selectMainFrame();
      //TODO: Ctrl+Home, Ctrl+End

     //IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
     IDE.EDITOR.closeTabIgnoringChanges(0);
   }

   @Test
   public void testUndoRedoHotkeys() throws Exception
   {
      refresh();
      //----- 1 -------
      //Create new text file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);

      final String textToRevert = "a";

      //----- 2 -------
      IDE.EDITOR.typeTextIntoEditor(0, textToRevert);
      //----- 3 -------
      //change text
      IDE.EDITOR.typeTextIntoEditor(0, "5");

      assertEquals(textToRevert + "5", selenium().getText("//body[@class='editbox']"));
      IDE.selectMainFrame();
      //press Ctrl+Z
     IDE.EDITOR.selectIFrameWithEditor(0);
      selenium().click("//body");

      //----- 4 -------
      //ctrl+z
      selenium().controlKeyDown();
      selenium().keyDown("//", "90");
      selenium().keyUp("//", "90");
      selenium().controlKeyUp();

      assertEquals(textToRevert, selenium().getText("//body[@class='editbox']"));
      IDE.selectMainFrame();

      //----- 5 -------
      //press Ctrl+Y
     IDE.EDITOR.selectIFrameWithEditor(0);
      selenium().controlKeyDown();
      selenium().keyDown("//body[@class='editbox']", "89");
      selenium().keyUp("//body[@class='editbox']", "89");
      selenium().controlKeyUp();
      assertEquals(textToRevert + "5", selenium().getText("//body[@class='editbox']"));
      IDE.selectMainFrame();
      //TODO: Ctrl+Home, Ctrl+End

     //IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
     IDE.EDITOR.closeTabIgnoringChanges(0);
   }

   /**
    * IDE-156:HotKeys customization
    * ----- 14 ------------
    * 
    * @throws Exception
    */
   //  @Test
   public void testCtrlSpaceForAutoComplete() throws Exception
   {
      refresh();
      //----- 14 ------------
      //Create JavaScript file and press Ctrl+Space
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.JAVASCRIPT_FILE);

      IDE.EDITOR.typeTextIntoEditor(0, Keys.CONTROL.toString() + Keys.SPACE);   
      
      //check autocomplete form appears
      assertTrue(selenium().isElementPresent("//td/div[@class='gwt-Label']"));

      //close autocoplete form
      selenium().keyDown("//", "13");
      selenium().keyUp("//", "13");

      //close file
     //IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
     IDE.EDITOR.closeTabIgnoringChanges(0);

   }

}