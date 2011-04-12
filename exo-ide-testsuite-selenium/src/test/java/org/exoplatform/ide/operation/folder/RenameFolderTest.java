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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.Locators;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
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
public class RenameFolderTest extends BaseTest
{
   private final static String FOLDER_NAME = RenameFolderTest.class.getSimpleName();

   private final static String NEW_FOLDER_NAME = "FolderRenamed";

   private final static String ORIG_URL =
      BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER_NAME;

   private final static String RENAME_URL =
      BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + NEW_FOLDER_NAME;

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(ORIG_URL);
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
    * Test the rename folder operation (TestCase IDE-51).
    * 
    * @throws Exception
    */
   @Test
   public void testRenameFolder() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
       IDE.navigator().selectItem(WS_URL);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      

      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.RENAME);

      assertTrue(selenium.isElementPresent(Locators.RenameItemForm.SC_RENAME_ITEM_WINDOW_LOCATOR));
      assertTrue(selenium.isElementPresent(Locators.RenameItemForm.SC_NAME_FIELD_LOCATOR));
      assertTrue(selenium.isElementPresent(Locators.RenameItemForm.SC_RENAME_BUTTON_LOCATOR));
      assertFalse(selenium.isElementPresent(Locators.RenameItemForm.MIME_TYPE_FIELD_LOCATOR));
      assertTrue(selenium.isElementPresent(Locators.RenameItemForm.SC_CANCEL_BUTTON_LOCATOR));
      
      selenium.click(Locators.RenameItemForm.SC_NAME_FIELD_LOCATOR);
      
      assertEquals(FOLDER_NAME,selenium.getValue(Locators.RenameItemForm.SC_NAME_FIELD_LOCATOR));
      selenium.type(Locators.RenameItemForm.SC_NAME_FIELD_LOCATOR, NEW_FOLDER_NAME);
      selenium.keyPress(Locators.RenameItemForm.SC_NAME_FIELD_LOCATOR, "\\13");
      
      Thread.sleep(TestConstants.SLEEP);
      
      IDE.navigator().assertItemNotPresent(ORIG_URL + "/");
      IDE.navigator().assertItemPresent(RENAME_URL + "/");

      assertEquals(404, VirtualFileSystemUtils.get(ORIG_URL).getStatusCode());
      assertEquals(200, VirtualFileSystemUtils.get(RENAME_URL).getStatusCode());
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(ORIG_URL);
         VirtualFileSystemUtils.delete(RENAME_URL);
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
