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
import static org.junit.Assert.assertTrue;

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
public class RenameFolderTest extends BaseTest
{
   private final static String FOLDER_NAME = RenameFolderTest.class.getSimpleName();

   private final static String NEW_FOLDER_NAME = "FolderRenamed";

   private final String ORIG_URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/"
      + WS_NAME + "/" + FOLDER_NAME;

   private final String RENAME_URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/"
      + WS_NAME + "/" + NEW_FOLDER_NAME;

   @Before
   public void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(ORIG_URL);
      }
      catch (Exception e)
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
      IDE.WORKSPACE.waitForItem(ORIG_URL + "/");
      
      //select folder and run rename command
      IDE.WORKSPACE.selectItem(ORIG_URL + "/");
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.RENAME);
      chekAppearRenameForm();
      //set cursor on rename field
      selenium().click("ideRenameItemFormRenameField");
      //check default name folder in rename field
      assertEquals(FOLDER_NAME, selenium().getValue("ideRenameItemFormRenameField"));
      //type new name and press "enter"
      selenium().type("ideRenameItemFormRenameField", NEW_FOLDER_NAME);
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      IDE.RENAME_DIALOG.waitForRenameDialogNotPresent();
      // check appear folder with new name
      waitForElementPresent(IDE.NAVIGATION.getItemId(RENAME_URL + "/"));
      IDE.NAVIGATION.assertItemNotVisible(ORIG_URL + "/");
      IDE.NAVIGATION.assertItemVisible(RENAME_URL + "/");
      assertEquals(404, VirtualFileSystemUtils.get(ORIG_URL).getStatusCode());
      assertEquals(200, VirtualFileSystemUtils.get(RENAME_URL).getStatusCode());
   }

   /**
    * chek appear elements rename form
    */
   public void chekAppearRenameForm()
   {
      assertTrue(selenium().isElementPresent("//div[@view-id='ideRenameItemForm']"));
      assertTrue(selenium().isElementPresent("ideRenameItemFormRenameField"));
      assertTrue(selenium().isElementPresent("ideRenameItemFormRenameField"));
   }

   @After
   public void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(ORIG_URL);
         VirtualFileSystemUtils.delete(RENAME_URL);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
   
}
