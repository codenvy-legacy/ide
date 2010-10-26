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
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/

// IDE-110  Delete several folders simultaneously

public class DeleteSeveralFoldersSimultaneously extends BaseTest
{

   private final static String FOLDER_NAME_1 = "test 1";

   private final static String FOLDER_NAME_2 = "test 2";

   private final static String FOLDER_NAME_3 = "test 3";
   
   private final static String FILE_NAME = "test.groovy";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(URL + FOLDER_NAME_1);
         VirtualFileSystemUtils.mkcol(URL + FOLDER_NAME_1 + "/" + FOLDER_NAME_2);
         VirtualFileSystemUtils.mkcol(URL + FOLDER_NAME_3);
         VirtualFileSystemUtils.put(
            "src/test/resources/org/exoplatform/ide/operation/restservice/RESTServiceGetURL.groovy",
            MimeType.GROOVY_SERVICE, URL + "/" + FOLDER_NAME_3 + "/" + FILE_NAME);
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

   @Test
   public void testDeleteSeveralFoldersSimultaneously() throws Exception
   {

      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(WS_NAME);
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);

      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=test 1]/col[0]/open");
      selenium.controlKeyDown();
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=test 2]/col[1]");
      selenium.controlKeyUp();
      assertTrue(selenium.isTextPresent("exact:Selected: 2 items"));
      assertTrue(selenium.isElementPresent("//div[@title='Delete Item(s)...']/div[@elementenabled='false']"));
      selenium.mouseDownAt("//div[@title='Delete Item(s)...']//img", "");
      selenium.mouseUpAt("//div[@title='Delete Item(s)...']//img", "");
      Thread.sleep(TestConstants.SLEEP);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]"));
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=test 1]/col[1]");
      selenium.controlKeyDown();
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=test 3]/col[1]");
      selenium.controlKeyUp();
      assertTrue(selenium.isTextPresent("exact:Selected: 2 items"));
      assertTrue(selenium.isElementPresent("//div[@title='Delete Item(s)...']/div[@elementenabled='true']"));
      selenium.mouseDownAt("//div[@title='Delete Item(s)...']//img", "");
      selenium.mouseUpAt("//div[@title='Delete Item(s)...']//img", "");
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormCancelButton\"]"));
      assertTrue(selenium.isTextPresent("exact:Do you want to delete 2 items?"));
      selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]"));

      assertElementNotPresentInWorkspaceTree(FOLDER_NAME_1);
      assertElementNotPresentInWorkspaceTree(FOLDER_NAME_2);
      assertElementNotPresentInWorkspaceTree(FOLDER_NAME_3);
      assertElementNotPresentInWorkspaceTree("test.groovy");

      assertEquals(404, VirtualFileSystemUtils.get(URL + URLEncoder.encode(FOLDER_NAME_1, "UTF-8")).getStatusCode());
      assertEquals(404, VirtualFileSystemUtils.get(URL + URLEncoder.encode(FOLDER_NAME_3, "UTF-8")).getStatusCode());

   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + FOLDER_NAME_1);
         VirtualFileSystemUtils.delete(URL + FOLDER_NAME_3);
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
