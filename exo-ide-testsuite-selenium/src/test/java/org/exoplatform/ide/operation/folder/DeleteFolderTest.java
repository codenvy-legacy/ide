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
package org.exoplatform.ide.operation.folder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class DeleteFolderTest extends BaseTest
{
  
   private final static String FOLDER_NAME_TOOLBAR = "deleteFolderToolBarTest";
   
   private final static String FOLDER_NAME_MENU = "deleteFolderMenuTest";

   private final static String URL_TOOLBAR = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER_NAME_TOOLBAR;
   
   private final static String URL_MENU = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER_NAME_MENU;
   
   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(URL_TOOLBAR);
         VirtualFileSystemUtils.mkcol(URL_MENU);
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
   
   
   /**
    * Test to delete folder using ToolBar button. (TestCase IDE-18)
    * 
    * @throws Exception
    */
   @Test
   public void testDeleteFolderFromToolbar() throws Exception
   {
    
      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(WS_NAME);
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(FOLDER_NAME_TOOLBAR);
      runToolbarButton("Delete Item(s)...");
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormCancelButton\"]"));
      assertTrue(selenium.isTextPresent("exact:Do you want to delete " + FOLDER_NAME_TOOLBAR +" ?"));
      selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]"));
      assertElementNotPresentInWorkspaceTree(FOLDER_NAME_TOOLBAR);
      assertEquals(404, VirtualFileSystemUtils.get(URL_TOOLBAR).getStatusCode());
   }
   
   
   /**
    * Test to delete folder using Main Menu. (TestCase IDE-18)
    * 
    * @throws Exception
    */
   @Test
   public void testDeleteFolderFromMainMenu() throws Exception
   {
    
      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(WS_NAME);
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(FOLDER_NAME_MENU);
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.DELETE);
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormCancelButton\"]"));
      assertTrue(selenium.isTextPresent("exact:Do you want to delete " + FOLDER_NAME_MENU +" ?"));
      selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]"));
      assertElementNotPresentInWorkspaceTree(FOLDER_NAME_MENU);
      assertEquals(404, VirtualFileSystemUtils.get(URL_MENU).getStatusCode());
   }
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL_TOOLBAR);
         VirtualFileSystemUtils.delete(URL_MENU);
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
