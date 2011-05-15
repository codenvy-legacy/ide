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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.Locators;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:musienko.maxim@gmail.com">Musienko Maxim</a>
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Dec 1, 2010 $
 *
 */

public class RenameClosedFileTest extends BaseTest
{
   private static String FOLDER_NAME;

   private static final String ORIG_FILE_NAME = "fileforrename.txt";
   
   private static final String RENAMED_FILE_NAME = "Renamed Test File.groovy";
   
   private static final String FILE_CONTENT = "file for rename";
   
   private static final String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME;
   
   private static final String PATH = "src/test/resources/org/exoplatform/ide/operation/file/" + ORIG_FILE_NAME;

   private static String ORIG_URL;
   
   private static String RENAME_URL;

   @Before
   public void setUp()
   {
      FOLDER_NAME = RenameClosedFileTest.class.getSimpleName() + "-" + System.currentTimeMillis();
      ORIG_URL = URL + "/" + FOLDER_NAME + "/" + ORIG_FILE_NAME;
      RENAME_URL = URL + "/" + FOLDER_NAME + "/" + RENAMED_FILE_NAME;
      
      try
      {
         VirtualFileSystemUtils.mkcol(URL + "/" + FOLDER_NAME);
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
   
   @After
   public void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + "/" + FOLDER_NAME);
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

   //IDE-121 Rename Closed File
   @Test
   public void testRenameClosedFile() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      IDE.WORKSPACE.selectItem(WS_URL);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      
      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);

      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME + "/" + ORIG_FILE_NAME);
      
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.RENAME);
      
      assertTrue(selenium.isElementPresent(Locators.RenameItemForm.SC_RENAME_ITEM_WINDOW_LOCATOR));
      assertTrue(selenium.isElementPresent(Locators.RenameItemForm.SC_NAME_FIELD_LOCATOR));
      assertTrue(selenium.isElementPresent(Locators.RenameItemForm.SC_MIME_TYPE_FIELD_LOCATOR));
      assertTrue(selenium.isElementPresent(Locators.RenameItemForm.SC_RENAME_BUTTON_LOCATOR));
      assertTrue(selenium.isElementPresent(Locators.RenameItemForm.SC_CANCEL_BUTTON_LOCATOR));
      
      selenium.type(Locators.RenameItemForm.SC_NAME_FIELD_LOCATOR, RENAMED_FILE_NAME);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      selenium.click(Locators.RenameItemForm.SC_RENAME_BUTTON_LOCATOR);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      IDE.NAVIGATION.assertItemVisible(WS_URL + FOLDER_NAME + "/" + RENAMED_FILE_NAME);
      IDE.NAVIGATION.assertItemNotVisible(WS_URL + FOLDER_NAME + "/" + ORIG_FILE_NAME);
      
      assertEquals(404, VirtualFileSystemUtils.get(ORIG_URL).getStatusCode());
      assertEquals(200, VirtualFileSystemUtils.get(RENAME_URL).getStatusCode());
   }
   
   @Test
   public void testChangeMimeType() throws Exception
   {
      refresh();
      IDE.WORKSPACE.selectItem(WS_URL);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);

      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME + "/" + ORIG_FILE_NAME);
      
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.RENAME);
      
      selenium.type(Locators.RenameItemForm.SC_MIME_TYPE_FIELD_LOCATOR, MimeType.TEXT_XML);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      selenium.click(Locators.RenameItemForm.SC_RENAME_BUTTON_LOCATOR);
      Thread.sleep(TestConstants.SLEEP);
      
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(ORIG_FILE_NAME, false);
      
      final String textFromEditor =IDE.EDITOR.getTextFromCodeEditor(0);
      
      assertEquals(FILE_CONTENT, textFromEditor);
      
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_PROPERTIES);
      
      assertEquals(MimeType.TEXT_XML, selenium.getText(Locators.PropertiesPanel.SC_CONTENT_TYPE_TEXTBOX));
   }
   
   @Test
   public void testRenameAndChangeMimeType() throws Exception
   {
      refresh();
      IDE.WORKSPACE.selectItem(WS_URL);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);

      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME + "/" + ORIG_FILE_NAME);
      
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.RENAME);
      
      selenium.type(Locators.RenameItemForm.SC_NAME_FIELD_LOCATOR, RENAMED_FILE_NAME);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium.type(Locators.RenameItemForm.SC_MIME_TYPE_FIELD_LOCATOR, MimeType.TEXT_XML);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      selenium.click(Locators.RenameItemForm.SC_RENAME_BUTTON_LOCATOR);
      Thread.sleep(TestConstants.SLEEP);
      
      IDE.NAVIGATION.assertItemVisible(WS_URL + FOLDER_NAME + "/" + RENAMED_FILE_NAME);
      IDE.NAVIGATION.assertItemNotVisible(WS_URL + FOLDER_NAME + "/" + ORIG_FILE_NAME);
      
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(RENAMED_FILE_NAME, false);
      
      final String textFromEditor =IDE.EDITOR.getTextFromCodeEditor(0);
      
      assertEquals(FILE_CONTENT, textFromEditor);
      
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_PROPERTIES);
      
      assertEquals(MimeType.TEXT_XML, selenium.getText(Locators.PropertiesPanel.SC_CONTENT_TYPE_TEXTBOX));
   }
   
}
