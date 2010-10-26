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

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * @author <a href="mailto:roman.iyvshyn@exoplatform.com">Iuvshyn Roman</a>
 * @version $Id: 
 * 
 */
public class CursorPositionStatusBarTest extends BaseTest
{
   //IDE-154
   @Test
   public void testCursorPositionInStatusBar() throws Exception
   {

      runCommandFromMenuNewOnToolbar(MenuCommands.New.HTML_FILE);
      Thread.sleep(TestConstants.SLEEP);
      saveAsUsingToolbarButton("Untitled File.html");

      Thread.sleep(TestConstants.SLEEP);

      assertEquals("1 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));
      Thread.sleep(TestConstants.SLEEP);
      selectIFrameWithEditor(0);
      Thread.sleep(TestConstants.SLEEP);
      // format text for example HTML
      for (int i = 0; i < 6; i++)
      {
         selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_RIGHT);
         selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_RIGHT);
      }
      selectMainFrame();
      Thread.sleep(TestConstants.SLEEP);
      assertEquals("1 : 7", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));

      selectIFrameWithEditor(0);
      // format text for example HTML
      for (int i = 0; i < 6; i++)
      {
         selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_DOWN);
         selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_DOWN);
      }
      Thread.sleep(TestConstants.SLEEP_SHORT);

      for (int i = 0; i < 1; i++)
      {
         selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_RIGHT);
         selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_RIGHT);
      }

      selectMainFrame();
      Thread.sleep(TestConstants.SLEEP);
      //		check status bar
      assertEquals("7 : 8", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));

      //	Create Css
      runCommandFromMenuNewOnToolbar(MenuCommands.New.CSS_FILE);
      Thread.sleep(TestConstants.SLEEP);

//      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]");
//
//      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[2]//iframe");
      selectEditorTab(0);

      selectMainFrame();
      Thread.sleep(TestConstants.SLEEP);
      //		check status bar
      assertEquals("7 : 8", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));
      //	refresh
      selenium.refresh();
      selenium.waitForPageToLoad("30000");
      Thread.sleep(TestConstants.SLEEP);

      //			check status bar
      assertEquals("1 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));

      Thread.sleep(TestConstants.SLEEP);
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[1]/col[1]");
      Thread.sleep(TestConstants.SLEEP);
      deleteSelectedItems();

   }
   
   @AfterClass
   public static void tearDown()
   {
      cleanRepository(REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/");
   }
}
