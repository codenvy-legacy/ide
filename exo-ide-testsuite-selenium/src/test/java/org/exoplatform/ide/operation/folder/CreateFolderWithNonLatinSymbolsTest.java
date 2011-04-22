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
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class CreateFolderWithNonLatinSymbolsTest extends BaseTest
{
   private static String FOLDER_NAME = "Папка з кирилічними символами";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   @Ignore
   @Test
   public void testCreateFolderWithNonLatinSymbols() throws Exception
   {
      Thread.sleep(3000);
      // Create folder with Cyrillic name
      createFolder(FOLDER_NAME);
      //Chek in repository
      assertEquals(200, VirtualFileSystemUtils.get(URL + URLEncoder.encode(FOLDER_NAME, "UTF-8")).getStatusCode());
      Thread.sleep(120000);
      //TODO will be possible check nonlatin folders name in navigator; ((further code is not working))
      IDE.NAVIGATION.assertItemPresent(URL + FOLDER_NAME + "/");

      selenium.mouseDownAt("//div[@title='Delete Item(s)...']//img", "");
      selenium.mouseUpAt("//div[@title='Delete Item(s)...']//img", "");
      Thread.sleep(TestConstants.SLEEP);

      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideDeleteItemFormCancelButton\"]"));
      assertTrue(selenium.isTextPresent("exact:Do you want to delete " + FOLDER_NAME + " ?"));
      selenium.click("scLocator=//IButton[ID=\"ideDeleteItemFormOkButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeleteItemForm\"]"));
      IDE.NAVIGATION.assertItemNotPresent(WS_URL + URLEncoder.encode(FOLDER_NAME, "UTF-8") + "/");
      assertEquals(404, VirtualFileSystemUtils.get(URL + URLEncoder.encode(FOLDER_NAME, "UTF-8")).getStatusCode());
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + URLEncoder.encode(FOLDER_NAME, "UTF-8"));
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
