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
package org.exoplatform.ide.operation.browse;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.EnumBrowserCommand;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class UsingKeyboardTest extends BaseTest
{

   private static final String TEST_SUBFOLDER = UsingKeyboardTest.class.getSimpleName() + "1";

   private static final String TEST_FOLDER = UsingKeyboardTest.class.getSimpleName() + "2";

   private static final String TEST_FILE = "usingKeyboardTestGoogleGadget.xml";

   private static final String TEST_FILE_PATH = "src/test/resources/org/exoplatform/ide/operation/file/" + TEST_FILE;

   private static final String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   @Before
   public void setUp() throws Exception
   {
      try
      {
         VirtualFileSystemUtils.mkcol(URL + TEST_FOLDER);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Keyboard works in the TreeGrid only within the Mozilla Firefox browser with SmartGWT 2.2, 2.3
    * @throws Exception
    */
   @Test
   public void testUsingKeyboardInNavigationPanel() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();

      // Keyboard works in the TreeGrid only within the Mozilla Firefox browser with SmartGWT 2.2, 2.3
      if (!BROWSER_COMMAND.equals(EnumBrowserCommand.CHROME)
         && !BROWSER_COMMAND.toString().toLowerCase().contains("firefox"))
      {
         return;
      }
      //create subfolder 
      VirtualFileSystemUtils.mkcol(URL + TEST_FOLDER + "/" + TEST_SUBFOLDER + "/");
      //refresh workspace    
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForRootItem();
      // test java.awt.event.KeyEvent.VK_UP,java.awt.event.KeyEvent.VK_LEFT      
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + TEST_SUBFOLDER + "/");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      IDE.WORKSPACE.waitForRootItem();
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_LEFT);
      IDE.WORKSPACE.waitForRootItem();
      IDE.NAVIGATION.assertItemNotVisible(WS_URL + TEST_FOLDER + "/" + TEST_SUBFOLDER + "/");

      // test java.awt.event.KeyEvent.VK_RIGHT,java.awt.event.KeyEvent.VK_DOWNT      
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_RIGHT);
      IDE.WORKSPACE.waitForRootItem();
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.NAVIGATION.assertItemVisible(WS_URL + TEST_FOLDER + "/" + TEST_SUBFOLDER + "/");

      // test keyboard with opened Content Panel
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);
      // test java.awt.event.KeyEvent.VK_UP,java.awt.event.KeyEvent.VK_LEFT      
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + TEST_SUBFOLDER + "/");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      IDE.WORKSPACE.waitForRootItem();
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_LEFT);
      IDE.WORKSPACE.waitForRootItem();
      IDE.NAVIGATION.assertItemNotVisible(WS_URL + TEST_FOLDER + "/" + TEST_SUBFOLDER + "/");
      //IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
      IDE.EDITOR.closeTabIgnoringChanges(0);
   }

   /**
    * Keyboard works in the TreeGrid only within the Mozilla Firefox browser with SmartGWT 2.2, 2.3
    * @throws Exception
    */
   @Test
   public void testUsingKeyboardInSearchPanel() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();

      // Keyboard works in the TreeGrid only within the Mozilla Firefox browser with SmartGWT 2.2, 2.3
      if (!BROWSER_COMMAND.equals(EnumBrowserCommand.CHROME)
         && !BROWSER_COMMAND.toString().toLowerCase().contains("firefox"))
      {
         return;
      }

      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      createSaveAndCloseFile(MenuCommands.New.GOOGLE_GADGET_FILE, TEST_FILE, 0);

      IDE.SEARCH.performSearch("/" + TEST_FOLDER + "/", "", MimeType.GOOGLE_GADGET);
      IDE.NAVIGATION.assertItemVisibleInSearchTree(WS_URL + TEST_FOLDER + "/" + TEST_FILE);

      // test java.awt.event.KeyEvent.VK_UP,java.awt.event.KeyEvent.VK_LEFT
      IDE.NAVIGATION.selectItemInSearchTree(WS_URL + TEST_FOLDER + "/" + TEST_FILE);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_LEFT);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.NAVIGATION.assertItemNotVisibleInSearchTree(WS_URL + TEST_FOLDER + "/" + TEST_FILE);

      // test java.awt.event.KeyEvent.VK_RIGHT,java.awt.event.KeyEvent.VK_DOWNT      
      IDE.NAVIGATION.selectItemInSearchTree(WS_URL);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_RIGHT);
      Thread.sleep(TestConstants.SLEEP);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      //IDE.NAVIGATION.selectItemInSerchTree(WS_URL);

      IDE.NAVIGATION.assertItemVisibleInSearchTree(WS_URL + TEST_FOLDER + "/" + TEST_FILE);
   }

   /**
    * Keyboard works in the TreeGrid only within the Mozilla Firefox browser with SmartGWT 2.2, 2.3
    * @throws Exception
    */
   //@Test
   public void testUsingKeyboardInOutlinePanel() throws Exception
   {
      // Keyboard works in the TreeGrid only within the Mozilla Firefox browser with SmartGWT 2.2, 2.3
      if (!BROWSER_COMMAND.equals(EnumBrowserCommand.CHROME)
         && !BROWSER_COMMAND.toString().toLowerCase().contains("firefox"))
      {
         return;
      }

      // copy test file into repository
      try
      {
         VirtualFileSystemUtils.put(TEST_FILE_PATH, MimeType.GOOGLE_GADGET, URL + TEST_FOLDER + "/" + TEST_FILE);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }

      // refresh page and open test file
      Thread.sleep(TestConstants.SLEEP);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL + TEST_FOLDER + "/" + TEST_FILE, false);

      // open Outline Panel
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      Thread.sleep(TestConstants.SLEEP);

      IDE.EDITOR.selectTab(0);
      IDE.EDITOR.clickOnEditor();

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);

      Thread.sleep(TestConstants.SLEEP);

      // check outline tree
      IDE.OUTLINE.assertElementPresentOutlineTree("Module");
      IDE.OUTLINE.assertElementPresentOutlineTree("ModulePrefs");
      IDE.OUTLINE.assertElementPresentOutlineTree("Content");
      //IDE.OUTLINE.assertElementNotPresentOutlineTree("CDATA");

      // verify keyboard key pressing within the outline
      IDE.OUTLINE.selectItemInOutlineTree("Module");
      assertEquals("2 : 1", getCursorPositionUsingStatusBar());

      // open "Content" node in the Outline Panel and got to "CDATA" node

      IDE.OUTLINE.selectItemInOutlineTree("Content");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.SLEEP_SHORT);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.SLEEP_SHORT);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_RIGHT);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.SLEEP_SHORT);

      // check outline tree     
      IDE.OUTLINE.assertElementPresentOutlineTree("CDATA");
      assertEquals("6 : 1", getCursorPositionUsingStatusBar());

      IDE.EDITOR.closeFile(0);
   }

   @After
   public void tearDown() throws Exception
   {
      VirtualFileSystemUtils.delete(URL + TEST_FOLDER);
      selectWorkspaceTab();
   }

}
