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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
      Thread.sleep(TestConstants.SLEEP);
      selectRootOfWorkspaceTree();
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
   }

   /**
    * Keyboard works in the TreeGrid only within the Mozilla Firefox browser with SmartGWT 2.2, 2.3
    * @throws Exception
    */
   @Test
   public void testUsingKeyboardInNavigationPanel() throws Exception
   {
      // Keyboard works in the TreeGrid only within the Mozilla Firefox browser with SmartGWT 2.2, 2.3
      if (!BROWSER_COMMAND.equals(EnumBrowserCommand.CHROME)
         && !BROWSER_COMMAND.toString().toLowerCase().contains("firefox"))
      {
         return;
      }

      createFolder(TEST_FOLDER);
      selectItemInWorkspaceTree(TEST_FOLDER);
      createFolder(TEST_SUBFOLDER);

      Thread.sleep(TestConstants.SLEEP);

      // test java.awt.event.KeyEvent.VK_UP,java.awt.event.KeyEvent.VK_LEFT      
      selectItemInWorkspaceTree(TEST_SUBFOLDER);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_LEFT);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      assertFalse(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[title="
         + TEST_SUBFOLDER + "]/col[fieldName=title]"));

      // test java.awt.event.KeyEvent.VK_RIGHT,java.awt.event.KeyEvent.VK_DOWNT      
      selectItemInWorkspaceTree(TEST_FOLDER);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_RIGHT);
      Thread.sleep(TestConstants.SLEEP);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      assertTrue(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[title="
         + TEST_SUBFOLDER + "]/col[fieldName=title]"));

      // test keyboard with opened Content Panel
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);

      // test java.awt.event.KeyEvent.VK_UP,java.awt.event.KeyEvent.VK_LEFT      
      selectItemInWorkspaceTree(TEST_SUBFOLDER);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_LEFT);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      assertFalse(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[title="
         + TEST_SUBFOLDER + "]/col[fieldName=title]"));

      IDE.editor().closeUnsavedFileAndDoNotSave(0);
   }

   /**
    * Keyboard works in the TreeGrid only within the Mozilla Firefox browser with SmartGWT 2.2, 2.3
    * @throws Exception
    */
   @Test
   public void testUsingKeyboardInSearchPanel() throws Exception
   {
      // Keyboard works in the TreeGrid only within the Mozilla Firefox browser with SmartGWT 2.2, 2.3
      if (!BROWSER_COMMAND.equals(EnumBrowserCommand.CHROME)
         && !BROWSER_COMMAND.toString().toLowerCase().contains("firefox"))
      {
         return;
      }

      createFolder(TEST_FOLDER);
      selectItemInWorkspaceTree(TEST_FOLDER);

      createSaveAndCloseFile(MenuCommands.New.GOOGLE_GADGET_FILE, TEST_FILE, 0);

      performSearch("/" + TEST_FOLDER + "/", "", MimeType.GOOGLE_GADGET);
      assertElementPresentSearchResultsTree(TEST_FILE);

      // test java.awt.event.KeyEvent.VK_UP,java.awt.event.KeyEvent.VK_LEFT
      selectItemInSearchResultsTree(TEST_FILE);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_LEFT);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      assertElementNotPresentSearchResultsTree(TEST_FILE);

      // test java.awt.event.KeyEvent.VK_RIGHT,java.awt.event.KeyEvent.VK_DOWNT      
      selectItemInWorkspaceTree(WS_NAME);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_RIGHT);
      Thread.sleep(TestConstants.SLEEP);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      assertElementPresentSearchResultsTree(TEST_FILE);
   }

   /**
    * Keyboard works in the TreeGrid only within the Mozilla Firefox browser with SmartGWT 2.2, 2.3
    * @throws Exception
    */
   @Test
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
         VirtualFileSystemUtils.mkcol(URL + TEST_FOLDER);
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
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      selectItemInWorkspaceTree(TEST_FOLDER);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);     

      openFileFromNavigationTreeWithCodeEditor(TEST_FILE, false);

      // open Outline Panel
      IDE.toolbar().runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      Thread.sleep(TestConstants.SLEEP);

      IDE.editor().selectTab(0);
      IDE.editor().clickOnEditor();

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);

      Thread.sleep(TestConstants.SLEEP);

      // check outline tree
      IDE.outline().assertElementPresentOutlineTree("Module");
      IDE.outline().assertElementPresentOutlineTree("ModulePrefs");
      IDE.outline().assertElementPresentOutlineTree("Content");
      IDE.outline().assertElementNotPresentOutlineTree("CDATA");

      // verify keyboard key pressing within the outline
      IDE.outline().selectItemInOutlineTree("Module");
      assertEquals("2 : 1", getCursorPositionUsingStatusBar());

      // open "Content" node in the Outline Panel and got to "CDATA" node

      IDE.outline().selectItemInOutlineTree("Content");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.SLEEP_SHORT);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.SLEEP_SHORT);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_RIGHT);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.SLEEP_SHORT);

      // check outline tree     
      IDE.outline().assertElementPresentOutlineTree("CDATA");
      assertEquals("6 : 1", getCursorPositionUsingStatusBar());

      IDE.editor().closeTab(0);
   }

   @After
   public void tearDown() throws Exception
   {
      IDE.editor().closeTab(0);
      VirtualFileSystemUtils.delete(URL + TEST_FOLDER);
      selectWorkspaceTab();
   }

}
