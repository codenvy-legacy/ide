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

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.utils.AbstractTextUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

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
public class HotkeysCustomizationTest extends BaseTest
{
   private static final String INFO_MESSAGE_STYLE = "exo-cutomizeHotKey-label-info";
   
   private static final String ERROR_MESSAGE_STYLE = "exo-cutomizeHotKey-label-error";
   
   private static final String GOOGLE_GADGET_FILE = "GoogleGadget.xml";
   
   private static final String DEFAULT_TEXT_IN_GADGET = "Hello, world!";
   
   private static final String FOLDER_NAME = "test";
   
   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" 
   + WS_NAME + "/";
   
   @BeforeClass
   public static void setUp()
   {
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
   
   @Before
   public void beforeMethod() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      deleteCookies();
      Thread.sleep(TestConstants.SLEEP);
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.SLEEP);
      
      runToolbarButton(ToolbarCommands.File.REFRESH);
      selectItemInWorkspaceTree(FOLDER_NAME);
      runToolbarButton(ToolbarCommands.File.REFRESH);
   }
   
   @After
   public void afterMethod()
   {
      cleanRegistry();
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
   
   /**
    * IDE-156:HotKeys customization
    * ----- 1-2 ------------
    * @throws Exception
    */
   @Test
   public void testDefaultHotkeys() throws Exception
   {
      //----- 1 ------------
      //Press Ctrl+N
      selenium.controlKeyDown();
      selenium.keyDown("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[0]", "N");
      selenium.keyUp("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[0]", "N");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP);
      //check, that Create file from template window appeared
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/"));
      //close
      selenium.click("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/closeButton/");
      
      //----- 2 ------------
      //Check Ctrl+S
      //open new file
      runCommandFromMenuNewOnToolbar(MenuCommands.New.XML_FILE);
      Thread.sleep(TestConstants.SLEEP);
      //press Ctrl+S
      selenium.controlKeyDown();
      selenium.keyDown("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[0]", "S");
      selenium.keyUp("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[0]", "S");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP);
      //check, that Save As dialog window appeared
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideAskForValueDialog\"]"));
      //close
      selenium.click("scLocator=//Window[ID=\"ideAskForValueDialog\"]/closeButton/");
      Thread.sleep(TestConstants.SLEEP);
      //close file
      closeUnsavedFileAndDoNotSave("0");
   }
   
   /**
    * IDE-156:HotKeys customization
    * ----- 3-5 ------------
    * @throws Exception
    */
   @Test
   public void testSpecifiedHotkeysForWYSIWYGEditor() throws Exception
   {
      //----- 3 ------------
      //open Google Gadget file with CK editor
      //and check Ctrl+B, Ctrl+I, Ctrl+U
      Thread.sleep(TestConstants.SLEEP);
      openFileFromNavigationTreeWithCkEditor(GOOGLE_GADGET_FILE, false);
      selectIFrameWithEditor(0);
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
      Thread.sleep(TestConstants.SLEEP_SHORT);
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
      selectMainFrame();
      Thread.sleep(TestConstants.SLEEP);
      
      //----- 4 ------------
      //Press Ctrl+S to check file saving
      //check tab title is marked by *
      assertEquals(GOOGLE_GADGET_FILE + " *", getTabTitle(0));
      selenium.controlKeyDown();
      selenium.keyDown("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[0]", "S");
      selenium.keyUp("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[0]", "S");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP);
      //check tab title is not marked by *
      assertEquals(GOOGLE_GADGET_FILE, getTabTitle(0));
      //close file
      closeTab("0");
      
      //----- 5 ------------
      //check, that if no file is opened, Ctrl+B, Ctrl+I, Ctrl+U
      //call default browser events
      // Ctrl+B
      selenium.controlKeyDown();
      selenium.keyDown("//", "B");
      selenium.keyUp("//", "B");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      // Ctrl+I
      selenium.controlKeyUp();
      selenium.controlKeyDown();
      selenium.keyDown("//", "I");
      selenium.keyUp("//", "I");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      // Ctrl+U
      selenium.controlKeyDown();
      selenium.keyDown("//", "U");
      selenium.keyUp("//", "U");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP_SHORT);
   }
   
   /**
    * IDE-156:HotKeys customization
    * ----- 6-12 ------------
    * @throws Exception
    */
   @Test
   public void testHotkeysWithinCodeEditor() throws Exception
   {
    //----- 6 ------------
      //Create new text file
      runCommandFromMenuNewOnToolbar(MenuCommands.New.TEXT_FILE);
      //type text to editor
      
      
      final String text = "Text File";
      typeTextIntoEditor(0, text);
      //----- 7 ------------
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
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideFindReplaceForm\"]"));
      //close form
      selenium.click("scLocator=//Window[ID=\"ideFindReplaceForm\"]/closeButton/");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      //----- 8 ------------
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
      
      //----- 9 ------------
      //check Ctrl+L
      selectIFrameWithEditor(0);
      selenium.controlKeyDown();
      selenium.keyDown("//body[@class='editbox']", "L");
      selenium.keyUp("//body[@class='editbox']", "L");
      selenium.controlKeyUp();
      selectMainFrame();
      Thread.sleep(TestConstants.SLEEP);
      //check go to line window dialog appeared
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideGoToLineForm\"]/"));
      //close
      selenium.click("scLocator=//Window[ID=\"ideGoToLineForm\"]/closeButton/");
      
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
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
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
      
      closeUnsavedFileAndDoNotSave("0");
   }
   
   @Test
   public void testUndoRedoHotkeys() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      //----- 1 -------
      //Create new text file
      runCommandFromMenuNewOnToolbar(MenuCommands.New.TEXT_FILE);
      
      final String textToRevert = "abcd";
      
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
      selenium.controlKeyDown();
      Thread.sleep(TestConstants.SLEEP_SHORT);
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
      
      closeUnsavedFileAndDoNotSave("0");
   }
   
   /**
    * IDE-156:HotKeys customization
    * ----- 13 ------------
    * @throws Exception
    */
   @Test
   public void testTypicalHotkeysForWYSIWYGEditor() throws Exception
   {
      //----- 13 ------------
      //check hotkeys from steps 7-12 in WYSIWYG editor
      //open file in WYDIWYG editor
      openFileFromNavigationTreeWithCkEditor(GOOGLE_GADGET_FILE, false);
      //check Ctrl+F
      selectIFrameWithEditor(0);
      selenium.click("//body");
      Thread.sleep(TestConstants.SLEEP);
      selenium.controlKeyDown();
      selenium.keyDown("//body", "F");
      selenium.keyUp("//body", "F");
      selenium.controlKeyUp();
      selectMainFrame();
      Thread.sleep(TestConstants.SLEEP);
      
      //check find-replace form doesn't appear
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideFindReplaceForm\"]"));
      
      //check Ctrl+D
      assertEquals(DEFAULT_TEXT_IN_GADGET, getTextFromCkEditor(0));
      
      selectIFrameWithEditor(0);
      selenium.controlKeyDown();
      selenium.keyDown("//body", "D");
      selenium.keyUp("//body", "D");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selectMainFrame();
      assertEquals(DEFAULT_TEXT_IN_GADGET, getTextFromCkEditor(0));
      Thread.sleep(TestConstants.SLEEP);
      
      //check Ctrl+L
      selectIFrameWithEditor(0);
      selenium.controlKeyDown();
      selenium.keyDown("//body", "L");
      selenium.keyUp("//body", "L");
      selenium.controlKeyUp();
      selectMainFrame();
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
      
      //check Ctrl+X
      selectIFrameWithEditor(0);
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
      selectMainFrame();
      
      
      Thread.sleep(TestConstants.SLEEP);
      //press Ctrl+Y
      selectIFrameWithEditor(0);
      selenium.click("//body");
      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_Y);
      selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      Thread.sleep(TestConstants.SLEEP);
      assertEquals("", selenium.getText("//body"));
      Thread.sleep(TestConstants.SLEEP);
      
      selectMainFrame();
      
      //check Ctrl+C, Ctrl+V
      final String textForCopyPaste = "copy-paste text";
      
      selectIFrameWithEditor(0);
      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CK_EDITOR_LOCATOR, textForCopyPaste);
      selectMainFrame();
      Thread.sleep(TestConstants.SLEEP);
      
      
      selectIFrameWithEditor(0);
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
      
      selectMainFrame();
      
      //check Ctrl+Home, Ctrl+End
      selectIFrameWithEditor(0);
      selenium.click("//body");
      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_HOME);
      selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      Thread.sleep(TestConstants.SLEEP);
      
      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_END);
      selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      Thread.sleep(TestConstants.SLEEP);
      
      selectMainFrame();
      
      Thread.sleep(TestConstants.SLEEP);
      closeUnsavedFileAndDoNotSave("0");
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
      //----- 14 ------------
      //Create JavaScript file and press Ctrl+Space
      runCommandFromMenuNewOnToolbar(MenuCommands.New.JAVASCRIPT_FILE);
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
      closeUnsavedFileAndDoNotSave("0");

   }
   
   /**
    * IDE-156:HotKeys customization
    * ----- 15-19 ------------
    * @throws Exception
    */
   @Test
   public void testHotkeysInSeveralTabs() throws Exception
   {
      //----- 15 ------------
      //Open several tabs (open existed documents and create some new)
      runCommandFromMenuNewOnToolbar(MenuCommands.New.TEXT_FILE);
      Thread.sleep(TestConstants.SLEEP);
      openFileFromNavigationTreeWithCodeEditor(GOOGLE_GADGET_FILE, false);
      Thread.sleep(TestConstants.SLEEP);
      runCommandFromMenuNewOnToolbar(MenuCommands.New.GROOVY_SCRIPT_FILE);
      
      //----- 16 ------------
      //Open "Customize Hotkeys" window (Window->Customize Hotkeys)
      runTopMenuCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_HOTKEYS);
      
      //check cutomize hotkeys dialog window appears
      checkCustomizeHotkeyDialogWindow();
      
      //----- 17 ------------
      //Select "New HTML File" and bind Ctrl+H to this command 
      //(Press Ctrl+H, press "Bind" button)
      selenium.click("scLocator=//ListGrid[ID=\"ideCustomizeHotKeysFormListGrid\"]/body/row[2]/col[0]");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //press Ctrl+H
      selenium.controlKeyDown();
      selenium.keyDown("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[0]", "H");
      selenium.keyUp("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[0]", "H");
      selenium.controlKeyUp();
      //check Bind button is enabled
      assertFalse(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleDisabled' and text()='Bind']"));
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitle' and text()='Bind']"));
      
      //check, Ctrl+H text appears in text field
      assertEquals("Ctrl+H", selenium.getValue("scLocator=//DynamicForm[ID=\"ideCustomizeHotKeysFormDynamicFormHotKeyField\"]/item[0][name=\"ideCustomizeHotKeysFormHotKeyField\"]/element"));
      //click Bind button
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeHotKeysFormBindButton\"]/");
      Thread.sleep(TestConstants.SLEEP);
      //check, Ctrl+H text appears near New HTML File in list grid
      assertEquals("Ctrl+H", selenium.getText("scLocator=//ListGrid[ID=\"ideCustomizeHotKeysFormListGrid\"]/body/row[2]/col[1]"));
      Thread.sleep(TestConstants.SLEEP);
      
      //----- 18 ------------
      //Select "Create File From Template" and bind Alt+N to this command. Press Save button
      
      selenium.click("scLocator=//ListGrid[ID=\"ideCustomizeHotKeysFormListGrid\"]/body/row[Binding=Ctrl+N]/col[0]");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //press Alt+N
      selenium.altKeyDown();
      selenium.keyDown("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[0]", "N");
      selenium.keyUp("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[0]", "N");
      selenium.altKeyUp();
      
      //click Bind button
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeHotKeysFormBindButton\"]/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //click Save button
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeHotKeysFormSaveButton\"]/");
      Thread.sleep(TestConstants.SLEEP);
      
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideCustomizeHotKeysForm\"]/"));
      
      
      //----- 19 ------------
      //Try Ctrl+H and Alt+N hotkeys in several tabs
      
      selectEditorTab(2);
      
      //press Alt+N
      runHotkeyWithinEditor(2, false, true, java.awt.event.KeyEvent.VK_N);
      
      Thread.sleep(TestConstants.SLEEP);
      
      checkCreateFileFromTemplateFormAndClose();
      
      //press Ctrl+H
      selectIFrameWithEditor(2);
      selenium.controlKeyDown();
      selenium.keyDown("//body", "H");
      selenium.keyUp("//body", "H");
      selectMainFrame();
      
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //check new html file created
      assertEquals("Untitled file.html *", getTabTitle(3));
      closeUnsavedFileAndDoNotSave("3");
      Thread.sleep(TestConstants.SLEEP);
      
      //select first tab
      selectEditorTab(0);
      selectIFrameWithEditor(0);
      selenium.click("//body");
      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_ALT);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_N);
      selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_ALT);
      selectMainFrame();
      
      checkCreateFileFromTemplateFormAndClose();
      
      selectIFrameWithEditor(0);
      selenium.controlKeyDown();
      selenium.keyDown("//body", "H");
      selenium.keyUp("//body", "H");
      selectMainFrame();
      
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //check new html file created
      assertEquals("Untitled file.html *", getTabTitle(3));
      closeUnsavedFileAndDoNotSave("3");
      Thread.sleep(TestConstants.SLEEP);
      
      //select second tab
      selectEditorTab(1);
      selectIFrameWithEditor(1);
      selenium.click("//body");
      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_ALT);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_N);
      selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_ALT);
      selectMainFrame();
      
      checkCreateFileFromTemplateFormAndClose();
      
      selectIFrameWithEditor(1);
      selenium.controlKeyDown();
      selenium.keyDown("//body", "H");
      selenium.keyUp("//body", "H");
      selectMainFrame();
      
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //check new html file created
      assertEquals("Untitled file.html *", getTabTitle(3));
      closeUnsavedFileAndDoNotSave("3");
      Thread.sleep(TestConstants.SLEEP);
      
      //close all tabs
      closeUnsavedFileAndDoNotSave("0");
      closeTab("0");
      closeUnsavedFileAndDoNotSave("0");
   }
   
   /**
    * IDE-156:HotKeys customization
    * ----- 20 ------------
    * @throws Exception
    */
   @Test
   public void testHotkeysAfterRefresh() throws Exception
   {
      //prepare hotkeys
      
      
      //Open "Customize Hotkeys" window (Window->Customize Hotkeys)
      runTopMenuCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_HOTKEYS);
      
      //check cutomize hotkeys dialog window appears
      checkCustomizeHotkeyDialogWindow();
      
      //Select "New HTML File" and bind Ctrl+H to this command 
      //(Press Ctrl+H, press "Bind" button)
      selenium.click("scLocator=//ListGrid[ID=\"ideCustomizeHotKeysFormListGrid\"]/body/row[2]/col[0]");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //press Ctrl+H
      selenium.controlKeyDown();
      selenium.keyDown("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[0]", "H");
      selenium.keyUp("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[0]", "H");
      selenium.controlKeyUp();
      //check Bind button is enabled
      assertFalse(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleDisabled' and text()='Bind']"));
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitle' and text()='Bind']"));
      
      //check, Ctrl+H text appears in text field
      assertEquals("Ctrl+H", selenium.getValue("scLocator=//DynamicForm[ID=\"ideCustomizeHotKeysFormDynamicFormHotKeyField\"]/item[0][name=\"ideCustomizeHotKeysFormHotKeyField\"]/element"));
      //click Bind button
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeHotKeysFormBindButton\"]/");
      Thread.sleep(TestConstants.SLEEP);
      //check, Ctrl+H text appears near New HTML File in list grid
      assertEquals("Ctrl+H", selenium.getText("scLocator=//ListGrid[ID=\"ideCustomizeHotKeysFormListGrid\"]/body/row[2]/col[1]"));
      Thread.sleep(TestConstants.SLEEP);
      
      //Select "Create File From Template" and bind Alt+N to this command. Press Save button
      selenium.click("scLocator=//ListGrid[ID=\"ideCustomizeHotKeysFormListGrid\"]/body/row[10]/col[0]");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //press Alt+N
      selenium.altKeyDown();
      selenium.keyDown("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[0]", "N");
      selenium.keyUp("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[0]", "N");
      selenium.altKeyUp();
      
      //click Bind button
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeHotKeysFormBindButton\"]/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //click Save button
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeHotKeysFormSaveButton\"]/");
      Thread.sleep(TestConstants.SLEEP);
      
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideCustomizeHotKeysForm\"]/"));
      
      //----- 20 ------------
      //refresh browser window and check Ctrl+H and Alt+N
      
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.SLEEP);
      Thread.sleep(TestConstants.SLEEP);
      
      selenium.altKeyDown();
      selenium.keyDown("//", "78");
      selenium.keyUp("//", "78");
      selenium.altKeyUp();
      Thread.sleep(TestConstants.SLEEP);
      Thread.sleep(TestConstants.SLEEP);
      
      checkCreateFileFromTemplateFormAndClose();
      
      selenium.controlKeyDown();
      selenium.keyDown("//body", "H");
      selenium.keyUp("//body", "H");
      selectMainFrame();
      
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //check new html file created
      assertEquals("Untitled file.html *", getTabTitle(0));
      closeUnsavedFileAndDoNotSave("0");
      Thread.sleep(TestConstants.SLEEP);
      
      runTopMenuCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_HOTKEYS);
      Thread.sleep(TestConstants.SLEEP);
      checkCustomizeHotkeyDialogWindow();
      
      //Select "New HTML File" and check Ctrl+H is bind 
      selenium.click("scLocator=//ListGrid[ID=\"ideCustomizeHotKeysFormListGrid\"]/body/row[2]/col[0]");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      
      assertEquals("Ctrl+H", selenium.getText("scLocator=//ListGrid[ID=\"ideCustomizeHotKeysFormListGrid\"]/body/row[2]/col[1]"));
      Thread.sleep(TestConstants.SLEEP);
      
      //Select "Create File From Template" and check Alt+N is bind to it
      selenium.click("scLocator=//ListGrid[ID=\"ideCustomizeHotKeysFormListGrid\"]/body/row[10]/col[0]");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      assertEquals("Alt+N", selenium.getText("scLocator=//ListGrid[ID=\"ideCustomizeHotKeysFormListGrid\"]/body/row[10]/col[1]"));
      
      //close
      closeHotkeysWindow();
      
   }
   

   /**
    * IDE-156:HotKeys customization
    * ----- 21-25 ------------
    * @throws Exception
    */
   @Test
   public void testTryToBindForbiddenHotkeys() throws Exception
   {
      //----- 21 ------------
      //Call "Customize Hotkeys" window
      runTopMenuCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_HOTKEYS);
      Thread.sleep(TestConstants.SLEEP);
      
      //----- 22 ------------
      //Binding forbidden hotkeys
      //Select "New Text file" command and try to bind Shift+N. 
      //Then try to bind ordinal keys Y, 8, PrintScreen and simmilar ordinal keys
      
      //After pressing forbidden keys (Shift, alphabet, digital, PrintScreen) 
      //Bind button is disabled and under text field appears error message 
      //(remember, that you can't bind ordinal keys such as N, Y, 8, PrintScreen, and keys, 
      //that start with Shift, but Ctrl+<digital> and Alt+<digital> will work)
      
      //click "New TEXT File" row
      selectRowInHotkeysListgrid(3, "New TEXT File");
      Thread.sleep(TestConstants.SLEEP);
      checkUnbindButtonEnabled(true);
      //press Shift+N 
      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_SHIFT);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_N);
      selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_SHIFT);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      checkMessage(ERROR_MESSAGE_STYLE, "First key should be Ctrl or Alt", true);
      
      //press Y
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_Y);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      checkMessage(ERROR_MESSAGE_STYLE, "First key should be Ctrl or Alt", true);
      
      //press 8
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_8);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      checkMessage(ERROR_MESSAGE_STYLE, "First key should be Ctrl or Alt", true);
      
      //----- 23 ------------
      //check, that after pressing Ctrl or Alt info message is displayed:
      //Holt Ctrl or Alt, then press key
      
      //select row with Css file
      selectRowInHotkeysListgrid(5, "New CSS File");
      checkUnbindButtonEnabled(true);
      //press Ctrl
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_CONTROL);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //check message
      checkMessage(INFO_MESSAGE_STYLE, "Holt Ctrl or Alt, then press key", true);
      //get text from text field
      checkHotkeyInTextField("Ctrl+");
      //check Bind button is disabled
      checkBindButtonEnabled(false);
      checkSaveButtonEnabled(false);
      
      //press alt
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ALT);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //check message
      checkMessage(INFO_MESSAGE_STYLE, "Holt Ctrl or Alt, then press key", true);
      //get text from text field
      checkHotkeyInTextField("Alt+");
      //check Bind button is disabled
      checkBindButtonEnabled(false);
      checkSaveButtonEnabled(false);
      
      //----- 24 ------------
      //Binding of hotkeys, that are reserved by editors
      
      //Try to bind Ctrl+C
      //select row with Css file
      selenium.click("scLocator=//ListGrid[ID=\"ideCustomizeHotKeysFormListGrid\"]/body/row[5]/col[0]");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      //Press Ctrl+C
      selenium.controlKeyDown();
      selenium.keyDown("//", "C");
      selenium.keyUp("//", "C");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //check text in text field
      checkHotkeyInTextField("Ctrl+C");
      //check message
      checkMessage(ERROR_MESSAGE_STYLE, "This hotkey is used by Code or WYSIWYG Editors", true);
      //check Bind button is disabled
      checkBindButtonEnabled(false);
      checkSaveButtonEnabled(false);
      
      //----- 25 ------------
      //Binding hotkeys, that are bound to another commands
      
      //Press Ctrl+D
      selenium.controlKeyDown();
      selenium.keyDown("//", "D");
      selenium.keyUp("//", "D");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //check text in text field
      checkHotkeyInTextField("Ctrl+D");
      //check message
      checkMessage(ERROR_MESSAGE_STYLE, "Such hotkey already bound to another command", true);
      //check Bind button is disabled
      checkBindButtonEnabled(false);
      checkSaveButtonEnabled(false);
      
      //Press Ctrl+P, check Bind button is enabled and no error message
      selenium.controlKeyDown();
      selenium.keyDown("//", "P");
      selenium.keyUp("//", "P");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //check no message
      checkMessage(ERROR_MESSAGE_STYLE, "Such hotkey already bound to another command", false);
      //check Bind button is enabled
      checkBindButtonEnabled(true);
      checkSaveButtonEnabled(false);
      
      //press bind button
      pressBindButton();
      checkSaveButtonEnabled(true);
      
      //Try to bind the same hotkes
      selectRowInHotkeysListgrid(5, "New CSS File");
      //press Ctrl+P
      selenium.controlKeyDown();
      selenium.keyDown("//", "P");
      selenium.keyUp("//", "P");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //check hotkeys in text field
      checkHotkeyInTextField("Ctrl+P");
      checkBindButtonEnabled(false);
      checkMessage(ERROR_MESSAGE_STYLE, "Such hotkey already bound to this command", true);
      
      //close
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeHotKeysFormCancelButton\"]/");

   }
   
   @Test
   public void testUnbindingHotkey() throws Exception
   {
      //press Ctrl+N
      selenium.controlKeyDown();
      selenium.keyDown("//", "N");
      selenium.keyUp("//", "N");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      checkCreateFileFromTemplateFormAndClose();
      
      //Call "Customize Hotkeys" window
      runTopMenuCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_HOTKEYS);
      Thread.sleep(TestConstants.SLEEP);
      //select row with Create File From Template... command
      
      //doesn't use selectRowInHotkeysListgrid method, because this method after selecting
      //check css style of element, that it is selected. But Create File From Template... command
      //is hidden. In deed, we need to scroll listgrid, but selenium doesn't scroll it.
      //So, command will be selected, but there will be no selected element on display
      selenium.click("scLocator=//ListGrid[ID=\"ideCustomizeHotKeysFormListGrid\"]/body/row[10]/col[0]");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      //unbind
      pressUnbindButton();
      checkSaveButtonEnabled(true);
      
      //select row with Css file
      selectRowInHotkeysListgrid(5, "New CSS File"); 
      //press Ctrl+P
      selenium.controlKeyDown();
      selenium.keyDown("//", "P");
      selenium.keyUp("//", "P");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      pressBindButton();
      pressSaveButton();
      
      //press Ctrl+P
      selenium.controlKeyDown();
      selenium.keyDown("//", "P");
      selenium.keyUp("//", "P");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      assertEquals("Untitled file.css *", getTabTitle(0));
      
      closeUnsavedFileAndDoNotSave("0");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //press Ctrl+N
      selenium.controlKeyDown();
      selenium.keyDown("//", "N");
      selenium.keyUp("//", "N");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/"));
      
      //Call "Customize Hotkeys" window
      runTopMenuCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_HOTKEYS);
      Thread.sleep(TestConstants.SLEEP);
      
      //unbind Ctrl+P from New Css File command
      selectRowInHotkeysListgrid(5, "New CSS File");
      pressUnbindButton();
      checkSaveButtonEnabled(true);
      pressSaveButton();
      Thread.sleep(TestConstants.SLEEP);
      
      //press Ctrl+P
      selenium.controlKeyDown();
      selenium.keyDown("//", "P");
      selenium.keyUp("//", "P");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      assertFalse(selenium.isTextPresent("Untitled file.css *"));
   }
   
   /**
    * IDE-156:HotKeys customization
    * ----- 26-31 ------------
    * @throws Exception
    */
   @Test
   public void testBindingAndUnbindingNewHotkey() throws Exception
   {
      //call customize hotkeys form
      runTopMenuCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_HOTKEYS);
      selenium.click("scLocator=//ListGrid[ID=\"ideCustomizeHotKeysFormListGrid\"]/body/row[5]/col[0]");
      
      //----- 26 ------------
      //Press Ctrl+P, press Bind button, press Save button
      selenium.controlKeyDown();
      selenium.keyDown("//", "P");
      selenium.keyUp("//", "P");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //check no message
      assertFalse(selenium.isElementPresent("//div[@class='windowBody']//div[text()='Such hot key already bound to another control']"));
      //check Bind button is enabled
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitle' and text()='Bind']"));
      //click Bind button
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeHotKeysFormBindButton\"]/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //click Save button
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeHotKeysFormSaveButton\"]/");
      Thread.sleep(TestConstants.SLEEP);
      
      //----- 27 ------------
      //Press Ctrl+P
      selenium.controlKeyDown();
      selenium.keyDown("//", "P");
      selenium.keyUp("//", "P");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //check new Css file is createed
      assertEquals("Untitled file.css *", getTabTitle(0));
      
      closeUnsavedFileAndDoNotSave("0");
      Thread.sleep(TestConstants.SLEEP);
      
      //----- 28 ------------
      //Call "Customize Hotkeys" window and select "New CSS file". Press Unbind button
      runTopMenuCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_HOTKEYS);
      selenium.click("scLocator=//ListGrid[ID=\"ideCustomizeHotKeysFormListGrid\"]/body/row[5]/col[0]");
      Thread.sleep(TestConstants.SLEEP);
      
      //check, Ctrl+H text appears near New HTML File in list grid
      assertEquals("Ctrl+P", selenium.getText("scLocator=//ListGrid[ID=\"ideCustomizeHotKeysFormListGrid\"]/body/row[5]/col[1]"));
      
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeHotKeysFormUnbindButton\"]/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      assertEquals("", selenium.getText("scLocator=//ListGrid[ID=\"ideCustomizeHotKeysFormListGrid\"]/body/row[5]/col[1]"));
      //Press Cancel button.
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeHotKeysFormCancelButton\"]/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //----- 29 ------------
      //Press Ctrl+P
      
      //New CSS file opened
      
      //Press Ctrl+P
      selenium.controlKeyDown();
      selenium.keyDown("//", "P");
      selenium.keyUp("//", "P");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //check new Css file is createed
      assertEquals("Untitled file.css *", getTabTitle(0));
      
      closeUnsavedFileAndDoNotSave("0");
      Thread.sleep(TestConstants.SLEEP);
      
      //----- 30 ------------
      //Call "Customize Hotkeys" window and select "New CSS file". 
      //Press Unbind button and then press Save button.
      runTopMenuCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_HOTKEYS);
      selenium.click("scLocator=//ListGrid[ID=\"ideCustomizeHotKeysFormListGrid\"]/body/row[5]/col[0]");
      Thread.sleep(TestConstants.SLEEP);
      
      //check, Ctrl+H text appears near New HTML File in list grid
      assertEquals("Ctrl+P", selenium.getText("scLocator=//ListGrid[ID=\"ideCustomizeHotKeysFormListGrid\"]/body/row[5]/col[1]"));
      
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeHotKeysFormUnbindButton\"]/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      assertEquals("", selenium.getText("scLocator=//ListGrid[ID=\"ideCustomizeHotKeysFormListGrid\"]/body/row[5]/col[1]"));
      
      //click Save button
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeHotKeysFormSaveButton\"]/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //----- 31 ------------
      //Press Ctrl+P
      //Nothing or default browser event works out
      checkIsTabPresentInEditorTabset("Untitled file.css", false);
      Thread.sleep(TestConstants.SLEEP);
   }
   
   
   private String getTextFromCkEditor(int tabIndex) throws Exception
   {
      selectIFrameWithEditor(tabIndex);
      String text = selenium.getText("//body");
      selectMainFrame();
      return text;
   }
   
   private void checkCreateFileFromTemplateFormAndClose() throws Exception
   {
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/"));
      selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCancelButton\"]/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/"));
      Thread.sleep(TestConstants.SLEEP);
   }
   
   private void checkCustomizeHotkeyDialogWindow()
   {
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCustomizeHotKeysForm\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideCustomizeHotKeysFormListGrid\"]"));
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//input[@class='textItemDisabled']"));
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleDisabled' and text()='Bind']"));
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleDisabled' and text()='Unbind']"));
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleDisabled' and text()='Save']"));
      assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitle' and text()='Cancel']"));
   }
   
   private void checkBindButtonEnabled(boolean isEnabled)
   {
      if (isEnabled)
      {
         assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitle' and text()='Bind']"));
      }
      else
      {
         assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleDisabled' and text()='Bind']"));
      }
   }
   
   private void checkUnbindButtonEnabled(boolean isEnabled)
   {
      if (isEnabled)
      {
         assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitle' and text()='Unbind']"));
      }
      else
      {
         assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleDisabled' and text()='Unbind']"));
      }
   }
   
   private void checkSaveButtonEnabled(boolean isEnabled)
   {
      if (isEnabled)
      {
         assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitle' and text()='Save']"));
      }
      else
      {
         assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleDisabled' and text()='Save']"));
      }
   }
   
   private void checkCancelButtonEnabled(boolean isEnabled)
   {
      if (isEnabled)
      {
         assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitle' and text()='Cancel']"));
      }
      else
      {
         assertTrue(selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleDisabled' and text()='Cancel']"));
      }
   }
   
   /**
    * Check, that in text field correctly displayed pressed hotkey
    * @param hotKey
    */
   private void checkHotkeyInTextField(String hotKey)
   {
      assertEquals(hotKey, selenium.getValue("scLocator=//DynamicForm[ID=\"ideCustomizeHotKeysFormDynamicFormHotKeyField\"]/item[0][name=\"ideCustomizeHotKeysFormHotKeyField\"]/element"));
   }
   
   /**
    * Select row in hotkeys listgrid and check, is right command selected.
    * 
    * @param rowNumber number of row, which will be selected
    * @param commandName name of command, which must be in this row
    * @throws Exception
    */
   private void selectRowInHotkeysListgrid(int rowNumber, String commandName) throws Exception
   {
      selenium.click("scLocator=//ListGrid[ID=\"ideCustomizeHotKeysFormListGrid\"]/body/row[" + String.valueOf(rowNumber) + "]/col[0]");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
      //check, that right command is selected
      assertEquals(commandName, selenium.getText("//td[@class='cellSelectedDark']//span[contains(text(), '" + commandName + "')]"));
   }
   
   /**
    * Check is message present.
    * 
    * If message is present, than check text
    * 
    * @param style INFO or ERROR message is shown (INFO - in blue color, ERROR - in red color)
    * @param message - text message
    * @param isPresent is message present
    */
   private void checkMessage(String style, String message, boolean isPresent)
   {
      //check is no message present
      if (!isPresent)
      {
         if (style.equals(INFO_MESSAGE_STYLE))
         {
            assertFalse(selenium
               .isElementPresent("//div[@class='windowBody']//div[@class='exo-cutomizeHotKey-label-info' and text()='" + message + "']"));
         }
         if (style.equals(ERROR_MESSAGE_STYLE))
         {
            assertFalse(selenium
               .isElementPresent("//div[@class='windowBody']//div[@class='exo-cutomizeHotKey-label-error' and text()='" + message + "']"));
         }
      }
      //check displayed message
      else
      {
         if (style.equals(INFO_MESSAGE_STYLE))
         {
            assertEquals(message, selenium
               .getText("//div[@class='windowBody']//div[@class='exo-cutomizeHotKey-label-info']"));
         }
         if (style.equals(ERROR_MESSAGE_STYLE))
         {
            assertEquals(message, selenium
               .getText("//div[@class='windowBody']//div[@class='exo-cutomizeHotKey-label-error']"));
         }
      }
   }
   
   private void pressBindButton() throws Exception
   {
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeHotKeysFormBindButton\"]/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   private void pressUnbindButton() throws Exception
   {
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeHotKeysFormUnbindButton\"]/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   private void pressSaveButton() throws Exception
   {
      selenium.click("scLocator=//IButton[ID=\"ideCustomizeHotKeysFormSaveButton\"]/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   private void closeHotkeysWindow() throws Exception
   {
      selenium.click("scLocator=//Window[ID=\"ideCustomizeHotKeysForm\"]/closeButton/");
      Thread.sleep(TestConstants.SLEEP);
   }
 
}