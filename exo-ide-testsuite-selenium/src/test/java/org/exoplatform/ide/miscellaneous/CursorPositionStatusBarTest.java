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

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:roman.iyvshyn@exoplatform.com">Iuvshyn Roman</a>
 * @version $Id: 
 * 
 */
public class CursorPositionStatusBarTest extends BaseTest
{
   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   private final static String TEST_FOLDER = CursorPositionStatusBarTest.class.getSimpleName();

   private final static String FILE_1 = "Untitled File.html";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(URL + TEST_FOLDER);
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

   //IDE-154
   @Test
   public void testCursorPositionInStatusBar() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();

      IDE.WORKSPACE.selectItem(URL + TEST_FOLDER + "/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);
      IDE.EDITOR.waitTabPresent(0);
      IDE.NAVIGATION.saveFileAs(FILE_1);

      //      IDE.WORKSPACE.waitForRootItem();

      assertEquals("1 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      //click on editor
      IDE.EDITOR.clickOnEditor();
      // change cursor position in editor
      for (int i = 0; i < 6; i++)
      {
         selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_RIGHT);
         selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_RIGHT);
      }

      IDE.selectMainFrame();
      //chek position in status bar 
      assertEquals("1 : 7", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));

      // change cursor position
      for (int i = 0; i < 6; i++)
      {
         selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_DOWN);
         selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_DOWN);
      }
      Thread.sleep(TestConstants.SLEEP * 3);
      for (int i = 0; i < 1; i++)
      {
         selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_RIGHT);
         selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_RIGHT);
      }

      IDE.selectMainFrame();

      //		check status bar
      assertEquals("7 : 8", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));

      //	Create Css
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.CSS_FILE);
      Thread.sleep(TestConstants.SLEEP);

      // selectMainFrame();
      IDE.EDITOR.selectTab(0);

      //TODO fix problem see issue IDE -713
      //		check status bar
      assertEquals("7 : 8", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));
      //	refresh
      refresh();

      waitForElementPresent("//td[@class='exo-statusText-table-middle']/nobr[text()='1 : 1']");
      //			check status bar
      assertEquals("1 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));
      IDE.EDITOR.closeFile(0);
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + TEST_FOLDER);
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
}
