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
import org.exoplatform.ide.ToolbarCommands;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * @author <a href="mailto:roman.iyvshyn@exoplatform.com">Roman Iyvshyn</a>
 * @version $Id: Aug 11, 2010
 *
 */
public class DeleteCurrentLineTest extends BaseTest
{
   
   private final static String TEST_FOLDER = DeleteCurrentLineTest.class.getSimpleName();
   
   //IDE-151
   @Test
   public void deleteLine() throws Exception
   {
      //      Create folder "Test" and file "test.html"
      Thread.sleep(TestConstants.SLEEP);
      createFolder(TEST_FOLDER);
      Thread.sleep(TestConstants.SLEEP);
      runCommandFromMenuNewOnToolbar(MenuCommands.New.HTML_FILE);
      Thread.sleep(TestConstants.SLEEP);
      
      //TODO****try****fix
      runToolbarButton(ToolbarCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      //****************
     
      String text1 = selenium.getText("//body[@class='editbox']");
          
      Thread.sleep(TestConstants.SLEEP_SHORT*3);
      saveAsUsingToolbarButton("test.html");
      Thread.sleep(TestConstants.SLEEP);
      assertEquals("1 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));
      Thread.sleep(TestConstants.SLEEP_SHORT*2);
      
      //      Click on "Edit->Delete Current Line" top menu command.
      runTopMenuCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.DELETE_CURRENT_LINE);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

      String text2 = selenium.getText("//body[@class='editbox']");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      assertFalse(text1.equals(selenium.getText("//body[@class='editbox']")));
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      assertEquals("1 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));
      
      //      Press "Ctrl+D" keys.
      selenium.controlKeyDown();
      selenium.keyDown("//body[@class='editbox']", "D");
      selenium.keyUp("//body[@class='editbox']", "D");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP_SHORT*3);
      
      String text3 = selenium.getText("//body[@class='editbox']");
      Thread.sleep(TestConstants.SLEEP_SHORT*2);
      assertFalse(text2.equals(selenium.getText("//body[@class='editbox']")));
      Thread.sleep(TestConstants.SLEEP);
      assertEquals("1 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));

      //      Move cursor
      for (int i = 0; i < 2; i++)
      {
         selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_DOWN);
         Thread.sleep(TestConstants.SLEEP_SHORT*3);
         selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_DOWN);
      }
      Thread.sleep(TestConstants.SLEEP_SHORT*2);
      
      //      Press "Ctrl+D" keys.
      selenium.controlKeyDown();
      selenium.keyDown("//body[@class='editbox']", "D");
      selenium.keyUp("//body[@class='editbox']", "D");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP_SHORT*3);
      
      String text4 = selenium.getText("//body[@class='editbox']");
      Thread.sleep(TestConstants.SLEEP_SHORT*2);
      assertFalse(text3.equals(selenium.getText("//body[@class='editbox']")));
      Thread.sleep(TestConstants.SLEEP_SHORT*2);
      assertEquals("3 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));
      Thread.sleep(TestConstants.SLEEP_SHORT*2);
      //    Click on "Edit->Delete Current Line" top menu command.
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='Edit']", "");
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[text()='Delete Current Line']", "");
      
      Thread.sleep(TestConstants.SLEEP_SHORT*2);
      String text5 = selenium.getText("//body[@class='editbox']");
      Thread.sleep(TestConstants.SLEEP_SHORT*2);
      assertFalse(text4.equals(selenium.getText("//body[@class='editbox']")));
      Thread.sleep(TestConstants.SLEEP_SHORT*2);
      assertEquals("3 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));
      Thread.sleep(TestConstants.SLEEP_SHORT*2);
      //      Click on "Edit->Delete Current Line" top menu command.

      runTopMenuCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.DELETE_CURRENT_LINE);
      Thread.sleep(TestConstants.SLEEP_SHORT*2);
      
      String text6 = selenium.getText("//body[@class='editbox']");
      Thread.sleep(TestConstants.SLEEP_SHORT*2);
      assertFalse(text5.equals(selenium.getText("//body[@class='editbox']")));
      Thread.sleep(TestConstants.SLEEP_SHORT*2);
      assertEquals("3 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));
      Thread.sleep(TestConstants.SLEEP_SHORT*2);
      //      Create empty "test1.txt" file end open this one in another tab.
      runCommandFromMenuNewOnToolbar(MenuCommands.New.TEXT_FILE);
      saveAsUsingToolbarButton("test1.txt");
      Thread.sleep(TestConstants.SLEEP_SHORT*2);
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
      Thread.sleep(TestConstants.SLEEP_SHORT*2);
      String texty1 = selenium.getText("//body[@class='editbox']");
      Thread.sleep(TestConstants.SLEEP_SHORT*2);
      selenium.selectFrame("relative=top");
      //      Go to line 2 and click on "Edit->Delete Current Line" top menu command.

      runTopMenuCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.DELETE_CURRENT_LINE);
      Thread.sleep(TestConstants.SLEEP_SHORT*2);
      
      assertFalse(texty1.equals(selenium.getText("//body[@class='editbox']")));
      Thread.sleep(TestConstants.SLEEP_SHORT*2);
      String texty2 = selenium.getText("//body[@class='editbox']");
      Thread.sleep(TestConstants.SLEEP_SHORT*2);
      assertEquals("2 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));
      //      Press "Ctrl+D" keys.
      selenium.controlKeyDown();
      selenium.keyDown("//body[@class='editbox']", "D");
      selenium.keyUp("//body[@class='editbox']", "D");
      selenium.controlKeyUp();

      selectEditorTab(0);
      Thread.sleep(TestConstants.SLEEP_SHORT*2);
      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=1]/");
      selectEditorTab(1);
      Thread.sleep(TestConstants.SLEEP);
      
      assertEquals("2 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));
      Thread.sleep(TestConstants.SLEEP_SHORT*2);
      assertEquals(texty2, selenium.getText("//body[@class='editbox']"));
      //      Go to line 1 and click on "Edit->Delete Current Line" top menu command.
      for (int i = 0; i < 1; i++)
      {
         selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_UP);
         selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_UP);
      }
      Thread.sleep(TestConstants.SLEEP_SHORT*2);
      runTopMenuCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.DELETE_CURRENT_LINE);

      Thread.sleep(TestConstants.SLEEP_SHORT*2);
      
      String texty3 = selenium.getText("//body[@class='editbox']");
      Thread.sleep(TestConstants.SLEEP_SHORT*2);
      assertEquals("1 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));
      Thread.sleep(TestConstants.SLEEP_SHORT*2);
      assertEquals(texty3, selenium.getText("//body[@class='editbox']"));
      Thread.sleep(TestConstants.SLEEP_SHORT*2);
      //      Press "Ctrl+D" keys.
      selenium.controlKeyDown();
      selenium.keyDown("//body[@class='editbox']", "D");
      selenium.keyUp("//body[@class='editbox']", "D");
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.SLEEP_SHORT*2);
      assertEquals(texty3, selenium.getText("//body[@class='editbox']"));
      Thread.sleep(TestConstants.SLEEP_SHORT*2);

      selectEditorTab(0);
      Thread.sleep(TestConstants.SLEEP_SHORT*2);
      //      Return to the tab with file "test.html".

      selectEditorTab(1);
      Thread.sleep(TestConstants.SLEEP);
      
      assertEquals("1 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));
      Thread.sleep(TestConstants.SLEEP_SHORT*2);

      selectEditorTab(0);
      Thread.sleep(TestConstants.SLEEP_SHORT*2);
      
      assertEquals("3 : 1", selenium.getText("//td[@class='exo-statusText-table-middle']/nobr"));
   }
   
   @AfterClass
   public static void tearDown()
   {
      cleanRepository(REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/");
   }
}
