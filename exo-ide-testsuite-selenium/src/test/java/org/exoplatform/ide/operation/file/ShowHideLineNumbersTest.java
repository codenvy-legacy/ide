/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.operation.file;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.Utils;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class ShowHideLineNumbersTest extends BaseTest
{

   //IDE-59 Show/hide line numbers  

   private static final String XML = System.currentTimeMillis() + ".xml";

   private static final String GROOVY = System.currentTimeMillis() + ".groovy";
   
   private static final String FOLDER_NAME = "Folder" + System.currentTimeMillis(); 
      
   @Test
   public void testShowHideLineRestService() throws Exception
   {
      if (selenium.isCookiePresent("line-numbers_bool"))
      {
         selenium.deleteCookie("line-numbers_bool", "/IDE-application/IDE/");
      }
      
      Thread.sleep(TestConstants.SLEEP);
      createFolder(FOLDER_NAME);
      //------- 1 ---------------
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='" + "Edit" + "']", "");
      //check there is no show line number command in menu (enabled or disabled)
      assertFalse(selenium.isElementPresent("//td[@class='exo-popupMenuTitleField']/nobr[text()='"
         + MenuCommands.Edit.SHOW_LINE_NUMBERS + "']"));
      assertFalse(selenium.isElementPresent("//td[@class='exo-popupMenuTitleFieldDisabled']/nobr[text()='"
         + MenuCommands.Edit.SHOW_LINE_NUMBERS + "']"));
      //check there is no hide line number command in menu (enabled or disabled)
      assertFalse(selenium.isElementPresent("//td[@class='exo-popupMenuTitleField']/nobr[text()='"
         + MenuCommands.Edit.HIDE_LINE_NUMBERS + "']"));
      assertFalse(selenium.isElementPresent("//td[@class='exo-popupMenuTitleFieldDisabled']/nobr[text()='"
         + MenuCommands.Edit.HIDE_LINE_NUMBERS + "']"));
      selenium.mouseDown("//div[@class='exo-lockLayer']/");
      //------- 2 ---------------
      runCommandFromMenuNewOnToolbar("REST Service");

      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS, true);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      checkLineNumbersVisible(true);
      //------- 3 ---------------
      runTopMenuCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      checkLineNumbersVisible(false);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS, true);

      //------- 4 ---------------
      runTopMenuCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      checkLineNumbersVisible(true);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS, true);

      //------- 5 ---------------
      runTopMenuCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      checkLineNumbersVisible(false);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS, true);

      //------- 6 ---------------
      runCommandFromMenuNewOnToolbar(MenuCommands.New.XML_FILE);
      checkLineNumbersVisible(false);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS, true);

      //------- 7 ---------------
      selectEditorTab(0);
      checkLineNumbersVisible(false);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS, true);

      //------- 8 ---------------
      selectEditorTab(1);
      checkLineNumbersVisible(false);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS, true);

      //------- 9 ---------------
      runTopMenuCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      checkLineNumbersVisible(true);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS, true);

      //------- 10 ---------------
      runTopMenuCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      checkLineNumbersVisible(false);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS, true);

      //------- 11 ---------------
      saveAsByTopMenu(XML);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      closeTab("1");

      saveAsByTopMenu(GROOVY);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      closeTab("0");

      //------- 12 ---------------
      //check show/hide line numbers in saved and reopened file
      openFileFromNavigationTreeWithCodeEditor(XML, false);
      checkLineNumbersVisible(false);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS, true);

      runTopMenuCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      checkLineNumbersVisible(true);
      checkMenuCommandState(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS, true);
   }

   /**
    * Check is line numbers are shown in editor
    * 
    * @param visible is line numbers must be shown
    */
   private void checkLineNumbersVisible(boolean visible)
   {
      if (visible)
      {
         assertTrue(selenium.isElementPresent("//div[@class='CodeMirror-line-numbers']"));
      }
      else
      {
         assertFalse(selenium.isElementPresent("//div[@class='CodeMirror-line-numbers']"));
      }
   }

   @AfterClass
   public static void tearDown()
   {
      if (selenium.isCookiePresent("line-numbers_bool"))
      {
         selenium.deleteCookie("line-numbers_bool", "/IDE-application/IDE/");
      }
      String url = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";
      try
      {
         VirtualFileSystemUtils.delete(url + FOLDER_NAME);
//         VirtualFileSystemUtils.delete(url + XML);
//         VirtualFileSystemUtils.delete(url + GROOVY);
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
