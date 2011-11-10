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
package org.exoplatform.ide.operation.file;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class ShowHideLineNumbersTest extends BaseTest
{

   private static final String PROJECT = ShowHideLineNumbersTest.class.getSimpleName();

   private static final String XML = "xml_file.xml";

   private static final String GROOVY = "groovy_file.groovy";

   @Before
   public void setUp()
   {
      if (driver.manage().getCookieNamed(LINE_NUMBERS_COOKIE) != null)
      {
         driver.manage().deleteCookieNamed(LINE_NUMBERS_COOKIE);
      }
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
         VirtualFileSystemUtils.put("xml file content".getBytes(), MimeType.TEXT_XML, WS_URL + PROJECT + "/" + XML);
         VirtualFileSystemUtils.put("public class a {}".getBytes(), MimeType.APPLICATION_GROOVY, WS_URL + PROJECT + "/"
            + GROOVY);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @After
   public void tearDown()
   {
      if (driver.manage().getCookieNamed(LINE_NUMBERS_COOKIE) != null)
      {
         driver.manage().deleteCookieNamed(LINE_NUMBERS_COOKIE);
      }

      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testShowHideLineNumbers() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + XML);

      /*
       * By default menu commands "Edit > Show line numbers" and "Edit > Hide line numbers" must be hidden. 
       */
      assertFalse(IDE.MENU.isCommandVisible(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS));
      assertFalse(IDE.MENU.isCommandVisible(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS));

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + XML);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + XML);

      assertTrue(IDE.EDITOR.isLineNumbersVisible());
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS));

      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS);
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS));
      assertFalse(IDE.EDITOR.isLineNumbersVisible());

      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS);
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS));
      assertTrue(IDE.EDITOR.isLineNumbersVisible());

      IDE.EDITOR.closeFile(1);
      assertFalse(IDE.MENU.isCommandVisible(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS));
      assertFalse(IDE.MENU.isCommandVisible(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS));

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + GROOVY);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + GROOVY);

      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS);
      IDE.EDITOR.closeFile(1);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + XML);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + XML);
      assertFalse(IDE.EDITOR.isLineNumbersVisible());
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS));

      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS);
      assertTrue(IDE.EDITOR.isLineNumbersVisible());
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS));

      IDE.EDITOR.closeFile(1);
   }

}
