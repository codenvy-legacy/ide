/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.operation.contextmenu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 7, 2012 2:55:58 PM anya $
 * 
 */
public class ProjectExplorerContextMenuTest extends BaseTest
{
   private final static String PROJECT = ProjectExplorerContextMenuTest.class.getSimpleName();

   private static final String FOLDER1 = "folder1";

   private static final String FOLDER2 = "folder2";

   private static final String GOGLE_CHROME_LOCATOR =
      "//div[@id='ideProjectExplorerItemTreeGrid']//div[@class='ide-Tree-label' and text()='" + PROJECT + "'" + "]";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FOLDER);
         VirtualFileSystemUtils.createFolder(link, FOLDER1);
         VirtualFileSystemUtils.createFolder(link, FOLDER2);
      }
      catch (Exception e)
      {
      }
   }

   /**
    * Clear tests results.
    */
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
      }
   }

   @Test
   public void testSelectItemByRightMouseClick() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();

      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER2);

      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FOLDER1);
      assertEquals(" " + PROJECT + " / " + FOLDER1, IDE.STATUSBAR.getNavigationStatus());

      IDE.PROJECT.EXPLORER.selectItemByRightClick(PROJECT + "/" + FOLDER2);
      assertEquals(" " + PROJECT + " / " + FOLDER2, IDE.STATUSBAR.getNavigationStatus());
      IDE.CONTEXT_MENU.waitOpened();
      IDE.CONTEXT_MENU.closeContextMenu();
   }

   @Test
   public void testRootContextMenuState() throws Exception
   {
      driver.navigate().refresh();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER2);
      //need for Google Chrome browser. Because if we use IDE.PROJECT.EXPLORER.selectItemByRightClick(PROJECT) method
      //in Chrome browser is select FOLDER1 
      if (BROWSER_COMMAND.toString().equals("*googlechrome"))
      {

         WebElement elem = driver.findElement(By.xpath(GOGLE_CHROME_LOCATOR));
         new Actions(driver).contextClick(elem).build().perform();
      }
      else
      {
         IDE.PROJECT.EXPLORER.selectItemByRightClick(PROJECT);
      }
      assertEquals(" " + PROJECT, IDE.STATUSBAR.getNavigationStatus());

      IDE.CONTEXT_MENU.waitOpened();

      assertTrue(IDE.CONTEXT_MENU.isCommandEnabled(MenuCommands.File.DELETE));
      assertTrue(IDE.CONTEXT_MENU.isCommandEnabled("Rename..."));
      assertTrue(IDE.CONTEXT_MENU.isCommandEnabled("Open..."));
      assertTrue(IDE.CONTEXT_MENU.isCommandEnabled("Close"));
      assertTrue(IDE.CONTEXT_MENU.isCommandEnabled("Properties..."));

      assertFalse(IDE.CONTEXT_MENU.isCommandEnabled("Paste Item(s)"));
      assertFalse(IDE.CONTEXT_MENU.isCommandEnabled(MenuCommands.Edit.CUT_MENU));
      assertFalse(IDE.CONTEXT_MENU.isCommandEnabled(MenuCommands.Edit.COPY_MENU));
      assertFalse(IDE.CONTEXT_MENU.isCommandEnabled(MenuCommands.Edit.PASTE_MENU));
   }

   @Test
   public void testFolderContextMenuState() throws Exception
   {
      driver.navigate().refresh();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER1);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER2);

      IDE.PROJECT.EXPLORER.selectItemByRightClick(PROJECT + "/" + FOLDER1);
      assertEquals(" " + PROJECT + " / " + FOLDER1, IDE.STATUSBAR.getNavigationStatus());

      IDE.CONTEXT_MENU.waitOpened();
      assertTrue(IDE.CONTEXT_MENU.isCommandEnabled(MenuCommands.File.DELETE));
      assertTrue(IDE.CONTEXT_MENU.isCommandEnabled(MenuCommands.Edit.CUT_MENU));
      assertTrue(IDE.CONTEXT_MENU.isCommandEnabled(MenuCommands.Edit.COPY_MENU));
      assertFalse(IDE.CONTEXT_MENU.isCommandEnabled(MenuCommands.Edit.PASTE_MENU));

      IDE.CONTEXT_MENU.runCommand(MenuCommands.Edit.CUT_MENU);
      IDE.CONTEXT_MENU.waitClosed();

      IDE.PROJECT.EXPLORER.selectItemByRightClick(PROJECT + "/" + FOLDER2);
      assertEquals(" " + PROJECT + " / " + FOLDER2, IDE.STATUSBAR.getNavigationStatus());

      IDE.CONTEXT_MENU.waitOpened();
      assertTrue(IDE.CONTEXT_MENU.isCommandEnabled(MenuCommands.File.DELETE));
      assertTrue(IDE.CONTEXT_MENU.isCommandEnabled(MenuCommands.Edit.CUT_MENU));
      assertTrue(IDE.CONTEXT_MENU.isCommandEnabled(MenuCommands.Edit.COPY_MENU));
      assertTrue(IDE.CONTEXT_MENU.isCommandEnabled(MenuCommands.Edit.PASTE_MENU));

      IDE.CONTEXT_MENU.runCommand(MenuCommands.Edit.PASTE_MENU);
      IDE.CONTEXT_MENU.waitClosed();

      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItemNotPresent(PROJECT + "/" + FOLDER1);
      IDE.PROJECT.EXPLORER.waitItemPresent(PROJECT + "/" + FOLDER2 + "/" + FOLDER1);
   }

}
