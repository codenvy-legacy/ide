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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class CreateFolderTest extends BaseTest
{

   private static String FOLDER_NAME_TOOLBAR = "CreateFolderToolbar";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";

   /**
    * Test to create folder using ToolBar button. (TestCase IDE-3)
    * 
    * @throws Exception
    */
   @Test
   public void testCreateFolderFromToolbar() throws Exception
   {
      selenium.mouseDownAt("//div[@title='New']//img", "");
      selenium.mouseUpAt("//div[@title='New']//img", "");
      selenium.mouseDownAt("//td[@class=\"exo-popupMenuTitleField\"]//nobr[contains(text(), \"Folder\")]", "");
      checkCreateFolderFormElements();
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element");
      selenium
         .type(
            "scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element",
            "");
      selenium
         .typeKeys(
            "scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element",
            FOLDER_NAME_TOOLBAR);
      selenium
         .keyPress(
            "scLocator=//DynamicForm[ID=\"ideCreateFolderFormDynamicForm\"]/item[name=ideCreateFolderFormNameField]/element",
            "\\13");
      Thread.sleep(TestConstants.SLEEP);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      assertTrue(selenium.isTextPresent(FOLDER_NAME_TOOLBAR));
      assertElementPresentInWorkspaceTree(FOLDER_NAME_TOOLBAR);
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
      selenium.refresh();
      selenium.waitForPageToLoad("30000");
      Thread.sleep(TestConstants.SLEEP);
      selenium.mouseDownAt("//td[@class='exo-menuBarItem' and @menubartitle='File']", "");
      Thread.sleep(TestConstants.SLEEP);
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[contains(text(), 'New')]", "");
      selenium.mouseDownAt("//td[@class='exo-popupMenuTitleField']/nobr[text()='Folder...']", "");
      checkCreateFolderFormElements();
      selenium.click("scLocator=//IButton[ID=\"ideCreateFolderFormCreateButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      assertElementPresentInWorkspaceTree("New Folder");
      assertEquals(200, VirtualFileSystemUtils.get(URL + URLEncoder.encode("New Folder", "UTF-8")).getStatusCode());
   }

   /**
    * Checks the present of create folder form elements.
    */
   private void checkCreateFolderFormElements()
   {
      assertTrue(selenium.isTextPresent("Name of new folder:"));
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFolderForm\"]//input"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFolderFormCreateButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFolderFormCancelButton\"]"));
   }

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
