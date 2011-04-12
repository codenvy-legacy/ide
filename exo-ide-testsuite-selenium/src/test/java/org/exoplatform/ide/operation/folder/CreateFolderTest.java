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

import java.io.IOException;
import java.net.URLEncoder;

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
public class CreateFolderTest extends BaseTest
{

   private static String FOLDER_NAME_TOOLBAR = CreateFolderTest.class.getSimpleName();

   private static String FOLDER_NAME_DEFOLT = "New Folder";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   /**
    * Test to create folder using ToolBar button. (TestCase IDE-3)
    * 
    * @throws Exception
    */
   @Test
   public void testCreateFolderFromToolbar() throws Exception
   {
      //****TODO********fix
      Thread.sleep(TestConstants.SLEEP);
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.FOLDER);
      //****************
      selenium.focus("//input[@name='ideCreateFolderFormNameField']");
      // clear dafault name in textfield
      selenium.type("ideCreateFolderFormNameField", "");
      //type name folder
      selenium.typeKeys("ideCreateFolderFormNameField", FOLDER_NAME_TOOLBAR);
      selenium.keyPress("ideCreateFolderFormNameField", "\\13");
      //Thread.sleep(TestConstants.SLEEP);
      assertFalse(selenium.isElementPresent("ideCreateFolderFormNameField"));
      assertTrue(selenium.isTextPresent(FOLDER_NAME_TOOLBAR));
      IDE.navigator().assertItemPresent(URL + FOLDER_NAME_TOOLBAR + "/");
      assertEquals(200, VirtualFileSystemUtils.get(URL + FOLDER_NAME_TOOLBAR).getStatusCode());
   }

   /**
    * Test to create folder using main menu (TestCase IDE-3).
    * 
    * @throws Exception
    */
   @Test
   public void testCreateFolderMenu() throws Exception
   {
      //refresh page
      selenium.refresh();
      //wait...
      selenium.waitForPageToLoad("30000");
      Thread.sleep(TestConstants.SLEEP);
      //run command for create folder
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.FOLDER);
      //create folder
      selenium.click("ideCreateFolderFormCreateButton");
      //check disapear menu after create of folder
      assertFalse(selenium.isElementPresent("ideCreateFolderForm"));
      //check folder in tread menu
      //TODO Change URL path to folder another (using WS_URL)  
      IDE.navigator().assertItemPresent(URL + FOLDER_NAME_DEFOLT + "/");
      assertEquals(200, VirtualFileSystemUtils.get(URL + URLEncoder.encode("New Folder", "UTF-8")).getStatusCode());
   }

   /**
    * Checks the present of create folder form elements.
    */
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + FOLDER_NAME_TOOLBAR);
         VirtualFileSystemUtils.delete(URL + URLEncoder.encode("New Folder", "UTF-8"));
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
