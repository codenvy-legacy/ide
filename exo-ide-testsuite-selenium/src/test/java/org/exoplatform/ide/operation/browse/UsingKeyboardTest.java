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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: ${date} ${time}
 * 
 */
public class UsingKeyboardTest extends BaseTest
{

   private static final String TEST_SUBFOLDER = UsingKeyboardTest.class.getSimpleName() + "1";

   private static final String PROJECT = UsingKeyboardTest.class.getSimpleName() + "2";

   private static final String TEST_FILE = "usingKeyboardTestGoogleGadget.xml";

   private static final String TEST_FILE_PATH = "src/test/resources/org/exoplatform/ide/operation/file/" + TEST_FILE;

   @Before
   public void setUp() throws Exception
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
    * Keyboard works in the TreeGrid only within the Mozilla Firefox browser with SmartGWT 2.2, 2.3
    * 
    * @throws Exception
    */
      @Test
   public void testUsingKeyboardInNavigationPanel() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_SUBFOLDER);

      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + TEST_SUBFOLDER);
      IDE.PROJECT.EXPLORER.typeKeys(Keys.ARROW_UP.toString() + Keys.ARROW_LEFT);
      IDE.PROJECT.EXPLORER.waitForItemNotVisible(PROJECT + "/" + TEST_SUBFOLDER);

      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      IDE.PROJECT.EXPLORER.typeKeys(Keys.ARROW_RIGHT.toString() + Keys.ARROW_DOWN);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_SUBFOLDER);

      // test keyboard with opened Content Panel
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.OPENSOCIAL_GADGET_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + TEST_SUBFOLDER + "/Untitled file.gadget");

      // test java.awt.event.KeyEvent.VK_UP,java.awt.event.KeyEvent.VK_LEFT
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + TEST_SUBFOLDER);
      IDE.PROJECT.EXPLORER.typeKeys(Keys.ARROW_UP.toString() + Keys.ARROW_LEFT);
      IDE.PROJECT.EXPLORER.waitForItemNotVisible(PROJECT + "/" + TEST_SUBFOLDER);

      IDE.EDITOR.closeTabIgnoringChanges(1);
   }

   /**
    * Keyboard works in the TreeGrid only within the Mozilla Firefox browser with SmartGWT 2.2, 2.3
    * 
    * @throws Exception
    */
   @Test
   public void testUsingKeyboardInSearchPanel() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_SUBFOLDER);

      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + TEST_SUBFOLDER);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.OPENSOCIAL_GADGET_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + TEST_SUBFOLDER + "/Untitled file.gadget");
      IDE.EDITOR.saveAndCloseFile(1, TEST_FILE);
      
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      Thread.sleep(2000);
      IDE.SEARCH.performSearch("/" + PROJECT, "", MimeType.GOOGLE_GADGET);
      IDE.SEARCH_RESULT.waitOpened();
      assertTrue(IDE.SEARCH_RESULT.isItemPresent(PROJECT + "/" + TEST_FILE));

      IDE.SEARCH_RESULT.selectItem(PROJECT + "/" + TEST_FILE);
      IDE.SEARCH_RESULT.typeKeys(Keys.UP.toString());
      Thread.sleep(TestConstants.REDRAW_PERIOD * 4);
      
      //for GOOGLE CHROME method IDE.SEARCH_RESULT.typeKeys(Keys.ARROW_LEFT.toString());
      // does not work in Chrome browser
      if (IDE_SETTINGS.getString("selenium.browser.commad").equals("GOOGLE_CHROME"))
      {
         new Actions(driver).sendKeys(Keys.ARROW_LEFT).build().perform();
      }
      else
      IDE.SEARCH_RESULT.typeKeys(Keys.ARROW_LEFT.toString());
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      assertFalse(IDE.SEARCH_RESULT.isItemVisible(PROJECT + "/" + TEST_FILE));

      IDE.SEARCH_RESULT.selectItem(PROJECT);
      IDE.SEARCH_RESULT.typeKeys(Keys.RIGHT.toString());
      IDE.SEARCH_RESULT.typeKeys(Keys.DOWN.toString());
      assertTrue(IDE.SEARCH_RESULT.isItemVisible(PROJECT + "/" + TEST_FILE));
   }

   /**
    * Keyboard works in the TreeGrid only within the Mozilla Firefox browser with SmartGWT 2.2, 2.3
    * 
    * @throws Exception
    */
   @Test
   public void testUsingKeyboardInOutlinePanel() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + TEST_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + TEST_FILE);

      // open Outline Panel
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      IDE.OUTLINE.waitOpened();

      IDE.EDITOR.moveCursorDown(0, 2);
      Thread.sleep(TestConstants.SLEEP);

      // check outline tree
      assertTrue(IDE.OUTLINE.isItemPresent("Module"));
      assertTrue(IDE.OUTLINE.isItemPresent("ModulePrefs"));
      assertTrue(IDE.OUTLINE.isItemPresent("Content"));
      // IDE.OUTLINE.assertElementNotPresentOutlineTree("CDATA");

      // verify keyboard key pressing within the outline
      IDE.OUTLINE.selectItem("Module");
      IDE.STATUSBAR.waitCursorPositionControl();
      assertEquals("2 : 1", IDE.STATUSBAR.getCursorPosition());

      // open "Content" node in the Outline Panel and got to "CDATA" node

      IDE.OUTLINE.selectItem("Content");

      //On this moment outline component change. Keys in outline tree does not work. Logic of the test should  change
      //Old logic cooment
      // IDE.OUTLINE.typeKeys(Keys.ARROW_DOWN.toString() + Keys.ARROW_DOWN + Keys.ARROW_RIGHT + Keys.ARROW_DOWN);
      // assertTrue(IDE.OUTLINE.isItemPresent("CDATA"));

      IDE.GOTOLINE.goToLine(6);
      IDE.OUTLINE.waitItemAtPosition("CDATA", 4);
      IDE.OUTLINE.isItemSelected(5);
      assertEquals("6 : 1", IDE.STATUSBAR.getCursorPosition());

      IDE.EDITOR.closeFile(1);
   }

   @After
   public void tearDown() throws Exception
   {
      VirtualFileSystemUtils.delete(WS_URL + PROJECT);
   }

}
