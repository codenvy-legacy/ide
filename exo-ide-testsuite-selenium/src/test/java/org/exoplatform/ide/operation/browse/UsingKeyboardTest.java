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

import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: ${date} ${time}
 * 
 */
public class UsingKeyboardTest extends BaseTest
{

   private static final String TEST_SUBFOLDER = "subFolder";

   private static final String PROJECT = "project";

   private static final String TEST_FILE = "Gadget.xml";

   private static final String NEW_FILE = "newcreatedfile.xml";

   private static final String TEST_FILE_PATH =
      "src/test/resources/org/exoplatform/ide/operation/file/usingKeyboardTestGoogleGadget.xml";

   private Robot robot;

   @BeforeClass
   public static void setUp() throws Exception
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + TEST_SUBFOLDER);
         VirtualFileSystemUtils.put(TEST_FILE_PATH, MimeType.GOOGLE_GADGET, WS_URL + PROJECT + "/" + TEST_FILE);
      }
      catch (Exception e)
      {
      }
   }

   /**
    * Keyboard works in the TreeGrid only within the Mozilla Firefox browser
    * with SmartGWT 2.2, 2.3
    * 
    * @throws Exception
    */
   @Test
   public void testUsingKeyboardInNavigationPanel() throws Exception
   {
      robot = new Robot();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_SUBFOLDER);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + TEST_SUBFOLDER);

      Thread.sleep(2000);
      robot.keyPress(KeyEvent.VK_UP);
      robot.keyRelease(KeyEvent.VK_UP);
      robot.keyPress(KeyEvent.VK_LEFT);
      robot.keyRelease(KeyEvent.VK_LEFT);

      IDE.PROJECT.EXPLORER.waitForItemNotVisible(PROJECT + "/" + TEST_SUBFOLDER);

      IDE.PROJECT.EXPLORER.selectItem(PROJECT);

      Thread.sleep(2000);
      robot.keyPress(KeyEvent.VK_RIGHT);
      robot.keyRelease(KeyEvent.VK_RIGHT);

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_SUBFOLDER);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE);

      // test keyboard with opened Content Panel
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + TEST_FILE);
      IDE.EDITOR.waitActiveFile();

      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + TEST_SUBFOLDER);

      Thread.sleep(2000);
      robot.keyPress(KeyEvent.VK_UP);
      robot.keyRelease(KeyEvent.VK_UP);

      robot.keyPress(KeyEvent.VK_LEFT);
      robot.keyRelease(KeyEvent.VK_LEFT);

      IDE.PROJECT.EXPLORER.waitForItemNotVisible(PROJECT + "/" + TEST_SUBFOLDER);
      IDE.PROJECT.EXPLORER.waitForItemNotVisible(PROJECT + "/" + TEST_FILE);
      IDE.EDITOR.forcedClosureFile(1);
   }

   /**
    * Keyboard works in the TreeGrid only within the Mozilla Firefox browser
    * with SmartGWT 2.2, 2.3
    * 
    * @throws Exception
    */
   @Test
   public void testUsingKeyboardInSearchPanel() throws Exception
   {
      robot = new Robot();

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.PROJECT.EXPLORER.openItem(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_SUBFOLDER);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + TEST_SUBFOLDER);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.OPENSOCIAL_GADGET_FILE);
      IDE.EDITOR.waitActiveFile();
      IDE.EDITOR.saveAndCloseFile(1, NEW_FILE);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_SUBFOLDER + "/" + NEW_FILE);

      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + TEST_SUBFOLDER + "/" + NEW_FILE);
      IDE.SEARCH.performSearch("/" + PROJECT + "/" + TEST_SUBFOLDER, "", MimeType.GOOGLE_GADGET);
      IDE.SEARCH_RESULT.waitOpened();
      IDE.SEARCH_RESULT.waitItemPresent(PROJECT + "/" + TEST_SUBFOLDER + "/" + NEW_FILE);

      // move with keys in search three
      IDE.SEARCH_RESULT.selectItem(PROJECT + "/" + TEST_SUBFOLDER + "/" + NEW_FILE);

      Thread.sleep(2000);
      robot.keyPress(KeyEvent.VK_UP);
      robot.keyRelease(KeyEvent.VK_UP);
      robot.keyPress(KeyEvent.VK_UP);
      robot.keyRelease(KeyEvent.VK_UP);
      robot.keyPress(KeyEvent.VK_LEFT);
      robot.keyRelease(KeyEvent.VK_LEFT);

      IDE.SEARCH_RESULT.waitItemNotPresent(PROJECT + "/" + TEST_SUBFOLDER + "/" + NEW_FILE);
      IDE.SEARCH_RESULT.close();
   }

   /**
    * Keyboard works in the TreeGrid only within the Mozilla Firefox browser
    * with SmartGWT 2.2, 2.3
    * 
    * @throws Exception
    */
   @Test
   public void testUsingKeyboardInOutlinePanel() throws Exception
   {
      robot = new Robot();

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + TEST_FILE);
      IDE.EDITOR.waitActiveFile();
      //TODO Pause for build outline tree
      //after implementation method for check ready state, should be remove
      Thread.sleep(5000);
      // open Outline Panel
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      IDE.OUTLINE.waitOpened();

      IDE.EDITOR.selectTab(TEST_FILE);
      IDE.EDITOR.moveCursorDown(2);
      Thread.sleep(TestConstants.SLEEP);

      // check outline tree
      IDE.OUTLINE.waitItemPresent("Module");
      IDE.OUTLINE.waitItemPresent("ModulePrefs");
      IDE.OUTLINE.waitItemPresent("Content");

      // verify keyboard key pressing within the outline
      IDE.OUTLINE.selectItem("Module");
      IDE.STATUSBAR.waitCursorPositionControl();
      IDE.STATUSBAR.waitCursorPositionAt("2 : 1");

      // open "Content" node in the Outline Panel and got to "CDATA" node

      IDE.OUTLINE.selectItem("Content");

      Thread.sleep(2000);
      robot.keyPress(KeyEvent.VK_DOWN);
      robot.keyRelease(KeyEvent.VK_DOWN);
      robot.keyPress(KeyEvent.VK_DOWN);
      robot.keyRelease(KeyEvent.VK_DOWN);

      IDE.OUTLINE.waitItemAtPosition("CDATA", 4);
      IDE.OUTLINE.waitElementIsSelect("CDATA");
      IDE.STATUSBAR.waitCursorPositionAt("6 : 1");

      IDE.EDITOR.forcedClosureFile(1);
   }

   @AfterClass
   public static void tearDown() throws Exception
   {
      VirtualFileSystemUtils.delete(WS_URL + PROJECT);
   }

}
