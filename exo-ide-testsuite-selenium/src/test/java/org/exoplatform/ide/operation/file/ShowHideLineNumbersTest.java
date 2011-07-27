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

   //IDE-59 Show/hide line numbers  

   private static final String FOLDER_NAME = ShowHideLineNumbersTest.class.getSimpleName();

   private static final String XML = "xml_file.xml";

   private static final String GROOVY = "groovy_file.groovy";

   @Before
   public void setUp()
   {
      if (selenium().isCookiePresent("line-numbers_bool"))
      {
         selenium().deleteCookie("line-numbers_bool", "/IDE-application/IDE/");
      }

      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_NAME);
         VirtualFileSystemUtils.put("xml file content".getBytes(), MimeType.TEXT_XML, WS_URL + FOLDER_NAME + "/" + XML);
         VirtualFileSystemUtils.put("public class a {}".getBytes(), MimeType.APPLICATION_GROOVY, WS_URL + FOLDER_NAME
            + "/" + GROOVY);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @After
   public void tearDown()
   {
      if (selenium().isCookiePresent("line-numbers_bool"))
      {
         selenium().deleteCookie("line-numbers_bool", "/IDE-application/IDE/");
      }

      try
      {
         VirtualFileSystemUtils.delete(WS_URL + FOLDER_NAME);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testShowHideLineRestService() throws Exception
   {
      IDE.WORKSPACE.waitForItem(WS_URL + FOLDER_NAME + "/");
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + FOLDER_NAME + "/");

      /*
       * 1. By default menu commands "Edit > Show line numbers" and "Edit > Hide line numbers" must be hidden. 
       */
      IDE.MENU.checkCommandVisibility(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS, false);
      IDE.MENU.checkCommandVisibility(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS, false);

      /*
       * 2. Open XML file by doubleclicking.
       *     Line numbers must shows in editor and Menu command "Edit > Hide line numbers" must be enabled.
       */
      IDE.WORKSPACE.doubleClickOnFile(WS_URL + FOLDER_NAME + "/" + XML);
      IDE.EDITOR.checkLineNumbersVisible(true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS, true);

      /*
       * 4. Run menu command Edit > Hide line numbers.
       *     Menu command "Edit > Show line numbers" must be enabled.
       *     Line numbers must be hidden. 
       */
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS, true);
      IDE.EDITOR.checkLineNumbersVisible(false);

      /*
       * 5. Run menu command Edit > Show Line Numbers. 
       *    Menu command Edit > Hide line numbers must be enabled.
       *    Line numbers must shows in editor.
       */
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS, true);
      IDE.EDITOR.checkLineNumbersVisible(true);

      /*
       * 6. Close XML file.
       *     Menu commands Edit > Show / Hide line numbers must be hidden.
       */
      IDE.EDITOR.closeFile(0);
      IDE.MENU.checkCommandVisibility(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS, false);
      IDE.MENU.checkCommandVisibility(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS, false);

      /*
       * 7. Open GROOVY file
       *    File must be opened in editor and line numbers must shows.
       */
      IDE.WORKSPACE.doubleClickOnFile(WS_URL + FOLDER_NAME + "/" + GROOVY);
      IDE.EDITOR.checkCodeEditorOpened(0);

      /*
       * 8. Run menu command Edit > Hide line numbers and close editor.
       */
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS);
      IDE.EDITOR.closeFile(0);

      /*
       * 9. Open XML file.
       *     Line numbers must be hidden and menu command "Edit > Show Line Numbers" must be enabled.
       */
      IDE.WORKSPACE.doubleClickOnFile(WS_URL + FOLDER_NAME + "/" + XML);
      IDE.EDITOR.checkLineNumbersVisible(false);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS, true);

      /*
       * 10. Run menu command "Edit > Show Line Numbers"
       *      Line numbers must shows in editor and menu command "Edit > Hide line numbers" must be enabled.
       */
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS);
      IDE.EDITOR.checkLineNumbersVisible(true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS, true);

      /*
       * 11. Close editor.
       */
      IDE.EDITOR.closeFile(0);
   }

}
