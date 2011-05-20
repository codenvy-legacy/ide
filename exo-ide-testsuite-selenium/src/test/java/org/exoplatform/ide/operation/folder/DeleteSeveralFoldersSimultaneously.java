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
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
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

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_NAME_1);
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_NAME_1 + "/" + FOLDER_NAME_2);
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_NAME_3);
         VirtualFileSystemUtils.put(
            "src/test/resources/org/exoplatform/ide/operation/restservice/RESTServiceGetURL.groovy",
            MimeType.GROOVY_SERVICE, WS_URL + "/" + FOLDER_NAME_3 + "/" + FILE_NAME);
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

   @Ignore
   @Test
   public void testDeleteSeveralFoldersSimultaneously() throws Exception
   {
      IDE.WORKSPACE.waitForItem(WS_URL + FOLDER_NAME_1 + "/");
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + FOLDER_NAME_1 + "/");

      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME_1 + "/");
      selenium.controlKeyDown();
      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME_2 + "/");
      selenium.controlKeyUp();

      // TODO After of capability select the few elements in IDE navigator
      //  assertTrue(selenium.isTextPresent("exact:Selected: 2 items"));

      //      IDE.TOOLBAR.assertButtonExistAtLeft(ToolbarCommands.File.DELETE, true);
      //      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.DELETE, false);

      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME_1 + "/");
      selenium.controlKeyDown();
      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME_3 + "/");
      selenium.controlKeyUp();

      // TODO After of capability select the few elements in IDE navigator
      //assertTrue(selenium.isTextPresent("exact:Selected: 2 items"));

      IDE.TOOLBAR.assertButtonExistAtLeft(ToolbarCommands.File.DELETE, true);
      //      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.File.DELETE, true);

      IDE.NAVIGATION.deleteSelectedItems();

      // TODO After of capability select the few elements in IDE navigator
      // assertTrue(selenium.isTextPresent("exact:Do you want to delete 2 items?"));

      chekDisappearDeleteItemForm();

      IDE.NAVIGATION.assertItemNotVisible(WS_URL + FOLDER_NAME_1 + "/");
      IDE.NAVIGATION.assertItemNotVisible(WS_URL + FOLDER_NAME_1 + "/" + FOLDER_NAME_2 + "/");
      IDE.NAVIGATION.assertItemNotVisible(WS_URL + FOLDER_NAME_3 + "/");
      IDE.NAVIGATION.assertItemNotVisible(WS_URL + FOLDER_NAME_3 + "/" + "test.groovy");

      assertEquals(404, VirtualFileSystemUtils.get(WS_URL + URLEncoder.encode(FOLDER_NAME_1, "UTF-8")).getStatusCode());
      assertEquals(404, VirtualFileSystemUtils.get(WS_URL + URLEncoder.encode(FOLDER_NAME_3, "UTF-8")).getStatusCode());
   }

   public void chekDisappearDeleteItemForm()
   {
      assertFalse(selenium.isElementPresent("//div[@view-id=\"ideDeleteItemsView\"]"));
      assertFalse(selenium.isElementPresent("//div[@class=\"Caption\"]/span[\"IDE\"]"));
      assertFalse(selenium.isElementPresent("//img[contains(@src,'http://localhost:8080/IDE/images/dialog/ask.png')]"));
      assertFalse(selenium
         .isElementPresent("//div[@class=\"gwt-Label\"]/br[\"Do you want to delete  \"]|/b[\"New Folder\"]"));
      assertFalse(selenium.isElementPresent("ideDeleteItemFormOkButton"));
      assertFalse(selenium.isElementPresent("ideDeleteItemFormCancelButton"));
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + FOLDER_NAME_1);
         VirtualFileSystemUtils.delete(WS_URL + FOLDER_NAME_3);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
   
}
