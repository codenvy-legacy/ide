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
import static org.exoplatform.ide.CloseFileUtils.closeUnsavedFileAndDoNotSave;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.Locators;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.SaveFileUtils;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Before;
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
public class HotkeysCustomizationTest extends AbstractHotkeysTest
{
   @Before
   public void setUp() throws Exception
   {
      FOLDER_NAME = HotkeysCustomizationTest.class.getSimpleName();
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
    * ----- 1-2 ------------
    * @throws Exception
    */
   @Test
   public void testDefaultHotkeys() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      //----- 1 ------------
      //Press Ctrl+N
      selenium.controlKeyDown();
      selenium.keyDown("//", "N");
      selenium.keyUp("//", "N");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP);
      //check, that Create file from template window appeared
      checkCreateFileFromTemplateFormAndClose();
      
      //----- 2 ------------
      //Check Ctrl+S
      //open new file
      runCommandFromMenuNewOnToolbar(MenuCommands.New.XML_FILE);
      Thread.sleep(TestConstants.SLEEP);
      //press Ctrl+S
      runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_S);
      //check, that Save As dialog window appeared
      SaveFileUtils.checkSaveAsDialog(false);
      //close
      closeForm(Locators.AskForValue.ASK_FOR_VALUE_DIALOG_LOCATOR);
      Thread.sleep(TestConstants.SLEEP);
   }
   
   /**
    * IDE-156:HotKeys customization
    * ----- 15-19 ------------
    * @throws Exception
    */
   @Test
   public void testHotkeysInSeveralTabs() throws Exception
   {
      refresh();
      openFolder();
      //----- 1 ------------
      //Open several tabs (open existed documents and create some new)
      runCommandFromMenuNewOnToolbar(MenuCommands.New.TEXT_FILE);
      openFileFromNavigationTreeWithCkEditor(GOOGLE_GADGET_FILE, false);
      Thread.sleep(TestConstants.SLEEP);
      runCommandFromMenuNewOnToolbar(MenuCommands.New.GROOVY_SCRIPT_FILE);
      
      //----- 2 ------------
      //Open "Customize Hotkeys" window (Window->Customize Hotkeys)
      runTopMenuCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_HOTKEYS);
      
      //----- 3 ------------
      //Select "New HTML File" and bind Ctrl+H to this command 
      //(Press Ctrl+H, press "Bind" button)
      selectRow(Commands.NEW_HTML_FILE);
      
      //press Ctrl+H
      selenium.controlKeyDown();
      selenium.keyDown("//", "H");
      selenium.keyUp("//", "H");
      selenium.controlKeyUp();
      //check Bind button is enabled
      checkBindButtonEnabled(true);
      //check, Ctrl+H text appears in text field
      assertEquals("Ctrl+H", getTextFromTextField());
      //click Bind button
      clickButton(BIND_BUTTON_LOCATOR);
      //check, Ctrl+H text appears near New HTML File in list grid
      assertEquals("Ctrl+H", getTextFromBindColumn(Commands.NEW_HTML_FILE));
      
      //----- 18 ------------
      //Select "Create File From Template" and bind Alt+N to this command. Press Save button
      selectRow(Commands.CREATE_FILE_FROM_TEMPLATE);
      //press Alt+N
      selenium.altKeyDown();
      selenium.keyDown("//", "N");
      selenium.keyUp("//", "N");
      selenium.altKeyUp();
      
      //click Bind button
      clickButton(BIND_BUTTON_LOCATOR);
      
      //click Save button
      clickButton(SAVE_BUTTON_LOCATOR);
      checkNoCustomizeHotkeyDialogWindow();
      
      //----- 19 ------------
      //Try Ctrl+H and Alt+N hotkeys in several tabs
      
      selectEditorTab(2);
      
      //press Alt+N
      runHotkeyWithinEditor(2, false, true, java.awt.event.KeyEvent.VK_N);
      Thread.sleep(TestConstants.SLEEP);
      
      checkCreateFileFromTemplateFormAndClose();
      
      //press Ctrl+H
      runHotkeyWithinEditor(2, true, false, java.awt.event.KeyEvent.VK_H);
      
      //check new html file created
      assertEquals("Untitled file.html *", getTabTitle(3));
      closeUnsavedFileAndDoNotSave(3);
      Thread.sleep(TestConstants.SLEEP);
      
      //select first tab
      selectEditorTab(0);
      runHotkeyWithinEditor(2, false, true, java.awt.event.KeyEvent.VK_N);
      Thread.sleep(TestConstants.SLEEP);
      
      checkCreateFileFromTemplateFormAndClose();
      //press Ctrl+H
      runHotkeyWithinEditor(2, true, false, java.awt.event.KeyEvent.VK_H);
      
      //check new html file created
      assertEquals("Untitled file.html *", getTabTitle(3));
      closeUnsavedFileAndDoNotSave(3);
      Thread.sleep(TestConstants.SLEEP);
      
      //select second tab
      selectEditorTab(1);
      runHotkeyWithinEditor(2, false, true, java.awt.event.KeyEvent.VK_N);
      
      checkCreateFileFromTemplateFormAndClose();
      
      //press Ctrl+H
      runHotkeyWithinEditor(2, true, false, java.awt.event.KeyEvent.VK_H);
      
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //check new html file created
      assertEquals("Untitled file.html *", getTabTitle(3));
      closeUnsavedFileAndDoNotSave(3);
      Thread.sleep(TestConstants.SLEEP);
      
      //close all tabs
      closeUnsavedFileAndDoNotSave(0);
      closeUnsavedFileAndDoNotSave(0);
      closeUnsavedFileAndDoNotSave(0);
   }
   
   /**
    * IDE-156:HotKeys customization
    * ----- 20 ------------
    * @throws Exception
    */
   @Test
   public void testHotkeysAfterRefresh() throws Exception
   {
      refresh();
      //----- 1 ------------
      //prepare hotkeys
      //Open "Customize Hotkeys" window (Window->Customize Hotkeys)
      runTopMenuCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_HOTKEYS);
      
      //check cutomize hotkeys dialog window appears
      checkCustomizeHotkeyDialogWindow();
      
      //Select "New HTML File" and bind Ctrl+H to this command 
      //(Press Ctrl+H, press "Bind" button)
      selectRow(Commands.NEW_HTML_FILE);
      //press Ctrl+H
      selenium.controlKeyDown();
      selenium.keyDown("//", "H");
      selenium.keyUp("//", "H");
      selenium.controlKeyUp();
      
      //click Bind button
      clickButton(BIND_BUTTON_LOCATOR);
      
      //Select "Create File From Template" and bind Alt+N to this command. Press Save button
      selectRow(Commands.CREATE_FILE_FROM_TEMPLATE);
      //press Alt+N
      selenium.altKeyDown();
      selenium.keyDown("//", "N");
      selenium.keyUp("//", "N");
      selenium.altKeyUp();
      
      //click Bind button
      clickButton(BIND_BUTTON_LOCATOR);
      
      //click Save button
      clickButton(SAVE_BUTTON_LOCATOR);
      
      checkNoCustomizeHotkeyDialogWindow();
      
      //----- 2 ------------
      //refresh browser window and check Ctrl+H and Alt+N
      
      refresh();
      Thread.sleep(TestConstants.SLEEP);
      
      selenium.altKeyDown();
      selenium.keyDown("//", "N");
      selenium.keyUp("//", "N");
      selenium.altKeyUp();
      Thread.sleep(TestConstants.SLEEP);
      
      checkCreateFileFromTemplateFormAndClose();
      
      selenium.controlKeyDown();
      selenium.keyDown("//body", "H");
      selenium.keyUp("//body", "H");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP);
      
      //check new html file created
      assertEquals("Untitled file.html *", getTabTitle(0));
      closeUnsavedFileAndDoNotSave(0);
      Thread.sleep(TestConstants.SLEEP);
      
      //----- 3 ------------
      runTopMenuCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.CUSTOMIZE_HOTKEYS);
      Thread.sleep(TestConstants.SLEEP);
      checkCustomizeHotkeyDialogWindow();
      
      //Select "New HTML File" and check Ctrl+H is bind
      selectRow(Commands.NEW_HTML_FILE);
      assertEquals("Ctrl+H", getTextFromBindColumn(Commands.NEW_HTML_FILE));
      
      //Select "Create File From Template" and check Alt+N is bind to it
      selectRow(Commands.CREATE_FILE_FROM_TEMPLATE);
      assertEquals("Alt+N", getTextFromBindColumn(Commands.CREATE_FILE_FROM_TEMPLATE));
      
      //close
      closeHotkeysWindow();
      
   }
   
}