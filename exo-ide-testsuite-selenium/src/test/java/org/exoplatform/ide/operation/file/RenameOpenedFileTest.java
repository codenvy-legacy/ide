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
package org.exoplatform.ide.operation.file;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.Locators;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Dialogs;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:musienko.maxim@gmail.com">Musienko Maxim</a>
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Dec 1, 2010 $
 *
 */
public class RenameOpenedFileTest extends BaseTest
{

   private final static String ORIG_FILE_NAME = "fileforrename.txt";

   private final static String RENAMED_FILE_NAME = "Renamed Test File.groovy";

   private final static String FOLDER_NAME = RenameOpenedFileTest.class.getSimpleName();
   
   private static final String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER_NAME;

   private final static String ORIG_URL = URL + "/" + ORIG_FILE_NAME;

   private final static String RENAME_URL = URL + "/" + RENAMED_FILE_NAME;

   private final static String PATH = "src/test/resources/org/exoplatform/ide/operation/file/" + ORIG_FILE_NAME;

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(URL);
         VirtualFileSystemUtils.put(PATH, MimeType.TEXT_PLAIN, ORIG_URL);
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
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL);
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

   //IDE-81 Rename Opened File 
   @Test
   public void testRenameClosedFile() throws Exception
   {

      Thread.sleep(TestConstants.SLEEP);
      IDE.navigator().selectItem(WS_URL);
      
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      IDE.navigator().selectItem(WS_URL + FOLDER_NAME + "/");
      
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);

      IDE.navigator().openFileFromNavigationTreeWithCodeEditor(ORIG_FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);

      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.RENAME);
      
      assertTrue(selenium.isElementPresent(Locators.RenameItemForm.SC_RENAME_ITEM_WINDOW_LOCATOR));
      assertTrue(selenium.isElementPresent(Locators.RenameItemForm.SC_NAME_FIELD_LOCATOR));
      assertTrue(selenium.isElementPresent(Locators.RenameItemForm.SC_RENAME_BUTTON_LOCATOR));
      assertTrue(selenium.isElementPresent(Locators.RenameItemForm.SC_CANCEL_BUTTON_LOCATOR));
      //check, that mime-type field is disabled
      assertTrue(selenium.isElementPresent(Locators.RenameItemForm.MIME_TYPE_FIELD_DISABLED_LOCATOR));
      //check, warning message is present
      assertTrue(selenium.isTextPresent("Can't change mime-type to opened file"));
      
      selenium.type(Locators.RenameItemForm.SC_NAME_FIELD_LOCATOR, RENAMED_FILE_NAME);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      selenium.click(Locators.RenameItemForm.SC_RENAME_BUTTON_LOCATOR);
      Thread.sleep(TestConstants.SLEEP);
      
      IDE.navigator().assertItemPresent(WS_URL + FOLDER_NAME + "/" + RENAMED_FILE_NAME);
      IDE.navigator().assertItemNotPresent(WS_URL + FOLDER_NAME + "/" + ORIG_FILE_NAME);
      
      assertEquals(404, VirtualFileSystemUtils.get(ORIG_URL).getStatusCode());
      assertEquals(200, VirtualFileSystemUtils.get(RENAME_URL).getStatusCode());
      
      assertEquals(RENAMED_FILE_NAME, IDE.editor().getTabTitle(0));

      IDE.editor().typeTextIntoEditor(0, "change content");
      saveCurrentFile();
      assertFalse(selenium.isElementPresent(Dialogs.Locators.SC_WARN_DIALOG));

      refresh();
      
      IDE.editor().typeTextIntoEditor(0, "cookies cookies cookies cookies !!!111");
      saveCurrentFile();
      assertFalse(selenium.isElementPresent(Dialogs.Locators.SC_WARN_DIALOG));
   }

}