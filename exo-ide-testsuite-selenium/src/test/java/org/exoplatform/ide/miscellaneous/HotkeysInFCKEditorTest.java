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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.utils.AbstractTextUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
public class HotkeysInFCKEditorTest extends AbstractHotkeysTest
{
   @Before
   public void setUp() throws Exception
   {
      FOLDER_NAME = HotkeysInFCKEditorTest.class.getSimpleName();
      String filePath ="src/test/resources/org/exoplatform/ide/miscellaneous/GoogleGadget.xml";
      try
      {
         VirtualFileSystemUtils.mkcol(URL + FOLDER_NAME);
         VirtualFileSystemUtils.put(filePath, MimeType.GOOGLE_GADGET, URL + FOLDER_NAME + "/" + GOOGLE_GADGET_FILE);
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
   
   @After
   public void tearDown()
   {
      cleanRegistry();
      deleteCookies();
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
   
   /**
    * IDE-156:HotKeys customization
    * ----- 3-5 ------------
    * @throws Exception
    */
   @Ignore
   @Test
   public void testSpecifiedHotkeysForFCKEditor() throws Exception
   {
      //----- 1 ------------
      // Select editors option in "Open File With" form Don't work see issue 732
      //open Google Gadget file with CK editor
      //and check Ctrl+B, Ctrl+I, Ctrl+U
      waitForRootElement();
      openFolder();
      openFileFromNavigationTreeWithCkEditor(URL + FOLDER_NAME + "/" + GOOGLE_GADGET_FILE, false);
      
      IDE.editor().selectIFrameWithEditor(0);
      selenium.click("//body");
      Thread.sleep(TestConstants.SLEEP);
      //press Ctrl+A to select all text
      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_A);
      selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //Press Ctrl+B
      selenium.controlKeyDown();
      selenium.keyDown("//", "B");
      selenium.keyUp("//", "B");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP_SHORT*60);
      //check text became bold
      assertTrue(selenium.isElementPresent("//body/strong[text()='Hello, world! ']"));
      
      //Press Ctrl+I
      selenium.controlKeyDown();
      selenium.keyDown("//", "I");
      selenium.keyUp("//", "I");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //check text became italic
      assertTrue(selenium.isElementPresent("//body/em/strong[text()='Hello, world! ']"));
      
      //Press Ctrl+U
      selenium.controlKeyDown();
      selenium.keyDown("//", "U");
      selenium.keyUp("//", "U");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //check text became underline
      assertTrue(selenium.isElementPresent("//body/u/em/strong[text()='Hello, world! ']"));
      IDE.selectMainFrame();
      Thread.sleep(TestConstants.SLEEP);
      
      //----- 4 ------------
      //Press Ctrl+S to check file saving
      //check tab title is marked by *
      assertEquals(GOOGLE_GADGET_FILE + " *", IDE.editor().getTabTitle(0));
      selenium.controlKeyDown();
      selenium.keyDown("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[0]", "S");
      selenium.keyUp("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[0]", "S");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP);
      //check tab title is not marked by *
      assertEquals(GOOGLE_GADGET_FILE, IDE.editor().getTabTitle(0));
      //close file      
      IDE.editor().closeTab(0);
   }
   
   /**
    * IDE-156:HotKeys customization
    * ----- 13 ------------
    * @throws Exception
    */
   //@Test
   public void testTypicalHotkeysInFCKEditor() throws Exception
   {
      refresh();
      openFolder();
      //----- 1 ------------
      //open file in WYDIWYG editor
      openFileFromNavigationTreeWithCkEditor(GOOGLE_GADGET_FILE, false);
      //check Ctrl+F
      IDE.editor().selectIFrameWithEditor(0);
      selenium.click("//body");
      Thread.sleep(TestConstants.SLEEP);
      selenium.controlKeyDown();
      selenium.keyDown("//body", "F");
      selenium.keyUp("//body", "F");
      selenium.controlKeyUp();
      IDE.selectMainFrame();
      Thread.sleep(TestConstants.SLEEP);
      
      //check find-replace form doesn't appear
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideFindReplaceForm\"]"));
      
      //check Ctrl+D
      assertEquals(DEFAULT_TEXT_IN_GADGET, getTextFromCkEditor(0));
      
      IDE.editor().selectIFrameWithEditor(0);
      selenium.controlKeyDown();
      selenium.keyDown("//body", "D");
      selenium.keyUp("//body", "D");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      IDE.selectMainFrame();
      assertEquals(DEFAULT_TEXT_IN_GADGET, getTextFromCkEditor(0));
      Thread.sleep(TestConstants.SLEEP);
      
      //check Ctrl+L
      IDE.editor().selectIFrameWithEditor(0);
      selenium.controlKeyDown();
      selenium.keyDown("//body", "L");
      selenium.keyUp("//body", "L");
      selenium.controlKeyUp();
      IDE.selectMainFrame();
      Thread.sleep(TestConstants.SLEEP);
      //check go to line window dialog appeared
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideGoToLineForm\"]/"));
      
      assertTrue(selenium.isElementPresent("//div[@class='cke_dialog_body']"));
      
      try
      {
         selenium.clickAt("//div[@class='cke_dialog_body']/div[@class='cke_dialog_close_button']/span", "2,2");
      }
      catch (Exception e)
      {
      }
      Thread.sleep(TestConstants.SLEEP);
      assertFalse(selenium.isElementPresent("//div[@class='cke_dialog_body']"));
      
      Thread.sleep(TestConstants.SLEEP);
      IDE.editor().closeFileTabIgnoreChanges(0);
   }
   
   //@Test
   public void testCopyPasteUndoRedo() throws Exception
   {
      refresh();
      openFolder();
      //----- 1 ------------
      //open file in WYDIWYG editor
      openFileFromNavigationTreeWithCkEditor(GOOGLE_GADGET_FILE, false);
      //select all
      IDE.editor().selectIFrameWithEditor(0);
      selenium.click("//body");
      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_A);
      selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      //press Ctrl+X
      selenium.controlKeyDown();
      selenium.keyPress("//body", "x");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      assertEquals("", selenium.getText("//body"));
      
      //press Ctrl+Z
      selenium.controlKeyDown();
      selenium.keyPress("//body", "z");
      selenium.controlKeyUp();
      
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      assertEquals(DEFAULT_TEXT_IN_GADGET, selenium.getText("//body"));
      selenium.keyDown("//body", "Y");
      IDE.selectMainFrame();
      
      
      Thread.sleep(TestConstants.SLEEP);
      //press Ctrl+Y
      IDE.editor().selectIFrameWithEditor(0);
      selenium.click("//body");
      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_Y);
      selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      Thread.sleep(TestConstants.SLEEP);
      assertEquals("", selenium.getText("//body"));
      Thread.sleep(TestConstants.SLEEP);
      
      IDE.selectMainFrame();
      
      //check Ctrl+C, Ctrl+V
      final String textForCopyPaste = "copy-paste text";
      
      IDE.editor().selectIFrameWithEditor(0);
      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CK_EDITOR_LOCATOR, textForCopyPaste);
      IDE.selectMainFrame();
      Thread.sleep(TestConstants.SLEEP);
      
      
      IDE.editor().selectIFrameWithEditor(0);
      selenium.click("//body");
      //select All text
      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_A);
      selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      Thread.sleep(TestConstants.SLEEP);
      
      //press Ctrl+C
      selenium.controlKeyDown();
      selenium.keyPress("//body", "c");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP);
      
      //press Del to delete selected text
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DELETE);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      //press Ctrl+V twice
      selenium.controlKeyDown();
      selenium.keyPress("//body", "v");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      assertEquals(textForCopyPaste, selenium.getText("//body"));
      
      IDE.selectMainFrame();
      
      //check Ctrl+Home, Ctrl+End
      IDE.editor().selectIFrameWithEditor(0);
      selenium.click("//body");
      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_HOME);
      selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      Thread.sleep(TestConstants.SLEEP);
      
      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_END);
      selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      Thread.sleep(TestConstants.SLEEP);
      
      IDE.selectMainFrame();
      
      Thread.sleep(TestConstants.SLEEP);
      
      IDE.editor().closeFileTabIgnoreChanges(0);
   }
   
   //@Test
   public void testHotkeysRunFromFCKEditor() throws Exception
   {
      refresh();
      openFolder();
      
      //----- 1 ------------
      //press Ctrl+N to check hotkey
      selenium.controlKeyDown();
      selenium.keyDown("//", "N");
      selenium.keyUp("//", "N");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      checkCreateFileFromTemplateFormAndClose();
      
      //open FCK editor
      openFileFromNavigationTreeWithCkEditor(GOOGLE_GADGET_FILE, false);
      
      IDE.editor().runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_N);
      
      checkCreateFileFromTemplateFormAndClose();
      
      Thread.sleep(TestConstants.SLEEP);
      IDE.editor().closeFileTabIgnoreChanges(0);
   }
   
   private String getTextFromCkEditor(int tabIndex) throws Exception
   {
      IDE.editor().selectIFrameWithEditor(tabIndex);
      String text = selenium.getText("//body");
      IDE.selectMainFrame();
      return text;
   }
   
}
