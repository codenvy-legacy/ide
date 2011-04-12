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
       IDE.navigator().selectItem(WS_URL);
      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
//      Thread.sleep(TestConstants.SLEEP);
      IDE.navigator().selectItem(URL_TOOLBAR + "/");
      IDE.toolbar().runCommand("Delete Item(s)...");
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormCancelButton\"]"));
      assertTrue(selenium.isTextPresent("exact:Do you want to delete " + FOLDER_NAME_TOOLBAR +" ?"));
      selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]"));
      IDE.navigator().assertItemNotPresent(URL_TOOLBAR + "/");
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
       IDE.navigator().selectItem(WS_URL);
      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
//      Thread.sleep(TestConstants.SLEEP);
      IDE.navigator().selectItem(URL_MENU + "/");
      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.DELETE);
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormCancelButton\"]"));
      assertTrue(selenium.isTextPresent("exact:Do you want to delete " + FOLDER_NAME_MENU +" ?"));
      selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]"));
      IDE.navigator().selectItem(URL_MENU + "/");
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
