/*
 * Copyright (C) 2013 eXo Platform SAS.
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

import static org.junit.Assert.fail;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:riuvshin@exoplatform.com">Roman Iuvshin</a>
 * @version $Id: 3:57:55 PM  Jan 28, 2013 $
 *
 */
public class ShowHideLineNumbersTest extends BaseTest
{
   private static final String PROJECT = ShowHideLineNumbersTest.class.getSimpleName();

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
   public void showHideLineNumbersTest() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);

      createFileAndCheckShowHideLinenumbers(MenuCommands.New.REST_SERVICE_FILE);
      createFileAndCheckShowHideLinenumbers(MenuCommands.New.TEXT_FILE);
      createFileAndCheckShowHideLinenumbers(MenuCommands.New.XML_FILE);
      createFileAndCheckShowHideLinenumbers(MenuCommands.New.HTML_FILE);
      createFileAndCheckShowHideLinenumbers(MenuCommands.New.CSS_FILE);
      createFileAndCheckShowHideLinenumbers(MenuCommands.New.OPENSOCIAL_GADGET_FILE);
      createFileAndCheckShowHideLinenumbers(MenuCommands.New.GROOVY_TEMPLATE_FILE);
      createFileAndCheckShowHideLinenumbers(MenuCommands.New.GROOVY_SCRIPT_FILE);
      createFileAndCheckShowHideLinenumbers(MenuCommands.New.CHROMATTIC);
   }

   private void createFileAndCheckShowHideLinenumbers(String menuTitle) throws InterruptedException, Exception
   {
      IDE.TOOLBAR.runCommandFromNewPopupMenu(menuTitle);
      IDE.EDITOR.waitActiveFile();
      IDE.EDITOR.waitLineNumbersVisible();
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS);
      IDE.EDITOR.waitLineNumbersNotVisible();
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS);
      IDE.EDITOR.waitLineNumbersVisible();
      IDE.EDITOR.forcedClosureFile(1);
   }

}
