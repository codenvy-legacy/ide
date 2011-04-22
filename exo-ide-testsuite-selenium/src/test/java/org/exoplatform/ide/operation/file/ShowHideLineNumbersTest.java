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

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Test;

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
   
   private static final String FOLDER_NAME = SavingPreviouslyEditedFileTest.class.getSimpleName(); 
      
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
      
      IDE.MENU.checkCommandVisibility(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS, false);
      IDE.MENU.checkCommandVisibility(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS, false);
      
//      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='" + "Edit" + "']", "");
//      
//      //check there is no show line number command in menu (enabled or disabled)
//      assertFalse(selenium.isElementPresent("//td[@class='exo-popupMenuTitleField']/nobr[text()='" + MenuCommands.Edit.SHOW_LINE_NUMBERS + "']"));
//      assertFalse(selenium.isElementPresent("//td[@class='exo-popupMenuTitleFieldDisabled']/nobr[text()='" + MenuCommands.Edit.SHOW_LINE_NUMBERS + "']"));
//
//      //check there is no hide line number command in menu (enabled or disabled)
//      assertFalse(selenium.isElementPresent("//td[@class='exo-popupMenuTitleField']/nobr[text()='" + MenuCommands.Edit.HIDE_LINE_NUMBERS + "']"));
//      assertFalse(selenium.isElementPresent("//td[@class='exo-popupMenuTitleFieldDisabled']/nobr[text()='" + MenuCommands.Edit.HIDE_LINE_NUMBERS + "']"));
//      
//      selenium.mouseDown("//div[@class='exo-lockLayer']/");    
      
      //------- 2 ---------------
      IDE.TOOLBAR.runCommandFromNewPopupMenu("REST Service");

      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS, true);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      checkLineNumbersVisible(true);
      //------- 3 ---------------
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS);
//      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      checkLineNumbersVisible(false);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS, true);

      //------- 4 ---------------
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS);
//      Thread.sleep(TestConstants.SLEEP_SHORT);
      checkLineNumbersVisible(true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS, true);

      //------- 5 ---------------
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS);
//      Thread.sleep(TestConstants.SLEEP_SHORT);
      checkLineNumbersVisible(false);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS, true);

      //------- 6 ---------------
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      checkLineNumbersVisible(false);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS, true);

      //------- 7 ---------------
     IDE.EDITOR.selectTab(0);
      checkLineNumbersVisible(false);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS, true);

      //------- 8 ---------------
     IDE.EDITOR.selectTab(1);
      checkLineNumbersVisible(false);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS, true);

      //------- 9 ---------------
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS);
//      Thread.sleep(TestConstants.SLEEP_SHORT);
      checkLineNumbersVisible(true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS, true);

      //------- 10 ---------------
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS);
//      Thread.sleep(TestConstants.SLEEP_SHORT);
      checkLineNumbersVisible(false);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS, true);

      //------- 11 ---------------
      saveAsByTopMenu(XML);
      Thread.sleep(TestConstants.SLEEP_SHORT);
     IDE.EDITOR.closeTab(1);

      saveAsByTopMenu(GROOVY);
      Thread.sleep(TestConstants.SLEEP_SHORT);
     IDE.EDITOR.closeTab(0);

      //------- 12 ---------------
      //check show/hide line numbers in saved and reopened file
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(XML, false);
      checkLineNumbersVisible(false);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS, true);

      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.SHOW_LINE_NUMBERS);
//      Thread.sleep(TestConstants.SLEEP_SHORT);
      checkLineNumbersVisible(true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.HIDE_LINE_NUMBERS, true);
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
