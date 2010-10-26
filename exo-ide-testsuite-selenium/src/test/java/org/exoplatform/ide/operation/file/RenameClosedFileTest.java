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
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:musienko.maxim@gmail.com">Musienko Maxim</a>
 *
 */

//IDE-121 Rename Closed File 
public class RenameClosedFileTest extends BaseTest
{

   private final static String ORIG_FILE_NAME = "fileforrename.txt";
   
   private final static String RENAMED_FILE_NAME = "Renamed Test File.groovy";

   private final static String ORIG_URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + ORIG_FILE_NAME;
   
   private final static String RENAME_URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + RENAMED_FILE_NAME;

   private final static String PATH = "src/test/resources/org/exoplatform/ide/operation/file/" + ORIG_FILE_NAME;

   @BeforeClass
   public static void setUp()
   {
      try
      {
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

   @Test
   public void testRenameClosedFile() throws Exception
   {

      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(WS_NAME);
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);

      selectItemInWorkspaceTree(ORIG_FILE_NAME);
      
      
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.RENAME);
      
      assertTrue(selenium.isTextPresent("Rename item"));
      assertTrue(selenium.isTextPresent("Rename item to:"));
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideRenameItemForm\"]/header/member"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideRenameItemFormRenameButton\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideRenameItemFormCancelButton\"]/"));
      selenium.type("scLocator=//DynamicForm[ID=\"ideRenameItemFormDynamicForm\"]/item[name=ideRenameItemFormRenameField||Class=TextItem]/element",
            RENAMED_FILE_NAME);
      // ----5-------
      Thread.sleep(TestConstants.SLEEP);
      selenium.click("scLocator=//IButton[ID=\"ideRenameItemFormRenameButton\"]/");
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isTextPresent(RENAMED_FILE_NAME));
      
      assertEquals(404, VirtualFileSystemUtils.get(ORIG_URL).getStatusCode());
      assertEquals(200, VirtualFileSystemUtils.get(RENAME_URL).getStatusCode());
   }
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(ORIG_URL);
         VirtualFileSystemUtils.delete(RENAME_URL);
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