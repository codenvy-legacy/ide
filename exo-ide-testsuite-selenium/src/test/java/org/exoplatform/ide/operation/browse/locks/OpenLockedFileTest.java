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
package org.exoplatform.ide.operation.browse.locks;

import org.everrest.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Check, that can open locked file only in read-only mode.
 * 
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Sep 21, 2010 $
 *
 */
public class OpenLockedFileTest extends LockFileAbstract
{
   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   private static final String FOLDER_NAME = OpenLockedFileTest.class.getSimpleName();

   static final String FILE_NAME = "file-" + OpenLockedFileTest.class.getSimpleName();

   static final String LOCK_BUTTON =
      "//div[@class=\"exoIconButtonPanel\"and @enabled='false' and @title=\"Lock File\"]";

   @Before
   public void setUp()
   {
      final String filePath = "src/test/resources/org/exoplatform/ide/operation/browse/locks/test.html";
      try
      {
         VirtualFileSystemUtils.mkcol(URL + FOLDER_NAME);
         VirtualFileSystemUtils.put(filePath, MimeType.TEXT_HTML, URL + FOLDER_NAME + "/" + FILE_NAME);
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
         VirtualFileSystemUtils.delete(URL + FOLDER_NAME);
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
   public void testOpenLockedFile() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(URL + FOLDER_NAME + "/");
      IDE.WORKSPACE.selectItem(URL + FOLDER_NAME + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);

      //----- 1 ----------
      //open file
      IDE.WORKSPACE.waitForItem(URL + FOLDER_NAME + "/" + FILE_NAME);
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL + FOLDER_NAME + "/" + FILE_NAME, false);

      //----- 2 ----------
      //lock file
      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.LOCK_FILE);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.UNLOCK_FILE, true);
      checkFileLocking(URL + FOLDER_NAME + "/"+FILE_NAME, false);

      //----- 3 ----------
      //delete lock tokens from cookies and refresh
      deleteLockTokensCookies();
      refresh();

      //----- 4 ----------
      //check that file is locked
      waitForElementPresent(LOCK_BUTTON);

      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Editor.LOCK_FILE, false);
      checkCantSaveLockedFile();

      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER);
      IDE.WORKSPACE.clickOpenIconOfFolder(URL + FOLDER_NAME + "/");
      checkFileLocking(URL + FOLDER_NAME + "/" + FILE_NAME, true);

      //----- 5 ----------
      //close and open file
      IDE.EDITOR.closeFile(0);
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL + FOLDER_NAME + "/" + FILE_NAME, false);

      checkCantSaveLockedFile();

      IDE.EDITOR.closeFile(0);
   }

}
