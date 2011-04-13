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

   @Ignore
   @Test
   public void testDeleteSeveralFoldersSimultaneously() throws Exception
   {

      waitForRootElement();
      IDE.navigator().selectItem(WS_URL);
      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      
      
      IDE.navigator().clickOpenIconOfFolder(URL+FOLDER_NAME_1+"/");
      Thread.sleep(TestConstants.SLEEP*4);
      
      IDE.navigator().selectItem(URL+FOLDER_NAME_1+"/");
      selenium.controlKeyDown();
      IDE.navigator().selectItem(URL+FOLDER_NAME_2+"/");
      selenium.controlKeyUp();
      
      // TODO After of capability select the few elements in IDE navigator
      //  assertTrue(selenium.isTextPresent("exact:Selected: 2 items"));

//      IDE.toolbar().assertButtonExistAtLeft(ToolbarCommands.File.DELETE, true);
//      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.DELETE, false);
//
      IDE.navigator().selectItem(URL+FOLDER_NAME_1+"/");
      selenium.controlKeyDown();
      IDE.navigator().selectItem(URL+FOLDER_NAME_3+"/");
      selenium.controlKeyUp();
      
      // TODO After of capability select the few elements in IDE navigator
      //assertTrue(selenium.isTextPresent("exact:Selected: 2 items"));
      
//      IDE.toolbar().assertButtonExistAtLeft(ToolbarCommands.File.DELETE, true);
//      IDE.toolbar().assertButtonEnabled(ToolbarCommands.File.DELETE, true);
      
//      IDE.toolbar().runCommand(ToolbarCommands.File.DELETE);
//      
//      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]"));
//      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]"));
//      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormCancelButton\"]"));
//      assertTrue(selenium.isTextPresent("exact:Do you want to delete 2 items?"));
//      selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]");
//      Thread.sleep(TestConstants.SLEEP);
//      
//      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]"));
//
//      IDE.navigator().assertItemNotPresent(WS_URL + FOLDER_NAME_1 + "/");
//      IDE.navigator().assertItemNotPresent(WS_URL + FOLDER_NAME_1 + "/" + FOLDER_NAME_2 + "/");
//      IDE.navigator().assertItemNotPresent(WS_URL + FOLDER_NAME_3 + "/");
//      IDE.navigator().assertItemNotPresent(WS_URL + FOLDER_NAME_3 + "/" + "test.groovy");
//
//      assertEquals(404, VirtualFileSystemUtils.get(URL + URLEncoder.encode(FOLDER_NAME_1, "UTF-8")).getStatusCode());
//      assertEquals(404, VirtualFileSystemUtils.get(URL + URLEncoder.encode(FOLDER_NAME_3, "UTF-8")).getStatusCode());
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
