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
package org.exoplatform.ide.operation.edit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * @author <a href="mailto:roman.iyvshyn@exoplatform.com">Roman Iyvshyn</a>
 * @version $Id: Aug 11, 2010
 *
 */
public class DeleteCurrentLineTest extends BaseTest
{
   //IDE-151
   @Test
   public void deleteLine() throws Exception
   {
      //      Create folder "Test" and file "test.html"
      Thread.sleep(TestConstants.SLEEP_SHORT);
      createFolder("test");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      runCommandFromMenuNewOnToolbar(MenuCommands.New.HTML_FILE);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      String text1 = selenium.getText("//body[@class='editbox']");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      saveAsUsingToolbarButton("test.html");
      Thread.sleep(TestConstants.SLEEP);
      assertEquals("1 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      //      Click on "Edit->Delete Current Line" top menu command.
      runTopMenuCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.DELETE_CURRENT_LINE);
      Thread.sleep(TestConstants.SLEEP_SHORT);

      String text2 = selenium.getText("//body[@class='editbox']");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertFalse(text1.equals(selenium.getText("//body[@class='editbox']")));
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertEquals("1 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));
      
      //      Press "Ctrl+D" keys.
      selenium.controlKeyDown();
      selenium.keyDown("//body[@class='editbox']", "D");
      selenium.keyUp("//body[@class='editbox']", "D");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      String text3 = selenium.getText("//body[@class='editbox']");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertFalse(text2.equals(selenium.getText("//body[@class='editbox']")));
      Thread.sleep(TestConstants.SLEEP);
      assertEquals("1 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));

      //      Move cursor
      for (int i = 0; i < 2; i++)
      {
         selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_DOWN);
         selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_DOWN);
      }
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      //      Press "Ctrl+D" keys.
      selenium.controlKeyDown();
      selenium.keyDown("//body[@class='editbox']", "D");
      selenium.keyUp("//body[@class='editbox']", "D");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      String text4 = selenium.getText("//body[@class='editbox']");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertFalse(text3.equals(selenium.getText("//body[@class='editbox']")));
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertEquals("3 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //    Click on "Edit->Delete Current Line" top menu command.
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='Edit']", "");
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[text()='Delete Current Line']", "");
      
      Thread.sleep(TestConstants.SLEEP_SHORT);
      String text5 = selenium.getText("//body[@class='editbox']");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertFalse(text4.equals(selenium.getText("//body[@class='editbox']")));
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertEquals("3 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //      Click on "Edit->Delete Current Line" top menu command.

      runTopMenuCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.DELETE_CURRENT_LINE);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      String text6 = selenium.getText("//body[@class='editbox']");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertFalse(text5.equals(selenium.getText("//body[@class='editbox']")));
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertEquals("3 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //      Create empty "test1.txt" file end open this one in another tab.
      runCommandFromMenuNewOnToolbar(MenuCommands.New.TEXT_FILE);
      saveAsUsingToolbarButton("test1.txt");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      Thread.sleep(TestConstants.SLEEP);
      assertEquals("1 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));
      selenium.selectFrame("//div[@class='tabSetContainer']/div/div[3]//iframe");
      //      Type (not paste) next text:
      //      line 1
      //      line 2
      selenium.typeKeys("//body[@class='editbox']", "line1");
      selenium.keyDown("//body[@class='editbox']", "13");
      selenium.keyUp("//body[@class='editbox']", "13");
      selenium.typeKeys("//body[@class='editbox']", "line2");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      String texty1 = selenium.getText("//body[@class='editbox']");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium.selectFrame("relative=top");
      //      Go to line 2 and click on "Edit->Delete Current Line" top menu command.

      runTopMenuCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.DELETE_CURRENT_LINE);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      assertFalse(texty1.equals(selenium.getText("//body[@class='editbox']")));
      Thread.sleep(TestConstants.SLEEP_SHORT);
      String texty2 = selenium.getText("//body[@class='editbox']");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertEquals("2 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));
      //      Press "Ctrl+D" keys.
      selenium.controlKeyDown();
      selenium.keyDown("//body[@class='editbox']", "D");
      selenium.keyUp("//body[@class='editbox']", "D");
      selenium.controlKeyUp();

      selectEditorTab(0);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=1]/");
      selectEditorTab(1);
      Thread.sleep(TestConstants.SLEEP);
      
      assertEquals("2 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertEquals(texty2, selenium.getText("//body[@class='editbox']"));
      //      Go to line 1 and click on "Edit->Delete Current Line" top menu command.
      for (int i = 0; i < 1; i++)
      {
         selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_UP);
         selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_UP);
      }
      Thread.sleep(TestConstants.SLEEP_SHORT);
      runTopMenuCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.DELETE_CURRENT_LINE);

      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      String texty3 = selenium.getText("//body[@class='editbox']");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertEquals("1 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertEquals(texty3, selenium.getText("//body[@class='editbox']"));
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //      Press "Ctrl+D" keys.
      selenium.controlKeyDown();
      selenium.keyDown("//body[@class='editbox']", "D");
      selenium.keyUp("//body[@class='editbox']", "D");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertEquals(texty3, selenium.getText("//body[@class='editbox']"));
      Thread.sleep(TestConstants.SLEEP_SHORT);

      selectEditorTab(0);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //      Return to the tab with file "test.html".

      selectEditorTab(1);
      Thread.sleep(TestConstants.SLEEP);
      
      assertEquals("1 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));
      Thread.sleep(TestConstants.SLEEP_SHORT);

      selectEditorTab(0);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      assertEquals("3 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));
   }
   
   @AfterClass
   public static void tearDown()
   {
      cleanRepository(REST_CONTEXT + "/jcr/" + REPO_NAME + "/" + WS_NAME + "/");
   }
}
