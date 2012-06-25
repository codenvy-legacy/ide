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
package org.exoplatform.ide.operation.browse;

import static org.junit.Assert.fail;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for Show/Hide Hidden Files command.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ShowHideHiddenFilesTest.java Apr 2, 2012 11:05:52 AM azatsarynnyy $
 *
 */
public class ShowHideHiddenFilesTest extends BaseTest
{
   private static final String PROJECT = ShowHideHiddenFilesTest.class.getSimpleName();
   
   private static final String HIDDEN_FILE_NAME = ".htaccess";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);

      }
      catch (Exception e)
      {
         fail("Cant create project ");
      }
   }

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
   public void showHideHiddenFilesTest() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      
      // open project
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();

      Assert.assertTrue(IDE.MENU.isCommandVisible(MenuCommands.View.VIEW, MenuCommands.View.SHOW_HIDDEN_FILES));
      
      // create new text file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitTabPresent(1);
      
      // save it as hidden
      IDE.EDITOR.saveAs(1, HIDDEN_FILE_NAME);
      Assert.assertFalse(IDE.PROJECT.EXPLORER.isItemPresent(PROJECT + "/" + HIDDEN_FILE_NAME));

      // show hidden files
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.SHOW_HIDDEN_FILES);
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + HIDDEN_FILE_NAME);
      Assert.assertTrue(IDE.PROJECT.EXPLORER.isItemPresent(PROJECT + "/" + HIDDEN_FILE_NAME));
      
      // hide hidden files
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.SHOW_HIDDEN_FILES);
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItemNotPresent(PROJECT + "/" + HIDDEN_FILE_NAME);
     //Thread.sleep(2000);
      Assert.assertFalse(IDE.PROJECT.EXPLORER.isItemPresent(PROJECT + "/" + HIDDEN_FILE_NAME));
   }

}
