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

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Test;

import java.net.URLEncoder;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class CreateFolderTest extends BaseTest
{

   private static String FOLDER_NAME_TOOLBAR = CreateFolderTest.class.getSimpleName();

   private static String FOLDER_NAME_DEFOLT = "New Folder";

   /**
    * Test to create folder using main menu (TestCase IDE-3).
    * 
    * @throws Exception
    */
   @Test
   public void testCreateFolder() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      
      //run command for create folder
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.FOLDER);
      //create folder
      selenium().click("ideCreateFolderFormCreateButton");
      Thread.sleep(2000);
      //check disapear menu after create of folder
      assertFalse(selenium().isElementPresent("//div[@view-id='ideCreateFolderForm']"));
      //check folder in tread menu
      IDE.NAVIGATION.assertItemVisible(WS_URL + FOLDER_NAME_DEFOLT + "/");
      assertEquals(200, VirtualFileSystemUtils.get(WS_URL + URLEncoder.encode("New Folder", "UTF-8")).getStatusCode());
   }

   /**
    * Checks the present of create folder form elements.
    */
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + FOLDER_NAME_TOOLBAR);
         VirtualFileSystemUtils.delete(WS_URL + URLEncoder.encode("New Folder", "UTF-8"));
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

}
